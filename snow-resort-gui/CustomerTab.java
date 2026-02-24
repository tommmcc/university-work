import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class CustomerTab extends JPanel {
    private ArrayList<Customer> customers;
    private JTextArea customerDisplay;
    private JTextField nameField;
    private JComboBox<String> levelCombo;
    private SnowResort resort;

    // Notify GUI/tabs that customers changed
    private Runnable onCustomerAdded;

    public CustomerTab(ArrayList<Customer> customers, SnowResort resort) {
        this(customers, resort, null);
    }

    public CustomerTab(ArrayList<Customer> customers, SnowResort resort, Runnable onCustomerAdded) {
        this.customers = customers;
        this.resort = resort;
        this.onCustomerAdded = onCustomerAdded;

        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        formPanel.add(nameField);

        // Teacher fix: enforce level with dropdown
        formPanel.add(new JLabel("Ski Level:"));
        levelCombo = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Expert"});
        formPanel.add(levelCombo);

        JButton addButton = new JButton("Add Customer");
        addButton.addActionListener(e -> addCustomer());
        formPanel.add(addButton);

        add(formPanel, BorderLayout.NORTH);

        customerDisplay = new JTextArea(10, 40);
        customerDisplay.setEditable(false);
        add(new JScrollPane(customerDisplay), BorderLayout.CENTER);

        displayAllCustomers();
    }

    public void displayAllCustomers() {
        StringBuilder displayText = new StringBuilder();
        for (Customer customer : customers) {
            displayText.append(customer).append("\n");
        }
        customerDisplay.setText(displayText.toString());
    }

    private void addCustomer() {
        String name = nameField.getText().trim();
        String level = (String) levelCombo.getSelectedItem();

        // Allow spaces/hyphens; still validate
        if (name.isEmpty() || !name.matches("[a-zA-Z\\-\\s]+")) {
            JOptionPane.showMessageDialog(this, "Customer name must contain letters only (spaces/hyphens allowed).");
            return;
        }

        int id = resort.getNextCustomerId();
        Customer newCustomer = new Customer(id, name, level);

        resort.addCustomer(newCustomer);
        resort.saveCustomersToFile();

        displayAllCustomers();

        nameField.setText("");
        levelCombo.setSelectedIndex(0);

        // Teacher fix: update dropdowns elsewhere
        if (onCustomerAdded != null) {
            onCustomerAdded.run();
        }
    }
}