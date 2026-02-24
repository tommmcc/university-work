import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class TravelPackageTab extends JPanel {
    private SnowResort resort;

    private JTextArea packageDisplay;

    private DefaultComboBoxModel<Customer> customerModel;
    private DefaultComboBoxModel<Accommodation> accommodationModel;
    private DefaultComboBoxModel<TravelPackage> packageModel;

    // Create package controls
    private JComboBox<Customer> customerComboBox;
    private JComboBox<Accommodation> accommodationComboBox;

    // Modify package controls
    private JComboBox<TravelPackage> packageComboBox;

    // Lift pass controls
    private JTextField liftPassDaysField;

    // Lesson controls
    private JComboBox<Lesson> lessonTypeCombo;
    private JSpinner lessonCountSpinner;

    public TravelPackageTab(ArrayList<Customer> customers,
                            ArrayList<Accommodation> accommodations,
                            ArrayList<TravelPackage> packages,
                            SnowResort resort) {

        this.resort = resort;
        setLayout(new BorderLayout());

        // ------------------------------
        // 1) Create ALL models + widgets first (prevents null NPEs)
        // ------------------------------
        customerModel = new DefaultComboBoxModel<>();
        accommodationModel = new DefaultComboBoxModel<>();
        packageModel = new DefaultComboBoxModel<>();

        customerComboBox = new JComboBox<>(customerModel);
        accommodationComboBox = new JComboBox<>(accommodationModel);
        packageComboBox = new JComboBox<>(packageModel);

        // IMPORTANT: lessonTypeCombo MUST exist before any refresh methods run
        lessonTypeCombo = new JComboBox<>(new DefaultComboBoxModel<>());
        lessonCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));

        liftPassDaysField = new JTextField(5);

        // ------------------------------
        // 2) Build UI
        // ------------------------------
        packageDisplay = new JTextArea(15, 40);
        packageDisplay.setEditable(false);
        add(new JScrollPane(packageDisplay), BorderLayout.CENTER);

        // ----- Create Panel -----
        JPanel createPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        createPanel.setBorder(BorderFactory.createTitledBorder("Create Package"));

        createPanel.add(new JLabel("Select Customer:"));
        createPanel.add(customerComboBox);

        createPanel.add(new JLabel("Select Accommodation (available):"));
        createPanel.add(accommodationComboBox);

        JButton createButton = new JButton("Create Package");
        createButton.addActionListener(e -> createPackage());
        createPanel.add(createButton);

        JButton refreshButton = new JButton("Refresh Lists");
        refreshButton.addActionListener(e -> refreshAll());
        createPanel.add(refreshButton);

        // ----- Modify Panel -----
        JPanel modifyPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        modifyPanel.setBorder(BorderFactory.createTitledBorder("Modify Existing Package"));

        modifyPanel.add(new JLabel("Select Package:"));
        modifyPanel.add(packageComboBox);

        modifyPanel.add(new JLabel("Lift Pass Days:"));
        modifyPanel.add(liftPassDaysField);

        JButton addLiftPassButton = new JButton("Add/Update Lift Pass");
        addLiftPassButton.addActionListener(e -> addOrUpdateLiftPass());
        modifyPanel.add(addLiftPassButton);
        modifyPanel.add(new JLabel("")); // spacer

        modifyPanel.add(new JLabel("Lesson Type (matches customer level):"));
        modifyPanel.add(lessonTypeCombo);

        modifyPanel.add(new JLabel("Number of Lessons:"));
        modifyPanel.add(lessonCountSpinner);

        JButton addLessonsButton = new JButton("Add Lessons");
        addLessonsButton.addActionListener(e -> addLessons());
        modifyPanel.add(addLessonsButton);

        JButton viewButton = new JButton("View Packages");
        viewButton.addActionListener(e -> displaySavedPackagesWindow());
        modifyPanel.add(viewButton);

        JPanel top = new JPanel(new GridLayout(1, 2, 10, 10));
        top.add(createPanel);
        top.add(modifyPanel);

        add(top, BorderLayout.NORTH);

        // ------------------------------
        // 3) Wire listeners (after widgets exist)
        // ------------------------------
        packageComboBox.addActionListener(e -> {
            refreshLessonTypesForSelectedPackage();
            refreshPackageSummary();
        });

        // ------------------------------
        // 4) Now it’s safe to refresh models/data
        // ------------------------------
        refreshAll();
        refreshLessonTypesForSelectedPackage();
        refreshPackageSummary();
    }

    // ------------------------------
    // Public refresh helpers (called from ResortGUI / other tabs)
    // ------------------------------
    public void refreshCustomers() {
        customerModel.removeAllElements();
        for (Customer c : resort.getCustomers()) {
            customerModel.addElement(c);
        }
    }

    public void refreshAccommodations() {
        accommodationModel.removeAllElements();
        for (Accommodation a : resort.getAvailableAccommodations()) {
            accommodationModel.addElement(a);
        }
    }

    public void refreshPackages() {
        packageModel.removeAllElements();
        for (TravelPackage p : resort.getPackages()) {
            packageModel.addElement(p);
        }
        refreshLessonTypesForSelectedPackage();
        refreshPackageSummary();
    }

    public void refreshAll() {
        refreshCustomers();
        refreshAccommodations();
        refreshPackages();
    }

    // ------------------------------
    // Spec-aligned workflow:
    // Create package first (customer + accommodation only)
    // ------------------------------
    public void createPackage() {
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
        Accommodation selectedAccommodation = (Accommodation) accommodationComboBox.getSelectedItem();

        if (selectedCustomer == null || selectedAccommodation == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer and an available accommodation.");
            return;
        }

        if (!selectedAccommodation.isAvailable()) {
            JOptionPane.showMessageDialog(this, "That accommodation is already booked. Refresh and select another.");
            return;
        }

        TravelPackage newPackage = new TravelPackage(selectedCustomer, selectedAccommodation);
        resort.addPackage(newPackage);

        // Mark accommodation unavailable immediately
        selectedAccommodation.setAvailable(false);

        refreshAccommodations();
        refreshPackages();

        JOptionPane.showMessageDialog(this,
                "Package created.\nNow select it in 'Modify Existing Package' to add lift pass and lessons.");
    }

    // ------------------------------
    // Add / update lift pass for selected package
    // ------------------------------
    private void addOrUpdateLiftPass() {
        TravelPackage selectedPackage = (TravelPackage) packageComboBox.getSelectedItem();
        if (selectedPackage == null) {
            JOptionPane.showMessageDialog(this, "Please select a package first.");
            return;
        }

        int days;
        try {
            days = Integer.parseInt(liftPassDaysField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer for lift pass days.");
            return;
        }

        if (days < 0) {
            JOptionPane.showMessageDialog(this, "Lift pass days cannot be negative.");
            return;
        }

        selectedPackage.setLiftPassDays(days);
        refreshPackageSummary();
        JOptionPane.showMessageDialog(this, "Lift pass updated for selected package.");
    }

    // ------------------------------
    // Add lessons (with quantity) for selected package
    // Enforces lesson level == customer ski level
    // ------------------------------
    private void addLessons() {
        TravelPackage selectedPackage = (TravelPackage) packageComboBox.getSelectedItem();
        if (selectedPackage == null) {
            JOptionPane.showMessageDialog(this, "Please select a package first.");
            return;
        }

        Lesson lesson = (Lesson) lessonTypeCombo.getSelectedItem();
        if (lesson == null) {
            JOptionPane.showMessageDialog(this, "No lesson available for this customer's ski level.");
            return;
        }

        String customerLevel = selectedPackage.getCustomer().getSkiLevel();
        if (!lesson.getLevel().equalsIgnoreCase(customerLevel)) {
            JOptionPane.showMessageDialog(this,
                    "Lesson level must match customer's ski level: " + customerLevel);
            return;
        }

        int count = (Integer) lessonCountSpinner.getValue();
        selectedPackage.addLessons(lesson, count);

        refreshPackageSummary();
        JOptionPane.showMessageDialog(this, "Added " + count + " lessons to selected package.");
    }

    // ------------------------------
    // Prevents NPE and ensures only correct-level lessons are selectable
    // ------------------------------
    private void refreshLessonTypesForSelectedPackage() {
        // Safety guard: during construction or weird states
        if (lessonTypeCombo == null) return;

        DefaultComboBoxModel<Lesson> model = (DefaultComboBoxModel<Lesson>) lessonTypeCombo.getModel();
        model.removeAllElements();

        TravelPackage selectedPackage = (TravelPackage) packageComboBox.getSelectedItem();
        if (selectedPackage == null) return;

        String customerLevel = selectedPackage.getCustomer().getSkiLevel();
        for (Lesson lesson : resort.getLessons()) {
            if (lesson.getLevel().equalsIgnoreCase(customerLevel)) {
                model.addElement(lesson);
            }
        }
    }

    private void refreshPackageSummary() {
        TravelPackage selectedPackage = (TravelPackage) packageComboBox.getSelectedItem();
        if (selectedPackage == null) {
            packageDisplay.setText("No package selected.");
            return;
        }
        packageDisplay.setText(selectedPackage.toString());
    }

    public void displaySavedPackagesWindow() {
        ArrayList<TravelPackage> savedPackages = resort.getPackages();

        JFrame viewPackagesFrame = new JFrame("Saved Packages");
        viewPackagesFrame.setSize(650, 500);

        JTextArea packageDisplayArea = new JTextArea();
        packageDisplayArea.setEditable(false);

        if (savedPackages.isEmpty()) {
            packageDisplayArea.setText("No saved packages available.");
        } else {
            StringBuilder packageDetails = new StringBuilder("Saved Packages:\n\n");
            for (TravelPackage travelPackage : savedPackages) {
                packageDetails.append(travelPackage.toString()).append("\n\n");
            }
            packageDisplayArea.setText(packageDetails.toString());
        }

        JScrollPane scrollPane = new JScrollPane(packageDisplayArea);
        viewPackagesFrame.add(scrollPane);
        viewPackagesFrame.setVisible(true);
    }
}