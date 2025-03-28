package models;

public class BorrowedBook {
    private int id;
    private String title;
    private String author;
    private String borrowerName;
    private String borrowDate;
    private String returnDate;

    public BorrowedBook(int id, String title, String author, String borrowerName, String borrowDate, String returnDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.borrowerName = borrowerName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getBorrowerName() { return borrowerName; }
    public String getBorrowDate() { return borrowDate; }
    public String getReturnDate() { return returnDate; }
}
