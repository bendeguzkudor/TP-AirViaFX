module com.example.tpairviafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;


    opens com.example.tpairviafx to javafx.fxml;
    exports com.example.tpairviafx;
}