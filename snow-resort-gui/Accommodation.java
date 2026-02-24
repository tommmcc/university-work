import java.io.Serializable;

public class Accommodation implements Serializable {
    private String name;
    private double pricePerDay;
    private boolean isAvailable;

    public Accommodation(String name, double pricePerDay, boolean isAvailable) {
        this.name = name;
        this.pricePerDay = pricePerDay;
        this.isAvailable = isAvailable;
    }

    public String getName() {
        return name;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return name + " - $" + pricePerDay + "/day" + (isAvailable ? " (Available)" : " (Booked)");
    }
}