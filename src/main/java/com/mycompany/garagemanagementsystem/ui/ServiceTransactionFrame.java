package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ClientDAO;
import com.mycompany.garagemanagementsystem.dao.MekanikDAO;
import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.dao.SparepartDAO;
import com.mycompany.garagemanagementsystem.dao.VehicleDAO;
import com.mycompany.garagemanagementsystem.model.Client;
import com.mycompany.garagemanagementsystem.model.Mekanik;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import com.mycompany.garagemanagementsystem.model.Sparepart;
import com.mycompany.garagemanagementsystem.model.TransactionDetail;
import com.mycompany.garagemanagementsystem.model.Vehicle;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * ServiceTransactionFrame = Dialog untuk input/edit transaksi servis.
 *
 * Tampilan:
 * ┌─── HEADER (NORTH) ──────────────────┐
 * │ Client:    [ComboBox] [+New Client]  │
 * │ Vehicle:   [ComboBox]                │
 * │ Mekanik:   [ComboBox]                │
 * │ Keluhan:   [TextArea]                │
 * │ Status:    [ComboBox]                │
 * ├─── DETAIL SPAREPART (CENTER) ───────┤────── TOTAL (EAST) ──┤
 * │ Tabel: ID|Nama|Qty|Harga|Subtotal    │ Total Jasa:    [___] │
 * │                                      │ Total Sparepart:[___]│
 * │                                      │ Grand Total:   [___] │
 * │                                      │ Bayar:         [___] │
 * │                                      │ Kembali:       [___] │
 * ├─── TOMBOL (SOUTH) ──────────────────┤──────────────────────┤
 * │ [Tambah Sparepart] [Hapus Detail] [Hitung] [Simpan] [Bayar]│
 * └─────────────────────────────────────┘──────────────────────┘
 *
 * Fitur khusus:
 * - Pilih Client → otomatis filter Vehicle milik client tersebut
 * - Pilih Vehicle → otomatis select Client pemiliknya
 * - isFiltering flag mencegah infinite loop saat ComboBox saling mempengaruhi
 * - editTransId > 0 berarti mode EDIT (bukan transaksi baru)
 */
public class ServiceTransactionFrame extends javax.swing.JDialog {

    // ===== DAO untuk akses database =====
    private final ClientDAO clientDAO = new ClientDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MekanikDAO mekanikDAO = new MekanikDAO();
    private final SparepartDAO sparepartDAO = new SparepartDAO();
    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();

    // Model tabel untuk detail sparepart
    private DefaultTableModel detailModel;

    // ID transaksi yang sedang diedit (-1 = transaksi baru)
    private int editTransId = -1;

    // Flag untuk mencegah infinite loop saat ComboBox saling mengubah
    private boolean isFiltering = false;

    // Data dari database untuk mengisi ComboBox
    private List<Client> clientList;
    private List<Vehicle> vehicleList;
    private List<Mekanik> mekanikList;
    private List<Sparepart> sparepartList;

    /** Constructor untuk TRANSAKSI BARU */
    public ServiceTransactionFrame(Frame owner) {
        super(owner, true); // true = modal (blok window induk)
        initComponents();
        myInit();
    }

    /** Constructor untuk EDIT TRANSAKSI yang sudah ada */
    public ServiceTransactionFrame(Frame owner, int transId) {
        super(owner, true);
        initComponents();
        myInit();
        this.editTransId = transId;
        loadTransactionInfo(transId); // Isi form dengan data transaksi yang ada
    }

    private void myInit() {
        // Setup tabel detail sparepart
        detailModel = new DefaultTableModel(
                new Object[]{"Sparepart ID", "Nama Sparepart", "Qty", "Harga", "Subtotal"}, 0);
        tblDetail.setModel(detailModel);

        // Muat data ke semua ComboBox
        loadComboBoxData();

        // Event: pilih client → filter vehicle, pilih vehicle → autofill client
        cbClient.addActionListener(e -> {
            if (!isFiltering) filterVehicleByClient();
        });
        cbVehicle.addActionListener(e -> {
            if (!isFiltering) autofillClientByVehicle();
        });
    }

