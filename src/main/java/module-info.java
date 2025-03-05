module library.main.librarymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires java.net.http;
    requires java.base;
    requires com.google.gson;

    opens library.main.librarymanagementsystem to javafx.fxml;
    exports library.main.librarymanagementsystem;
    exports library.main.librarymanagementsystem.application;
    opens library.main.librarymanagementsystem.application to javafx.fxml;

    opens library.services to com.google.gson;
    exports library.services;
}
