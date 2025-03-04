package library.main.librarymanagementsystem;

import java.io.IOException;

import javafx.fxml.FXML;
import library.main.librarymanagementsystem.application.LibraryController;

public class HelloController {

    @FXML
    protected void onStartButtonClick() throws IOException {
        LibraryController.changeScene();
    }
}