    /** Muat semua data (client, vehicle, mekanik, sparepart) ke ComboBox. */
    private void loadComboBoxData() {
        try {
            isFiltering = true; // Cegah event listener aktif saat mengisi ComboBox

            clientList = clientDAO.findAll();
            vehicleList = vehicleDAO.findAll();
            mekanikList = mekanikDAO.findAll();
            sparepartList = sparepartDAO.findAll();

            // Isi ComboBox Client
            cbClient.removeAllItems();
            cbClient.addItem("-- Pilih Client --");
            for (Client c : clientList) {
                cbClient.addItem(c.getClientId() + " - " + c.getNama());
            }

            // Isi ComboBox Vehicle
            cbVehicle.removeAllItems();
            cbVehicle.addItem("-- Pilih Kendaraan --");
            for (Vehicle v : vehicleList) {
                cbVehicle.addItem(v.getVehicleId() + " - " + v.getNoPolisi() + " - " + v.getMerk());
            }

            // Isi ComboBox Mekanik
            cbMekanik.removeAllItems();
            cbMekanik.addItem("-- Pilih Mekanik --");
            for (Mekanik m : mekanikList) {
                cbMekanik.addItem(m.getMekanikId() + " - " + m.getNama());
            }

            isFiltering = false;
        } catch (SQLException ex) {
            isFiltering = false;
            JOptionPane.showMessageDialog(this, "Error load combo: " + ex.getMessage());
        }
    }

    /**
     * Ketika user memilih Client, filter ComboBox Vehicle supaya
     * hanya menampilkan kendaraan milik client tersebut.
     */
    private void filterVehicleByClient() {
        int idx = cbClient.getSelectedIndex();
        if (idx <= 0) return; // "-- Pilih Client --" dipilih

        isFiltering = true;
        int clientId = clientList.get(idx - 1).getClientId();

        cbVehicle.removeAllItems();
        cbVehicle.addItem("-- Pilih Kendaraan --");
        for (Vehicle v : vehicleList) {
            if (v.getClientId() == clientId) {
                cbVehicle.addItem(v.getVehicleId() + " - " + v.getNoPolisi() + " - " + v.getMerk());
            }
        }
        isFiltering = false;
    }

    /**
     * Ketika user memilih Vehicle, otomatis pilih Client pemiliknya di ComboBox.
     */
    private void autofillClientByVehicle() {
        int idx = cbVehicle.getSelectedIndex();
        if (idx <= 0) return;

        // Parse vehicleId dari teks ComboBox (format: "5 - B1234XYZ - Honda")
        String sel = cbVehicle.getSelectedItem().toString();
        int vId = Integer.parseInt(sel.split(" - ")[0].trim());

        // Cari vehicle → ambil clientId → pilih di ComboBox
        for (Vehicle v : vehicleList) {
            if (v.getVehicleId() == vId) {
                isFiltering = true;
                for (int i = 0; i < clientList.size(); i++) {
                    if (clientList.get(i).getClientId() == v.getClientId()) {
                        cbClient.setSelectedIndex(i + 1);
                        break;
                    }
                }
                isFiltering = false;
                break;
            }
        }
    }

