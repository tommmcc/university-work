package lms;

import lms.model.Book;
import lms.model.Borrower;
import lms.model.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryDB {

    private static final String URL = "jdbc:sqlite:library.db";

    public LibraryDB() {
        createTables();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void createTables() {
        String createBooks = """
                CREATE TABLE IF NOT EXISTS books (
                    id INTEGER PRIMARY KEY,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    pricePerDay REAL NOT NULL,
                    available INTEGER NOT NULL
                );
                """;

        String createBorrowers = """
                CREATE TABLE IF NOT EXISTS borrowers (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL
                );
                """;

        String createLoans = """
                CREATE TABLE IF NOT EXISTS loans (
                    id INTEGER PRIMARY KEY,
                    bookId INTEGER NOT NULL,
                    borrowerId INTEGER NOT NULL,
                    startDate TEXT NOT NULL,
                    duration INTEGER NOT NULL,
                    FOREIGN KEY(bookId) REFERENCES books(id),
                    FOREIGN KEY(borrowerId) REFERENCES borrowers(id)
                );
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createBooks);
            stmt.execute(createBorrowers);
            stmt.execute(createLoans);

        } catch (SQLException e) {
            throw new RuntimeException("Failed creating tables", e);
        }
    }

    public void saveAll(List<Book> books, List<Borrower> borrowers, List<Loan> loans) {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            clearAll(conn);

            saveBooks(conn, books);
            saveBorrowers(conn, borrowers);
            saveLoans(conn, loans);

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Failed saving to DB", e);
        }
    }

    public LoadedData loadAll() {
        try (Connection conn = connect()) {
            List<Book> books = loadBooks(conn);
            List<Borrower> borrowers = loadBorrowers(conn);
            List<Loan> loans = loadLoans(conn);
            return new LoadedData(books, borrowers, loans);
        } catch (SQLException e) {
            throw new RuntimeException("Failed loading from DB", e);
        }
    }

    private void clearAll(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM loans");
            stmt.execute("DELETE FROM borrowers");
            stmt.execute("DELETE FROM books");
        }
    }

    private void saveBooks(Connection conn, List<Book> books) throws SQLException {
        String sql = "INSERT INTO books (id, title, author, pricePerDay, available) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Book b : books) {
                ps.setInt(1, b.getId());
                ps.setString(2, b.getTitle());
                ps.setString(3, b.getAuthor());
                ps.setDouble(4, b.getPricePerDay());
                ps.setInt(5, b.isAvailable() ? 1 : 0);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void saveBorrowers(Connection conn, List<Borrower> borrowers) throws SQLException {
        String sql = "INSERT INTO borrowers (id, name, email) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Borrower b : borrowers) {
                ps.setInt(1, b.getId());
                ps.setString(2, b.getName());
                ps.setString(3, b.getEmail());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void saveLoans(Connection conn, List<Loan> loans) throws SQLException {
        String sql = "INSERT INTO loans (id, bookId, borrowerId, startDate, duration) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Loan l : loans) {
                ps.setInt(1, l.getId());
                ps.setInt(2, l.getBookId());
                ps.setInt(3, l.getBorrowerId());
                ps.setString(4, l.getStartDate().toString());
                ps.setInt(5, l.getDurationDays());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<Book> loadBooks(Connection conn) throws SQLException {
        List<Book> out = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, title, author, pricePerDay, available FROM books")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                double pricePerDay = rs.getDouble("pricePerDay");
                boolean available = rs.getInt("available") == 1;

                out.add(new Book(id, title, author, pricePerDay, available));
            }
        }
        return out;
    }

    private List<Borrower> loadBorrowers(Connection conn) throws SQLException {
        List<Borrower> out = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, email FROM borrowers")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");

                out.add(new Borrower(id, name, email));
            }
        }
        return out;
    }

    private List<Loan> loadLoans(Connection conn) throws SQLException {
        List<Loan> out = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, bookId, borrowerId, startDate, duration FROM loans")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int bookId = rs.getInt("bookId");
                int borrowerId = rs.getInt("borrowerId");
                LocalDate startDate = LocalDate.parse(rs.getString("startDate"));
                int duration = rs.getInt("duration");

                out.add(new Loan(id, bookId, borrowerId, startDate, duration));
            }
        }
        return out;
    }

    public record LoadedData(List<Book> books, List<Borrower> borrowers, List<Loan> loans) {}
}