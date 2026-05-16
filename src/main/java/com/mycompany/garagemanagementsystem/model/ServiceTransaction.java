package com.mycompany.garagemanagementsystem.model;

import java.util.Date;
import java.util.List;

public class ServiceTransaction {

    private int transId;
    private Integer registrationId;
    private Date tanggal;
    private int clientId;
    private int vehicleId;
    private int mekanikId;
    private String keluhan;
    private String statusServis;
    private double totalJasa;
    private double totalSparepart;
    private double grandTotal;
    private double bayar;
    private double kembali;
    private String userKasir;
    private List<TransactionDetail> details;

    public int getTransId() {
        return transId;
    }

    public void setTransId(int transId) {
        this.transId = transId;
    }

    public Integer getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getMekanikId() {
        return mekanikId;
    }

    public void setMekanikId(int mekanikId) {
        this.mekanikId = mekanikId;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

    public String getStatusServis() {
        return statusServis;
    }

    public void setStatusServis(String statusServis) {
        this.statusServis = statusServis;
    }

    public double getTotalJasa() {
        return totalJasa;
    }

    public void setTotalJasa(double totalJasa) {
        this.totalJasa = totalJasa;
    }

    public double getTotalSparepart() {
        return totalSparepart;
    }

    public void setTotalSparepart(double totalSparepart) {
        this.totalSparepart = totalSparepart;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public double getBayar() {
        return bayar;
    }

    public void setBayar(double bayar) {
        this.bayar = bayar;
    }

    public double getKembali() {
        return kembali;
    }

    public void setKembali(double kembali) {
        this.kembali = kembali;
    }

    public String getUserKasir() {
        return userKasir;
    }

    public void setUserKasir(String userKasir) {
        this.userKasir = userKasir;
    }

    public List<TransactionDetail> getDetails() {
        return details;
    }

    public void setDetails(List<TransactionDetail> details) {
        this.details = details;
    }

    private String clientNama;
    private String noPolisi;
    private String mekanikNama;

    public String getClientNama() {
        return clientNama;
    }

    public void setClientNama(String clientNama) {
        this.clientNama = clientNama;
    }

    public String getNoPolisi() {
        return noPolisi;
    }

    public void setNoPolisi(String noPolisi) {
        this.noPolisi = noPolisi;
    }

    public String getMekanikNama() {
        return mekanikNama;
    }

    public void setMekanikNama(String mekanikNama) {
        this.mekanikNama = mekanikNama;
    }
}
