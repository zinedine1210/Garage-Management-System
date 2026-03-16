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

    public List<ServiceTransaction> findAll() throws SQLException {
        List<ServiceTransaction> list = new java.util.ArrayList<>();
        String sql = "SELECT t.*, c.nama as client_nama, v.no_polisi, m.nama as mekanik_nama "
                + "FROM service_transaction t "
                + "LEFT JOIN client c ON t.client_id = c.client_id "
                + "LEFT JOIN vehicle v ON t.vehicle_id = v.vehicle_id "
                + "LEFT JOIN mekanik m ON t.mekanik_id = m.mekanik_id "
                + "ORDER BY t.tanggal DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while(rs.next()) {
                ServiceTransaction t = new ServiceTransaction();
                t.setTransId(rs.getInt("trans_id"));
                t.setTanggal(rs.getTimestamp("tanggal"));
                t.setClientId(rs.getInt("client_id"));
                t.setVehicleId(rs.getInt("vehicle_id"));
                t.setMekanikId(rs.getInt("mekanik_id"));
                t.setKeluhan(rs.getString("keluhan"));
                t.setStatusServis(rs.getString("status_servis"));
                t.setTotalJasa(rs.getDouble("total_jasa"));
                t.setTotalSparepart(rs.getDouble("total_sparepart"));
                t.setGrandTotal(rs.getDouble("grand_total"));
                t.setBayar(rs.getDouble("bayar"));
                t.setKembali(rs.getDouble("kembali"));
                t.setUserKasir(rs.getString("user_kasir"));
                
                // Set extra view fields
                t.setClientNama(rs.getString("client_nama"));
                t.setNoPolisi(rs.getString("no_polisi"));
                t.setMekanikNama(rs.getString("mekanik_nama"));
                
                list.add(t);
            }
        }
        return list;
    }

    public void delete(int transId) throws SQLException {
        String sql = "DELETE FROM service_transaction WHERE trans_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transId);
            ps.executeUpdate();
        }
    }

    public List<com.mycompany.garagemanagementsystem.model.ServiceHistoryItem> findHistoryByNoPolisi(String noPolisi) throws SQLException {
        List<com.mycompany.garagemanagementsystem.model.ServiceHistoryItem> list = new java.util.ArrayList<>();
        String sql = "SELECT t.tanggal, t.keluhan, t.grand_total, m.nama as nama_mekanik, "
                   + "GROUP_CONCAT(CONCAT(s.nama_sparepart, ' (', td.qty, ')') SEPARATOR ', ') as spareparts "
                   + "FROM service_transaction t "
                   + "JOIN vehicle v ON t.vehicle_id = v.vehicle_id "
                   + "JOIN mekanik m ON t.mekanik_id = m.mekanik_id "
                   + "LEFT JOIN transaction_detail td ON t.trans_id = td.trans_id "
                   + "LEFT JOIN sparepart s ON td.sparepart_id = s.sparepart_id "
                   + "WHERE v.no_polisi LIKE ? "
                   + "GROUP BY t.trans_id "
                   + "ORDER BY t.tanggal DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + noPolisi + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    com.mycompany.garagemanagementsystem.model.ServiceHistoryItem item = new com.mycompany.garagemanagementsystem.model.ServiceHistoryItem();
                    item.setTanggal(rs.getTimestamp("tanggal").toString());
                    item.setKeluhan(rs.getString("keluhan"));
                    item.setMekanik(rs.getString("nama_mekanik"));
                    item.setSpareparts(rs.getString("spareparts") != null ? rs.getString("spareparts") : "-");
                    item.setTotalBiaya(rs.getDouble("grand_total"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    public List<ServiceTransaction> getAntrian() throws SQLException {
        List<ServiceTransaction> list = new java.util.ArrayList<>();
        String sql = "SELECT t.trans_id, t.status_servis, v.no_polisi "
                   + "FROM service_transaction t "
                   + "JOIN vehicle v ON t.vehicle_id = v.vehicle_id "
                   + "WHERE DATE(t.tanggal) = CURDATE() AND t.status_servis IN ('Menunggu', 'Dikerjakan') "
                   + "ORDER BY t.tanggal ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ServiceTransaction t = new ServiceTransaction();
                t.setTransId(rs.getInt("trans_id"));
                t.setStatusServis(rs.getString("status_servis"));
                // Pakai property keluhan sebagai penitipan sementara nopol
                t.setKeluhan(rs.getString("no_polisi")); 
                list.add(t);
            }
        }
        return list;
    }

    public ServiceTransaction findById(int transId) throws SQLException {
        ServiceTransaction t = null;
        String sqlHeader = "SELECT * FROM service_transaction WHERE trans_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlHeader)) {
            ps.setInt(1, transId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t = new ServiceTransaction();
                    t.setTransId(rs.getInt("trans_id"));
                    t.setTanggal(rs.getTimestamp("tanggal"));
                    t.setClientId(rs.getInt("client_id"));
                    t.setVehicleId(rs.getInt("vehicle_id"));
                    t.setMekanikId(rs.getInt("mekanik_id"));
                    t.setKeluhan(rs.getString("keluhan"));
                    t.setStatusServis(rs.getString("status_servis"));
                    t.setTotalJasa(rs.getDouble("total_jasa"));
                    t.setTotalSparepart(rs.getDouble("total_sparepart"));
                    t.setGrandTotal(rs.getDouble("grand_total"));
                    t.setBayar(rs.getDouble("bayar"));
                    t.setKembali(rs.getDouble("kembali"));
                    t.setUserKasir(rs.getString("user_kasir"));
                }
            }
        }
        if (t != null) {
            String sqlDetail = "SELECT * FROM transaction_detail WHERE trans_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sqlDetail)) {
                ps.setInt(1, transId);
                try (ResultSet rs = ps.executeQuery()) {
                    List<TransactionDetail> details = new java.util.ArrayList<>();
                    while (rs.next()) {
                        TransactionDetail d = new TransactionDetail();
                        d.setDetailId(rs.getInt("detail_id"));
                        d.setTransId(rs.getInt("trans_id"));
                        d.setSparepartId(rs.getInt("sparepart_id"));
                        d.setQty(rs.getInt("qty"));
                        d.setHarga(rs.getDouble("harga"));
                        d.setSubtotal(rs.getDouble("subtotal"));
                        details.add(d);
                    }
                    t.setDetails(details);
                }
            }
        }
        return t;
    }

    public void updateWithDetails(ServiceTransaction t) throws SQLException {
        String sqlSelectOldDetails = "SELECT sparepart_id, qty FROM transaction_detail WHERE trans_id=?";
        String sqlDeleteDetails = "DELETE FROM transaction_detail WHERE trans_id=?";
        String sqlUpdateHeader = "UPDATE service_transaction SET "
                + "client_id=?, vehicle_id=?, mekanik_id=?, keluhan=?, status_servis=?, "
                + "total_jasa=?, total_sparepart=?, grand_total=?, bayar=?, kembali=?, user_kasir=? "
                + "WHERE trans_id=?";
        String sqlInsertDetail = "INSERT INTO transaction_detail "
                + "(trans_id, sparepart_id, qty, harga, subtotal) VALUES (?,?,?,?,?)";
        String sqlRevertStok = "UPDATE sparepart SET stok = stok + ? WHERE sparepart_id = ?";
        String sqlReduceStok = "UPDATE sparepart SET stok = stok - ? WHERE sparepart_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Revert Old Stock
            try (PreparedStatement psOld = conn.prepareStatement(sqlSelectOldDetails);
                 PreparedStatement psRevert = conn.prepareStatement(sqlRevertStok)) {
                psOld.setInt(1, t.getTransId());
                try (ResultSet rs = psOld.executeQuery()) {
                    while (rs.next()) {
                        psRevert.setInt(1, rs.getInt("qty"));
                        psRevert.setInt(2, rs.getInt("sparepart_id"));
                        psRevert.addBatch();
                    }
                }
                psRevert.executeBatch();
            }

            // 2. Delete Old Details
            try (PreparedStatement psDel = conn.prepareStatement(sqlDeleteDetails)) {
                psDel.setInt(1, t.getTransId());
                psDel.executeUpdate();
            }

            // 3. Update Header
            try (PreparedStatement psHeader = conn.prepareStatement(sqlUpdateHeader)) {
                psHeader.setInt(1, t.getClientId());
                psHeader.setInt(2, t.getVehicleId());
                psHeader.setInt(3, t.getMekanikId());
                psHeader.setString(4, t.getKeluhan());
                psHeader.setString(5, t.getStatusServis());
                psHeader.setDouble(6, t.getTotalJasa());
                psHeader.setDouble(7, t.getTotalSparepart());
                psHeader.setDouble(8, t.getGrandTotal());
                psHeader.setDouble(9, t.getBayar());
                psHeader.setDouble(10, t.getKembali());
                psHeader.setString(11, t.getUserKasir());
                psHeader.setInt(12, t.getTransId());
                psHeader.executeUpdate();
            }

            // 4. Insert New Details & Reduce Stock
            try (PreparedStatement psDetail = conn.prepareStatement(sqlInsertDetail);
                 PreparedStatement psReduce = conn.prepareStatement(sqlReduceStok)) {
                List<TransactionDetail> details = t.getDetails();
                if (details != null) {
                    for (TransactionDetail d : details) {
                        psDetail.setInt(1, t.getTransId());
                        psDetail.setInt(2, d.getSparepartId());
                        psDetail.setInt(3, d.getQty());
                        psDetail.setDouble(4, d.getHarga());
                        psDetail.setDouble(5, d.getSubtotal());
                        psDetail.addBatch();

                        psReduce.setInt(1, d.getQty());
                        psReduce.setInt(2, d.getSparepartId());
                        psReduce.addBatch();
                    }
                    psDetail.executeBatch();
                    psReduce.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }
}

