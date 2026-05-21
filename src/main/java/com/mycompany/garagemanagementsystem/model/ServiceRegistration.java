package com.mycompany.garagemanagementsystem.model;

import java.util.Date;

public class ServiceRegistration {
    private int registrationId;
    private int vehicleId;
    private int clientId;
    private String keluhan;
    private int mekanikId;
    private String status; // "Registered", "InProgress", "Completed"
    private Date tanggalDaftar;
    private Date tanggalMulai;
    private String catatan;

    // Display fields (from JOINs)
    private String noPolisi;
    private String mekanikNama;

    // Getters and Setters
    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public String getKeluhan() { return keluhan; }
    public void setKeluhan(String keluhan) { this.keluhan = keluhan; }

    public int getMekanikId() { return mekanikId; }
    public void setMekanikId(int mekanikId) { this.mekanikId = mekanikId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getTanggalDaftar() { return tanggalDaftar; }
    public void setTanggalDaftar(Date tanggalDaftar) { this.tanggalDaftar = tanggalDaftar; }

    public Date getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(Date tanggalMulai) { this.tanggalMulai = tanggalMulai; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public String getNoPolisi() { return noPolisi; }
    public void setNoPolisi(String noPolisi) { this.noPolisi = noPolisi; }

    public String getMekanikNama() { return mekanikNama; }
    public void setMekanikNama(String mekanikNama) { this.mekanikNama = mekanikNama; }
}
