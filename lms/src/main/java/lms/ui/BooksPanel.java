package lms.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import lms.LibraryService;
import lms.model.Book;

public class BooksPanel extends JPanel {

    private final LibraryService service;

    private final BooksTableModel tableModel;
    private final JTable table;
    private final TableRowSorter<BooksTableModel> sorter;

    private final JTextField txtSearch;

    private final JTextField txtId;
    private final JTextField txtTitle;
    private final JTextField txtAuthor;
    private final JTextField txtPricePerDay;
    private final JCheckBox chkAvailable;

    public BooksPanel(LibraryService service) {
        this.service = service;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search
        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        txtSearch = new JTextField();
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // Table
        tableModel = new BooksTableModel(service);
        table = new JTable(tableModel);
        table.setRowHeight(26);

        sorter = new TableRowSorter<>(tableModel);
        // No custom comparator needed: Integer/Double/Boolean columns will sort naturally
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Books"));

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add / Remove Book"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtId = new JTextField();
        txtTitle = new JTextField();
        txtAuthor = new JTextField();
        txtPricePerDay = new JTextField();
        chkAvailable = new JCheckBox("Available", true);

        int r = 0;
        addField(form, gbc, r++, "ID", txtId);
        addField(form, gbc, r++, "Title", txtTitle);
        addField(form, gbc, r++, "Author", txtAuthor);
        addField(form, gbc, r++, "Price/Day", txtPricePerDay);

        gbc.gridx = 1; gbc.gridy = r; gbc.weightx = 1;
        form.add(chkAvailable, gbc);

        // Buttons
        JButton btnAdd = new JButton("Add");
        JButton btnRemove = new JButton("Remove (by ID)");
        JButton btnToggleAvailability = new JButton("Toggle Availability (selected)");
        JButton btnSeed = new JButton("Seed Sample Books");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnAdd);
        buttons.add(btnRemove);
        buttons.add(btnToggleAvailability);
        buttons.add(btnSeed);

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.add(form, BorderLayout.NORTH);
        rightPanel.add(buttons, BorderLayout.SOUTH);

        // Layout combine
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // Search filtering
        txtSearch.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            String text = txtSearch.getText().trim();
            if (text.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        // Actions
        btnAdd.addActionListener(e -> onAdd());
        btnRemove.addActionListener(e -> onRemove());
        btnToggleAvailability.addActionListener(e -> onToggleAvailability());
        btnSeed.addActionListener(e -> seed());
    }

    private void onAdd() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String title = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            double price = Double.parseDouble(txtPricePerDay.getText().trim());
            boolean available = chkAvailable.isSelected();

            if (title.isBlank() || author.isBlank()) {
                JOptionPane.showMessageDialog(this, "Title and Author are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (service.findBook(id).isPresent()) {
                JOptionPane.showMessageDialog(this, "A book with that ID already exists.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.addBook(new Book(id, title, author, price, available));
            refreshTable();
            clearInputs();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be integer and Price/Day must be numeric.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onRemove() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            service.removeBook(id);
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid ID to remove.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onToggleAvailability() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        Book b = service.getBooks().get(modelRow);
        b.setAvailable(!b.isAvailable());
        refreshTable();
    }

    private void seed() {
        if (!service.getBooks().isEmpty()) {
            int opt = JOptionPane.showConfirmDialog(this, "Books already exist. Add samples anyway?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;
        }
        int base = service.getBooks().size() + 1;
        service.addBook(new Book(100 + base, "Clean Code", "Robert C. Martin", 2.5, true));
        service.addBook(new Book(101 + base, "Effective Java", "Joshua Bloch", 3.0, true));
        service.addBook(new Book(102 + base, "Design Patterns", "GoF", 3.5, true));
        refreshTable();
    }

    private void clearInputs() {
        txtId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtPricePerDay.setText("");
        chkAvailable.setSelected(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label + ":"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    public void refreshTable() {
        tableModel.fireTableDataChanged();
    }

    private static class BooksTableModel extends AbstractTableModel {
        private final LibraryService service;
        private final String[] cols = {"ID", "Title", "Author", "Price/Day", "Available"};

        BooksTableModel(LibraryService service) {
            this.service = service;
        }

        @Override public int getRowCount() { return service.getBooks().size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Book b = service.getBooks().get(rowIndex);
            return switch (columnIndex) {
                case 0 -> b.getId();
                case 1 -> b.getTitle();
                case 2 -> b.getAuthor();
                case 3 -> b.getPricePerDay();
                case 4 -> b.isAvailable();
                default -> "";
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0 -> Integer.class;
                case 3 -> Double.class;
                case 4 -> Boolean.class;
                default -> String.class;
            };
        }
    }
}