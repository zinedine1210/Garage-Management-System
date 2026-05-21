package com.mycompany.garagemanagementsystem.model;

public class TransactionDetail {

    private int detailId;
    private int transId;
    private int sparepartId;
    private int qty;
    private double harga;
    private double subtotal;

    // Display fields
    private String sparepartNama;

    public String getSparepartNama() { return sparepartNama; }
    public void setSparepartNama(String sparepartNama) { this.sparepartNama = sparepartNama; }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getTransId() {
        return transId;
    }

    public void setTransId(int transId) {
        this.transId = transId;
    }

    public int getSparepartId() {
        return sparepartId;
    }

    public void setSparepartId(int sparepartId) {
        this.sparepartId = sparepartId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
