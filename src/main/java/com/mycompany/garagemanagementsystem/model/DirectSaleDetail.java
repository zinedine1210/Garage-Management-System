package com.mycompany.garagemanagementsystem.model;

public class DirectSaleDetail {
    private int detailId;
    private int penjualanId;
    private int sparepartId;
    private int qty;
    private double harga;
    private double subtotal;

    // Display fields
    private String kodeSparepart;
    private String sparepartNama;

    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }

    public int getPenjualanId() { return penjualanId; }
    public void setPenjualanId(int penjualanId) { this.penjualanId = penjualanId; }

    public int getSparepartId() { return sparepartId; }
    public void setSparepartId(int sparepartId) { this.sparepartId = sparepartId; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public String getKodeSparepart() { return kodeSparepart; }
    public void setKodeSparepart(String kodeSparepart) { this.kodeSparepart = kodeSparepart; }

    public String getSparepartNama() { return sparepartNama; }
    public void setSparepartNama(String sparepartNama) { this.sparepartNama = sparepartNama; }
}
