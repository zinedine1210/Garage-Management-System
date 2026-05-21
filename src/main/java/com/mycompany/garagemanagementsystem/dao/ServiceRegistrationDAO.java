package com.mycompany.garagemanagementsystem.dao;

import com.mycompany.garagemanagementsystem.model.ServiceRegistration;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceRegistrationDAO {

    public void insert(ServiceRegistration sr) throws SQLException {
        String sql = "INSERT INTO service_registration (vehicle_id, client_id, keluhan, mekanik_id, status, tanggal_daftar, tanggal_mulai, catatan) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sr.getVehicleId());
            ps.setInt(2, sr.getClientId());
            ps.setString(3, sr.getKeluhan());
            ps.setInt(4, sr.getMekanikId());
            ps.setString(5, sr.getStatus());
            ps.setTimestamp(6, new java.sql.Timestamp(sr.getTanggalDaftar().getTime()));
            ps.setTimestamp(7, sr.getTanggalMulai() != null ? new java.sql.Timestamp(sr.getTanggalMulai().getTime()) : null);
            ps.setString(8, sr.getCatatan());
            ps.executeUpdate();
        }
    }

    public void update(ServiceRegistration sr) throws SQLException {
        String sql = "UPDATE service_registration SET vehicle_id=?, client_id=?, keluhan=?, mekanik_id=?, status=?, tanggal_mulai=?, catatan=? WHERE registration_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sr.getVehicleId());
            ps.setInt(2, sr.getClientId());
            ps.setString(3, sr.getKeluhan());
            ps.setInt(4, sr.getMekanikId());
            ps.setString(5, sr.getStatus());
            ps.setTimestamp(6, sr.getTanggalMulai() != null ? new java.sql.Timestamp(sr.getTanggalMulai().getTime()) : null);
            ps.setString(7, sr.getCatatan());
            ps.setInt(8, sr.getRegistrationId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM service_registration WHERE registration_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<ServiceRegistration> findAll() throws SQLException {
        List<ServiceRegistration> list = new ArrayList<>();
        String sql = "SELECT * FROM service_registration ORDER BY tanggal_daftar DESC";
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ServiceRegistration sr = new ServiceRegistration();
                sr.setRegistrationId(rs.getInt("registration_id"));
                sr.setVehicleId(rs.getInt("vehicle_id"));
                sr.setClientId(rs.getInt("client_id"));
                sr.setKeluhan(rs.getString("keluhan"));
                sr.setMekanikId(rs.getInt("mekanik_id"));
                sr.setStatus(rs.getString("status"));
                java.sql.Timestamp tsDaftar = rs.getTimestamp("tanggal_daftar");
                if (tsDaftar != null) sr.setTanggalDaftar(new java.util.Date(tsDaftar.getTime()));
                java.sql.Timestamp tsMulai = rs.getTimestamp("tanggal_mulai");
                if (tsMulai != null) sr.setTanggalMulai(new java.util.Date(tsMulai.getTime()));
                sr.setCatatan(rs.getString("catatan"));
                list.add(sr);
            }
        }
        return list;
    }

    public ServiceRegistration findById(int id) throws SQLException {
        String sql = "SELECT * FROM service_registration WHERE registration_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ServiceRegistration sr = new ServiceRegistration();
                    sr.setRegistrationId(rs.getInt("registration_id"));
                    sr.setVehicleId(rs.getInt("vehicle_id"));
                    sr.setClientId(rs.getInt("client_id"));
                    sr.setKeluhan(rs.getString("keluhan"));
                    sr.setMekanikId(rs.getInt("mekanik_id"));
                    sr.setStatus(rs.getString("status"));
                    java.sql.Timestamp tsDaftar = rs.getTimestamp("tanggal_daftar");
                    if (tsDaftar != null) sr.setTanggalDaftar(new java.util.Date(tsDaftar.getTime()));
                    java.sql.Timestamp tsMulai = rs.getTimestamp("tanggal_mulai");
                    if (tsMulai != null) sr.setTanggalMulai(new java.util.Date(tsMulai.getTime()));
                    sr.setCatatan(rs.getString("catatan"));
                    return sr;
                }
            }
        }
        return null;
    }

    public List<ServiceRegistration> findByStatus(String status) throws SQLException {
        List<ServiceRegistration> list = new ArrayList<>();
        String sql = "SELECT * FROM service_registration WHERE status=? ORDER BY tanggal_daftar DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ServiceRegistration sr = new ServiceRegistration();
                    sr.setRegistrationId(rs.getInt("registration_id"));
                    sr.setVehicleId(rs.getInt("vehicle_id"));
                    sr.setClientId(rs.getInt("client_id"));
                    sr.setKeluhan(rs.getString("keluhan"));
                    sr.setMekanikId(rs.getInt("mekanik_id"));
                    sr.setStatus(rs.getString("status"));
                    java.sql.Timestamp tsDaftar = rs.getTimestamp("tanggal_daftar");
                    if (tsDaftar != null) sr.setTanggalDaftar(new java.util.Date(tsDaftar.getTime()));
                    java.sql.Timestamp tsMulai = rs.getTimestamp("tanggal_mulai");
                    if (tsMulai != null) sr.setTanggalMulai(new java.util.Date(tsMulai.getTime()));
                    sr.setCatatan(rs.getString("catatan"));
                    list.add(sr);
                }
            }
        }
        return list;
    }
}
