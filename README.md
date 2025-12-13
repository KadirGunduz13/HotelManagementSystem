# 🏨 Hotel Management System (Otel Yönetim Sistemi)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-007396?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

JavaFX ve MySQL kullanılarak geliştirilmiş, **Yazılım Tasarım Desenleri (Design Patterns)** ile güçlendirilmiş, ölçeklenebilir ve modüler bir Otel Yönetim Otomasyonu.

---

## 📖 İçindekiler
- [Proje Hakkında](#-proje-hakkında)
- [Özellikler](#-özellikler)
- [Kullanılan Teknolojiler](#-kullanılan-teknolojiler)
- [Yazılım Mimarisi ve Tasarım Desenleri](#-yazılım-mimarisi-ve-tasarım-desenleri)
- [Veritabanı Yapısı](#-veritabanı-yapısı)
- [Kurulum ve Çalıştırma](#-kurulum-ve-çalıştırma)
- [Ekran Görüntüleri](#-ekran-görüntüleri)

---

## 🚀 Proje Hakkında
Bu proje, bir otelin günlük operasyonlarını (rezervasyon, oda yönetimi, müşteri takibi) dijitalleştirmek amacıyla geliştirilmiştir. Sistem, **Yönetici (Admin)** ve **Müşteri (Customer)** olmak üzere iki farklı rolü destekler.

Projenin temel amacı sadece çalışan bir yazılım yapmak değil; **Singleton, Factory, Observer, DAO** gibi endüstri standardı tasarım desenlerini kullanarak **bakımı kolay (maintainable)** ve **geliştirilebilir** bir mimari kurmaktır.

---

## ✨ Özellikler

### 👨‍💼 Yönetici (Personel) Paneli
- **Oda Yönetimi:** Yeni oda ekleme, silme, güncelleme (Standart, Suit, Aile odaları).
- **Rezervasyon Takibi:** Tüm rezervasyonları listeleme ve durumlarını yönetme.
- **Akıllı Arama:** Müşterileri İsim, TC No, Telefon veya Kullanıcı Adı ile filtreleme.
- **Log Sistemi:** Yapılan işlemleri anlık olarak izleme (Observer Pattern).

### 👤 Müşteri Paneli
- **Oda Arama:** Tarih aralığına ve kişi sayısına göre uygun oda sorgulama.
- **Sıralama:** Odaları kapasiteye ve fiyata göre akıllı sıralama.
- **Rezervasyon Yapma:** Seçilen odayı rezerve etme ve geçmiş rezervasyonları görüntüleme.

---

## 🛠 Kullanılan Teknolojiler

| Teknoloji | Açıklama |
|-----------|----------|
| **Dil** | Java 17+ |
| **Arayüz** | JavaFX (FXML & CSS) |
| **Veritabanı** | MySQL |
| **Veri Erişimi** | JDBC & DAO Pattern |
| **IDE** | IntelliJ IDEA / Eclipse |
| **Build Tool** | Maven / Gradle (Opsiyonel) |

---

## 🏗 Yazılım Mimarisi ve Tasarım Desenleri

Projede **Layered Architecture (Katmanlı Mimari)** kullanılmıştır.

### 1. Singleton Pattern 🔒
Veritabanı bağlantısı (`DatabaseConnection`) ve Bildirim Yöneticisi (`NotificationManager`) için kullanıldı. Böylece sistem genelinde tek bir bağlantı nesnesi garanti altına alındı.

### 2. Factory Pattern 🏭
`RoomFactory` sınıfı kullanılarak; Standart, Suit ve Aile odası gibi farklı nesnelerin üretim süreci soyutlandı. Yeni bir oda tipi eklendiğinde ana kod bozulmaz.

### 3. Observer Pattern 👀
Rezervasyon yapıldığında sistemin farklı birimlerini (Log sistemi, SMS simülasyonu) uyarmak için kullanıldı. `NotificationManager` tetiklendiğinde tüm `Observer`lar (Gözlemciler) haberdar olur.

### 4. DAO (Data Access Object) Pattern 💾
Veritabanı işlemleri (`UserDAO`, `RoomDAO`, `ReservationDAO`) iş mantığından tamamen ayrılarak kodun okunabilirliği artırıldı.

---

## 🗄 Veritabanı Yapısı (ER Diagram)

Proje ilişkisel veritabanı yapısına sahiptir:
- **Users:** Kullanıcı bilgileri ve rolleri.
- **Rooms:** Oda özellikleri, fiyatları ve durumu.
- **Reservations:** Müşteri ve oda arasındaki bağlantı (N-to-N relation).
- **Logs:** Sistem hareketlerinin kaydı.

<img width="1488" height="731" alt="ER Diyagram" src="https://github.com/user-attachments/assets/7778ad2b-82de-4a0e-b0c2-fec5856e4557" />

---

## 📸 Ekran Görüntüleri

### 1. Giriş Ekranı
<img width="496" height="532" alt="giris" src="https://github.com/user-attachments/assets/32c488de-8d67-45be-8105-feba6dc2077d" />


### 2. Personel Yönetim Paneli
<img width="1243" height="907" alt="adminPanel" src="https://github.com/user-attachments/assets/5933334b-5c1a-4f02-8d55-908d97fbbe6c" />


### 3. Rezervasyon Oluşturma (Akıllı Sıralama)
<img width="1247" height="908" alt="rezervasyonOlusturma" src="https://github.com/user-attachments/assets/8f21ba4b-16b0-415b-96a6-63809135fbe0" />


### 4. Log ve Bildirim Sistemi
<img width="1918" height="978" alt="logKayıt" src="https://github.com/user-attachments/assets/abcf48b3-e19b-4ee8-ba4e-178c89593317" />


---

### 2. Veritabanını Oluşturun
MySQL'de hotel_db adında bir veritabanı oluşturun ve sql/database.sql dosyasını import edin.

### 3. Bağlantı Ayarlarını Yapın
DatabaseConnection.java dosyasındaki kullanıcı adı ve şifreyi kendi MySQL bilgilerinizle güncelleyin:

private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
private static final String USER = "root";
private static final String PASSWORD = "sifreniz";

### 4. Çalıştırın
Projenin Launcher.java dosyasını çalıştırın.

---

📝 UML Diyagramları
Projenin mimarisini daha iyi anlamak için çizilen diyagramlar:

### Abstract Class Diyagramı

<img width="1742" height="668" alt="Abstract Class Diyagram" src="https://github.com/user-attachments/assets/f7bf29c9-fe95-46fc-8ada-abe6227bd968" />

### Sequence Diyagramı (Rezervasyon Senaryosu)

<img width="1452" height="662" alt="Sequence Diyagram" src="https://github.com/user-attachments/assets/992205c1-fec5-418c-b666-3a60ed899986" />

### Use-Case Diyagramı

![Use-Case Diyagram](https://github.com/user-attachments/assets/193e4c2c-edd7-4124-8f3d-c383126a4aec)

