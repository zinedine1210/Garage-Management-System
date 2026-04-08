package com.mycompany.garagemanagementsystem.model;

/**
 * Model Sparepart = representasi data suku cadang.
 * 
 * Field sesuai kolom tabel 'sparepart' di database.
 * supplierId = ID supplier yang memasok sparepart ini (relasi ke tabel supplier).
 * hargaBeli = harga beli dari supplier, hargaJual = harga jual ke pelanggan.
 * toString() menampilkan nama sparepart di JComboBox.
 */
public class Sparepart {

    private int sparepartId;
    private String kodeSparepart;
    private String namaSparepart;
    private String satuan;
    private int stok;
    private double hargaBeli;
    private double hargaJual;
    private int supplierId;

    public int getSparepartId() {
        return sparepartId;
    }

    public void setSparepartId(int sparepartId) {
        this.sparepartId = sparepartId;
    }

    public String getKodeSparepart() {
        return kodeSparepart;
    }

    public void setKodeSparepart(String kodeSparepart) {
        this.kodeSparepart = kodeSparepart;
    }

    public String getNamaSparepart() {
        return namaSparepart;
    }

    public void setNamaSparepart(String namaSparepart) {
        this.namaSparepart = namaSparepart;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public double getHargaBeli() {
        return hargaBeli;
    }

    public void setHargaBeli(double hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(double hargaJual) {
        this.hargaJual = hargaJual;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    @Override
    public String toString() {
        return this.namaSparepart;
    }
}

