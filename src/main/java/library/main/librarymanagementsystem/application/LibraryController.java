package library.main.librarymanagementsystem.application;
import library.main.librarymanagementsystem.HelloApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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
        loadBooks();
        if (searchText.length() >= 1) {
            ArrayList<String> results = new ArrayList<>();
            for (String bo : booksList.getItems()) {
                if (bo.toLowerCase().contains(searchText)) results.add(bo);
            }
            booksList.getItems().setAll(results);
        }
    }

    @FXML
    protected void editBook() throws IOException {
        ObservableList<Integer> selectedIndices = booksList.getSelectionModel().getSelectedIndices();
        if (selectedIndices.size() == 1) {
            String bookToEdit = booksList.getItems().get(selectedIndices.get(0));
            String isbnToEdit = bookToEdit.split(";")[2];  // Extract the ISBN
            
            EditBook eb = new EditBook();
            String str = eb.getResult();

            if (str != null) {
                // Use BookService to edit the book
                String response = BookService.editBook(isbnToEdit, str.split(";")[0], str.split(";")[1], str.split(";")[2], str.split(";")[3]);

                if (response != null) {
                    loadBooks();  // Reload the books after editing
                } else {
                    System.out.println("Failed to edit book");
                }

                search.clear();
        }
    }
}


    @FXML
    protected void deleteBook() throws IOException {
        ObservableList<Integer> selectedIndices = booksList.getSelectionModel().getSelectedIndices();
        if (selectedIndices.size() == 1) {
            String bookToDelete = booksList.getItems().get(selectedIndices.get(0));
            String isbnToDelete = bookToDelete.split(";")[2];

            // Use BookService to delete the book
            String response = BookService.deleteBook(isbnToDelete);

            if (response != null) {
                loadBooks();  // Reload the books after deleting
            } else {
                System.out.println("Failed to delete book");
            }

            search.clear();
        }
    }

    @FXML
    protected void addItem() throws IOException {
        // Collect the data from input fields
        String title = book.getText();
        String authorText = author.getText();
        String isbnText = isbn.getText();
        String categoryText = category.getText();

        // Use BookService to add the book to the backend
        String response = BookService.addBook(title, authorText, isbnText, categoryText);

        if (response != null) {
            System.out.println("Book added: " + response);
            loadBooks(); // Reload the books after adding
        } else {
            System.out.println("Failed to add book");
        }

        // Clear the input fields after adding the book
        author.clear();
        book.clear();
        isbn.clear();
        category.clear();
    }

    public void loadBooks() {
        // Fetch books from the backend using BookService
        List<Book> books = BookService.fetchBooks();
    
        if (books != null) {
            // Update the ListView with the books
            List<String> bookStrings = new ArrayList<>();
            for (Book bo : books) {
                bookStrings.add(bo.toString());
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
