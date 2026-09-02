module com.maisonneuve.netflix {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.maisonneuve.netflix to javafx.fxml;
    exports com.maisonneuve.netflix;
}