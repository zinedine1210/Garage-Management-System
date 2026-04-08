package com.mycompany.garagemanagementsystem.model;

/**
 * Model Vehicle = representasi data kendaraan milik client.
 * 
 * Setiap field sesuai kolom tabel 'vehicle' di database.
 * clientId = ID pemilik kendaraan (relasi ke tabel client).
 * tipeKendaraan = "Roda 2" atau "Lebih dari Roda 2".
 * toString() menampilkan "B 1234 XYZ - Honda" di JComboBox.
 */
public class Vehicle {

    private int vehicleId;
    private int clientId;
    private String noPolisi;
    private String merk;
    private String tipe;
    private int cc;
    private String tipeKendaraan; // Roda 2 / Lebih dari Roda 2
    private int tahun;
    private String noRangka;
    private String noMesin;

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getNoPolisi() {
        return noPolisi;
    }

    public void setNoPolisi(String noPolisi) {
        this.noPolisi = noPolisi;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public int getCc() {
        return cc;
    }

    public void setCc(int cc) {
        this.cc = cc;
    }

    public String getTipeKendaraan() {
        return tipeKendaraan;
    }

    public void setTipeKendaraan(String tipeKendaraan) {
        this.tipeKendaraan = tipeKendaraan;
    }

    public int getTahun() {
        return tahun;
    }

    public void setTahun(int tahun) {
        this.tahun = tahun;
    }

    public String getNoRangka() {
        return noRangka;
    }

    public void setNoRangka(String noRangka) {
        this.noRangka = noRangka;
    }

    public String getNoMesin() {
        return noMesin;
    }

    public void setNoMesin(String noMesin) {
        this.noMesin = noMesin;
    }

    @Override
    public String toString() {
        return this.noPolisi + " - " + this.merk;
    }
}

