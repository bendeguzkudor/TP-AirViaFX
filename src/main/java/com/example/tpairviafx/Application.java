package com.example.tpairviafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Application extends javafx.application.Application {
    public static double interlineCommissionRate = 0.09;
    public static double domesticCommisionRate = 0.05;
    public static double taxRate = 0.1;
    private DateTimeFormatter dtf;

    private LocalDateTime now;
    public static String date;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("login.fxml"));
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setScene(scene);
        stage.show();


    }
    public static String getDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.now();
        return date.format(dtf);

    }

    public static void main(String[] args) {
        launch();
    }
}