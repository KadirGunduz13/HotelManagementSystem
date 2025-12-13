package com.hotel.ui;

import com.hotel.dao.UserDAO;
import com.hotel.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Hyperlink;

public class LoginView {

    private final UserDAO userDAO = new UserDAO();

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Otel Yönetim Sistemi - Giriş");

        Label lblTitle = new Label("Kullanıcı Girişi");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label nameTitle = new Label("Kullanıcı Adı, E-Posta veya T.C. No");
        nameTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;"); // Biraz kalınlaştırdık

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Kullanıcı Adı, E-Posta veya T.C. No");

        VBox usernameGroup = new VBox(5);
        usernameGroup.setMaxWidth(300);
        usernameGroup.setAlignment(Pos.CENTER_LEFT);
        usernameGroup.getChildren().addAll(nameTitle, txtUsername);

        Label passTitle = new Label("Şifre");
        passTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Şifre");

        VBox passwordGroup = new VBox(5);
        passwordGroup.setMaxWidth(300);
        passwordGroup.setAlignment(Pos.CENTER_LEFT); // SOLA YASLA
        passwordGroup.getChildren().addAll(passTitle, txtPassword);

        Button btnLogin = new Button("Giriş Yap");
        btnLogin.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14px;");
        btnLogin.setMaxWidth(300);

        Label lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: red;");

        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            User user = userDAO.login(username, password);

            if (user != null) {
                lblStatus.setStyle("-fx-text-fill: green;");
                lblStatus.setText("Giriş Başarılı!");

                if ("ADMIN".equals(user.getRole())) {
                    new StaffDashboardView().show();
                    primaryStage.close();
                } else {
                    if (user instanceof com.hotel.models.Customer) {
                        new CustomerView((com.hotel.models.Customer) user).show();
                        primaryStage.close();
                    }
                }
            } else {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("Hatalı kullanıcı adı veya şifre!");
            }
        });

        Hyperlink linkRegister = new Hyperlink("Hesabınız yok mu? Kayıt Olun");
        linkRegister.setOnAction(e -> {
            new RegisterView().show();
        });

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        root.getChildren().addAll(lblTitle, usernameGroup, passwordGroup, btnLogin, linkRegister, lblStatus);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}