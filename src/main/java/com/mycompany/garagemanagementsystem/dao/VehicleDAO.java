package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.model.Vehicle;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public void insert(Vehicle v) throws SQLException {
        String sql = "INSERT INTO vehicle (client_id, no_polisi, merk, tipe, tahun, no_rangka, no_mesin) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getClientId());
            ps.setString(2, v.getNoPolisi());
            ps.setString(3, v.getMerk());
            ps.setString(4, v.getTipe());
            ps.setInt(5, v.getTahun());
            ps.setString(6, v.getNoRangka());
            ps.setString(7, v.getNoMesin());
            ps.executeUpdate();
        }
    }

    public void update(Vehicle v) throws SQLException {
        String sql = "UPDATE vehicle SET client_id=?, no_polisi=?, merk=?, tipe=?, tahun=?, "
                + "no_rangka=?, no_mesin=? WHERE vehicle_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getClientId());
            ps.setString(2, v.getNoPolisi());
            ps.setString(3, v.getMerk());
            ps.setString(4, v.getTipe());
            ps.setInt(5, v.getTahun());
            ps.setString(6, v.getNoRangka());
            ps.setString(7, v.getNoMesin());
            ps.setInt(8, v.getVehicleId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM vehicle WHERE vehicle_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Vehicle> findAll() throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicle ORDER BY no_polisi";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleId(rs.getInt("vehicle_id"));
                v.setClientId(rs.getInt("client_id"));
                v.setNoPolisi(rs.getString("no_polisi"));
                v.setMerk(rs.getString("merk"));
                v.setTipe(rs.getString("tipe"));
                v.setTahun(rs.getInt("tahun"));
                v.setNoRangka(rs.getString("no_rangka"));
                v.setNoMesin(rs.getString("no_mesin"));
                list.add(v);
            }
        }
        return list;
    }
}

