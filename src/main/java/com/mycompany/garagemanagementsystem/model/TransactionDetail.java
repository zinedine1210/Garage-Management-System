package com.mycompany.garagemanagementsystem.model;

/**
 * Model TransactionDetail = 1 baris detail sparepart di dalam transaksi servis.
 * 
 * Contoh: Transaksi #5 pakai Oli Mesin 2 botol @ Rp50.000 = subtotal Rp100.000
 * - transId     = ID transaksi induknya (relasi ke service_transaction)
 * - sparepartId = ID sparepart yang dipakai (relasi ke tabel sparepart)
 * - qty         = jumlah unit yang dipakai
 * - harga       = harga satuan sparepart
 * - subtotal    = qty x harga
 */
public class TransactionDetail {

    private int detailId;
    private int transId;
    private int sparepartId;
    private int qty;
    private double harga;
    private double subtotal;

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

