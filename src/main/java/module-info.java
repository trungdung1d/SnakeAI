module com.example.snakeai {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires java.desktop;
    opens com.example.snakeai to javafx.fxml;
    exports com.example.snakeai to javafx.graphics, javafx.controls, javafx.fxml;
}