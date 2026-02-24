import java.awt.*;
import javax.swing.*;

public class AccommodationTab extends JPanel {
    private JTextArea displayArea;
    private SnowResort resort;
    private boolean isAllDisplayed = false;
    private boolean isAvailableDisplayed = false;

    public AccommodationTab(java.util.ArrayList<Accommodation> accommodations, SnowResort resort) {
        // Keep signature the same, but use resort as the source of truth.
        this.resort = resort;

        setLayout(new BorderLayout());

        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JButton toggleAllButton = new JButton("Accommodations");
        toggleAllButton.addActionListener(e -> toggleAllAccommodations());

        JButton toggleAvailableButton = new JButton("Available Accommodations");
        toggleAvailableButton.addActionListener(e -> toggleAvailableAccommodations());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(toggleAllButton);
        buttonPanel.add(toggleAvailableButton);
        add(buttonPanel, BorderLayout.NORTH);
    }

    public void refreshDisplay() {
        if (isAllDisplayed) {
            displayAllAccommodations();
        } else if (isAvailableDisplayed) {
            displayAvailableAccommodations();
        }
    }

    private void toggleAllAccommodations() {
        if (isAllDisplayed) {
            displayArea.setText("");
            isAllDisplayed = false;
        } else {
            displayAllAccommodations();
            isAllDisplayed = true;
        }
        isAvailableDisplayed = false;
    }

    private void toggleAvailableAccommodations() {
        if (isAvailableDisplayed) {
            displayArea.setText("");
            isAvailableDisplayed = false;
        } else {
            displayAvailableAccommodations();
            isAvailableDisplayed = true;
        }
        isAllDisplayed = false;
    }

    private void displayAllAccommodations() {
        StringBuilder displayText = new StringBuilder();
        for (Accommodation accommodation : resort.getAccommodations()) {
            displayText.append(accommodation).append("\n");
        }
        displayArea.setText(displayText.toString());
    }

    private void displayAvailableAccommodations() {
        StringBuilder displayText = new StringBuilder();
        for (Accommodation accommodation : resort.getAvailableAccommodations()) {
            displayText.append(accommodation).append("\n");
        }
        displayArea.setText(displayText.toString());
    }
}