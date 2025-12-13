package com.hotel.ui;

import com.hotel.dao.ReservationDAO;
import com.hotel.models.Customer;
import com.hotel.models.Reservation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Date;

public class CustomerDetailsView {

    private final Customer customer;
    private final ReservationDAO reservationDAO = new ReservationDAO();

    public CustomerDetailsView(Customer customer) {
        this.customer = customer;
    }

    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Müşteri Detayları: " + customer.getFullName());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label lblInfo = new Label("Müşteri Bilgileri");
        lblInfo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #007bff;");

        VBox infoBox = new VBox(5);
        infoBox.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-radius: 5;");

        infoBox.getChildren().addAll(
                new Label("Ad Soyad: " + customer.getFullName()),
                new Label("TC Kimlik: " + customer.getTcNo()),
                new Label("Telefon: " + (customer.getPhone() != null ? customer.getPhone() : "-")),
                new Label("E-Posta: " + (customer.getEmail() != null ? customer.getEmail() : "-")),
                new Label("Kullanıcı Adı: " + customer.getUsername())
        );

        Label lblHistory = new Label("Rezervasyon & Ödeme Geçmişi");
        lblHistory.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #007bff;");

        TableView<Reservation> table = new TableView<>();

        TableColumn<Reservation, String> colRoom = new TableColumn<>("Oda No");
        colRoom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoomNumber()));

        TableColumn<Reservation, Date> colIn = new TableColumn<>("Giriş");
        colIn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCheckInDate()));

        TableColumn<Reservation, Date> colOut = new TableColumn<>("Çıkış");
        colOut.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCheckOutDate()));

        TableColumn<Reservation, Double> colPrice = new TableColumn<>("Tutar (TL)");
        colPrice.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTotalPrice()));

        TableColumn<Reservation, String> colStatus = new TableColumn<>("Durum");
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        table.getColumns().addAll(colRoom, colIn, colOut, colPrice, colStatus);

        table.setItems(FXCollections.observableArrayList(
                reservationDAO.getReservationsByCustomerId(customer.getId())
        ));

        table.setPlaceholder(new Label("Bu müşteriye ait kayıt bulunamadı."));

        layout.getChildren().addAll(lblInfo, infoBox, lblHistory, table);

        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.show();
    }
}