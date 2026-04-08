package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.model.User;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO (Data Access Object) untuk tabel 'users'.
 * 
 * DAO = kelas yang bertugas menghubungkan Java dengan database.
 * Semua operasi SQL (SELECT, INSERT, UPDATE, DELETE) ditulis di sini.
 * UserDAO hanya punya 1 method: login() untuk verifikasi username & password.
 */
public class UserDAO {

    /**
     * Proses login: cocokkan username dan password dengan data di database.
     * 
     * @param username = username yang diinput user
     * @param password = password yang diinput user
     * @return object User jika cocok, null jika tidak cocok
     */
    public User login(String username, String password) throws SQLException {
        // Query SQL: cari user yang username DAN password-nya cocok
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        // try-with-resources: koneksi otomatis ditutup setelah selesai
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Isi parameter ? di query dengan nilai dari user
            ps.setString(1, username);
            ps.setString(2, password);

            // Jalankan query dan cek hasilnya
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Jika ditemukan, buat object User dan isi datanya
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setRole(rs.getString("role"));
                    u.setNamaLengkap(rs.getString("nama_lengkap"));
                    return u; // Login berhasil
                }
            }
        }
        return null; // Login gagal (username/password tidak cocok)
    }
}
