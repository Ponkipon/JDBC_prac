package frontend;

import models.Book;
import models.BorrowedBook;
import models.User;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import backend.DBManager;

public class LibraryGUI extends JFrame {
	private JTable userTable;
	private DefaultTableModel userModel;
	private JTable bookTable;
	private DefaultTableModel bookModel;
	private JTable borrowedBooksTable;
	private DefaultTableModel borrowedBooksModel;
	
    public LibraryGUI() {
        setTitle("Bibliotheksverwaltung");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel userPanel = createUserPanel();        
        JPanel bookPanel = createBookPanel();
        JPanel borrowedBooksPanel = createBorrowedBooksPanel();
        
        tabbedPane.addTab("Users", userPanel);
        tabbedPane.addTab("Books", bookPanel);
        tabbedPane.addTab("Borrowed Books", borrowedBooksPanel);
        
        add(tabbedPane);
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }

            private void performSearch() {
                String keyword = searchField.getText().trim();
                if (!keyword.isEmpty()) {
                    loadSearchedUsers(keyword);
                } else {
                    loadUsers(); 
                }
            }
        });

        String[] columnNames = {"ID", "First Name", "Last Name", "Email", "Phone"};
        userModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(userModel);
        loadUsers(); 
        JScrollPane scrollPane = new JScrollPane(userTable);

        JPanel buttonPanel = new JPanel();

        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(e -> showAddUserDialog());

        JButton editUserButton = new JButton("Edit User");
        editUserButton.addActionListener(e -> showEditUserDialog());

        JButton deleteUserButton = new JButton("Delete User");
        deleteUserButton.addActionListener(e -> deleteUser());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshUserTable());

        buttonPanel.add(addUserButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(refreshButton);

        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadSearchedUsers(String keyword) {
        userModel.setRowCount(0); // Clear table
        for (User user : DBManager.searchUsers(keyword)) {
            userModel.addRow(new Object[]{user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone()});
        }
    }

    private void refreshUserTable() {
        userModel.setRowCount(0);
        loadUsers();
    }
    // See User class in models package for model properties. Backend function is getAllUsers in DBManager class in backend package.
    private void loadUsers() {
        List<User> users = DBManager.getAllUsers(); 
        for (User user : users) {
            userModel.addRow(new Object[]{
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone()
            });
        }
    }


    private void showAddUserDialog() {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        Object[] fields = {
            "First Name:", firstNameField,
            "Last Name:", lastNameField,
            "Email:", emailField,
            "Phone:", phoneField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            DBManager.addUser(
                firstNameField.getText(),
                lastNameField.getText(),
                emailField.getText(),
                phoneField.getText()
            );
            JOptionPane.showMessageDialog(null, "User added successfully!");
            refreshUserTable();
        }
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a user to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get existing user data from selected row
        int userId = (int) userModel.getValueAt(selectedRow, 0);
        String firstName = (String) userModel.getValueAt(selectedRow, 1);
        String lastName = (String) userModel.getValueAt(selectedRow, 2);
        String email = (String) userModel.getValueAt(selectedRow, 3);
        String phone = (String) userModel.getValueAt(selectedRow, 4);

        JTextField firstNameField = new JTextField(firstName, 15);
        JTextField lastNameField = new JTextField(lastName, 15);
        JTextField emailField = new JTextField(email, 15);
        JTextField phoneField = new JTextField(phone, 15);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(null, panel, 
            "Edit User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            DBManager.updateUser(userId, 
                firstNameField.getText().trim(), 
                lastNameField.getText().trim(), 
                emailField.getText().trim(), 
                phoneField.getText().trim());

            refreshUserTable();
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a user to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = (int) userModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Are you sure you want to delete this user?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DBManager.deleteUser(userId);
            refreshUserTable(); 
        }
    }

    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }

            private void performSearch() {
                String keyword = searchField.getText().trim();
                if (!keyword.isEmpty()) {
                    loadSearchedBooks(keyword);
                } else {
                    loadBooks(); 
                }
            }
        });

        String[] columnNames = {"ID", "Title", "Author", "Year", "ISBN", "Borrowed"};
        bookModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(bookModel);
        TableStyler.applyTableStyle(bookTable);
        loadBooks(); // Load books into the global model
        JScrollPane scrollPane = new JScrollPane(bookTable);
        
        
        
        JPanel buttonPanel = new JPanel();

        JButton addBookButton = new JButton("Add Book");
        addBookButton.addActionListener(e -> showAddBookDialog());

        JButton editBookButton = new JButton("Edit Book");
        editBookButton.addActionListener(e -> showEditBookDialog());

        JButton deleteBookButton = new JButton("Delete Book");
        deleteBookButton.addActionListener(e -> deleteBook());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshBookTable());
        
        JButton borrowBookButton = new JButton("Borrow Book");
        borrowBookButton.addActionListener(e -> borrowBook());
        
        JButton returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(e -> returnBook());

        

        buttonPanel.add(addBookButton);
        buttonPanel.add(editBookButton);
        buttonPanel.add(deleteBookButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(borrowBookButton);
        buttonPanel.add(returnBookButton);


        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadSearchedBooks(String keyword) {
        bookModel.setRowCount(0); // Clear table
        for (Book book : DBManager.searchBooks(keyword)) {
            bookModel.addRow(new Object[]{
            		book.getId(),
            		book.getTitle(), 
            		book.getAuthor(), 
            		book.getYear(),
            		book.getIsbn(), 
            		(book.isBorrowed()) ? "Yes" : "No"
        			});
        }
    }


    // see Book class in models package for model info. Backend function is getAllBooks in DBManager in backend package.
    private void loadBooks() {
        for (Book book : DBManager.getAllBooks()) {
            bookModel.addRow(new Object[]{
            		book.getId(), 
            		book.getTitle(), 
            		book.getAuthor(), 
            		book.getYear(), 
            		book.getIsbn(),
            		(book.isBorrowed()) ? "Yes" : "No"
            		});
        }
    }
    
    private void showAddBookDialog() {
        JTextField titleField = new JTextField(15);
        JTextField authorField = new JTextField(15);
        JTextField yearField = new JTextField(5);
        JTextField isbnField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            DBManager.addBook(
                titleField.getText().trim(),
                authorField.getText().trim(),
                Integer.parseInt(yearField.getText().trim()),
                isbnField.getText().trim()
            );
            refreshBookTable();
        }
    }

    private void showEditBookDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a book to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookId = (int) bookModel.getValueAt(selectedRow, 0);
        String title = (String) bookModel.getValueAt(selectedRow, 1);
        String author = (String) bookModel.getValueAt(selectedRow, 2);
        int year = (int) bookModel.getValueAt(selectedRow, 3);
        String isbn = (String) bookModel.getValueAt(selectedRow, 4);

        JTextField titleField = new JTextField(title, 15);
        JTextField authorField = new JTextField(author, 15);
        JTextField yearField = new JTextField(String.valueOf(year), 5);
        JTextField isbnField = new JTextField(isbn, 15);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            DBManager.updateBook(bookId, titleField.getText().trim(), authorField.getText().trim(), Integer.parseInt(yearField.getText().trim()), isbnField.getText().trim());
            refreshBookTable();
        }
    }
    
    private void borrowBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a book to borrow.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookId = (int) bookModel.getValueAt(selectedRow, 0);
        boolean isBorrowed = (bookModel.getValueAt(selectedRow, 5) == "Yes") ? true : false; // checks if there's a "yes" or a "no" in the cell, and converts it to boolean cuz in SQL table it's a bool value

        if (isBorrowed) {
            JOptionPane.showMessageDialog(null, "This book is already borrowed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // User selection menu modal window to borrow the book
        List<User> users = DBManager.getAllUsers();
        String[] userNames = users.stream().map(u -> u.getId() + " - " + u.getFirstName() + " " + u.getLastName()).toArray(String[]::new);
        String selectedUser = (String) JOptionPane.showInputDialog(null, "Select a user:", "Borrow Book",
                JOptionPane.QUESTION_MESSAGE, null, userNames, userNames[0]);

        if (selectedUser != null) {
            int userId = Integer.parseInt(selectedUser.split(" - ")[0]); // Extract user ID
            DBManager.borrowBook(bookId, userId);
            refreshBookTable();
        }
    }
    
    private void returnBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a book to return.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookId = (int) bookModel.getValueAt(selectedRow, 0);
        boolean isBorrowed = (bookModel.getValueAt(selectedRow, 5) == "Yes") ? true : false; // checks if there's a "yes" or a "no" in the cell, and converts it to boolean cuz in SQL table it's a bool value

        if (!isBorrowed) {
            JOptionPane.showMessageDialog(null, "This book is not borrowed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DBManager.returnBook(bookId);
        refreshBookTable();
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a book to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookId = (int) bookModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this book?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DBManager.deleteBook(bookId);
            refreshBookTable();
        }
    }

    private void refreshBookTable() {
    	bookModel.setRowCount(0);
    	loadBooks();
    }

    private JPanel createBorrowedBooksPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        String[] columnNames = {"Book ID", "Title", "Author", "Borrower", "Borrow Date", "Return Date"};
        borrowedBooksModel = new DefaultTableModel(columnNames, 0);
        borrowedBooksTable = new JTable(borrowedBooksModel);
        JScrollPane scrollPane = new JScrollPane(borrowedBooksTable);
        loadBorrowedBooks();
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadBorrowedBooks());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);
        return panel;
    }

    private void loadBorrowedBooks() {
        borrowedBooksModel.setRowCount(0);
        for (BorrowedBook book : DBManager.getBorrowedBooks()) {
            borrowedBooksModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(),
                    book.getBorrowerName(), book.getBorrowDate(), book.getReturnDate()});
        }
    }

    
    public static void main(String[] args) {
    	// See themeManager class. Used FlatLAF for Themes.
        ThemeManager.applyTheme();
        
        SwingUtilities.invokeLater(() -> {
            LibraryGUI app = new LibraryGUI();
            app.setVisible(true);
        });
    }
}
