module com.example.tpairviafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.logging.log4j;


    opens com.example.tpairviafx to javafx.fxml;
    exports com.example.tpairviafx;
}