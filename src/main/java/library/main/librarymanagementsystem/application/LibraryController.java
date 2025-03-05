package library.main.librarymanagementsystem.application;
import library.main.librarymanagementsystem.HelloApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import library.services.Book;


import library.services.BookService;

public class LibraryController implements Initializable {

    @FXML
    private ListView<String> booksList;

    @FXML
    private TextField author;

    @FXML
    private TextField book;

    @FXML
    private TextField isbn;

    @FXML
    private TextField category;

    @FXML
    private TextField search;

    private List<String> allBooks = new ArrayList<>();


    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadBooks();
        } catch (Exception e) {
            e.printStackTrace();
        }

        booksList.setFixedCellSize(50.0);

        Platform.runLater(() -> {
            if (booksList.getScene() != null) {
                String css = getClass().getResource("/library/main/librarymanagementsystem/library.css").toExternalForm();
                booksList.getScene().getStylesheets().add(css);
            }
        });
    }

    @FXML
    protected void searchBook() throws IOException {
    String searchText = search.getText().strip().toLowerCase();
    
    if (searchText.length() < 3) {
        booksList.getItems().setAll(allBooks); // Reset to full list if search text is too short
        return;
    }

    Task<List<String>> searchTask = new Task<>() {
        @Override
        protected List<String> call() {
            return allBooks.stream()
                .filter(book -> book.toLowerCase().contains(searchText))
                .toList();
        }
    };

    searchTask.setOnSucceeded(event -> booksList.getItems().setAll(searchTask.getValue()));

    new Thread(searchTask).start(); // Run in background to prevent UI lag
}


    @FXML
    protected void editBook() throws IOException {
        ObservableList<Integer> selectedIndices = booksList.getSelectionModel().getSelectedIndices();
        
        if (selectedIndices.size() == 1) {
            String bookToEdit = booksList.getItems().get(selectedIndices.get(0));
    
            if (bookToEdit == null || !bookToEdit.contains(";")) {
                System.out.println("Error: Invalid book format.");
                return;
            }
    
            String[] bookDetails = bookToEdit.split(";");
            if (bookDetails.length < 4) {
                System.out.println("Error: Book data is incomplete.");
                return;
            }
    
            String titleToEdit = bookDetails[0].trim();
            String authorToEdit = bookDetails[1].trim();
            String isbnToEdit = bookDetails[2].trim();
            String categoryToEdit = bookDetails[3].trim();
    
            System.out.println("\n--- Original Book Details ---");
            System.out.println("Title: " + titleToEdit);
            System.out.println("Author: " + authorToEdit);
            System.out.println("ISBN: " + isbnToEdit);
            System.out.println("Category: " + categoryToEdit);
    
            List<Book> books = BookService.fetchBooks();
            Book selectedBook = books.stream()
                    .filter(book -> book.getIsbn().equals(isbnToEdit))
                    .findFirst()
                    .orElse(null);
    
            if (selectedBook != null) {
                String idToEdit = selectedBook.getId();
                EditBook eb = new EditBook();
                String newDetails = eb.getResult();
    
                System.out.println("\nRaw newDetails input: " + newDetails);
    
                if (newDetails != null) {
                    String[] newBookDetails = newDetails.split(";");
                    System.out.println("Split newBookDetails: " + Arrays.toString(newBookDetails));
    
                    if (newBookDetails.length < 4) {
                        System.out.println("Error: New book data is incomplete.");
                        return;
                    }
    
                    // Ensure correct order: Title, Author, ISBN, Category
                    String newTitle = newBookDetails[0].trim();
                    String newAuthor = newBookDetails[1].trim();
                    String newIsbn = newBookDetails[2].trim();
                    String newCategory = newBookDetails[3].trim();
    
                    System.out.println("\n--- Updated Book Details ---");
                    System.out.println("Title: " + newTitle);
                    System.out.println("Author: " + newAuthor);
                    System.out.println("ISBN: " + newIsbn);
                    System.out.println("Category: " + newCategory);
    
                    String response = BookService.editBook(idToEdit, newTitle, newAuthor, newIsbn, newCategory);
    
                    if (response != null) {
                        System.out.println("Book successfully updated!");
                        loadBooks(); // Reload books
                    } else {
                        System.out.println("Failed to edit book");
                    }
                    search.clear();
                }
            } else {
                System.out.println("Error: Book not found in the database.");
            }
        } else {
            System.out.println("Error: No book selected for editing.");
        }
    }

    @FXML
    protected void deleteBook() throws IOException {
    ObservableList<Integer> selectedIndices = booksList.getSelectionModel().getSelectedIndices();
    if (selectedIndices.size() == 1) {
        String bookToDelete = booksList.getItems().get(selectedIndices.get(0));
        String isbnToDelete = bookToDelete.split("; ")[2]; // Extract ISBN

        // Find the corresponding book from the backend to get its ID
        List<Book> books = BookService.fetchBooks();
        Book selectedBook = books.stream()
                .filter(book -> book.getIsbn().equals(isbnToDelete)) // Find by ISBN
                .findFirst()
                .orElse(null);

        if (selectedBook != null) {
            String idToDelete = selectedBook.getId(); // Get the book ID

            // Call backend to delete the book
            String response = BookService.deleteBook(idToDelete);

            if (response != null) {
                loadBooks();  // Reload books after deleting
            } else {
                System.out.println("Failed to delete book");
            }
            search.clear();
        }
    }
}


    @FXML
    protected void addItem() throws IOException {
    // Collect data from input fields
    String titleText = book.getText();
    String authorText = author.getText();
    String isbnText = isbn.getText();
    String categoryText = category.getText();

    // Call backend to add the book
    System.out.println("Sending book data: " + titleText + ", " + authorText + ", " + isbnText + ", " + categoryText);
    String response = BookService.addBook(titleText, authorText, isbnText, categoryText);

    if (response != null) {
        System.out.println("Book added: " + response);
        System.out.println("Sending book data: " + titleText + ", " + authorText + ", " + isbnText + ", " + categoryText);
        loadBooks(); // Reload books after adding
    } else {
        System.out.println("Failed to add book");
    }

    // Clear input fields after adding the book
    author.clear();
    book.clear();
    isbn.clear();
    category.clear();
}


    public void loadBooks() {
        // Fetch books from the backend using BookService
        List<Book> books = BookService.fetchBooks();

        allBooks = BookService.fetchBooks().stream()
        .map(book -> book.getTitle() + "; " + book.getAuthor() + "; " + book.getIsbn() + "; " + book.getCategory())
        .toList();

    
        if (books != null) {
            // Update the ListView with the books (excluding _id)
            List<String> bookStrings = new ArrayList<>();
            for (Book bo : books) {
                bookStrings.add(bo.getTitle() + "; " + bo.getAuthor() + "; " + bo.getIsbn() + "; " + bo.getCategory());
            }
            booksList.getItems().setAll(bookStrings);
        } else {
            System.out.println("Failed to load books from the server");
        }
    }
    
    
    

    public static void changeScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("library.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = HelloApplication.getPrimaryStage();
        stage.hide();
        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
    }
}
