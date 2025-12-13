package com.hotel.ui;

import com.hotel.dao.UserDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterView {

    private final UserDAO userDAO = new UserDAO();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Yeni Müşteri Kaydı");

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("Kayıt Ol");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField txtName = new TextField();
        txtName.setPromptText("Ad Soyad");

        TextField txtTc = new TextField();
        txtTc.setPromptText("T.C. Kimlik No");

        TextField txtPhone = new TextField();
        txtPhone.setPromptText("Telefon");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("E-Posta");

        TextField txtUser = new TextField();
        txtUser.setPromptText("Kullanıcı Adı");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Şifre");

        grid.add(new Label("Ad Soyad:"), 0, 0);
        grid.add(txtName, 1, 0);

        grid.add(new Label("T.C. No:"), 0, 1);
        grid.add(txtTc, 1, 1);

        grid.add(new Label("Telefon:"), 0, 2);
        grid.add(txtPhone, 1, 2);

        grid.add(new Label("E-Posta:"), 0, 3);
        grid.add(txtEmail, 1, 3);

        grid.add(new Label("Kullanıcı Adı:"), 0, 4);
        grid.add(txtUser, 1, 4);

        grid.add(new Label("Şifre:"), 0, 5);
        grid.add(txtPass, 1, 5);

        Button btnRegister = new Button("Kaydı Tamamla");
        btnRegister.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        btnRegister.setMaxWidth(Double.MAX_VALUE);

        btnRegister.setOnAction(e -> {
            if (txtName.getText().isEmpty() || txtTc.getText().isEmpty() || txtUser.getText().isEmpty() || txtPass.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Lütfen zorunlu alanları doldurunuz!").show();
                return;
            }

            boolean isSuccess = userDAO.registerCustomer(
                    txtTc.getText(),
                    txtUser.getText(),
                    txtPass.getText(),
                    txtName.getText(),
                    txtPhone.getText(),
                    txtEmail.getText()
            );

            if (isSuccess) {
                new Alert(Alert.AlertType.INFORMATION, "Kayıt Başarılı! Giriş yapabilirsiniz.").showAndWait();
                stage.close();
            } else {
                new Alert(Alert.AlertType.ERROR, "Kayıt Başarısız! (TC veya Kullanıcı Adı kullanılıyor olabilir)").show();
            }
        });

        mainLayout.getChildren().addAll(lblTitle, grid, btnRegister);

        Scene scene = new Scene(mainLayout, 400, 450);
        stage.setScene(scene);
        stage.show();
    }
}