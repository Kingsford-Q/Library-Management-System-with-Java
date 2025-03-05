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
        
        if (selectedIndices.size() != 1) {
            System.out.println("Error: No book selected for editing.");
            return;
        }
    
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
    
        // Run book fetching in a background thread
        Task<Book> fetchTask = new Task<>() {
            @Override
            protected Book call() {
                return BookService.fetchBooks().stream()
                    .filter(book -> book.getIsbn().equals(isbnToEdit))
                    .findFirst()
                    .orElse(null);
            }
        };
    
        fetchTask.setOnSucceeded(event -> {
            Book selectedBook = fetchTask.getValue();
    
            if (selectedBook == null) {
                System.out.println("Error: Book not found in the database.");
                return;
            }
    
            String idToEdit = selectedBook.getId();
            EditBook eb = new EditBook();
            String newDetails = eb.getResult();
    
            if (newDetails != null) {
                String[] newBookDetails = newDetails.split(";");
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
    
                // Run book update in a background thread
                Task<String> updateTask = new Task<>() {
                    @Override
                    protected String call() {
                        return BookService.editBook(idToEdit, newTitle, newAuthor, newIsbn, newCategory);
                    }
                };
    
                updateTask.setOnSucceeded(updateEvent -> {
                    if (updateTask.getValue() != null) {
                        System.out.println("Book successfully updated!");
                        loadBooks(); // Reload books only after a successful update
                    } else {
                        System.out.println("Failed to edit book");
                    }
                    search.clear();
                });
    
                new Thread(updateTask).start();
            }
        });
    
        new Thread(fetchTask).start(); // Fetch book details in the background
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
        String titleText = book.getText().trim();
        String authorText = author.getText().trim();
        String isbnText = isbn.getText().trim();
        String categoryText = category.getText().trim();
    
        // Validate input fields
        if (titleText.isEmpty() || authorText.isEmpty() || isbnText.isEmpty() || categoryText.isEmpty()) {
            System.out.println("Error: All fields must be filled.");
            return;
        }
    
        System.out.println("Sending book data: " + titleText + ", " + authorText + ", " + isbnText + ", " + categoryText);
    
        // Run book addition in a background thread
        Task<String> addBookTask = new Task<>() {
            @Override
            protected String call() {
                return BookService.addBook(titleText, authorText, isbnText, categoryText);
            }
        };
    
        addBookTask.setOnSucceeded(event -> {
            String response = addBookTask.getValue();
    
            if (response != null) {
                System.out.println("Book added: " + response);
                loadBooks(); // Reload books only if the addition is successful
            } else {
                System.out.println("Failed to add book");
            }
    
            // Clear input fields after adding the book
            author.clear();
            book.clear();
            isbn.clear();
            category.clear();
        });
    
        new Thread(addBookTask).start(); // Start background thread
    }
    


private void loadBooks() {
    Task<List<String>> loadTask = new Task<>() {
        @Override
        protected List<String> call() {
            return BookService.fetchBooks().stream()
                .map(book -> book.getTitle() + "; " + book.getAuthor() + "; " + book.getIsbn() + "; " + book.getCategory())
                .toList();
        }
    };

    loadTask.setOnSucceeded(event -> {
        booksList.getItems().setAll(loadTask.getValue());
        allBooks = loadTask.getValue(); // Cache for search optimization
    });

    new Thread(loadTask).start(); // Run in background
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
