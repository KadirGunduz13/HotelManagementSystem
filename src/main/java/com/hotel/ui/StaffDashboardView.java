package com.hotel.ui;

import com.hotel.dao.ReservationDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.dao.UserDAO;
import com.hotel.models.Customer;
import com.hotel.models.Reservation;
import com.hotel.models.Room;
import com.hotel.services.RoomFactory;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class StaffDashboardView {

    private final RoomDAO roomDAO = new RoomDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    // DÜZELTME 1: Bu ComboBox'ı sınıf seviyesine taşıdık ki her yerden erişebilelim
    private ComboBox<Customer> cmbCustomer;

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Personel Yönetim Paneli - Otel Sistemi");

        BorderPane mainLayout = new BorderPane();
        TabPane tabPane = new TabPane();

        Tab roomTab = new Tab("Oda Yönetimi", createRoomContent());
        roomTab.setClosable(false);

        Tab customerTab = new Tab("Müşteriler", createCustomerContent());
        customerTab.setClosable(false);

        Tab reservationTab = new Tab("Rezervasyonlar", createReservationContent());
        reservationTab.setClosable(false);

        // DÜZELTME 2: Rezervasyon sekmesine tıklandığında listeyi yenile
        reservationTab.setOnSelectionChanged(e -> {
            if (reservationTab.isSelected()) {
                refreshCustomerCombo(); // Müşteri listesini güncelle
                System.out.println("DEBUG: Rezervasyon sekmesi açıldı, müşteri listesi yenilendi.");
            }
        });

        tabPane.getTabs().addAll(roomTab, customerTab, reservationTab);
        mainLayout.setCenter(tabPane);

        Scene scene = new Scene(mainLayout, 1000, 700);
        stage.setScene(scene);
        stage.show();
    }

    // Yardımcı Metot: Müşteri Listesini Yeniler
    private void refreshCustomerCombo() {
        if (cmbCustomer != null) {
            cmbCustomer.setItems(FXCollections.observableArrayList(userDAO.searchCustomers("")));
        }
    }

    // --- 3. REZERVASYON SEKMESİ ---
    private VBox createReservationContent() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label lblTitle = new Label("Rezervasyon İşlemleri");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TitledPane createPane = new TitledPane();
        createPane.setText("Müşteri Adına Rezervasyon Oluştur");
        createPane.setExpanded(false);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(10));

        // 1. Müşteri Seçimi (Artık sınıf değişkenini kullanıyoruz)
        cmbCustomer = new ComboBox<>();
        cmbCustomer.setPromptText("Müşteri Seçiniz");
        cmbCustomer.setPrefWidth(200);

        // İlk yükleme
        refreshCustomerCombo();

        cmbCustomer.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer c) { return c != null ? c.getFullName() + " (" + c.getTcNo() + ")" : ""; }
            @Override
            public Customer fromString(String string) { return null; }
        });

        // 2. Tarih Seçimi
        DatePicker dpIn = new DatePicker(LocalDate.now());
        dpIn.setPromptText("Giriş Tarihi");
        DatePicker dpOut = new DatePicker(LocalDate.now().plusDays(1));
        dpOut.setPromptText("Çıkış Tarihi");

        // 3. Oda Filtreleme
        TextField txtCapacity = new TextField();
        txtCapacity.setPromptText("Kişi Sayısı");
        txtCapacity.setPrefWidth(80);

        Button btnFindRooms = new Button("Uygun Odaları Getir");
        ComboBox<Room> cmbRoom = new ComboBox<>();
        cmbRoom.setPromptText("Önce Oda Arayınız");
        cmbRoom.setPrefWidth(200);

        cmbRoom.setConverter(new StringConverter<Room>() {
            @Override
            public String toString(Room r) { return r != null ? "Oda: " + r.getRoomNumber() + " (" + r.getType() + ") - " + r.getPrice() + " TL" : ""; }
            @Override
            public Room fromString(String string) { return null; }
        });

        btnFindRooms.setOnAction(e -> {
            int capacity = 0;
            try { if(!txtCapacity.getText().isEmpty()) capacity = Integer.parseInt(txtCapacity.getText()); }
            catch(NumberFormatException ex) {}
            int finalCap = capacity;

            List<Room> availableRooms = roomDAO.getAllRooms().stream()
                    .filter(r -> "AVAILABLE".equals(r.getStatus()))
                    .filter(r -> r.getCapacity() >= finalCap)
                    .collect(Collectors.toList());

            if(availableRooms.isEmpty()) {
                showAlert("Uygun oda bulunamadı!", Alert.AlertType.WARNING);
            } else {
                cmbRoom.setItems(FXCollections.observableArrayList(availableRooms));
                cmbRoom.show();
            }
        });

        Button btnSave = new Button("Rezervasyonu Oluştur");
        btnSave.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");

        grid.add(new Label("Müşteri:"), 0, 0); grid.add(cmbCustomer, 1, 0);
        grid.add(new Label("Giriş:"), 2, 0); grid.add(dpIn, 3, 0);
        grid.add(new Label("Çıkış:"), 4, 0); grid.add(dpOut, 5, 0);

        grid.add(new Label("Kişi Sayısı:"), 0, 1); grid.add(txtCapacity, 1, 1);
        grid.add(btnFindRooms, 2, 1);
        grid.add(new Label("Oda Seç:"), 3, 1); grid.add(cmbRoom, 4, 1, 2, 1);

        grid.add(btnSave, 1, 2);

        createPane.setContent(grid);

        HBox actionBox = new HBox(10);
        Button btnRefresh = new Button("Yenile");
        Button btnCheckIn = new Button("Check-In Yap (Giriş)");
        Button btnCheckOut = new Button("Check-Out Yap (Çıkış)");

        btnCheckIn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        btnCheckOut.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

        actionBox.getChildren().addAll(btnRefresh, btnCheckIn, btnCheckOut);

        TableView<Reservation> table = new TableView<>();
        TableColumn<Reservation, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Reservation, String> colCustomer = new TableColumn<>("Müşteri");
        colCustomer.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCustomerName()));

        TableColumn<Reservation, String> colRoom = new TableColumn<>("Oda No");
        colRoom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoomNumber()));

        TableColumn<Reservation, Date> colCheckIn = new TableColumn<>("Giriş");
        colCheckIn.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getCheckInDate()));

        TableColumn<Reservation, Date> colCheckOut = new TableColumn<>("Çıkış");
        colCheckOut.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getCheckOutDate()));

        TableColumn<Reservation, String> colStatus = new TableColumn<>("Durum");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));

        table.getColumns().addAll(colId, colCustomer, colRoom, colCheckIn, colCheckOut, colStatus);

        refreshReservationTable(table);

        btnRefresh.setOnAction(e -> refreshReservationTable(table));

        btnSave.setOnAction(e -> {
            Customer cust = cmbCustomer.getValue();
            Room room = cmbRoom.getValue();
            LocalDate inDate = dpIn.getValue();
            LocalDate outDate = dpOut.getValue();

            if (cust == null || room == null || inDate == null || outDate == null) {
                showAlert("Lütfen tüm alanları seçiniz!", Alert.AlertType.WARNING);
                return;
            }

            long days = ChronoUnit.DAYS.between(inDate, outDate);
            if (days < 1) {
                showAlert("Tarihler hatalı!", Alert.AlertType.ERROR);
                return;
            }

            double totalPrice = days * room.getPrice();

            Reservation res = new Reservation(0, cust.getId(), room.getId(),
                    Date.valueOf(inDate), Date.valueOf(outDate), totalPrice, "PENDING");

            if (reservationDAO.createReservation(res)) {
                roomDAO.updateRoomStatus(room.getId(), "RESERVED");
                showAlert("Rezervasyon Oluşturuldu!", Alert.AlertType.INFORMATION);
                refreshReservationTable(table);
                cmbCustomer.getSelectionModel().clearSelection();
                cmbRoom.getItems().clear();
            } else {
                showAlert("Hata oluştu!", Alert.AlertType.ERROR);
            }
        });

        btnCheckIn.setOnAction(e -> {
            Reservation selected = table.getSelectionModel().getSelectedItem();
            if (selected != null && reservationDAO.updateStatus(selected.getId(), "CHECKED_IN")) {
                showAlert("Giriş Yapıldı", Alert.AlertType.INFORMATION);
                refreshReservationTable(table);
            }
        });

        btnCheckOut.setOnAction(e -> {
            Reservation selected = table.getSelectionModel().getSelectedItem();
            if (selected != null && reservationDAO.updateStatus(selected.getId(), "CHECKED_OUT")) {
                showAlert("Çıkış Yapıldı", Alert.AlertType.INFORMATION);
                refreshReservationTable(table);
            }
        });

        layout.getChildren().addAll(lblTitle, createPane, actionBox, table);
        return layout;
    }

    private void refreshReservationTable(TableView<Reservation> table) {
        table.setItems(FXCollections.observableArrayList(reservationDAO.getAllReservations()));
    }

    // --- MÜŞTERİ SEKMESİ (Değişmedi - Önceki kodun aynısı) ---
    private VBox createCustomerContent() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        Label lblTitle = new Label("Müşteri Yönetimi");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TitledPane addPane = new TitledPane();
        addPane.setText("Yeni Müşteri Ekle (Panel)");
        addPane.setExpanded(false);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(10));

        TextField txtName = new TextField(); txtName.setPromptText("Ad Soyad");
        TextField txtTc = new TextField(); txtTc.setPromptText("TC Kimlik No");
        TextField txtPhone = new TextField(); txtPhone.setPromptText("Telefon");
        TextField txtEmail = new TextField(); txtEmail.setPromptText("E-Posta");
        TextField txtUser = new TextField(); txtUser.setPromptText("Kullanıcı Adı");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Şifre");

        grid.add(new Label("Ad Soyad:"), 0, 0); grid.add(txtName, 1, 0);
        grid.add(new Label("TC No:"), 2, 0); grid.add(txtTc, 3, 0);
        grid.add(new Label("Telefon:"), 0, 1); grid.add(txtPhone, 1, 1);
        grid.add(new Label("E-Posta:"), 2, 1); grid.add(txtEmail, 3, 1);
        grid.add(new Label("Kullanıcı Adı:"), 0, 2); grid.add(txtUser, 1, 2);
        grid.add(new Label("Şifre:"), 2, 2); grid.add(txtPass, 3, 2);

        Button btnAddCustomer = new Button("Müşteriyi Kaydet");
        btnAddCustomer.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        grid.add(btnAddCustomer, 1, 3);
        addPane.setContent(grid);

        HBox searchBox = new HBox(10);
        TextField txtSearch = new TextField();
        Button btnSearch = new Button("Ara");
        searchBox.getChildren().addAll(new Label("Müşteri Ara:"), txtSearch, btnSearch);

        TableView<Customer> table = new TableView<>();
        TableColumn<Customer, String> colTc = new TableColumn<>("TC Kimlik");
        colTc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTcNo()));
        TableColumn<Customer, String> colName = new TableColumn<>("Ad Soyad");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        TableColumn<Customer, String> colUser = new TableColumn<>("Kullanıcı Adı");
        colUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        TableColumn<Customer, String> colPhone = new TableColumn<>("Telefon");
        colPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        table.getColumns().addAll(colTc, colName, colUser, colPhone);

        table.setItems(FXCollections.observableArrayList(userDAO.searchCustomers("")));

        btnAddCustomer.setOnAction(e -> {
            if(userDAO.registerCustomer(txtTc.getText(), txtUser.getText(), txtPass.getText(), txtName.getText(), txtPhone.getText(), txtEmail.getText())) {
                showAlert("Müşteri Eklendi", Alert.AlertType.INFORMATION);
                txtName.clear(); txtTc.clear(); txtPhone.clear(); txtEmail.clear(); txtUser.clear(); txtPass.clear();
                table.setItems(FXCollections.observableArrayList(userDAO.searchCustomers("")));
            } else {
                showAlert("Kayıt Başarısız!", Alert.AlertType.ERROR);
            }
        });
        btnSearch.setOnAction(e -> table.setItems(FXCollections.observableArrayList(userDAO.searchCustomers(txtSearch.getText()))));

        // SAĞ TIK MENÜSÜ EKLENDİ
        ContextMenu contextMenu = new ContextMenu();
        MenuItem detailsItem = new MenuItem("Müşteri Detaylarını Gör");
        detailsItem.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if(selected != null) new CustomerDetailsView(selected).show();
        });
        contextMenu.getItems().add(detailsItem);
        table.setContextMenu(contextMenu);

        layout.getChildren().addAll(lblTitle, addPane, searchBox, table);
        return layout;
    }

    // --- ODA SEKMESİ (Değişmedi - Önceki kodun aynısı) ---
    private VBox createRoomContent() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        Label lblTitle = new Label("Oda Yönetimi");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox formLayout = new HBox(10);
        formLayout.setPadding(new Insets(10));
        formLayout.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");

        TextField txtRoomNo = new TextField(); txtRoomNo.setPromptText("Oda No"); txtRoomNo.setPrefWidth(80);
        ComboBox<String> cmbType = new ComboBox<>(); cmbType.getItems().addAll("STANDARD", "SUITE", "FAMILY"); cmbType.getSelectionModel().selectFirst(); cmbType.setPrefWidth(100);
        TextField txtCapacity = new TextField(); txtCapacity.setPromptText("Kapasite"); txtCapacity.setPrefWidth(70);
        TextField txtPrice = new TextField(); txtPrice.setPromptText("Fiyat"); txtPrice.setPrefWidth(80);
        Button btnAdd = new Button("Oda Ekle");
        formLayout.getChildren().addAll(new Label("Yeni Oda:"), txtRoomNo, cmbType, txtCapacity, txtPrice, btnAdd);

        TableView<Room> table = new TableView<>();
        TableColumn<Room, String> colNum = new TableColumn<>("Oda No"); colNum.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        TableColumn<Room, String> colType = new TableColumn<>("Tip"); colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Room, Integer> colCap = new TableColumn<>("Kapasite"); colCap.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        TableColumn<Room, Double> colPrice = new TableColumn<>("Fiyat"); colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Room, String> colStatus = new TableColumn<>("Durum"); colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(colNum, colType, colCap, colPrice, colStatus);

        ContextMenu ctx = new ContextMenu();
        MenuItem delItem = new MenuItem("Seçili Odayı Sil");
        delItem.setOnAction(e -> {
            Room r = table.getSelectionModel().getSelectedItem();
            if(r != null && roomDAO.deleteRoom(r.getId())) refreshRoomTable(table);
        });
        ctx.getItems().add(delItem);
        table.setContextMenu(ctx);

        refreshRoomTable(table);
        btnAdd.setOnAction(e -> {
            try {
                Room newRoom = RoomFactory.createRoom(cmbType.getValue(), 0, txtRoomNo.getText(), Double.parseDouble(txtPrice.getText()), "AVAILABLE");
                newRoom.setCapacity(Integer.parseInt(txtCapacity.getText()));
                if (roomDAO.addRoom(newRoom)) { refreshRoomTable(table); showAlert("Oda Eklendi", Alert.AlertType.INFORMATION); }
            } catch (Exception ex) { showAlert("Hata: " + ex.getMessage(), Alert.AlertType.ERROR); }
        });
        layout.getChildren().addAll(lblTitle, formLayout, table);
        return layout;
    }

    private void refreshRoomTable(TableView<Room> table) {
        table.setItems(FXCollections.observableArrayList(roomDAO.getAllRooms()));
    }

    private void showAlert(String message, Alert.AlertType type) {
        new Alert(type, message).show();
    }
}