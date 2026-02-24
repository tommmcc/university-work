import java.io.Serializable;

public class Customer implements Serializable {
    private int id;
    private String name;
    private String skiLevel;

    public Customer(int id, String name, String skiLevel) {
        this.id = id;
        this.name = name;
        this.skiLevel = skiLevel;
    }

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return name;
    }

    public String getSkiLevel() {
        return skiLevel;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + " (Ski Level: " + skiLevel + ")";
    }
}