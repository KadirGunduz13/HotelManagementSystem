# 🏨 Hotel Management System (Otel Yönetim Sistemi)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-007396?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

JavaFX ve MySQL kullanılarak geliştirilmiş, **6 farklı Tasarım Deseni (Design Patterns)** ile güçlendirilmiş, ölçeklenebilir ve modüler bir Otel Yönetim Otomasyonu.

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

Projenin temel amacı sadece çalışan bir yazılım yapmak değil; **Singleton, Factory, Observer, Decorator, State, Strategy ve DAO** gibi endüstri standardı tasarım desenlerini kullanarak **bakımı kolay (maintainable)** ve **geliştirilebilir** bir mimari kurmaktır.

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

Projede **Layered Architecture (Katmanlı Mimari)** kullanılmış ve **GoF (Gang of Four)** tasarım desenleri ile kodun esnekliği artırılmıştır.

### 1. Singleton Pattern 🔒
Sistem genelinde tek bir örneğinin olması gereken sınıflar için kullanıldı.
- **Kullanım Yeri:** `DatabaseConnection` (Veritabanı bağlantısı) ve `NotificationManager` (Bildirim sistemi).
- **Amaç:** Kaynak tüketimini azaltmak ve global erişim sağlamak.

### 2. Factory Pattern 🏭
Nesne oluşturma sürecini soyutlamak için kullanıldı.
- **Kullanım Yeri:** `RoomFactory` sınıfı.
- **Amaç:** İstemci (Client) tarafı `StandardRoom`, `SuiteRoom` gibi alt sınıfları bilmek zorunda kalmadan sadece "Tip" ve "Kapasite" bilgisiyle oda üretebilir.

### 3. Observer Pattern 👀
Bir nesnedeki durum değişikliğinin, ona bağlı diğer nesnelere otomatik bildirilmesi için kullanıldı.
- **Kullanım Yeri:** `NotificationManager` (Subject), `ManagerSMSObserver` ve `DatabaseLoggerObserver` (Observers).
- **Amaç:** Yeni bir rezervasyon yapıldığında (Event), hem log tutulması hem de yöneticiye bildirim gitmesi işlemini tetiklemek.

### 4. Decorator Pattern 🎀
Nesnelere çalışma zamanında dinamik olarak yeni özellikler eklemek için kullanıldı.
- **Kullanım Yeri:** `BreakfastDecorator` sınıfı.
- **Amaç:** Bir rezervasyonun temel maliyetine (`ICost`), temel sınıfı bozmadan "Kahvaltı" gibi ekstra hizmetlerin maliyetini eklemek.

### 5. State Pattern 🚦
Nesnelerin durumlarına göre farklı davranışlar sergilemesini yönetmek için kullanıldı.
- **Kullanım Yeri:** `Room` (`AVAILABLE`, `FULL`, `CLEANING`) ve `Reservation` (`PENDING`, `CHECKED_IN`, `CANCELLED`) durumları.
- **Amaç:** Bir oda "Dolu" iken rezervasyon yapılamaması veya "Temizlikte" iken müşteri alınamaması gibi durum geçişlerini kontrol altına almak.

### 6. Strategy Pattern 🧠
Bir işlemin farklı algoritmalarla yapılabilmesine olanak sağlamak için kullanıldı.
- **Kullanım Yeri:** Fiyat hesaplama ve Müşteri arama algoritmaları.
- **Amaç:** Farklı oda tipleri veya mevsimsel durumlarda fiyat hesaplama mantığının (`ICost` implementasyonları) kolayca değiştirilebilmesini sağlamak.

### 7. DAO (Data Access Object) Pattern 💾
Veritabanı erişim kodlarını iş mantığından ayırmak için kullanıldı.
- **Kullanım Yeri:** `UserDAO`, `RoomDAO`, `ReservationDAO`.
- **Amaç:** Veritabanı sorgularını (SQL) ana kodun içine karıştırmadan temiz bir yapı kurmak.

---

## 🗄 Veritabanı Yapısı (ER Diagram)

Proje ilişkisel veritabanı yapısına sahiptir:
- **Users:** Kullanıcı bilgileri ve rolleri.
- **Rooms:** Oda özellikleri, fiyatları ve durumu.
- **Reservations:** Müşteri ve oda arasındaki bağlantı (N-to-N relation).
- **Logs:** Sistem hareketlerinin kaydı.

<img width="100%" alt="ER Diyagram" src="https://github.com/user-attachments/assets/7778ad2b-82de-4a0e-b0c2-fec5856e4557" />

---

## 📸 Ekran Görüntüleri

### 1. Giriş Ekranı
<img width="496" height="532" alt="giris" src="https://github.com/user-attachments/assets/32c488de-8d67-45be-8105-feba6dc2077d" />

### 2. Personel Yönetim Paneli
<img width="100%" alt="adminPanel" src="https://github.com/user-attachments/assets/5933334b-5c1a-4f02-8d55-908d97fbbe6c" />

### 3. Rezervasyon Oluşturma (Akıllı Sıralama)
<img width="100%" alt="rezervasyonOlusturma" src="https://github.com/user-attachments/assets/8f21ba4b-16b0-415b-96a6-63809135fbe0" />

### 4. Log ve Bildirim Sistemi
<img width="100%" alt="logKayıt" src="https://github.com/user-attachments/assets/abcf48b3-e19b-4ee8-ba4e-178c89593317" />

---

## ⚙️ Kurulum ve Çalıştırma

**1. Projeyi İndirin**
Projeyi bilgisayarınıza klonlayın veya ZIP olarak indirin.

**2. Veritabanını Oluşturun**
MySQL'de `hotel_db` adında bir veritabanı oluşturun ve `sql/database.sql` dosyasını import edin.

**3. Bağlantı Ayarlarını Yapın**
`DatabaseConnection.java` dosyasındaki kullanıcı adı ve şifreyi kendi MySQL bilgilerinizle güncelleyin:

```java
private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
private static final String USER = "root";
private static final String PASSWORD = "sifreniz";



