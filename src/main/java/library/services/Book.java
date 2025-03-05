package library.services;

import com.google.gson.annotations.Expose;

public class Book {
    @Expose
    private String title;
    
    @Expose
    private String author;
    
    @Expose
    private String isbn;
    
    @Expose
    private String category;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return title + "; " + author + "; " + isbn + "; " + category;
    }
}
