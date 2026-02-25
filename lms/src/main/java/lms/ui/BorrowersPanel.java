package lms.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import lms.LibraryService;
import lms.model.Borrower;

public class BorrowersPanel extends JPanel {

    private final LibraryService service;

    private final BorrowersTableModel tableModel;
    private final JTable table;
    private final TableRowSorter<BorrowersTableModel> sorter;

    private final JTextField txtSearch;

    private final JTextField txtId;
    private final JTextField txtName;
    private final JTextField txtEmail;

    public BorrowersPanel(LibraryService service) {
        this.service = service;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        txtSearch = new JTextField();
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        tableModel = new BorrowersTableModel(service);
        table = new JTable(tableModel);
        table.setRowHeight(26);

        sorter = new TableRowSorter<>(tableModel);
        // No custom comparator needed
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Borrowers"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add / Remove Borrower"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtId = new JTextField();
        txtName = new JTextField();
        txtEmail = new JTextField();

        int r = 0;
        addField(form, gbc, r++, "ID", txtId);
        addField(form, gbc, r++, "Name", txtName);
        addField(form, gbc, r++, "Email", txtEmail);

        JButton btnAdd = new JButton("Add");
        JButton btnRemove = new JButton("Remove (by ID)");
        JButton btnSeed = new JButton("Seed Sample Borrowers");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnAdd);
        buttons.add(btnRemove);
        buttons.add(btnSeed);

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.add(form, BorderLayout.NORTH);
        rightPanel.add(buttons, BorderLayout.SOUTH);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        txtSearch.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            String text = txtSearch.getText().trim();
            if (text.isEmpty()) sorter.setRowFilter(null);
            else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        });

        btnAdd.addActionListener(e -> onAdd());
        btnRemove.addActionListener(e -> onRemove());
        btnSeed.addActionListener(e -> seed());
    }

    private void onAdd() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();

            if (name.isBlank() || email.isBlank()) {
                JOptionPane.showMessageDialog(this, "Name and Email are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this, "Enter a valid email.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (service.findBorrower(id).isPresent()) {
                JOptionPane.showMessageDialog(this, "A borrower with that ID already exists.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.addBorrower(new Borrower(id, name, email));
            refreshTable();
            clearInputs();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be an integer.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onRemove() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            service.removeBorrower(id);
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid ID to remove.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void seed() {
        if (!service.getBorrowers().isEmpty()) {
            int opt = JOptionPane.showConfirmDialog(this, "Borrowers already exist. Add samples anyway?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;
        }
        int base = service.getBorrowers().size() + 1;
        service.addBorrower(new Borrower(200 + base, "Alex Chen", "alex.chen@example.com"));
        service.addBorrower(new Borrower(201 + base, "Sam Taylor", "sam.taylor@example.com"));
        service.addBorrower(new Borrower(202 + base, "Jordan Lee", "jordan.lee@example.com"));
        refreshTable();
    }

    private void clearInputs() {
        txtId.setText("");
        txtName.setText("");
        txtEmail.setText("");
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

    private static class BorrowersTableModel extends AbstractTableModel {
        private final LibraryService service;
        private final String[] cols = {"ID", "Name", "Email"};

        BorrowersTableModel(LibraryService service) {
            this.service = service;
        }

        @Override public int getRowCount() { return service.getBorrowers().size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Borrower b = service.getBorrowers().get(rowIndex);
            return switch (columnIndex) {
                case 0 -> b.getId();
                case 1 -> b.getName();
                case 2 -> b.getEmail();
                default -> "";
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Integer.class : String.class;
        }
    }
}