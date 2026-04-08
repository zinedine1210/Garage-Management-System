package com.mycompany.garagemanagementsystem.model;

import java.util.Date;

public class Client {

    private int clientId;
    private String nama;
    private String alamat;
    private String telepon;
    private String email;
    private Date tanggalDaftar;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void setTanggalDaftar(Date tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar;
    }

    @Override
    public String toString() {
        return this.nama;
    }
}
