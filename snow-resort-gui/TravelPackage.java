import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class TravelPackage implements Serializable {
    private Customer customer;
    private Accommodation accommodation;

    // Lesson type -> quantity
    private Map<Lesson, Integer> lessonCounts;

    // 0 until added
    private int liftPassDays;

    public TravelPackage(Customer customer, Accommodation accommodation) {
        this.customer = customer;
        this.accommodation = accommodation;
        this.lessonCounts = new LinkedHashMap<>();
        this.liftPassDays = 0;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public int getLiftPassDays() {
        return liftPassDays;
    }

    public void setLiftPassDays(int liftPassDays) {
        if (liftPassDays < 0) liftPassDays = 0;
        this.liftPassDays = liftPassDays;
    }

    public Map<Lesson, Integer> getLessonCounts() {
        return Collections.unmodifiableMap(lessonCounts);
    }

    public void addLessons(Lesson lesson, int quantity) {
        if (lesson == null) return;
        if (quantity <= 0) return;
        lessonCounts.put(lesson, lessonCounts.getOrDefault(lesson, 0) + quantity);
    }

    public double getTotalCost() {
        double totalCost = 0.0;

        // original assumption for similarity:
        totalCost += accommodation.getPricePerDay() * liftPassDays;

        // Lessons
        for (Map.Entry<Lesson, Integer> entry : lessonCounts.entrySet()) {
            totalCost += entry.getKey().getPrice() * entry.getValue();
        }

        // Lift pass pricing logic 
        double liftPassCost = liftPassDays * 26.0;
        if (liftPassDays >= 5) {
            liftPassCost *= 0.9;
        }
        if (liftPassCost > 200) {
            liftPassCost = 200;
        }
        totalCost += liftPassCost;

        return totalCost;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer: ").append(customer.getCustomerName())
          .append(" (").append(customer.getSkiLevel()).append(")\n");
        sb.append("Accommodation: ").append(accommodation.getName())
          .append(" - $").append(accommodation.getPricePerDay()).append("/day\n");
        sb.append("Lift Pass Days: ").append(liftPassDays).append("\n");

        if (lessonCounts.isEmpty()) {
            sb.append("Lessons: 0\n");
        } else {
            sb.append("Lessons:\n");
            for (Map.Entry<Lesson, Integer> entry : lessonCounts.entrySet()) {
                sb.append("  - ").append(entry.getKey().getLevel())
                  .append(" x ").append(entry.getValue())
                  .append(" ($").append(entry.getKey().getPrice()).append(" each)\n");
            }
        }

        sb.append("Total Cost: $").append(String.format("%.2f", getTotalCost()));
        return sb.toString();
    }
}