import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

public class ResortGUI extends JFrame implements ActionListener {
    private SnowResort resort;

    private AccommodationTab accommodationTab;
    private CustomerTab customerTab;
    private TravelPackageTab travelPackageTab;

    private JPanel controlPanel;
    private JButton saveButton, loadButton, createPackageButton, viewPackagesButton, quitButton;

    public ResortGUI() {
        setTitle("Snow Resort");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        resort = new SnowResort();

        JTabbedPane tabbedPane = new JTabbedPane();

        // Home tab (won't crash if image missing)
        JPanel homeTab = new JPanel();
        JLabel imageLabel = new JLabel();
        ImageIcon icon = loadHomeImage();
        if (icon != null) {
            imageLabel.setIcon(icon);
        } else {
            imageLabel.setText("Snow Resort");
            imageLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        }
        homeTab.add(imageLabel);
        tabbedPane.addTab("Home", homeTab);

        accommodationTab = new AccommodationTab(resort.getAccommodations(), resort);
        tabbedPane.addTab("Accommodations", accommodationTab);

        customerTab = new CustomerTab(resort.getCustomers(), resort, () -> travelPackageTab.refreshCustomers());
        tabbedPane.addTab("Customers", customerTab);

        travelPackageTab = new TravelPackageTab(resort.getCustomers(), resort.getAccommodations(), resort.getPackages(), resort);
        tabbedPane.addTab("Packages", travelPackageTab);

        add(tabbedPane, BorderLayout.CENTER);

        // Control buttons
        controlPanel = new JPanel(new FlowLayout());

        saveButton = new JButton("Save Data");
        saveButton.addActionListener(e -> saveData());

        loadButton = new JButton("Load Data");
        loadButton.addActionListener(e -> loadData());

        createPackageButton = new JButton("Create Package");
        createPackageButton.addActionListener(e -> travelPackageTab.createPackage());

        viewPackagesButton = new JButton("View Packages");
        viewPackagesButton.addActionListener(e -> travelPackageTab.displaySavedPackagesWindow());

        quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));

        controlPanel.add(saveButton);
        controlPanel.add(loadButton);
        controlPanel.add(createPackageButton);
        controlPanel.add(viewPackagesButton);
        controlPanel.add(quitButton);

        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private ImageIcon loadHomeImage() {
        String[] candidates = {
                "images/snow.jpg",
                "SnowResort/images/snow.jpg",
                "snow.jpg"
        };
        for (String path : candidates) {
            File f = new File(path);
            if (f.exists()) {
                return new ImageIcon(path);
            }
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Not used
    }

    private void saveData() {
        resort.saveCustomersToFile();
        resort.savePackagesToFile();
        JOptionPane.showMessageDialog(this, "Data saved successfully.");
    }

    private void loadData() {
        // Reload inside same resort instance (keeps tab references valid)
        resort.reloadFromFiles();

        // Refresh displays/models
        customerTab.displayAllCustomers();
        travelPackageTab.refreshAll();
        accommodationTab.refreshDisplay();

        JOptionPane.showMessageDialog(this, "Data loaded successfully.");
    }

    public static void main(String[] args) {
        new ResortGUI();
    }
}