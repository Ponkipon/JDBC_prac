# Library Management System

This is a simple Java-based Library Management System using JDBC and SQLite for database management. It provides a GUI for managing users, books, and borrowed books.

## Features

-   **User Management:**
    -   Add, edit, delete, and search users.
-   **Book Management:**
    -   Add, edit, delete, search, borrow, and return books.
-   **Borrowed Books Tracking:**
    -   View a list of currently borrowed books with borrower details.
-   **GUI:**
    -   User-friendly graphical interface using Swing.
    -   Theming support via FlatLAF.
-   **Database:**
    -   SQLite database for persistent data storage.

## Prerequisites

-   Java Development Kit (JDK) 8 or later.
-   SQLite JDBC driver.

## Setup

1.  **Clone the Repository:**

    ```bash
    git clone https://github.com/Ponkipon/JDBC_prac
    cd JDBC_prac/JDBC_prac
    ```

2.  **Import the Project:**

    -   Import the project into your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse).

3.  **Run DBManager.java:**

    -   Run the `DBManager.java` file located in `JDBC_prac/src/backend/` to create the SQLite database file (`library.db`) and initialize the tables. This step is crucial before running the GUI.

4.  **Run LibraryGUI.java:**

    -   Run the `LibraryGUI.java` file located in `JDBC_prac/src/frontend/` to launch the application.

## Project Structure

Library-Management-System/
├── .gitignore
├── JDBC_prac/
│   ├── .gitignore
│   ├── src/
│   │   ├── backend/
│   │   │   └── DBManager.java
│   │   ├── frontend/
│   │   │   ├── LibraryGUI.java
│   │   │   ├── TableStyler.java
│   │   │   └── ThemeManager.java
│   │   ├── models/
│   │   │   ├── Book.java
│   │   │   ├── BorrowedBook.java
│   │   │   └── User.java
│   │   └── module-info.java
│   └── database/
│       └── library.db (created after running DBManager.java)
├── README.md


-   `.gitignore`: Specifies intentionally untracked files that Git should ignore.
-   `JDBC_prac/src/backend/DBManager.java`: Manages the database connection and CRUD operations.
-   `JDBC_prac/src/frontend/LibraryGUI.java`: Contains the main GUI application.
-   `JDBC_prac/src/frontend/TableStyler.java`: Handles custom styling for JTables.
-   `JDBC_prac/src/frontend/ThemeManager.java`: Manages the application's theme.
-   `JDBC_prac/src/models/`: Contains the data model classes (Book, BorrowedBook, User).
-   `JDBC_prac/database/library.db`: The SQLite database file.
-   `README.md`: this thing lol

## Usage

-   Use the tabs to navigate between user, book, and borrowed book management.
-   Use the search fields to filter data.
-   Use the buttons to perform CRUD operations and other actions.

## Dependencies

-   SQLite JDBC Driver
-   FlatLAF
