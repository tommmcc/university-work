package lms.ui;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import lms.LibraryService;
import lms.model.Book;
import lms.model.Borrower;
import lms.model.Loan;

public class CheckoutPanel extends JPanel {

    private final LibraryService service;

    private final JTextField txtLoanId;
    private final JComboBox<Book> cmbBooks;
    private final JComboBox<Borrower> cmbBorrowers;
    private final JTextField txtStartDate;
    private final JTextField txtDuration;

    public CheckoutPanel(LibraryService service) {
        this.service = service;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Checkout Book"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtLoanId = new JTextField();
        cmbBooks = new JComboBox<>();
        cmbBorrowers = new JComboBox<>();
        txtStartDate = new JTextField(Loan.UI_DATE_FORMAT.format(LocalDate.now()));
        txtDuration = new JTextField("7");

        int r = 0;
        addField(form, gbc, r++, "Loan ID", txtLoanId);
        addField(form, gbc, r++, "Book (available only)", cmbBooks);
        addField(form, gbc, r++, "Borrower", cmbBorrowers);
        addField(form, gbc, r++, "Start Date (DD-MM-YYYY)", txtStartDate);
        addField(form, gbc, r++, "Duration (days)", txtDuration);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCheckout = new JButton("Checkout");
        JButton btnReturn = new JButton("Return Selected Book");
        JButton btnRefresh = new JButton("Refresh Lists");
        buttons.add(btnCheckout);
        buttons.add(btnReturn);
        buttons.add(btnRefresh);

        add(form, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);

        btnCheckout.addActionListener(e -> onCheckout());
        btnReturn.addActionListener(e -> onReturn());
        btnRefresh.addActionListener(e -> refreshDropdowns());

        refreshDropdowns();
    }

    public void refreshDropdowns() {
        List<Book> availableBooks = service.getBooks().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());

        cmbBooks.removeAllItems();
        for (Book b : availableBooks) {
            cmbBooks.addItem(b);
        }

        cmbBorrowers.removeAllItems();
        for (Borrower br : service.getBorrowers()) {
            cmbBorrowers.addItem(br);
        }
    }

    private void onCheckout() {
        try {
            int loanId = Integer.parseInt(txtLoanId.getText().trim());
            Book book = (Book) cmbBooks.getSelectedItem();
            Borrower borrower = (Borrower) cmbBorrowers.getSelectedItem();
            LocalDate startDate = LocalDate.parse(txtStartDate.getText().trim(), Loan.UI_DATE_FORMAT);
            int duration = Integer.parseInt(txtDuration.getText().trim());

            if (service.findLoan(loanId).isPresent()) {
                JOptionPane.showMessageDialog(this, "A loan with that ID already exists.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (book == null) {
                JOptionPane.showMessageDialog(this, "No available book selected.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (borrower == null) {
                JOptionPane.showMessageDialog(this, "No borrower selected.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!book.isAvailable()) {
                JOptionPane.showMessageDialog(this, "Selected book is not available.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            book.setAvailable(false);
            service.addLoan(new Loan(loanId, book.getId(), borrower.getId(), startDate, duration));

            refreshDropdowns();
            clearInputs();

            JOptionPane.showMessageDialog(this, "Checkout successful.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Check inputs. Date must be DD-MM-YYYY and Loan ID/Duration must be numbers.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void onReturn() {
        try {
            String loanIdText = txtLoanId.getText().trim();
            if (loanIdText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter the Loan ID to return.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int loanId = Integer.parseInt(loanIdText);
            Loan loan = service.findLoan(loanId).orElse(null);
            if (loan == null) {
                JOptionPane.showMessageDialog(this, "Loan not found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            service.findBook(loan.getBookId()).ifPresent(b -> b.setAvailable(true));
            service.removeLoan(loanId);

            refreshDropdowns();
            JOptionPane.showMessageDialog(this, "Book returned successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid Loan ID.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearInputs() {
        txtLoanId.setText("");
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
}