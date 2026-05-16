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
        String sql = "SELECT COUNT(*) FROM sparepart WHERE stok <= 5";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

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
                   + "GROUP BY m.mekanik_id, m.nama ORDER BY total_servis DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("nama") + " (" + rs.getInt("total_servis") + " unit)";
        }
        return "Belum ada data";
    }

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
        String sql = "SELECT c.nama, v.no_polisi, DATEDIFF(CURDATE(), MAX(t.tanggal)) as days_ago "
                   + "FROM service_transaction t "
                   + "JOIN vehicle v ON t.vehicle_id = v.vehicle_id "
                   + "JOIN client c ON t.client_id = c.client_id "
                   + "GROUP BY v.vehicle_id, c.nama, v.no_polisi "
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
                   + "GROUP BY s.sparepart_id, s.nama_sparepart ORDER BY total_qty DESC LIMIT 5";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("nama_sparepart"), rs.getInt("total_qty") + " pcs"});
            }
        }
        return list;
    }

    public java.util.List<Object[]> getTop3StokKritis() throws SQLException {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql = "SELECT nama_sparepart, stok FROM sparepart WHERE stok <= 5 ORDER BY stok ASC LIMIT 3";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("nama_sparepart"), rs.getInt("stok") + " pcs"});
            }
        }
        return list;
    }

    public java.util.List<Object[]> getKinerjaMekanik() throws SQLException {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql = "SELECT m.nama, COUNT(t.trans_id) as total_servis FROM service_transaction t "
                   + "JOIN mekanik m ON t.mekanik_id = m.mekanik_id "
                   + "GROUP BY m.mekanik_id, m.nama ORDER BY total_servis DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("nama"), rs.getInt("total_servis")});
            }
        }
        return list;
    }

    public double getOmzetMingguan() throws SQLException {
        String sql = "SELECT SUM(grand_total) FROM service_transaction WHERE YEARWEEK(tanggal, 1) = YEARWEEK(CURDATE(), 1)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public double getOmzetTahunan() throws SQLException {
        String sql = "SELECT SUM(grand_total) FROM service_transaction WHERE YEAR(tanggal) = YEAR(CURDATE())";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public java.util.List<Object[]> getOmzetPerBulanTahunIni() throws SQLException {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql = "SELECT MONTH(tanggal) as bulan, SUM(grand_total) as total "
                   + "FROM service_transaction "
                   + "WHERE YEAR(tanggal) = YEAR(CURDATE()) "
                   + "GROUP BY MONTH(tanggal) ORDER BY bulan ASC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getInt("bulan"), rs.getDouble("total")});
            }
        }
        return list;
    }

    public java.util.List<Object[]> getOmzetPerTahun() throws SQLException {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql = "SELECT YEAR(tanggal) as tahun, SUM(grand_total) as total "
                   + "FROM service_transaction "
                   + "GROUP BY YEAR(tanggal) ORDER BY tahun ASC LIMIT 5";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("tahun"), rs.getDouble("total")});
            }
        }
        return list;
    }

    public java.util.List<Object[]> getPerbandinganTipeKendaraan() throws SQLException {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql = "SELECT v.tipe_kendaraan, COUNT(t.trans_id) as total FROM service_transaction t "
                   + "JOIN vehicle v ON t.vehicle_id = v.vehicle_id GROUP BY v.tipe_kendaraan";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("tipe_kendaraan"), rs.getInt("total")});
            }
        }
        return list;
    }

    public java.util.List<Object[]> getKategoriServis() throws SQLException {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql = "SELECT "
                   + "SUM(CASE WHEN total_jasa <= 50000 THEN 1 ELSE 0 END) as servis_kecil, "
                   + "SUM(CASE WHEN total_jasa > 50000 AND total_jasa <= 80000 THEN 1 ELSE 0 END) as servis_sedang, "
                   + "SUM(CASE WHEN total_jasa > 80000 THEN 1 ELSE 0 END) as servis_berat "
                   + "FROM service_transaction";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                list.add(new Object[]{"Servis Kecil", rs.getInt("servis_kecil")});
                list.add(new Object[]{"Servis Sedang", rs.getInt("servis_sedang")});
                list.add(new Object[]{"Servis Berat", rs.getInt("servis_berat")});
            }
        }
        return list;
    }

    public int getTransaksiByDateRange(java.util.Date dateFrom, java.util.Date dateTo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM service_transaction WHERE DATE(tanggal) BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(dateFrom.getTime()));
            ps.setDate(2, new java.sql.Date(dateTo.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public double getOmzetByDateRange(java.util.Date dateFrom, java.util.Date dateTo) throws SQLException {
        String sql = "SELECT SUM(grand_total) FROM service_transaction WHERE DATE(tanggal) BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(dateFrom.getTime()));
            ps.setDate(2, new java.sql.Date(dateTo.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0;
    }
}
