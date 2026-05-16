package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.model.Mekanik;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MekanikDAO {

    public void insert(Mekanik m) throws SQLException {
        String sql = "INSERT INTO mekanik (nama, telepon, spesialis) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNama());
            ps.setString(2, m.getTelepon());
            ps.setString(3, m.getSpesialis());
            ps.executeUpdate();
        }
    }

    public void update(Mekanik m) throws SQLException {
        String sql = "UPDATE mekanik SET nama=?, telepon=?, spesialis=? WHERE mekanik_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNama());
            ps.setString(2, m.getTelepon());
            ps.setString(3, m.getSpesialis());
            ps.setInt(4, m.getMekanikId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM mekanik WHERE mekanik_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Mekanik> findAll() throws SQLException {
        List<Mekanik> list = new ArrayList<>();
        String sql = "SELECT * FROM mekanik ORDER BY nama";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Mekanik m = new Mekanik();
                m.setMekanikId(rs.getInt("mekanik_id"));
                m.setNama(rs.getString("nama"));
                m.setTelepon(rs.getString("telepon"));
                m.setSpesialis(rs.getString("spesialis"));
                list.add(m);
            }
        }
        return list;
    }

    public Mekanik findById(int id) throws SQLException {
        String sql = "SELECT * FROM mekanik WHERE mekanik_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Mekanik m = new Mekanik();
                    m.setMekanikId(rs.getInt("mekanik_id"));
                    m.setNama(rs.getString("nama"));
                    m.setTelepon(rs.getString("telepon"));
                    m.setSpesialis(rs.getString("spesialis"));
                    return m;
                }
            }
        }
        return null;
    }
}
