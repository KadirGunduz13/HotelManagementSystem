package com.hotel.ui;

import com.hotel.dao.ReservationDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.dao.UserDAO;
import com.hotel.models.Customer;
import com.hotel.models.Reservation;
import com.hotel.models.Room;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerView {

    private final Customer currentCustomer;
    private final RoomDAO roomDAO = new RoomDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    public CustomerView(Customer customer) {
        this.currentCustomer = customer;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Müşteri Paneli - Hoşgeldiniz " + currentCustomer.getFullName());

        TabPane tabPane = new TabPane();

        Tab searchTab = new Tab("Oda Ara & Kirala", createSearchContent());
        searchTab.setClosable(false);

        Tab historyTab = new Tab("Rezervasyonlarım", createHistoryContent());
        historyTab.setClosable(false);

        Tab profileTab = new Tab("Profil Güncelle", createProfileContent());
        profileTab.setClosable(false);

        tabPane.getTabs().addAll(searchTab, historyTab, profileTab);

        Scene scene = new Scene(tabPane, 1000, 650);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createSearchContent() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label lblInfo = new Label("Arama Kriterlerini Belirleyiniz:");
        lblInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox filterBox = new HBox(10);
        filterBox.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

        DatePicker dpCheckIn = new DatePicker(LocalDate.now());
        dpCheckIn.setPromptText("Giriş"); dpCheckIn.setPrefWidth(110);

        DatePicker dpCheckOut = new DatePicker(LocalDate.now().plusDays(1));
        dpCheckOut.setPromptText("Çıkış"); dpCheckOut.setPrefWidth(110);

        ComboBox<String> cmbType = new ComboBox<>();
        cmbType.getItems().addAll("Hepsi", "STANDARD", "SUITE", "FAMILY");
        cmbType.getSelectionModel().selectFirst();
        cmbType.setPrefWidth(100);

        TextField txtCapacity = new TextField();
        txtCapacity.setPromptText("Kişi"); txtCapacity.setPrefWidth(50);

        Button btnSearch = new Button("Ara");
        btnSearch.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold;");

        filterBox.getChildren().addAll(new Label("Giriş:"), dpCheckIn, new Label("Çıkış:"), dpCheckOut, new Label("Tip:"), cmbType, new Label("Kişi:"), txtCapacity, btnSearch);

        TableView<Room> table = new TableView<>();
        TableColumn<Room, String> colNum = new TableColumn<>("Oda No"); colNum.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        TableColumn<Room, String> colType = new TableColumn<>("Tip"); colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Room, Integer> colCap = new TableColumn<>("Kapasite"); colCap.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        TableColumn<Room, Double> colPrice = new TableColumn<>("Fiyat"); colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Room, String> colStatus = new TableColumn<>("Durum"); colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        table.getColumns().addAll(colNum, colType, colCap, colPrice, colStatus);

        HBox bottomBox = new HBox(15);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));

        CheckBox chkBreakfast = new CheckBox("Kahvaltı Ekle (+500 TL)");

        ComboBox<String> cmbPayment = new ComboBox<>();
        cmbPayment.getItems().addAll("Kredi Kartı", "Banka Havalesi");
        cmbPayment.getSelectionModel().selectFirst();

        Button btnBook = new Button("Öde ve Rezerve Et");
        btnBook.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");

        bottomBox.getChildren().addAll(chkBreakfast, new Label("Ödeme:"), cmbPayment, btnBook);

        btnSearch.setOnAction(e -> {
            List<Room> allRooms = roomDAO.getAllRooms();
            String selectedType = cmbType.getValue();
            String capacityText = txtCapacity.getText();
            int minCapacity = 0;
            try { if (!capacityText.isEmpty()) minCapacity = Integer.parseInt(capacityText); } catch (NumberFormatException ex) {}
            final int finalMinCapacity = minCapacity;

            List<Room> filteredRooms = allRooms.stream()
                    .filter(r -> "AVAILABLE".equals(r.getStatus()))
                    .filter(r -> "Hepsi".equals(selectedType) || r.getType().equalsIgnoreCase(selectedType))
                    .filter(r -> r.getCapacity() >= finalMinCapacity)
                    .collect(Collectors.toList());
            table.setItems(FXCollections.observableArrayList(filteredRooms));
        });

        btnBook.setOnAction(e -> {
            Room selectedRoom = table.getSelectionModel().getSelectedItem();
            if (selectedRoom == null) { showAlert("Lütfen oda seçiniz!", Alert.AlertType.WARNING); return; }

            LocalDate inDate = dpCheckIn.getValue(); LocalDate outDate = dpCheckOut.getValue();
            if (inDate == null || outDate == null) { showAlert("Tarih seçiniz!", Alert.AlertType.WARNING); return; }
            long days = ChronoUnit.DAYS.between(inDate, outDate);
            if (days < 1) { showAlert("Tarih hatalı!", Alert.AlertType.ERROR); return; }

            com.hotel.patterns.ICost costCalc = new com.hotel.patterns.BaseReservationCost(selectedRoom.getPrice(), days);
            if (chkBreakfast.isSelected()) costCalc = new com.hotel.patterns.BreakfastDecorator(costCalc, days);
            double finalPrice = costCalc.getCost();

            com.hotel.patterns.PaymentStrategy paymentStrategy;
            String method = cmbPayment.getValue();

            if ("Kredi Kartı".equals(method)) {
                paymentStrategy = new com.hotel.patterns.CreditCardPayment("1234-5678-9012-3456", "123");
            } else {
                paymentStrategy = new com.hotel.patterns.BankTransferPayment("TR99000111222333");
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Tutar: " + finalPrice + " TL (" + method + ")\nOnaylıyor musunuz?", ButtonType.YES, ButtonType.NO);

            final com.hotel.patterns.PaymentStrategy finalStrategy = paymentStrategy; // Lambda için final olmalı

            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    if (finalStrategy.pay(finalPrice)) {
                        Reservation res = new Reservation(0, currentCustomer.getId(), selectedRoom.getId(), Date.valueOf(inDate), Date.valueOf(outDate), finalPrice, "PENDING");
                        if (reservationDAO.createReservation(res)) {
                            roomDAO.updateRoomStatus(selectedRoom.getId(), "RESERVED");
                            showAlert("Ödeme Alındı & Rezervasyon Başarılı!", Alert.AlertType.INFORMATION);
                            btnSearch.fire();
                        }
                    }
                }
            });
        });

        layout.getChildren().addAll(lblInfo, filterBox, table, bottomBox);
        return layout;
    }

    private void showAlert(String msg, Alert.AlertType type) { new Alert(type, msg).show(); }

    private VBox createHistoryContent() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label lblTitle = new Label("Geçmiş Rezervasyonlarım");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Reservation> table = new TableView<>();

        TableColumn<Reservation, String> colRoom = new TableColumn<>("Oda No");
        colRoom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoomNumber()));

        TableColumn<Reservation, Date> colIn = new TableColumn<>("Giriş");
        colIn.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getCheckInDate()));

        TableColumn<Reservation, Date> colOut = new TableColumn<>("Çıkış");
        colOut.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getCheckOutDate()));

        TableColumn<Reservation, Double> colPrice = new TableColumn<>("Tutar");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        TableColumn<Reservation, String> colStatus = new TableColumn<>("Durum");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));

        table.getColumns().addAll(colRoom, colIn, colOut, colPrice, colStatus);

        HBox buttonBox = new HBox(10);

        Button btnRefresh = new Button("Yenile");
        btnRefresh.setOnAction(e -> table.setItems(FXCollections.observableArrayList(reservationDAO.getReservationsByCustomerId(currentCustomer.getId()))));

        Button btnCancel = new Button("Seçili Rezervasyonu İptal Et");
        btnCancel.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;"); // Kırmızı Buton

        btnCancel.setOnAction(e -> {
            Reservation selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Lütfen iptal etmek istediğiniz rezervasyonu seçiniz!").show();
                return;
            }

            if (!"PENDING".equals(selected.getStatus())) {
                new Alert(Alert.AlertType.ERROR, "Sadece 'Beklemede' (PENDING) olan rezervasyonlar iptal edilebilir!").show();
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Rezervasyonu iptal etmek istediğinize emin misiniz?\n(Oda tekrar başkalarına açılacaktır)", ButtonType.YES, ButtonType.NO);

            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    if (reservationDAO.cancelReservation(selected.getId(), selected.getRoomId())) {
                        new Alert(Alert.AlertType.INFORMATION, "Rezervasyon başarıyla iptal edildi.").show();
                        btnRefresh.fire(); // Listeyi yenile
                    } else {
                        new Alert(Alert.AlertType.ERROR, "İptal işlemi sırasında hata oluştu!").show();
                    }
                }
            });
        });

        btnRefresh.fire();

        buttonBox.getChildren().addAll(btnRefresh, btnCancel);
        layout.getChildren().addAll(lblTitle, buttonBox, table);
        return layout;
    }

    private VBox createProfileContent() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label lblTitle = new Label("Profil Bilgilerim");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField txtName = new TextField(currentCustomer.getFullName());
        txtName.setDisable(true);

        TextField txtTc = new TextField(currentCustomer.getTcNo());
        txtTc.setDisable(true);

        String phoneVal = currentCustomer.getPhone() != null ? currentCustomer.getPhone() : "";
        TextField txtPhone = new TextField(phoneVal);
        txtPhone.setPromptText("Telefon Giriniz");

        String emailVal = currentCustomer.getEmail() != null ? currentCustomer.getEmail() : "";
        TextField txtEmail = new TextField(emailVal);
        txtEmail.setPromptText("E-Posta Giriniz");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Yeni Şifre");
        txtPass.setText(currentCustomer.getPassword());

        Button btnUpdate = new Button("Bilgileri Güncelle");
        btnUpdate.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");

        btnUpdate.setOnAction(e -> {
            if (new UserDAO().updateCustomerProfile(
                    currentCustomer.getId(),
                    txtPhone.getText(),
                    txtEmail.getText(),
                    txtPass.getText())) {
                new Alert(Alert.AlertType.INFORMATION, "Profil Güncellendi! Çıkış yapıp tekrar girmeniz önerilir.").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Hata oluştu!").show();
            }
        });

        layout.getChildren().addAll(
                lblTitle,
                new Label("Ad Soyad:"), txtName,
                new Label("TC No:"), txtTc,
                new Label("Telefon:"), txtPhone,
                new Label("E-Posta:"), txtEmail,
                new Label("Şifre:"), txtPass,
                btnUpdate
        );
        return layout;
    }
}