package lms;

import lms.model.Book;
import lms.model.Borrower;
import lms.model.Loan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibraryService {

    private final ArrayList<Book> books = new ArrayList<>();
    private final ArrayList<Borrower> borrowers = new ArrayList<>();
    private final ArrayList<Loan> loans = new ArrayList<>();

    public List<Book> getBooks() { return books; }
    public List<Borrower> getBorrowers() { return borrowers; }
    public List<Loan> getLoans() { return loans; }

    public void addBook(Book book) { books.add(book); }
    public void addBorrower(Borrower borrower) { borrowers.add(borrower); }
    public void addLoan(Loan loan) { loans.add(loan); }

    public void removeBook(int id) { books.removeIf(b -> b.getId() == id); }
    public void removeBorrower(int id) { borrowers.removeIf(b -> b.getId() == id); }
    public void removeLoan(int id) { loans.removeIf(l -> l.getId() == id); }

    public Optional<Book> findBook(int id) {
        return books.stream().filter(b -> b.getId() == id).findFirst();
    }

    public Optional<Borrower> findBorrower(int id) {
        return borrowers.stream().filter(b -> b.getId() == id).findFirst();
    }

    public Optional<Loan> findLoan(int id) {
        return loans.stream().filter(l -> l.getId() == id).findFirst();
    }

    public void clearAll() {
        books.clear();
        borrowers.clear();
        loans.clear();
    }
}