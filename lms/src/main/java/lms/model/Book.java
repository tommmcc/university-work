package lms.model;

public class Book {

    private int id;
    private String title;
    private String author;
    private double pricePerDay;
    private boolean available;

    public Book() {
        this(0, "", "", 0.0, true);
    }

    public Book(int id, String title, String author, double pricePerDay, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.pricePerDay = pricePerDay;
        this.available = available;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "Book{id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", pricePerDay=" + pricePerDay +
                ", available=" + available +
                '}';
    }
}