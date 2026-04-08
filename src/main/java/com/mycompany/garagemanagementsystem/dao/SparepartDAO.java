package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.model.Sparepart;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SparepartDAO {

    public void insert(Sparepart s) throws SQLException {
        String sql = "INSERT INTO sparepart (kode_sparepart, nama_sparepart, satuan, stok, "
                + "harga_beli, harga_jual, supplier_id) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getKodeSparepart());
            ps.setString(2, s.getNamaSparepart());
            ps.setString(3, s.getSatuan());
            ps.setInt(4, s.getStok());
            ps.setDouble(5, s.getHargaBeli());
            ps.setDouble(6, s.getHargaJual());
            ps.setInt(7, s.getSupplierId());
            ps.executeUpdate();
        }
    }

    public void update(Sparepart s) throws SQLException {
        String sql = "UPDATE sparepart SET kode_sparepart=?, nama_sparepart=?, satuan=?, stok=?, "
                + "harga_beli=?, harga_jual=?, supplier_id=? WHERE sparepart_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getKodeSparepart());
            ps.setString(2, s.getNamaSparepart());
            ps.setString(3, s.getSatuan());
            ps.setInt(4, s.getStok());
            ps.setDouble(5, s.getHargaBeli());
            ps.setDouble(6, s.getHargaJual());
            ps.setInt(7, s.getSupplierId());
            ps.setInt(8, s.getSparepartId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM sparepart WHERE sparepart_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Sparepart> findAll() throws SQLException {
        List<Sparepart> list = new ArrayList<>();
        String sql = "SELECT * FROM sparepart ORDER BY nama_sparepart";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Sparepart s = new Sparepart();
                s.setSparepartId(rs.getInt("sparepart_id"));
                s.setKodeSparepart(rs.getString("kode_sparepart"));
                s.setNamaSparepart(rs.getString("nama_sparepart"));
                s.setSatuan(rs.getString("satuan"));
                s.setStok(rs.getInt("stok"));
                s.setHargaBeli(rs.getDouble("harga_beli"));
                s.setHargaJual(rs.getDouble("harga_jual"));
                s.setSupplierId(rs.getInt("supplier_id"));
                list.add(s);
            }
        }
        return list;
    }

    public Sparepart findById(int id) throws SQLException {
        String sql = "SELECT * FROM sparepart WHERE sparepart_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sparepart s = new Sparepart();
                    s.setSparepartId(rs.getInt("sparepart_id"));
                    s.setKodeSparepart(rs.getString("kode_sparepart"));
                    s.setNamaSparepart(rs.getString("nama_sparepart"));
                    s.setSatuan(rs.getString("satuan"));
                    s.setStok(rs.getInt("stok"));
                    s.setHargaBeli(rs.getDouble("harga_beli"));
                    s.setHargaJual(rs.getDouble("harga_jual"));
                    s.setSupplierId(rs.getInt("supplier_id"));
                    return s;
                }
            }
        }
        return null;
    }
}
