package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.model.Supplier;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public void insert(Supplier s) throws SQLException {
        String sql = "INSERT INTO supplier (nama, alamat, telepon, email) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNama());
            ps.setString(2, s.getAlamat());
            ps.setString(3, s.getTelepon());
            ps.setString(4, s.getEmail());
            ps.executeUpdate();
        }
    }

    public void update(Supplier s) throws SQLException {
        String sql = "UPDATE supplier SET nama=?, alamat=?, telepon=?, email=? WHERE supplier_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNama());
            ps.setString(2, s.getAlamat());
            ps.setString(3, s.getTelepon());
            ps.setString(4, s.getEmail());
            ps.setInt(5, s.getSupplierId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM supplier WHERE supplier_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Supplier> findAll() throws SQLException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier ORDER BY nama";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Supplier s = new Supplier();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setNama(rs.getString("nama"));
                s.setAlamat(rs.getString("alamat"));
                s.setTelepon(rs.getString("telepon"));
                s.setEmail(rs.getString("email"));
                list.add(s);
            }
        }
        return list;
    }
}

