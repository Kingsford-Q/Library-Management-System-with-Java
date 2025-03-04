package library.main.librarymanagementsystem.application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
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
import library.main.librarymanagementsystem.HelloApplication;

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
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadBooks();
        } catch (IOException e) {
        }

        booksList.setFixedCellSize(50.0);
        
        Platform.runLater(() -> {
            if (booksList.getScene() != null) {
                String css = Objects.requireNonNull(getClass().getResource("/library/main/librarymanagementsystem/library.css")).toExternalForm();
                booksList.getScene().getStylesheets().add(css);
            }
        });
    }

    @FXML
    protected void searchBook() throws IOException {
        String searchText = search.getText().strip().toLowerCase();
        loadBooks();
        if (searchText.length() >= 3) {
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
            String oldIsbn = bookToEdit.split(";")[2];

            EditBook eb = new EditBook();
            String str = eb.getResult();

            if (str != null) {
                Path p = Paths.get("src/main/data/" + oldIsbn + ".txt");
                Files.deleteIfExists(p);

                Path p2 = Paths.get("src/main/data/" + str.split(";")[2] + ".txt");
                try (FileWriter writer = new FileWriter(p2.toString())) {
                    writer.write(str);
                }

                loadBooks();
                search.clear();
            }
        }
    }

    @FXML
    protected void deleteBook() throws IOException {
        ObservableList<Integer> selectedIndices = booksList.getSelectionModel().getSelectedIndices();
        if (selectedIndices.size() == 1) {
            String bookToDelete = booksList.getItems().get(selectedIndices.get(0));
            String oldIsbn = bookToDelete.split(";")[2];
            Path p = Paths.get("src/main/data/" + oldIsbn + ".txt");
            Files.deleteIfExists(p);
            loadBooks();
            search.clear();
        }
    }

    @FXML
    protected void addItem() throws IOException {
        String data = String.join(";", author.getText(), book.getText(), isbn.getText(), category.getText());
        Path p = Paths.get("src/main/data/" + isbn.getText() + ".txt");
        
        if (Files.notExists(p)) {
            try (FileWriter writer = new FileWriter(p.toString())) {
                writer.write(data);
            }
        }

        author.clear();
        book.clear();
        isbn.clear();
        category.clear();

        loadBooks();
    }

    public static ArrayList<String> listFilesForFolder(final File folder) throws IOException {
        ArrayList<String> al = new ArrayList<>();
        if (folder.exists() && folder.isDirectory()) {
            for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                String read = Files.readAllLines(Paths.get(fileEntry.getPath())).get(0);
                al.add(read.strip());
            }
        }
        return al;
    }

    public void loadBooks() throws IOException {
        Path p = Paths.get("src/main/data");
        final File folder = new File(p.toString());
        ArrayList<String> al = listFilesForFolder(folder);
        booksList.getItems().setAll(al);
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
