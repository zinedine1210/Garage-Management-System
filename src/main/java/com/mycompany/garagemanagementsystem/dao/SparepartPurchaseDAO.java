package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.model.SparepartPurchase;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SparepartPurchaseDAO {

    public void insert(SparepartPurchase p) throws SQLException {
        String sqlInsert = "INSERT INTO transaksi_pembelian "
                + "(tanggal, supplier_id, sparepart_id, qty, harga_beli, total_harga, keterangan) "
                + "VALUES (?,?,?,?,?,?,?)";
        String sqlUpdateStok = "UPDATE sparepart SET stok = stok + ? WHERE sparepart_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setTimestamp(1, new Timestamp(p.getTanggal().getTime()));
                ps.setInt(2, p.getSupplierId());
                ps.setInt(3, p.getSparepartId());
                ps.setInt(4, p.getQty());
                ps.setDouble(5, p.getHargaBeli());
                ps.setDouble(6, p.getTotalHarga());
                ps.setString(7, p.getKeterangan());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStok)) {
                ps.setInt(1, p.getQty());
                ps.setInt(2, p.getSparepartId());
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

    public List<SparepartPurchase> findAll() throws SQLException {
        List<SparepartPurchase> list = new ArrayList<>();
        String sql = "SELECT p.*, s.nama as supplier_nama, sp.nama_sparepart, sp.kode_sparepart "
                + "FROM transaksi_pembelian p "
                + "LEFT JOIN supplier s ON p.supplier_id = s.supplier_id "
                + "LEFT JOIN sparepart sp ON p.sparepart_id = sp.sparepart_id "
                + "ORDER BY p.tanggal DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                SparepartPurchase p = new SparepartPurchase();
                p.setPurchaseId(rs.getInt("purchase_id"));
                p.setTanggal(rs.getTimestamp("tanggal"));
                p.setSupplierId(rs.getInt("supplier_id"));
                p.setSparepartId(rs.getInt("sparepart_id"));
                p.setQty(rs.getInt("qty"));
                p.setHargaBeli(rs.getDouble("harga_beli"));
                p.setTotalHarga(rs.getDouble("total_harga"));
                p.setKeterangan(rs.getString("keterangan"));
                p.setSupplierNama(rs.getString("supplier_nama"));
                p.setSparepartNama(rs.getString("nama_sparepart"));
                p.setKodeSp(rs.getString("kode_sparepart"));
                list.add(p);
            }
        }
        return list;
    }

    public void delete(int purchaseId) throws SQLException {
        String sql = "DELETE FROM transaksi_pembelian WHERE purchase_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, purchaseId);
            ps.executeUpdate();
        }
    }
}
