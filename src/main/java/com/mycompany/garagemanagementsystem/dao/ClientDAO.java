package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.model.Client;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk tabel 'client' - operasi CRUD (Create, Read, Update, Delete).
 * 
 * Pola yang sama dipakai di semua DAO CRUD:
 * - insert()  = tambah data baru ke database (CREATE)
 * - findAll() = ambil semua data dari database (READ)
 * - update()  = ubah data yang sudah ada (UPDATE)
 * - delete()  = hapus data berdasarkan ID (DELETE)
 * 
 * PreparedStatement dipakai supaya aman dari SQL Injection.
 * try-with-resources memastikan koneksi otomatis ditutup.
 */
public class ClientDAO {

    public void insert(Client c) throws SQLException {
        String sql = "INSERT INTO client (nama, alamat, telepon, email, tanggal_daftar) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNama());
            ps.setString(2, c.getAlamat());
            ps.setString(3, c.getTelepon());
            ps.setString(4, c.getEmail());
            ps.setDate(5, new Date(c.getTanggalDaftar().getTime()));
            ps.executeUpdate();
        }
    }

    public void update(Client c) throws SQLException {
        String sql = "UPDATE client SET nama=?, alamat=?, telepon=?, email=?, tanggal_daftar=? WHERE client_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNama());
            ps.setString(2, c.getAlamat());
            ps.setString(3, c.getTelepon());
            ps.setString(4, c.getEmail());
            ps.setDate(5, new Date(c.getTanggalDaftar().getTime()));
            ps.setInt(6, c.getClientId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM client WHERE client_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Client> findAll() throws SQLException {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM client ORDER BY nama";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Client c = new Client();
                c.setClientId(rs.getInt("client_id"));
                c.setNama(rs.getString("nama"));
                c.setAlamat(rs.getString("alamat"));
                c.setTelepon(rs.getString("telepon"));
                c.setEmail(rs.getString("email"));
                c.setTanggalDaftar(rs.getDate("tanggal_daftar"));
                list.add(c);
            }
        }
        return list;
    }
}

