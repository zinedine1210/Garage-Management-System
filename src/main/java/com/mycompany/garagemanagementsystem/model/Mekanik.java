package com.mycompany.garagemanagementsystem.model;

/**
 * Model Mekanik = representasi data mekanik/teknisi bengkel.
 * 
 * Field sesuai kolom tabel 'mekanik' di database.
 * toString() menampilkan nama mekanik di JComboBox.
 */
public class Mekanik {

    private int mekanikId;
    private String nama;
    private String telepon;
    private String spesialis;

    public int getMekanikId() {
        return mekanikId;
    }

    public void setMekanikId(int mekanikId) {
        this.mekanikId = mekanikId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getSpesialis() {
        return spesialis;
    }

    public void setSpesialis(String spesialis) {
        this.spesialis = spesialis;
    }

    @Override
    public String toString() {
        return this.nama;
    }
}

