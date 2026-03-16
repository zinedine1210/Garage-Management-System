package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {

    public int getTransaksiHariIni() throws SQLException {
        String sql = "SELECT COUNT(*) FROM service_transaction WHERE DATE(tanggal) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public double getLabaHariIni() throws SQLException {
        String sql = "SELECT SUM(grand_total) FROM service_transaction WHERE DATE(tanggal) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public int getTotalPelanggan() throws SQLException {
        String sql = "SELECT COUNT(*) FROM client";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
    
    public int getSparepartTerjualHariIni() throws SQLException {
        String sql = "SELECT SUM(td.qty) FROM transaction_detail td "
                   + "JOIN service_transaction st ON td.trans_id = st.trans_id "
                   + "WHERE DATE(st.tanggal) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}
