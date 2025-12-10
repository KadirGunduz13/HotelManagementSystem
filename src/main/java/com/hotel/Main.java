package com.hotel;

import com.hotel.ui.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

// JavaFX uygulamaları Application sınıfından türetilir
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Uygulama başladığında Login ekranını aç
        LoginView loginView = new LoginView();
        loginView.show(primaryStage);
    }

    public static void main(String[] args) {
        // JavaFX uygulamasını başlatır
        launch(args);
    }
}