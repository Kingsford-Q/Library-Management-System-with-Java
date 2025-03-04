package library.main.librarymanagementsystem;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private static Stage primaryStage;

    @SuppressWarnings("exports")
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/library/main/librarymanagementsystem/hello-view.fxml")));


        Scene scene = new Scene(fxmlLoader.load());

        // Load external CSS (Ensure listview.css is in the correct directory)
        String cssPath = "/library/main/librarymanagementsystem/listview.css";
        scene.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource(cssPath)).toExternalForm()
        );

        primaryStage = stage;
        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
