package com.mycompany.garagemanagementsystem.model;

import java.util.Date;

public class SparepartPurchase {

    private int purchaseId;
    private Date tanggal;
    private int supplierId;
    private int sparepartId;
    private int qty;
    private double hargaBeli;
    private double totalHarga;
    private String keterangan;

    private String supplierNama;
    private String sparepartNama;
    private String kodeSp;

    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }

    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public int getSparepartId() { return sparepartId; }
    public void setSparepartId(int sparepartId) { this.sparepartId = sparepartId; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }

    public double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(double totalHarga) { this.totalHarga = totalHarga; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public String getSupplierNama() { return supplierNama; }
    public void setSupplierNama(String supplierNama) { this.supplierNama = supplierNama; }

    public String getSparepartNama() { return sparepartNama; }
    public void setSparepartNama(String sparepartNama) { this.sparepartNama = sparepartNama; }

    public String getKodeSp() { return kodeSp; }
    public void setKodeSp(String kodeSp) { this.kodeSp = kodeSp; }
}
