package com.mycompany.garagemanagementsystem.model;

public class TransactionJasaDetail {

    private int detailId;
    private int transId;
    private String namaJasa;
    private double harga;
    private int qty;
    private double subtotal;

    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }

    public int getTransId() { return transId; }
    public void setTransId(int transId) { this.transId = transId; }

    public String getNamaJasa() { return namaJasa; }
    public void setNamaJasa(String namaJasa) { this.namaJasa = namaJasa; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
