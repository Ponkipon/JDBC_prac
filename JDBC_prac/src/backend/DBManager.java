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
import models.BorrowedBook;
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
    
    public static List<User> searchUsers(String keyword) {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM users WHERE first_name LIKE ? OR last_name LIKE ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();
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
    
    public static List<BorrowedBook> getBorrowedBooks() {
        List<BorrowedBook> borrowedBooks = new ArrayList<>();
        String query = "SELECT b.id, b.title, b.author, u.first_name, u.last_name, br.borrow_date, br.return_date " +
                       "FROM borrowed_books br " +
                       "JOIN books b ON br.book_id = b.id " +
                       "JOIN users u ON br.user_id = u.id";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                BorrowedBook book = new BorrowedBook(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("borrow_date"),
                    rs.getString("return_date")
                );
                borrowedBooks.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowedBooks;
    }

    public static List<Book> searchBooks(String keyword) {
        List<Book> bookList = new ArrayList<>();
        String query = "SELECT books.*, " +
                "(SELECT COUNT(*) FROM borrowed_books " +
                " WHERE borrowed_books.book_id = books.id AND borrowed_books.return_date IS NULL) AS borrowed " +
                "FROM books " +
                "WHERE title LIKE ? OR isbn LIKE ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();
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
    
    
    
    public static void populateDatabase() {
        // Sample users
        String[][] users = {
            {"John", "Doe", "john.doe@example.com", "1234567890"},
            {"Alice", "Smith", "alice.smith@example.com", "9876543210"},
            {"Bob", "Johnson", "bob.johnson@example.com", "5647382910"},
            {"Emily", "Davis", "emily.davis@example.com", "1928374650"},
            {"Michael", "Brown", "michael.brown@example.com", "1112223333"}
        };

        // Sample books
        Object[][] books = {
            {"The Great Gatsby", "F. Scott Fitzgerald", 1925, "9780743273565"},
            {"To Kill a Mockingbird", "Harper Lee", 1960, "9780061120084"},
            {"1984", "George Orwell", 1949, "9780451524935"},
            {"Pride and Prejudice", "Jane Austen", 1813, "9780141439518"},
            {"The Hobbit", "J.R.R. Tolkien", 1937, "9780547928227"}
        };

        System.out.println("Populating database with test data...");

        // Insert users
        for (String[] user : users) {
            addUser(user[0], user[1], user[2], user[3]);
        }

        // Insert books
        for (Object[] book : books) {
            addBook((String) book[0], (String) book[1], (int) book[2], (String) book[3]);
        }

        System.out.println("Database populated successfully!");
    }



    public static void main(String[] args) {
        connect();
        createTables();
        // populateDatabase();
    }
}
