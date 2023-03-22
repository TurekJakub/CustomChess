module com.example.fieldbuilderfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.fieldbuilderfx to javafx.fxml;
    exports com.example.fieldbuilderfx;
}