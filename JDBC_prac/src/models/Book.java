package models;

public class Book {
    private int id;
    private String title;
    private String author;
    private int year;
    private String isbn;
    private boolean borrowed;
    
    // Constructor
    public Book(int id, String title, String author, int year, String isbn, boolean borrowed) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.isbn = isbn;
        this.borrowed = borrowed;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public String getIsbn() { return isbn; }
    public boolean isBorrowed() { return borrowed; }
}
