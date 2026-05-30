# Garage Management System

Aplikasi manajemen bengkel berbasis Java Swing dan database MySQL. Project ini dikembangkan dengan menggunakan Maven untuk manajemen dependency dan build.

## Prasyarat (Prerequisites)

Sebelum menjalankan aplikasi, pastikan Anda telah menginstal perangkat lunak berikut:

1. **Java Development Kit (JDK) 21** atau yang lebih baru.
2. **Apache Maven** (untuk mengompilasi dan menjalankan aplikasi).
3. **MySQL Server** (bisa menggunakan XAMPP, MAMP, atau instalasi MySQL mandiri).

---

## Langkah Instalasi & Persiapan

### 1. Persiapan Database MySQL

Aplikasi ini membutuhkan database MySQL dengan nama `garage_management`. Ikuti langkah berikut untuk menyiapkannya:

1. Jalankan MySQL Server Anda (misal via XAMPP Control Panel atau MAMP).
2. Buat database baru bernama `garage_management` atau impor langsung file SQL yang disediakan.
3. Impor file skema database dan data uji coba (dummy data) yang berada di root direktori:
   ```bash
   mysql -u root -p < setup_database.sql
   ```
   *Atau gunakan tool GUI seperti phpMyAdmin, DBeaver, atau MySQL Workbench untuk mengimpor file `setup_database.sql`.*

### 2. Konfigurasi Koneksi Database

Koneksi database dikonfigurasi di dalam file Java:
`src/main/java/com/mycompany/garagemanagementsystem/util/DBConnection.java`

Secara default, konfigurasinya adalah sebagai berikut:
* **Host**: `localhost`
* **Port**: `8889` (Port default untuk MAMP pada macOS)
* **Database Name**: `garage_management`
* **Username**: `root`
* **Password**: `root`

> [!IMPORTANT]
> Jika Anda menggunakan XAMPP (yang biasanya menggunakan port `3306` dan password kosong `""` atau `"root"`), silakan buka file `DBConnection.java` dan sesuaikan nilai konstanta berikut sebelum menjalankan aplikasi:
> ```java
> private static final String PORT = "3306"; // ubah ke port MySQL Anda
> private static final String PASS = "";     // ubah sesuai password database Anda
> ```

### 3. Konfigurasi Aplikasi (Opsional)

Anda dapat menyesuaikan nama bengkel, alamat, nomor telepon, dan logo yang ditampilkan di aplikasi melalui file:
`app.properties`

Cukup buka file tersebut dan edit nilai properti yang diinginkan.

---

## Cara Menjalankan Aplikasi

Anda dapat menjalankan aplikasi langsung menggunakan Maven melalui terminal/command prompt.

1. Buka terminal dan masuk ke direktori project.
2. Jalankan perintah berikut:
   ```bash
   mvn compile exec:java
   ```

Aplikasi akan otomatis mengunduh dependency yang dibutuhkan (seperti MySQL Connector, JFreeChart, Apache POI, iText, dll.) lalu meluncurkan antarmuka pengguna (GUI).

---

## Akun Demo untuk Login

Setelah mengimpor database, Anda dapat login menggunakan beberapa akun uji coba berikut:

| Username | Password | Role | Nama Lengkap |
|---|---|---|---|
| `admin` | `admin123` | Admin | Administrator Utama |
| `kasir1` | `kasir123` | Kasir | Kasir Bulan |
| `manager1` | `manager123` | Manager | Manager Surya |

---

## Struktur File Project yang Penting

* `src/main/java` - Berisi source code utama aplikasi Java (Swing UI, logika bisnis, utilitas database).
* `src/main/resources` - Aset aplikasi seperti gambar logo (`logo.png`).
* `pom.xml` - Konfigurasi Maven dan daftar library pihak ketiga (dependencies).
* `app.properties` - Konfigurasi eksternal aplikasi.
* `setup_database.sql` - Skrip database lengkap (skema tabel + data dummy).
* `.gitignore` - Menentukan file/folder mana saja yang harus diabaikan saat upload ke Git.
