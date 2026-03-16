package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import com.mycompany.garagemanagementsystem.model.TransactionDetail;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

public class ServiceTransactionDAO {

    public int insertWithDetails(ServiceTransaction t) throws SQLException {
        String sqlHeader = "INSERT INTO service_transaction "
                + "(tanggal, client_id, vehicle_id, mekanik_id, keluhan, status_servis, "
                + " total_jasa, total_sparepart, grand_total, bayar, kembali, user_kasir) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        String sqlDetail = "INSERT INTO transaction_detail "
                + "(trans_id, sparepart_id, qty, harga, subtotal) VALUES (?,?,?,?,?)";
        String sqlUpdateStok = "UPDATE sparepart SET stok = stok - ? WHERE sparepart_id = ?";

        Connection conn = null;
        PreparedStatement psHeader = null;
        PreparedStatement psDetail = null;
        PreparedStatement psStok = null;
        ResultSet rsKeys = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            psHeader = conn.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS);
            psHeader.setTimestamp(1, new Timestamp(t.getTanggal().getTime()));
            psHeader.setInt(2, t.getClientId());
            psHeader.setInt(3, t.getVehicleId());
            psHeader.setInt(4, t.getMekanikId());
            psHeader.setString(5, t.getKeluhan());
            psHeader.setString(6, t.getStatusServis());
            psHeader.setDouble(7, t.getTotalJasa());
            psHeader.setDouble(8, t.getTotalSparepart());
            psHeader.setDouble(9, t.getGrandTotal());
            psHeader.setDouble(10, t.getBayar());
            psHeader.setDouble(11, t.getKembali());
            psHeader.setString(12, t.getUserKasir());
            psHeader.executeUpdate();

            rsKeys = psHeader.getGeneratedKeys();
            int transId = 0;
            if (rsKeys.next()) {
                transId = rsKeys.getInt(1);
            } else {
                conn.rollback();
                throw new SQLException("Gagal mendapatkan ID transaksi");
            }

            psDetail = conn.prepareStatement(sqlDetail);
            psStok = conn.prepareStatement(sqlUpdateStok);

            List<TransactionDetail> details = t.getDetails();
            if (details != null) {
                for (TransactionDetail d : details) {
                    psDetail.setInt(1, transId);
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
            return transId;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (rsKeys != null) {
                rsKeys.close();
            }
            if (psHeader != null) {
                psHeader.close();
            }
            if (psDetail != null) {
                psDetail.close();
            }
            if (psStok != null) {
                psStok.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }

    public void updateStatusPembayaran(int transId, double bayar, double kembali) throws SQLException {
        String sql = "UPDATE service_transaction "
                + "SET bayar=?, kembali=?, status_servis='Lunas' WHERE trans_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, bayar);
            ps.setDouble(2, kembali);
            ps.setInt(3, transId);
            ps.executeUpdate();
        }
    }

    public void updateStatusServis(int transId, String status) throws SQLException {
        String sql = "UPDATE service_transaction SET status_servis=? WHERE trans_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, transId);
            ps.executeUpdate();
        }
    }
}

