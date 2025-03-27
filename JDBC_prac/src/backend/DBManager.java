package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Book;
import models.User;
// You need to run this Java file before first start to create DB file and build tables.
// SQLite-JDBC lib is used for db Connection. data storage path is set in DB_URL var. See JDBC doc for info on queries.
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
        		+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "book_id INTEGER NOT NULL, "
                + "borrow_date TEXT DEFAULT CURRENT_TIMESTAMP, "
                + "return_date TEXT, "
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
        String query = "INSERT INTO books (title, author, year, isbn) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setInt(3, year);
            pstmt.setString(4, isbn);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // R in CRUD - Read da suka
    public static List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone")
                );
                userList.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    // returns an Array of Book objects from models package.
    public static List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT books.*, " +
                "(SELECT COUNT(*) FROM borrowed_books WHERE borrowed_books.book_id = books.id AND borrowed_books.return_date IS NULL) AS borrowed " +
                "FROM books";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("year"),
                    rs.getString("isbn"),
                    rs.getInt("borrowed") > 0
                );
                bookList.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }

    // U in CRUD - Update nahui
    public static void updateUser(int id, String firstName, String lastName, String email, String phone) {
        String query = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void updateBook(int id, String title, String author, int year, String isbn) {
        String query = "UPDATE books SET title = ?, author = ?, year = ?, isbn = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setInt(3, year);
            pstmt.setString(4, isbn);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // pass the userId and bookId to add the book into borrowed_books table. Borrow_date creates automatically
    // borrowed_books table contains:
    // 	id INTEGER PRIMARY KEY AUTOINCREMENT
	// 	user_id INTEGER NOT NULL
	// 	book_id INTEGER NOT NULL
	// 	borrow_date TEXT DEFAULT CURRENT_TIMESTAMP
	// 	return_date TEXT
	// 	FOREIGN KEY(user_id) REFERENCES users(id)
	// 	FOREIGN KEY(book_id) REFERENCES books(id))
    public static void borrowBook(int bookId, int userId) {
        String query = "INSERT INTO borrowed_books (book_id, user_id) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, bookId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // adds return_date to the selected row, because of sql query filtering in getAllBooks basically makes the book returned.
    public static void returnBook(int bookId) {
        String query = "UPDATE borrowed_books SET return_date = CURRENT_TIMESTAMP WHERE book_id = ? AND return_date IS NULL";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // D in CRUD - Delete ebat'
    public static void deleteUser(int id) {
        String query = "DELETE FROM users WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
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
    }
}
