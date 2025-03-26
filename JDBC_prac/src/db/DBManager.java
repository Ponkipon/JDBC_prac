package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {
    private static final String DB_URL = "jdbc:sqlite:database/library.db"; 

    // sql verbindung
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("db connected");
        } catch (SQLException e) {
            System.out.println("connection error: " + e.getMessage());
        }
        return conn;
    }

    // tabellen lol
    public static void createTables() {
        String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "first_name TEXT NOT NULL, "
                + "last_name TEXT NOT NULL, "
                + "email TEXT UNIQUE NOT NULL, "
                + "phone TEXT);";

        String booksTable = "CREATE TABLE IF NOT EXISTS books ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "author TEXT NOT NULL, "
                + "year INTEGER, "
                + "isbn TEXT UNIQUE NOT NULL, "
                + "available BOOLEAN DEFAULT 1);";

        String borrowedBooksTable = "CREATE TABLE IF NOT EXISTS borrowed_books ("
                + "user_id INTEGER, "
                + "book_id INTEGER, "
                + "FOREIGN KEY(user_id) REFERENCES users(id), "
                + "FOREIGN KEY(book_id) REFERENCES books(id));";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(booksTable);
            stmt.execute(borrowedBooksTable);
            System.out.println("tables created");
        } catch (SQLException e) {
            System.out.println("error creating tables: " + e.getMessage());
        }
    }


    // C in CRUD - Create
    public static void addUser(String firstName, String lastName, String email, String phone) {
    String sql = "INSERT INTO users(first_name, last_name, email, phone) VALUES(?, ?, ?, ?)";

    try (Connection conn = connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, firstName);
        pstmt.setString(2, lastName);
        pstmt.setString(3, email);
        pstmt.setString(4, phone);
        pstmt.executeUpdate();
        System.out.println("User added successfully.");
    } catch (SQLException e) {
        System.out.println("Error adding user: " + e.getMessage());
    }
    }

    public static void addBook(String title, String author, int year, String isbn) {
        String sql = "INSERT INTO books(title, author, year, isbn, available) VALUES(?, ?, ?, ?, 1)";
    
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setInt(3, year);
            pstmt.setString(4, isbn);
            pstmt.executeUpdate();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }
    

    // R in CRUD - Read da suka
    public static void getAllUsers() {
    String sql = "SELECT * FROM users";

    try (Connection conn = connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        System.out.println("Users:");
        while (rs.next()) {
            System.out.println(rs.getInt("id") + " | " +
                    rs.getString("first_name") + " " +
                    rs.getString("last_name") + " | " +
                    rs.getString("email") + " | " +
                    rs.getString("phone"));
        }
    } catch (SQLException e) {
        System.out.println("Error retrieving users: " + e.getMessage());
    }
    }

    public static void getAllBooks() {
        String sql = "SELECT * FROM books";
    
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            System.out.println("Books:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " +
                        rs.getString("title") + " | " +
                        rs.getString("author") + " | " +
                        rs.getInt("year") + " | " +
                        rs.getString("isbn") + " | " +
                        (rs.getBoolean("available") ? "Available" : "Borrowed"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving books: " + e.getMessage());
        }
    }
    

    // U in CRUD - Update nahui
    public static void updateUser(int id, String newEmail, String newPhone) {
        String sql = "UPDATE users SET email = ?, phone = ? WHERE id = ?";
    
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, newPhone);
            pstmt.setInt(3, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    public static void updateBookAvailability(int bookId, boolean isAvailable) {
        String sql = "UPDATE books SET available = ? WHERE id = ?";
    
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, bookId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Book availability updated.");
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating book availability: " + e.getMessage());
        }
    }
    
    // D in CRUD - Delete ebat'
    public static void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
    
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User deleted.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    public static void deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
    
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Book deleted.");
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
    }
    

    public static void main(String[] args) {
        connect();
        createTables();

        // Testing
        addUser("Alice", "Johnson", "alice@example.com", "123-456-7890");
        addUser("Bob", "Smith", "bob@example.com", "987-654-3210");
        addBook("The Great Gatsby", "F. Scott Fitzgerald", 1925, "9780743273565");
        addBook("1984", "George Orwell", 1949, "9780451524935");
        getAllUsers();
        getAllBooks();
        updateUser(1, "alice.new@example.com", "555-555-5555");
        updateBookAvailability(1, false);
        deleteUser(2);
        deleteBook(2);
    }
}
