package com.mycompany.garagemanagementsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection = kelas untuk menghubungkan aplikasi Java ke database MySQL.
 *
 * Cara kerja:
 * 1. Ketika kode memanggil DBConnection.getConnection(), kelas ini
 *    membuat koneksi ke MySQL menggunakan URL, username, dan password.
 * 2. Koneksi disimpan di variabel static 'connection' supaya bisa dipakai ulang
 *    tanpa perlu buat koneksi baru setiap kali (pola Singleton sederhana).
 *
 * PENTING: Ganti PORT, USER, dan PASS sesuai konfigurasi MySQL kamu.
 * - MAMP default: PORT=8889, USER=root, PASS=root
 * - XAMPP default: PORT=3306, USER=root, PASS="" (kosong)
 */
public class DBConnection {

    // ===== KONFIGURASI DATABASE (sesuaikan dengan MySQL kamu) =====
    private static final String DB_NAME = "garage_management";
    private static final String PORT = "8889";
    private static final String URL = "jdbc:mysql://localhost:" + PORT + "/" + DB_NAME;
    private static final String USER = "root";
    private static final String PASS = "root";

    // Variabel untuk menyimpan koneksi yang aktif
    private static Connection connection;

    /**
     * Mendapatkan koneksi ke database.
     * Jika belum ada koneksi atau sudah tertutup, buat koneksi baru.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Buat koneksi ke database
                connection = DriverManager.getConnection(URL, USER, PASS);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL tidak ditemukan", e);
            }
        }
        return connection;
    }
}

