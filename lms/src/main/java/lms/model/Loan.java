package lms.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Loan {

    // UI convention: DD-MM-YYYY
    public static final DateTimeFormatter UI_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu");

    private final int id;
    private final int bookId;
    private final int borrowerId;
    private final LocalDate startDate;
    private final int durationDays;

    public Loan(int id, int bookId, int borrowerId, LocalDate startDate, int durationDays) {
        this.id = id;
        this.bookId = bookId;
        this.borrowerId = borrowerId;
        this.startDate = startDate;
        this.durationDays = durationDays;
    }

    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }

    public int getBorrowerId() {
        return borrowerId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public String getStartDateUi() {
        return UI_DATE_FORMAT.format(startDate);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", borrowerId=" + borrowerId +
                ", startDate=" + getStartDateUi() +
                ", durationDays=" + durationDays +
                '}';
    }
}