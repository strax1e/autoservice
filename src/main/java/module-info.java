module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.app to javafx.fxml;
    exports com.example.app;
    exports com.example.app.db;
    exports com.example.app.entity;
    opens com.example.app.db to javafx.fxml;
    opens com.example.app.entity to javafx.fxml;
}
