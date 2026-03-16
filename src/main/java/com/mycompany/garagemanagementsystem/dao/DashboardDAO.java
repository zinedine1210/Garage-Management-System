package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {

    public int getTransaksiHariIni() throws SQLException {
        String sql = "SELECT COUNT(*) FROM service_transaction WHERE DATE(tanggal) = CURDATE()";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public double getOmzetHariIni() throws SQLException {
        String sql = "SELECT SUM(grand_total) FROM service_transaction WHERE DATE(tanggal) = CURDATE()";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    // 1. STATISTIK UTAMA
    public int getTotalAntrean() throws SQLException {
        String sql = "SELECT COUNT(*) FROM service_transaction WHERE DATE(tanggal) = CURDATE() AND status_servis = 'Menunggu'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getDalamPengerjaan() throws SQLException {
        String sql = "SELECT COUNT(*) FROM service_transaction WHERE DATE(tanggal) = CURDATE() AND status_servis = 'Dikerjakan'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getServisSelesai() throws SQLException {
        String sql = "SELECT COUNT(*) FROM service_transaction WHERE DATE(tanggal) = CURDATE() AND status_servis = 'Selesai Lunas'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getStokKritis() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sparepart WHERE stok <= 5"; // Threshold stok kritis: 5
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // 2. KEUANGAN & PERFORMA
    public double getOmzetBulanIni() throws SQLException {
        String sql = "SELECT SUM(grand_total) FROM service_transaction WHERE MONTH(tanggal) = MONTH(CURDATE()) AND YEAR(tanggal) = YEAR(CURDATE())";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public String getMekanikTerajin() throws SQLException {
        String sql = "SELECT m.nama, COUNT(t.trans_id) as total_servis FROM service_transaction t "
                   + "JOIN mekanik m ON t.mekanik_id = m.mekanik_id "
                   + "WHERE MONTH(t.tanggal) = MONTH(CURDATE()) AND YEAR(t.tanggal) = YEAR(CURDATE()) "
                   + "GROUP BY m.mekanik_id ORDER BY total_servis DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("nama") + " (" + rs.getInt("total_servis") + " unit)";
        }
        return "Belum ada data";
    }

    // 3. MONITORING & NOTIFIKASI
    public java.util.List<Object[]> getTabelAntrean() throws SQLException {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql = "SELECT v.no_polisi, t.status_servis "
                   + "FROM service_transaction t JOIN vehicle v ON t.vehicle_id = v.vehicle_id "
                   + "WHERE DATE(t.tanggal) = CURDATE() AND t.status_servis IN ('Menunggu', 'Dikerjakan') "
                   + "ORDER BY t.tanggal ASC LIMIT 5";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("no_polisi"), rs.getString("status_servis")});
            }
        }
        return list;
    }

    public java.util.List<Object[]> getReminderServis() throws SQLException {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        // Ambil kendaraan yang servis terakhirnya > 60 hari yang lalu
        String sql = "SELECT c.nama, v.no_polisi, DATEDIFF(CURDATE(), MAX(t.tanggal)) as days_ago "
                   + "FROM service_transaction t "
                   + "JOIN vehicle v ON t.vehicle_id = v.vehicle_id "
                   + "JOIN client c ON t.client_id = c.client_id "
                   + "GROUP BY v.vehicle_id "
                   + "HAVING days_ago >= 60 "
                   + "ORDER BY days_ago DESC LIMIT 5";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("nama"), rs.getString("no_polisi"), rs.getInt("days_ago") + " hari"});
            }
        }
        return list;
    }

    public java.util.List<Object[]> getSparepartTerlaris() throws SQLException {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql = "SELECT s.nama_sparepart, SUM(td.qty) as total_qty "
                   + "FROM transaction_detail td "
                   + "JOIN sparepart s ON td.sparepart_id = s.sparepart_id "
                   + "JOIN service_transaction t ON td.trans_id = t.trans_id "
                   + "WHERE MONTH(t.tanggal) = MONTH(CURDATE()) AND YEAR(t.tanggal) = YEAR(CURDATE()) "
                   + "GROUP BY s.sparepart_id ORDER BY total_qty DESC LIMIT 5";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("nama_sparepart"), rs.getInt("total_qty") + " pcs"});
            }
        }
        return list;
    }
}
