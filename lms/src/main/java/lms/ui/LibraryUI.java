package lms.ui;

import java.awt.*;
import javax.swing.*;
import lms.LibraryDB;
import lms.LibraryService;

public class LibraryUI extends JFrame {

    private final LibraryService service;
    private final LibraryDB db;

    private final JTextArea outputArea;

    public LibraryUI() {
        super("Library Management System (LMS)");

        this.service = new LibraryService();
        this.db = new LibraryDB();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        // Output panel (rubric-style display)
        outputArea = new JTextArea(12, 80);
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Output (for display loop / messages)"));

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        BooksPanel booksPanel = new BooksPanel(service);
        BorrowersPanel borrowersPanel = new BorrowersPanel(service);
        LoansPanel loansPanel = new LoansPanel(service);
        CheckoutPanel checkoutPanel = new CheckoutPanel(service);

        Runnable refreshAll = () -> {
            booksPanel.refreshTable();
            borrowersPanel.refreshTable();
            loansPanel.refreshTable();
            checkoutPanel.refreshDropdowns();
        };

        Runnable displayAll = this::displayAllToOutput;

        HomePanel homePanel = new HomePanel(service, db, refreshAll, displayAll, outputArea);

        tabs.addTab("Home", homePanel);
        tabs.addTab("Books", booksPanel);
        tabs.addTab("Borrowers", borrowersPanel);
        tabs.addTab("Loans", loansPanel);
        tabs.addTab("Checkout", checkoutPanel);

        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tabs, BorderLayout.CENTER);
        centerPanel.add(outputScroll, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);
    }

    private void displayAllToOutput() {
        outputArea.setText("");

        outputArea.append("=== BOOKS ===\n");
        for (var b : service.getBooks()) { // explicit loop for rubric
            outputArea.append(b.toString());
            outputArea.append("\n");
        }

        outputArea.append("\n=== BORROWERS ===\n");
        for (var br : service.getBorrowers()) {
            outputArea.append(br.toString());
            outputArea.append("\n");
        }

        outputArea.append("\n=== LOANS ===\n");
        for (var l : service.getLoans()) {
            outputArea.append(l.toString());
            outputArea.append("\n");
        }
    }
}