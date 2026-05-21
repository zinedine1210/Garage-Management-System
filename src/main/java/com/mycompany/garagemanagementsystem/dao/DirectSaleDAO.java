package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.model.DirectSale;
import com.mycompany.garagemanagementsystem.model.DirectSaleDetail;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DirectSaleDAO {

    public void insertWithDetails(DirectSale sale) throws SQLException {
        String sqlHeader = "INSERT INTO penjualan_langsung "
                + "(tanggal, nama_pembeli, telepon_pembeli, grand_total, bayar, kembali, metode_bayar, user_kasir, catatan) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";
        String sqlDetail = "INSERT INTO penjualan_langsung_detail "
                + "(penjualan_id, sparepart_id, qty, harga, subtotal) VALUES (?,?,?,?,?)";
        String sqlStok = "UPDATE sparepart SET stok = stok - ? WHERE sparepart_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert header
            int penjualanId;
            try (PreparedStatement ps = conn.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS)) {
                ps.setTimestamp(1, new Timestamp(sale.getTanggal().getTime()));
                ps.setString(2, sale.getNamaPembeli());
                ps.setString(3, sale.getTeleponPembeli());
                ps.setDouble(4, sale.getGrandTotal());
                ps.setDouble(5, sale.getBayar());
                ps.setDouble(6, sale.getKembali());
                ps.setString(7, sale.getMetodeBayar());
                ps.setString(8, sale.getUserKasir());
                ps.setString(9, sale.getCatatan());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    penjualanId = rs.getInt(1);
                }
            }

            // Batch insert details + reduce stock
            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                 PreparedStatement psStok = conn.prepareStatement(sqlStok)) {
                for (DirectSaleDetail d : sale.getDetails()) {
                    psDetail.setInt(1, penjualanId);
                    psDetail.setInt(2, d.getSparepartId());
                    psDetail.setInt(3, d.getQty());
                    psDetail.setDouble(4, d.getHarga());
                    psDetail.setDouble(5, d.getSubtotal());
                    psDetail.addBatch();

                    psStok.setInt(1, d.getQty());
                    psStok.setInt(2, d.getSparepartId());
                    psStok.addBatch();
                }
                psDetail.executeBatch();
                psStok.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public List<DirectSale> findAll() throws SQLException {
        List<DirectSale> list = new ArrayList<>();
        String sql = "SELECT p.*, "
                + "(SELECT COUNT(*) FROM penjualan_langsung_detail d WHERE d.penjualan_id = p.penjualan_id) as total_item, "
                + "(SELECT GROUP_CONCAT(CONCAT(s.nama_sparepart, ' (', d.qty, ')') SEPARATOR ', ') "
                + " FROM penjualan_langsung_detail d "
                + " LEFT JOIN sparepart s ON d.sparepart_id = s.sparepart_id "
                + " WHERE d.penjualan_id = p.penjualan_id) as daftar_item "
                + "FROM penjualan_langsung p ORDER BY p.tanggal DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                DirectSale s = new DirectSale();
                s.setPenjualanId(rs.getInt("penjualan_id"));
                s.setTanggal(rs.getTimestamp("tanggal"));
                s.setNamaPembeli(rs.getString("nama_pembeli"));
                s.setTeleponPembeli(rs.getString("telepon_pembeli"));
                s.setGrandTotal(rs.getDouble("grand_total"));
                s.setBayar(rs.getDouble("bayar"));
                s.setKembali(rs.getDouble("kembali"));
                s.setMetodeBayar(rs.getString("metode_bayar"));
                s.setUserKasir(rs.getString("user_kasir"));
                s.setCatatan(rs.getString("catatan"));
                list.add(s);
            }
        }
        return list;
    }

    public List<DirectSaleDetail> findDetailsById(int penjualanId) throws SQLException {
        List<DirectSaleDetail> list = new ArrayList<>();
        String sql = "SELECT d.*, s.kode_sparepart, s.nama_sparepart "
                + "FROM penjualan_langsung_detail d "
                + "LEFT JOIN sparepart s ON d.sparepart_id = s.sparepart_id "
                + "WHERE d.penjualan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, penjualanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DirectSaleDetail d = new DirectSaleDetail();
                    d.setDetailId(rs.getInt("detail_id"));
                    d.setPenjualanId(rs.getInt("penjualan_id"));
                    d.setSparepartId(rs.getInt("sparepart_id"));
                    d.setQty(rs.getInt("qty"));
                    d.setHarga(rs.getDouble("harga"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    d.setKodeSparepart(rs.getString("kode_sparepart"));
                    d.setSparepartNama(rs.getString("nama_sparepart"));
                    list.add(d);
                }
            }
        }
        return list;
    }

    public void delete(int penjualanId) throws SQLException {
        String sqlGetDetails = "SELECT sparepart_id, qty FROM penjualan_langsung_detail WHERE penjualan_id = ?";
        String sqlRestoreStok = "UPDATE sparepart SET stok = stok + ? WHERE sparepart_id = ?";
        String sqlDelete = "DELETE FROM penjualan_langsung WHERE penjualan_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Restore stock from details
            try (PreparedStatement psGet = conn.prepareStatement(sqlGetDetails)) {
                psGet.setInt(1, penjualanId);
                try (ResultSet rs = psGet.executeQuery();
                     PreparedStatement psRestore = conn.prepareStatement(sqlRestoreStok)) {
                    while (rs.next()) {
                        psRestore.setInt(1, rs.getInt("qty"));
                        psRestore.setInt(2, rs.getInt("sparepart_id"));
                        psRestore.addBatch();
                    }
                    psRestore.executeBatch();
                }
            }

            // Delete header (details cascade)
            try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                ps.setInt(1, penjualanId);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }
}