    /**
     * Muat data transaksi yang sudah ada ke form (untuk mode EDIT).
     * Langkah:
     *   1. Ambil data transaksi dari DB berdasarkan transId
     *   2. Pilih client, vehicle, mekanik yang sesuai di ComboBox
     *   3. Isi keluhan, status, total jasa, bayar
     *   4. Isi tabel detail sparepart
     *   5. Hitung ulang total
     */
    private void loadTransactionInfo(int transId) {
        try {
            ServiceTransaction t = transDAO.findById(transId);
            if (t == null) return;

            isFiltering = true;

            // --- Pilih Client yang sesuai di ComboBox ---
            for (int i = 0; i < clientList.size(); i++) {
                if (clientList.get(i).getClientId() == t.getClientId()) {
                    cbClient.setSelectedIndex(i + 1);
                    break;
                }
            }

            // --- Filter & pilih Vehicle milik client tersebut ---
            cbVehicle.removeAllItems();
            cbVehicle.addItem("-- Pilih Kendaraan --");
            for (Vehicle v : vehicleList) {
                if (v.getClientId() == t.getClientId()) {
                    cbVehicle.addItem(v.getVehicleId() + " - " + v.getNoPolisi() + " - " + v.getMerk());
                }
            }
            for (int i = 0; i < cbVehicle.getItemCount(); i++) {
                if (cbVehicle.getItemAt(i).toString().startsWith(t.getVehicleId() + " - ")) {
                    cbVehicle.setSelectedIndex(i);
                    break;
                }
            }

            // --- Pilih Mekanik ---
            for (int i = 0; i < mekanikList.size(); i++) {
                if (mekanikList.get(i).getMekanikId() == t.getMekanikId()) {
                    cbMekanik.setSelectedIndex(i + 1);
                    break;
                }
            }

            isFiltering = false;

            // --- Isi field text ---
            txtKeluhan.setText(t.getKeluhan());
            for (int i = 0; i < cbStatusServis.getItemCount(); i++) {
                if (cbStatusServis.getItemAt(i).toString().equals(t.getStatusServis())) {
                    cbStatusServis.setSelectedIndex(i);
                    break;
                }
            }
            txtTotalJasa.setText(String.valueOf(t.getTotalJasa()));
            txtBayar.setText(String.valueOf(t.getBayar()));

            // --- Isi tabel detail sparepart ---
            detailModel.setRowCount(0);
            if (t.getDetails() != null) {
                for (TransactionDetail d : t.getDetails()) {
                    Sparepart sp = sparepartDAO.findById(d.getSparepartId());
                    String nama = (sp != null) ? sp.getNamaSparepart() : "?";
                    detailModel.addRow(new Object[]{
                        d.getSparepartId(), nama, d.getQty(), d.getHarga(), d.getSubtotal()
                    });
                }
            }

            hitungTotal();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load transaksi: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new javax.swing.JPanel();
        lblClient = new javax.swing.JLabel();
        cbClient = new javax.swing.JComboBox();
        btnAddClient = new javax.swing.JButton();
        lblVehicle = new javax.swing.JLabel();
        cbVehicle = new javax.swing.JComboBox();
        lblSpacer1 = new javax.swing.JLabel();
        lblMekanik = new javax.swing.JLabel();
        cbMekanik = new javax.swing.JComboBox();
        lblSpacer2 = new javax.swing.JLabel();
        lblKeluhan = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtKeluhan = new javax.swing.JTextArea();
        lblSpacer3 = new javax.swing.JLabel();
        lblStatusAntrian = new javax.swing.JLabel();
        cbStatusServis = new javax.swing.JComboBox();
        lblSpacer4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        totalPanel = new javax.swing.JPanel();
        lblTotalJasa = new javax.swing.JLabel();
        txtTotalJasa = new javax.swing.JTextField();
        lblTotalSparepart = new javax.swing.JLabel();
        txtTotalSparepart = new javax.swing.JTextField();
        lblGrandTotal = new javax.swing.JLabel();
        txtGrandTotal = new javax.swing.JTextField();
        lblBayar = new javax.swing.JLabel();
        txtBayar = new javax.swing.JTextField();
        lblKembali = new javax.swing.JLabel();
        txtKembali = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        btnTambahDetail = new javax.swing.JButton();
        btnHapusDetail = new javax.swing.JButton();
        btnHitungTotal = new javax.swing.JButton();
        btnSimpanTrans = new javax.swing.JButton();
        btnBayar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Transaksi Servis");
        setModal(true);
        getContentPane().setLayout(new java.awt.BorderLayout());

        headerPanel.setLayout(new java.awt.GridLayout(5, 3, 5, 5));
        lblClient.setText("Client:"); headerPanel.add(lblClient);
        headerPanel.add(cbClient);
        btnAddClient.setText("+ New Client");
        btnAddClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnAddClientActionPerformed(evt); }
        });
        headerPanel.add(btnAddClient);
        lblVehicle.setText("Vehicle:"); headerPanel.add(lblVehicle);
        headerPanel.add(cbVehicle);
        lblSpacer1.setText(""); headerPanel.add(lblSpacer1);
        lblMekanik.setText("Mekanik:"); headerPanel.add(lblMekanik);
        headerPanel.add(cbMekanik);
        lblSpacer2.setText(""); headerPanel.add(lblSpacer2);
        lblKeluhan.setText("Keluhan:"); headerPanel.add(lblKeluhan);
        txtKeluhan.setColumns(30); txtKeluhan.setRows(3);
        jScrollPane2.setViewportView(txtKeluhan);
        headerPanel.add(jScrollPane2);
        lblSpacer3.setText(""); headerPanel.add(lblSpacer3);
        lblStatusAntrian.setText("Status Antrian:"); headerPanel.add(lblStatusAntrian);
        cbStatusServis.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Menunggu", "Dikerjakan", "Selesai Lunas" }));
        headerPanel.add(cbStatusServis);
        lblSpacer4.setText(""); headerPanel.add(lblSpacer4);
        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        jScrollPane1.setViewportView(tblDetail);
        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        totalPanel.setLayout(new java.awt.GridLayout(5, 2));
        lblTotalJasa.setText("Total Jasa:"); totalPanel.add(lblTotalJasa);
        txtTotalJasa.setText("0"); txtTotalJasa.setColumns(10); totalPanel.add(txtTotalJasa);
        lblTotalSparepart.setText("Total Sparepart:"); totalPanel.add(lblTotalSparepart);
        txtTotalSparepart.setText("0"); txtTotalSparepart.setColumns(10); txtTotalSparepart.setEditable(false); totalPanel.add(txtTotalSparepart);
        lblGrandTotal.setText("Grand Total:"); totalPanel.add(lblGrandTotal);
        txtGrandTotal.setText("0"); txtGrandTotal.setColumns(10); txtGrandTotal.setEditable(false); totalPanel.add(txtGrandTotal);
        lblBayar.setText("Bayar:"); totalPanel.add(lblBayar);
        txtBayar.setText("0"); txtBayar.setColumns(10); totalPanel.add(txtBayar);
        lblKembali.setText("Kembali:"); totalPanel.add(lblKembali);
        txtKembali.setText("0"); txtKembali.setColumns(10); txtKembali.setEditable(false); totalPanel.add(txtKembali);
        getContentPane().add(totalPanel, java.awt.BorderLayout.EAST);

        buttonPanel.setLayout(new java.awt.FlowLayout());
        btnTambahDetail.setText("Tambah Sparepart");
        btnTambahDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnTambahDetailActionPerformed(evt); }
        });
        buttonPanel.add(btnTambahDetail);
        btnHapusDetail.setText("Hapus Detail");
        btnHapusDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnHapusDetailActionPerformed(evt); }
        });
        buttonPanel.add(btnHapusDetail);
        btnHitungTotal.setText("Hitung Total");
        btnHitungTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnHitungTotalActionPerformed(evt); }
        });
        buttonPanel.add(btnHitungTotal);
        btnSimpanTrans.setText("Simpan Transaksi");
        btnSimpanTrans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnSimpanTransActionPerformed(evt); }
        });
        buttonPanel.add(btnSimpanTrans);
        btnBayar.setText("Bayar");
        btnBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnBayarActionPerformed(evt); }
        });
        buttonPanel.add(btnBayar);
        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        setSize(900, 600);
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddClientActionPerformed
        JOptionPane.showMessageDialog(this, "Silakan tambahkan client baru lewat menu Data Client terlebih dahulu.");
    }//GEN-LAST:event_btnAddClientActionPerformed

    private void btnTambahDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahDetailActionPerformed
        addDetailRow();
    }//GEN-LAST:event_btnTambahDetailActionPerformed

    private void btnHapusDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusDetailActionPerformed
        int row = tblDetail.getSelectedRow();
        if (row >= 0) { detailModel.removeRow(row); hitungTotal(); }
        else JOptionPane.showMessageDialog(this, "Pilih baris detail yang akan dihapus.");
    }//GEN-LAST:event_btnHapusDetailActionPerformed

    private void btnHitungTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHitungTotalActionPerformed
        hitungTotal();
    }//GEN-LAST:event_btnHitungTotalActionPerformed

    private void btnSimpanTransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanTransActionPerformed
        simpanTransaksi();
    }//GEN-LAST:event_btnSimpanTransActionPerformed

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        prosesBayar();
    }//GEN-LAST:event_btnBayarActionPerformed

    /**
     * Tampilkan dialog untuk memilih sparepart, masukkan qty, lalu tambahkan ke tabel detail.
     * Langkah:
     *   1. Tampilkan dropdown pilihan sparepart (JOptionPane.showInputDialog)
     *   2. Minta user input qty
     *   3. Hitung subtotal = harga × qty
     *   4. Tambahkan baris baru ke tabel detail
     *   5. Hitung ulang total
     */
    private void addDetailRow() {
        // Cek apakah ada sparepart yang tersedia
        if (sparepartList == null || sparepartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data sparepart.");
            return;
        }

        // Buat array pilihan untuk dialog dropdown
        String[] options = new String[sparepartList.size()];
        for (int i = 0; i < sparepartList.size(); i++) {
            Sparepart s = sparepartList.get(i);
            options[i] = s.getSparepartId() + " - " + s.getNamaSparepart()
                       + " (Stok:" + s.getStok() + ", Harga:" + s.getHargaJual() + ")";
        }

        // Tampilkan dialog pilih sparepart
        String choice = (String) JOptionPane.showInputDialog(
            this, "Pilih sparepart:", "Tambah Sparepart",
            JOptionPane.PLAIN_MESSAGE, null, options, options[0]
        );
        if (choice == null) return; // User klik Cancel

        // Parse ID sparepart dari pilihan (format: "5 - Oli Mesin (Stok:10, Harga:50000)")
        int spId = Integer.parseInt(choice.split(" - ")[0].trim());

        // Minta input qty
        String qtyStr = JOptionPane.showInputDialog(this, "Jumlah qty:");
        if (qtyStr == null || qtyStr.trim().isEmpty()) return;
        int qty = Integer.parseInt(qtyStr.trim());

        // Cari sparepart yang dipilih, hitung subtotal, tambahkan ke tabel
        for (Sparepart s : sparepartList) {
            if (s.getSparepartId() == spId) {
                double harga = s.getHargaJual();
                double subtotal = harga * qty;
                detailModel.addRow(new Object[]{
                    spId, s.getNamaSparepart(), qty, harga, subtotal
                });
                break;
            }
        }

        hitungTotal();
    }

    /**
     * Hitung total sparepart dari tabel detail, lalu hitung grand total dan kembalian.
     *
     * Rumus:
     *   totalSparepart = jumlah semua subtotal di tabel detail
     *   grandTotal     = totalJasa + totalSparepart
     *   kembali        = bayar - grandTotal
     */
    private void hitungTotal() {
        // Hitung total sparepart dari kolom subtotal (kolom index 4)
        double totalSparepart = 0;
        for (int i = 0; i < detailModel.getRowCount(); i++) {
            totalSparepart += Double.parseDouble(detailModel.getValueAt(i, 4).toString());
        }

        // Ambil total jasa dari text field
        double totalJasa = 0;
        try {
            totalJasa = Double.parseDouble(txtTotalJasa.getText().trim());
        } catch (NumberFormatException ignored) {
        }

        // Hitung grand total
        double grandTotal = totalJasa + totalSparepart;
        txtTotalSparepart.setText(String.valueOf(totalSparepart));
        txtGrandTotal.setText(String.valueOf(grandTotal));

        // Hitung kembalian
        double bayar = 0;
        try {
            bayar = Double.parseDouble(txtBayar.getText().trim());
        } catch (NumberFormatException ignored) {
        }
        double kembali = bayar - grandTotal;
        txtKembali.setText(String.valueOf(kembali));
    }

    /**
     * Simpan transaksi ke database.
     * Jika editTransId > 0, berarti UPDATE transaksi yang sudah ada.
     * Jika editTransId == 0, berarti INSERT transaksi baru.
     *
     * Langkah:
     *   1. Validasi: client, vehicle, mekanik harus dipilih
     *   2. Ambil semua data dari form
     *   3. Ambil detail sparepart dari tabel
     *   4. Simpan ke DB (insert atau update)
     */
    private void simpanTransaksi() {
        try {
            // === VALIDASI: pastikan semua ComboBox sudah dipilih ===
            if (cbClient.getSelectedIndex() <= 0
             || cbVehicle.getSelectedIndex() <= 0
             || cbMekanik.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, "Pilih Client, Vehicle, dan Mekanik.");
                return;
            }

            // === AMBIL DATA DARI COMBOBOX ===
            int clientId = clientList.get(cbClient.getSelectedIndex() - 1).getClientId();

            String vSel = cbVehicle.getSelectedItem().toString();
            int vehicleId = Integer.parseInt(vSel.split(" - ")[0].trim());

            String mSel = cbMekanik.getSelectedItem().toString();
            int mekanikId = Integer.parseInt(mSel.split(" - ")[0].trim());

            // === BUAT OBJEK TRANSAKSI ===
            ServiceTransaction t = new ServiceTransaction();
            t.setTanggal(new Date());
            t.setClientId(clientId);
            t.setVehicleId(vehicleId);
            t.setMekanikId(mekanikId);
            t.setKeluhan(txtKeluhan.getText().trim());
            t.setStatusServis(cbStatusServis.getSelectedItem().toString());
            t.setTotalJasa(Double.parseDouble(txtTotalJasa.getText().trim()));

            double totalSp = 0;
            try {
                totalSp = Double.parseDouble(txtTotalSparepart.getText().trim());
            } catch (NumberFormatException ignored) {
            }
            t.setTotalSparepart(totalSp);
            t.setGrandTotal(t.getTotalJasa() + totalSp);

            double bayar = 0;
            try {
                bayar = Double.parseDouble(txtBayar.getText().trim());
            } catch (NumberFormatException ignored) {
            }
            t.setBayar(bayar);
            t.setKembali(bayar - t.getGrandTotal());
            t.setUserKasir("admin");

            // === AMBIL DETAIL SPAREPART DARI TABEL ===
            List<TransactionDetail> details = new ArrayList<>();
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                TransactionDetail d = new TransactionDetail();
                d.setSparepartId(Integer.parseInt(detailModel.getValueAt(i, 0).toString()));
                d.setQty(Integer.parseInt(detailModel.getValueAt(i, 2).toString()));
                d.setHarga(Double.parseDouble(detailModel.getValueAt(i, 3).toString()));
                d.setSubtotal(Double.parseDouble(detailModel.getValueAt(i, 4).toString()));
                details.add(d);
            }
            t.setDetails(details);

            // === SIMPAN KE DATABASE ===
            if (editTransId > 0) {
                // Mode EDIT → update transaksi yang sudah ada
                t.setTransId(editTransId);
                transDAO.updateWithDetails(t);
                JOptionPane.showMessageDialog(this, "Transaksi berhasil diupdate!");
            } else {
                // Mode BARU → insert transaksi baru
                int newId = transDAO.insertWithDetails(t);
                editTransId = newId;
                JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan! ID: " + newId);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error simpan: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format angka tidak valid: " + ex.getMessage());
        }
    }

    /**
     * Proses pembayaran:
     *   1. Pastikan transaksi sudah disimpan (editTransId > 0)
     *   2. Cek apakah bayar >= grandTotal
     *   3. Hitung kembalian
     *   4. Update status ke "Selesai Lunas" di database
     */
    private void prosesBayar() {
        if (editTransId <= 0) {
            JOptionPane.showMessageDialog(this, "Simpan transaksi terlebih dahulu.");
            return;
        }
        try {
            hitungTotal(); // Pastikan total sudah dihitung ulang

            double grandTotal = Double.parseDouble(txtGrandTotal.getText().trim());
            double bayar = Double.parseDouble(txtBayar.getText().trim());

            // Cek apakah pembayaran cukup
            if (bayar < grandTotal) {
                JOptionPane.showMessageDialog(this, "Pembayaran kurang!");
                return;
            }

            // Update ke database
            double kembali = bayar - grandTotal;
            transDAO.updateStatusPembayaran(editTransId, bayar, kembali);

            // Update tampilan
            txtKembali.setText(String.valueOf(kembali));
            cbStatusServis.setSelectedItem("Selesai Lunas");
            JOptionPane.showMessageDialog(this, "Pembayaran berhasil! Kembali: " + kembali);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error bayar: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format angka tidak valid.");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddClient;
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnHapusDetail;
    private javax.swing.JButton btnHitungTotal;
    private javax.swing.JButton btnSimpanTrans;
    private javax.swing.JButton btnTambahDetail;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JComboBox cbClient;
    private javax.swing.JComboBox cbMekanik;
    private javax.swing.JComboBox cbStatusServis;
    private javax.swing.JComboBox cbVehicle;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblBayar;
    private javax.swing.JLabel lblClient;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JLabel lblKeluhan;
    private javax.swing.JLabel lblKembali;
    private javax.swing.JLabel lblMekanik;
    private javax.swing.JLabel lblSpacer1;
    private javax.swing.JLabel lblSpacer2;
    private javax.swing.JLabel lblSpacer3;
    private javax.swing.JLabel lblSpacer4;
    private javax.swing.JLabel lblStatusAntrian;
    private javax.swing.JLabel lblTotalJasa;
    private javax.swing.JLabel lblTotalSparepart;
    private javax.swing.JLabel lblVehicle;
    private javax.swing.JTable tblDetail;
    private javax.swing.JPanel totalPanel;
    private javax.swing.JTextArea txtKeluhan;
    private javax.swing.JTextField txtBayar;
    private javax.swing.JTextField txtGrandTotal;
    private javax.swing.JTextField txtKembali;
    private javax.swing.JTextField txtTotalJasa;
    private javax.swing.JTextField txtTotalSparepart;
    // End of variables declaration//GEN-END:variables
}
