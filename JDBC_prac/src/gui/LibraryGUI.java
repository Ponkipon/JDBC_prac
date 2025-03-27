package gui;

import db.DBManager;
import models.User;

import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LibraryGUI extends JFrame {
    public LibraryGUI() {
        setTitle("Bibliotheksverwaltung");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create panels for Users and Books
        JPanel userPanel = createUserPanel();
        JPanel bookPanel = createBookPanel();

        // Add tabs
        tabbedPane.addTab("Users", userPanel);
        tabbedPane.addTab("Books", bookPanel);

        // Add tabbedPane to frame
        add(tabbedPane);
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Table for users
        String[] columnNames = {"ID", "First Name", "Last Name", "Email", "Phone"};
        JTable userTable = new JTable(new DefaultTableModel(columnNames, 0));
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        loadUsers(model);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane);

        // Add button
        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(e -> showAddUserDialog());

        panel.add(addUserButton);
        return panel;
    }

    private void loadUsers(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing rows before adding new ones
        List<User> users = DBManager.getAllUsers(); // Fetch users from DB

        for (User user : users) {
            model.addRow(new Object[]{
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
        }
    }

    private JPanel createBookPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Table for books
        String[] columnNames = {"ID", "Title", "Author", "Year", "ISBN", "Available"};
        JTable bookTable = new JTable(new DefaultTableModel(columnNames, 0));
        
        JScrollPane scrollPane = new JScrollPane(bookTable);
        panel.add(scrollPane);

        // Add button
        JButton addBookButton = new JButton("Add Book");
        addBookButton.addActionListener(e -> showAddBookDialog());

        panel.add(addBookButton);
        return panel;
    }

    private void showAddBookDialog() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField isbnField = new JTextField();

        Object[] fields = {
            "Title:", titleField,
            "Author:", authorField,
            "Year:", yearField,
            "ISBN:", isbnField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            DBManager.addBook(
                titleField.getText(),
                authorField.getText(),
                Integer.parseInt(yearField.getText()),
                isbnField.getText()
            );
            JOptionPane.showMessageDialog(null, "Book added successfully!");
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryGUI app = new LibraryGUI();
            app.setVisible(true);
        });
    }
}
