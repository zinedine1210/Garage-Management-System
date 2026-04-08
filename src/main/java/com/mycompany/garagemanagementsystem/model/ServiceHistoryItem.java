package com.mycompany.garagemanagementsystem.model;

import java.util.Date;

/**
 * Model ServiceHistoryItem = 1 baris riwayat servis kendaraan.
 * 
 * Dipakai di halaman "Riwayat Servis" untuk menampilkan history per No Polisi.
 * Semua field bertipe String/double karena langsung ditampilkan di tabel.
 */
public class ServiceHistoryItem {
    private String tanggal;
    private String keluhan;
    private String mekanik;
    private String spareparts;
    private double totalBiaya;

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    public String getKeluhan() { return keluhan; }
    public void setKeluhan(String keluhan) { this.keluhan = keluhan; }
    public String getMekanik() { return mekanik; }
    public void setMekanik(String mekanik) { this.mekanik = mekanik; }
    public String getSpareparts() { return spareparts; }
    public void setSpareparts(String spareparts) { this.spareparts = spareparts; }
    public double getTotalBiaya() { return totalBiaya; }
    public void setTotalBiaya(double totalBiaya) { this.totalBiaya = totalBiaya; }
}
