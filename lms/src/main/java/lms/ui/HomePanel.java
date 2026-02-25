package lms.ui;

import lms.LibraryDB;
import lms.LibraryService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class HomePanel extends JPanel {

    private final LibraryService service;
    private final LibraryDB db;

    private final Runnable refreshAllViews;
    private final Runnable displayAllToOutput;
    private final JTextArea outputArea;

    public HomePanel(
            LibraryService service,
            LibraryDB db,
            Runnable refreshAllViews,
            Runnable displayAllToOutput,
            JTextArea outputArea
    ) {
        this.service = service;
        this.db = db;
        this.refreshAllViews = refreshAllViews;
        this.displayAllToOutput = displayAllToOutput;
        this.outputArea = outputArea;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);

        JPanel logoTile = new JPanel(new GridBagLayout());
        logoTile.setPreferredSize(new Dimension(120, 120));
        logoTile.setBackground(new Color(245, 245, 245));
        logoTile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel logoLabel = new JLabel(loadLogoOrFallbackIcon());
        logoTile.add(logoLabel);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("LMS");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 30f));

        JLabel subtitle = new JLabel(
                "Library Management System — modernized Swing + SQLite + clean architecture"
        );
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 14f));

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(subtitle);

        header.add(logoTile, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel(new GridLayout(1, 2, 20, 20));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(25, 0, 25, 0));

        center.add(buildCard(
                "Quick Start",
                """
                1) Seed sample books + borrowers
                2) Checkout a book in the Checkout tab
                3) View Loans tab
                4) Save to DB, Clear, then Load to verify persistence
                """.trim()
        ));

        center.add(buildCard(
                "What’s Improved",
                """
                • Clean separation: UI ↔ Service ↔ DB
                • JTable views + search/filtering
                • Portable SQLite persistence
                • Input validation + business rules
                • Modern look via FlatLaf (optional)
                """.trim()
        ));

        return center;
    }

    private JComponent buildCard(String heading, String body) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(16, 16, 16, 16)
        ));
        card.setBackground(Color.WHITE);

        JLabel headingLabel = new JLabel(heading);
        headingLabel.setFont(headingLabel.getFont().deriveFont(Font.BOLD, 17f));

        JTextArea content = new JTextArea(body);
        content.setEditable(false);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setOpaque(false);
        content.setFont(content.getFont().deriveFont(Font.PLAIN, 13f));

        card.add(headingLabel, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JComponent buildActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actions.setOpaque(false);

        JButton btnSeedAll = new JButton("Seed Sample Data");
        JButton btnDisplayAll = new JButton("Display All (toString loop)");
        JButton btnSave = new JButton("Save to DB");
        JButton btnLoad = new JButton("Load from DB");
        JButton btnClear = new JButton("Clear All");

        actions.add(btnSeedAll);
        actions.add(btnDisplayAll);
        actions.add(btnSave);
        actions.add(btnLoad);
        actions.add(btnClear);

        btnSeedAll.addActionListener(e -> {
            seedIfMissing();
            refreshAllViews.run();
            outputArea.setText("Seeded sample data.\n");
        });

        btnDisplayAll.addActionListener(e -> displayAllToOutput.run());

        btnSave.addActionListener(e -> {
            try {
                db.saveAll(service.getBooks(), service.getBorrowers(), service.getLoans());
                outputArea.setText("Saved all data to database successfully.\n");
            } catch (Exception ex) {
                outputArea.setText("ERROR saving to DB:\n" + ex.getMessage());
            }
        });

        btnLoad.addActionListener(e -> {
            try {
                var loaded = db.loadAll();
                service.clearAll();
                loaded.books().forEach(service::addBook);
                loaded.borrowers().forEach(service::addBorrower);
                loaded.loans().forEach(service::addLoan);
                refreshAllViews.run();
                outputArea.setText("Loaded all data from database successfully.\n");
            } catch (Exception ex) {
                outputArea.setText("ERROR loading from DB:\n" + ex.getMessage());
            }
        });

        btnClear.addActionListener(e -> {
            service.clearAll();
            refreshAllViews.run();
            outputArea.setText("Cleared all in-memory data.\n");
        });

        return actions;
    }

    private void seedIfMissing() {
        if (service.getBooks().isEmpty()) {
            service.addBook(new lms.model.Book(1001, "Clean Code", "Robert C. Martin", 2.5, true));
            service.addBook(new lms.model.Book(1002, "Effective Java", "Joshua Bloch", 3.0, true));
            service.addBook(new lms.model.Book(1003, "Design Patterns", "GoF", 3.5, true));
        }
        if (service.getBorrowers().isEmpty()) {
            service.addBorrower(new lms.model.Borrower(2001, "Alex Chen", "alex.chen@example.com"));
            service.addBorrower(new lms.model.Borrower(2002, "Sam Taylor", "sam.taylor@example.com"));
            service.addBorrower(new lms.model.Borrower(2003, "Jordan Lee", "jordan.lee@example.com"));
        }
    }

    private Icon loadLogoOrFallbackIcon() {
        // We render into a square box and preserve aspect ratio
        final int box = 100;

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("lms.png")) {
            if (in == null) {
                return UIManager.getIcon("OptionPane.informationIcon");
            }

            BufferedImage src = ImageIO.read(in);
            if (src == null) {
                return UIManager.getIcon("OptionPane.informationIcon");
            }

            int srcW = src.getWidth();
            int srcH = src.getHeight();
            if (srcW <= 0 || srcH <= 0) {
                return UIManager.getIcon("OptionPane.informationIcon");
            }

            double scale = Math.min((double) box / srcW, (double) box / srcH);
            int newW = (int) Math.round(srcW * scale);
            int newH = (int) Math.round(srcH * scale);

            BufferedImage canvas = new BufferedImage(box, box, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = canvas.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = (box - newW) / 2;
            int y = (box - newH) / 2;

            g2.drawImage(src, x, y, newW, newH, null);
            g2.dispose();

            return new ImageIcon(canvas);

        } catch (Exception e) {
            return UIManager.getIcon("OptionPane.informationIcon");
        }
    }
}