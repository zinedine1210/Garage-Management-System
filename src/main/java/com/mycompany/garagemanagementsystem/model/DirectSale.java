package com.mycompany.garagemanagementsystem.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DirectSale {
    private int penjualanId;
    private Date tanggal;
    private String namaPembeli;
    private String teleponPembeli;
    private double grandTotal;
    private double bayar;
    private double kembali;
    private String metodeBayar;
    private String userKasir;
    private String catatan;
    private List<DirectSaleDetail> details = new ArrayList<>();

    public int getPenjualanId() { return penjualanId; }
    public void setPenjualanId(int penjualanId) { this.penjualanId = penjualanId; }

    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }

    public String getNamaPembeli() { return namaPembeli; }
    public void setNamaPembeli(String namaPembeli) { this.namaPembeli = namaPembeli; }

    public String getTeleponPembeli() { return teleponPembeli; }
    public void setTeleponPembeli(String teleponPembeli) { this.teleponPembeli = teleponPembeli; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public double getBayar() { return bayar; }
    public void setBayar(double bayar) { this.bayar = bayar; }

    public double getKembali() { return kembali; }
    public void setKembali(double kembali) { this.kembali = kembali; }

    public String getMetodeBayar() { return metodeBayar; }
    public void setMetodeBayar(String metodeBayar) { this.metodeBayar = metodeBayar; }

    public String getUserKasir() { return userKasir; }
    public void setUserKasir(String userKasir) { this.userKasir = userKasir; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public List<DirectSaleDetail> getDetails() { return details; }
    public void setDetails(List<DirectSaleDetail> details) { this.details = details; }
}
