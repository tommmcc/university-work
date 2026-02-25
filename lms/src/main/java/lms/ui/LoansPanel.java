package lms.ui;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import lms.LibraryService;
import lms.model.Book;
import lms.model.Borrower;
import lms.model.Loan;

public class LoansPanel extends JPanel {

    private final LibraryService service;

    private final LoansTableModel tableModel;
    private final JTable table;
    private final TableRowSorter<LoansTableModel> sorter;

    private final JTextField txtSearch;

    private final JTextField txtId;
    private final JTextField txtBookId;
    private final JTextField txtBorrowerId;
    private final JTextField txtStartDate;
    private final JTextField txtDuration;

    public LoansPanel(LibraryService service) {
        this.service = service;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        txtSearch = new JTextField();
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        tableModel = new LoansTableModel(service);
        table = new JTable(tableModel);
        table.setRowHeight(26);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Loans"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add / Remove Loan (manual)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtId = new JTextField();
        txtBookId = new JTextField();
        txtBorrowerId = new JTextField();
        txtStartDate = new JTextField(Loan.UI_DATE_FORMAT.format(LocalDate.now()));
        txtDuration = new JTextField("7");

        int r = 0;
        addField(form, gbc, r++, "Loan ID", txtId);
        addField(form, gbc, r++, "Book ID", txtBookId);
        addField(form, gbc, r++, "Borrower ID", txtBorrowerId);
        addField(form, gbc, r++, "Start Date (DD-MM-YYYY)", txtStartDate);
        addField(form, gbc, r++, "Duration (days)", txtDuration);

        JButton btnAdd = new JButton("Add");
        JButton btnRemove = new JButton("Remove (by Loan ID)");
        JButton btnSeed = new JButton("Seed Sample Loans");

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
            int bookId = Integer.parseInt(txtBookId.getText().trim());
            int borrowerId = Integer.parseInt(txtBorrowerId.getText().trim());
            LocalDate startDate = LocalDate.parse(txtStartDate.getText().trim(), Loan.UI_DATE_FORMAT);
            int duration = Integer.parseInt(txtDuration.getText().trim());

            if (service.findLoan(id).isPresent()) {
                JOptionPane.showMessageDialog(this, "A loan with that ID already exists.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Book book = service.findBook(bookId).orElse(null);
            if (book == null) {
                JOptionPane.showMessageDialog(this, "Book ID not found.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!book.isAvailable()) {
                JOptionPane.showMessageDialog(this, "That book is not available.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Borrower borrower = service.findBorrower(borrowerId).orElse(null);
            if (borrower == null) {
                JOptionPane.showMessageDialog(this, "Borrower ID not found.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            book.setAvailable(false);
            service.addLoan(new Loan(id, bookId, borrowerId, startDate, duration));
            refreshTable();
            clearInputs();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Check inputs. Date must be DD-MM-YYYY and IDs/duration must be numbers.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void onRemove() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            Loan loan = service.findLoan(id).orElse(null);
            if (loan != null) {
                service.findBook(loan.getBookId()).ifPresent(b -> b.setAvailable(true));
            }
            service.removeLoan(id);
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid Loan ID to remove.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void seed() {
        if (service.getBooks().isEmpty() || service.getBorrowers().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seed Books and Borrowers first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!service.getLoans().isEmpty()) {
            int opt = JOptionPane.showConfirmDialog(this, "Loans already exist. Add samples anyway?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;
        }

        Book book = service.getBooks().stream().filter(Book::isAvailable).findFirst().orElse(null);
        Borrower borrower = service.getBorrowers().stream().findFirst().orElse(null);

        if (book == null || borrower == null) {
            JOptionPane.showMessageDialog(this, "Need at least one available book and borrower.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int base = service.getLoans().size() + 1;
        int loanId = 300 + base;

        book.setAvailable(false);
        service.addLoan(new Loan(loanId, book.getId(), borrower.getId(), LocalDate.now(), 14));
        refreshTable();
    }

    private void clearInputs() {
        txtId.setText("");
        txtBookId.setText("");
        txtBorrowerId.setText("");
        txtStartDate.setText(Loan.UI_DATE_FORMAT.format(LocalDate.now()));
        txtDuration.setText("7");
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    public void refreshTable() {
        tableModel.fireTableDataChanged();
    }

    private static class LoansTableModel extends AbstractTableModel {

        private final LibraryService service;
        private final String[] cols = {"Loan ID", "Book ID", "Borrower ID", "Start Date", "Duration (days)"};

        LoansTableModel(LibraryService service) {
            this.service = service;
        }

        @Override public int getRowCount() { return service.getLoans().size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Loan l = service.getLoans().get(rowIndex);
            return switch (columnIndex) {
                case 0 -> l.getId();
                case 1 -> l.getBookId();
                case 2 -> l.getBorrowerId();
                case 3 -> l.getStartDateUi(); // DD-MM-YYYY
                case 4 -> l.getDurationDays();
                default -> "";
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0, 1, 2, 4 -> Integer.class;
                default -> String.class;
            };
        }
    }
}