import java.io.Serializable;
import java.util.Objects;

public class Lesson implements Serializable {
    private String level;
    private double price;

    public Lesson(String level, double price) {
        this.level = level;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return level + " Level - $" + price;
    }

    // Needed if using Lesson as a map key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson)) return false;
        Lesson lesson = (Lesson) o;
        return Double.compare(lesson.price, price) == 0 &&
                Objects.equals(level, lesson.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, price);
    }
}