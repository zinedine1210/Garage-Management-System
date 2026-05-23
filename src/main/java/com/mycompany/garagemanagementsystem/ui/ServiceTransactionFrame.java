package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ClientDAO;
import com.mycompany.garagemanagementsystem.dao.MekanikDAO;
import com.mycompany.garagemanagementsystem.dao.ServiceRegistrationDAO;
import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.dao.SparepartDAO;
import com.mycompany.garagemanagementsystem.dao.VehicleDAO;
import com.mycompany.garagemanagementsystem.model.Client;
import com.mycompany.garagemanagementsystem.model.Mekanik;
import com.mycompany.garagemanagementsystem.model.ServiceRegistration;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import com.mycompany.garagemanagementsystem.model.Sparepart;
import com.mycompany.garagemanagementsystem.model.TransactionDetail;
import com.mycompany.garagemanagementsystem.model.TransactionJasaDetail;
import com.mycompany.garagemanagementsystem.model.Vehicle;
import java.awt.Color;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.mycompany.garagemanagementsystem.util.UIHelper;

public class ServiceTransactionFrame extends javax.swing.JDialog {

    private final ClientDAO clientDAO = new ClientDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MekanikDAO mekanikDAO = new MekanikDAO();
    private final SparepartDAO sparepartDAO = new SparepartDAO();
    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private final ServiceRegistrationDAO regDAO = new ServiceRegistrationDAO();

    private DefaultTableModel detailModel;
    private DefaultTableModel jasaDetailModel;
    private javax.swing.JTable tblJasaDetail;

    private boolean viewOnly = false;

    private int editTransId = -1;
    private Integer sourceRegistrationId = null;

    private boolean isFiltering = false;

    private List<Client> clientList;
    private List<Vehicle> vehicleList;
    private List<Mekanik> mekanikList;
    private List<Sparepart> sparepartList;

    public ServiceTransactionFrame(Frame owner) {
        super(owner, true);
        initComponents();
        myInit();
    }

    public ServiceTransactionFrame(Frame owner, int transId) {
        super(owner, true);
        initComponents();
        myInit();
        this.editTransId = transId;
        loadTransactionInfo(transId);
    }

    public ServiceTransactionFrame(Frame owner, ServiceRegistration reg) {
        super(owner, true);
        initComponents();
        myInit();
        prefillFromRegistration(reg);
    }

    private void myInit() {
        // Hide legacy button
        btnAddClient.setVisible(false);
        lblSpacer1.setVisible(false);
        lblSpacer2.setVisible(false);
        lblSpacer3.setVisible(false);

        // Rebuild header to cleaner 4-row x 2-col layout
        headerPanel.removeAll();
        headerPanel.setLayout(new java.awt.GridBagLayout());
        headerPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 235)),
            new javax.swing.border.EmptyBorder(10, 15, 10, 15)));
        headerPanel.setBackground(Color.WHITE);
        java.awt.GridBagConstraints hg = new java.awt.GridBagConstraints();
        hg.insets = new java.awt.Insets(4, 6, 4, 6);
        hg.anchor = java.awt.GridBagConstraints.WEST;
        hg.fill = java.awt.GridBagConstraints.HORIZONTAL;
        hg.gridx = 0; hg.gridy = 0; headerPanel.add(lblClient, hg);
        hg.gridx = 1; hg.weightx = 1; headerPanel.add(cbClient, hg); hg.weightx = 0;
        hg.gridx = 0; hg.gridy = 1; headerPanel.add(lblVehicle, hg);
        hg.gridx = 1; hg.weightx = 1; headerPanel.add(cbVehicle, hg); hg.weightx = 0;
        hg.gridx = 0; hg.gridy = 2; headerPanel.add(lblMekanik, hg);
        hg.gridx = 1; hg.weightx = 1; headerPanel.add(cbMekanik, hg); hg.weightx = 0;
        hg.gridx = 0; hg.gridy = 3; headerPanel.add(lblKeluhan, hg);
        hg.gridx = 1; hg.weightx = 1; hg.fill = java.awt.GridBagConstraints.BOTH; hg.weighty = 1;
        headerPanel.add(jScrollPane2, hg);
        headerPanel.revalidate();

        detailModel = new DefaultTableModel(
                new Object[]{"Sparepart ID", "Nama Sparepart", "Qty", "Harga", "Subtotal"}, 0);
        tblDetail.setModel(detailModel);

        // Jasa detail table
        jasaDetailModel = new DefaultTableModel(
                new Object[]{"Nama Jasa", "Harga", "Qty", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblJasaDetail = new javax.swing.JTable(jasaDetailModel);

        // Restructure center: split pane with jasa table (top) + sparepart table (bottom)
        getContentPane().remove(jScrollPane1);
        javax.swing.JPanel jasaPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        jasaPanel.add(new javax.swing.JLabel("  Jasa / Layanan:"), java.awt.BorderLayout.NORTH);
        jasaPanel.add(new javax.swing.JScrollPane(tblJasaDetail), java.awt.BorderLayout.CENTER);
        javax.swing.JPanel spPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        spPanel.add(new javax.swing.JLabel("  Sparepart:"), java.awt.BorderLayout.NORTH);
        spPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        javax.swing.JSplitPane splitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT, jasaPanel, spPanel);
        splitPane.setDividerLocation(180);
        splitPane.setResizeWeight(0.4);
        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        // Make txtTotalJasa read-only (calculated from jasa table)
        txtTotalJasa.setEditable(false);

        loadComboBoxData();

        cbClient.addActionListener(e -> {
            if (!isFiltering) filterVehicleByClient();
        });
        cbVehicle.addActionListener(e -> {
            if (!isFiltering) autofillClientByVehicle();
        });

        txtBayar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { hitungTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { hitungTotal(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { hitungTotal(); }
        });
    }

    private void applyViewOnly() {
        viewOnly = true;
        setTitle("Detail Transaksi Servis (View Only)");
        cbClient.setEnabled(false);
        cbVehicle.setEnabled(false);
        cbMekanik.setEnabled(false);
        txtKeluhan.setEditable(false);
        txtKeluhan.setBackground(new Color(245, 245, 250));
        txtBayar.setEditable(false);
        if (cbMetodeBayar != null) cbMetodeBayar.setEnabled(false);

        // Hide all edit buttons, keep only Print and Close
        for (java.awt.Component c : buttonPanel.getComponents()) {
            if (c instanceof javax.swing.JButton) {
                javax.swing.JButton b = (javax.swing.JButton) c;
                String txt = b.getText();
                if (txt != null && !txt.contains("Print") && !txt.contains("Tutup")) {
                    b.setVisible(false);
                }
            }
        }
        // Add close button if not present
        javax.swing.JButton btnTutup = new javax.swing.JButton("Tutup");
        btnTutup.addActionListener(ev -> dispose());
        buttonPanel.add(btnTutup);
        buttonPanel.revalidate();
    }

    private void loadComboBoxData() {
        try {
            isFiltering = true;

            clientList = clientDAO.findAll();
            vehicleList = vehicleDAO.findAll();
            mekanikList = mekanikDAO.findAll();
            sparepartList = sparepartDAO.findAll();

            cbClient.removeAllItems();
            cbClient.addItem("-- Pilih Client --");
            for (Client c : clientList) {
                cbClient.addItem(c.getClientId() + " - " + c.getNama());
            }

            cbVehicle.removeAllItems();
            cbVehicle.addItem("-- Pilih Kendaraan --");
            for (Vehicle v : vehicleList) {
                cbVehicle.addItem(v.getVehicleId() + " - " + v.getNoPolisi() + " - " + v.getMerk());
            }

            cbMekanik.removeAllItems();
            cbMekanik.addItem("-- Pilih Mekanik --");
            for (Mekanik m : mekanikList) {
                cbMekanik.addItem(m.getMekanikId() + " - " + m.getNama());
            }

            isFiltering = false;
        } catch (SQLException ex) {
            isFiltering = false;
            UIHelper.error(this, "Error load combo: " + ex.getMessage());
        }
    }

    private void filterVehicleByClient() {
        int idx = cbClient.getSelectedIndex();
        if (idx <= 0) return;

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

    private void autofillClientByVehicle() {
        int idx = cbVehicle.getSelectedIndex();
        if (idx <= 0) return;

        String sel = cbVehicle.getSelectedItem().toString();
        int vId = Integer.parseInt(sel.split(" - ")[0].trim());

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

    private void loadTransactionInfo(int transId) {
        try {
            ServiceTransaction t = transDAO.findByIdFull(transId);
            if (t == null) return;

            sourceRegistrationId = t.getRegistrationId();

            isFiltering = true;

            for (int i = 0; i < clientList.size(); i++) {
                if (clientList.get(i).getClientId() == t.getClientId()) {
                    cbClient.setSelectedIndex(i + 1);
                    break;
                }
            }

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

            for (int i = 0; i < mekanikList.size(); i++) {
                if (mekanikList.get(i).getMekanikId() == t.getMekanikId()) {
                    cbMekanik.setSelectedIndex(i + 1);
                    break;
                }
            }

            isFiltering = false;

            txtKeluhan.setText(t.getKeluhan());
            txtBayar.setText(String.valueOf(t.getBayar()));

            if (t.getMetodeBayar() != null) {
                for (int i = 0; i < cbMetodeBayar.getItemCount(); i++) {
                    if (cbMetodeBayar.getItemAt(i).toString().equals(t.getMetodeBayar())) {
                        cbMetodeBayar.setSelectedIndex(i);
                        break;
                    }
                }
            }

            // Load sparepart details
            detailModel.setRowCount(0);
            if (t.getDetails() != null) {
                for (TransactionDetail d : t.getDetails()) {
                    String nama = d.getSparepartNama() != null ? d.getSparepartNama() : "Sparepart #" + d.getSparepartId();
                    detailModel.addRow(new Object[]{
                        d.getSparepartId(), nama, d.getQty(), d.getHarga(), d.getSubtotal()
                    });
                }
            }

            // Load jasa details
            jasaDetailModel.setRowCount(0);
            if (t.getJasaDetails() != null) {
                for (TransactionJasaDetail jd : t.getJasaDetails()) {
                    jasaDetailModel.addRow(new Object[]{
                        jd.getNamaJasa(), jd.getHarga(), jd.getQty(), jd.getSubtotal()
                    });
                }
            }

            hitungTotal();

            // If transaction is completed, set view-only mode
            if ("Selesai Lunas".equalsIgnoreCase(t.getStatusServis())) {
                applyViewOnly();
            }
        } catch (SQLException ex) {
            UIHelper.error(this, "Error load transaksi: " + ex.getMessage());
        }
    }

    private void prefillFromRegistration(ServiceRegistration reg) {
        if (reg == null) return;
        sourceRegistrationId = reg.getRegistrationId();
        txtKeluhan.setText(reg.getKeluhan() != null ? reg.getKeluhan() : "");

        isFiltering = true;
        for (int i = 0; i < clientList.size(); i++) {
            if (clientList.get(i).getClientId() == reg.getClientId()) {
                cbClient.setSelectedIndex(i + 1);
                break;
            }
        }

        cbVehicle.removeAllItems();
        cbVehicle.addItem("-- Pilih Kendaraan --");
        for (Vehicle v : vehicleList) {
            if (v.getClientId() == reg.getClientId()) {
                cbVehicle.addItem(v.getVehicleId() + " - " + v.getNoPolisi() + " - " + v.getMerk());
            }
        }
        for (int i = 0; i < cbVehicle.getItemCount(); i++) {
            if (cbVehicle.getItemAt(i).toString().startsWith(reg.getVehicleId() + " - ")) {
                cbVehicle.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < mekanikList.size(); i++) {
            if (mekanikList.get(i).getMekanikId() == reg.getMekanikId()) {
                cbMekanik.setSelectedIndex(i + 1);
                break;
            }
        }
        isFiltering = false;
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
        btnSimpanTrans = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Transaksi Servis");
        setModal(true);
        getContentPane().setLayout(new java.awt.BorderLayout());

        headerPanel.setLayout(new java.awt.GridLayout(4, 3, 5, 5));
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
        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        jScrollPane1.setViewportView(tblDetail);
        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        totalPanel.setLayout(new java.awt.GridLayout(6, 2));
        lblTotalJasa.setText("Total Jasa:"); totalPanel.add(lblTotalJasa);
        txtTotalJasa.setText("0"); txtTotalJasa.setColumns(10); totalPanel.add(txtTotalJasa);
        lblTotalSparepart.setText("Total Sparepart:"); totalPanel.add(lblTotalSparepart);
        txtTotalSparepart.setText("0"); txtTotalSparepart.setColumns(10); txtTotalSparepart.setEditable(false); totalPanel.add(txtTotalSparepart);
        lblGrandTotal.setText("Grand Total:"); totalPanel.add(lblGrandTotal);
        txtGrandTotal.setText("0"); txtGrandTotal.setColumns(10); txtGrandTotal.setEditable(false); totalPanel.add(txtGrandTotal);
        lblBayar.setText("Bayar (optional):"); totalPanel.add(lblBayar);
        txtBayar.setText("0"); txtBayar.setColumns(10); totalPanel.add(txtBayar);
        lblKembali.setText("Kembali:"); totalPanel.add(lblKembali);
        txtKembali.setText("0"); txtKembali.setColumns(10); txtKembali.setEditable(false); totalPanel.add(txtKembali);
        lblMetodeBayar = new javax.swing.JLabel("Metode Bayar:"); totalPanel.add(lblMetodeBayar);
        cbMetodeBayar = new javax.swing.JComboBox(new String[]{"Cash", "QRIS", "Transfer Bank", "Debit", "Lainnya"});
        totalPanel.add(cbMetodeBayar);
        getContentPane().add(totalPanel, java.awt.BorderLayout.EAST);

        buttonPanel.setLayout(new java.awt.FlowLayout());
        javax.swing.JButton btnTambahJasa = new javax.swing.JButton("Tambah Jasa");
        btnTambahJasa.addActionListener(evt -> addJasaRow());
        buttonPanel.add(btnTambahJasa);
        javax.swing.JButton btnHapusJasa = new javax.swing.JButton("Hapus Jasa");
        btnHapusJasa.addActionListener(evt -> {
            int row = tblJasaDetail.getSelectedRow();
            if (row >= 0) { jasaDetailModel.removeRow(row); hitungTotal(); }
            else UIHelper.warn(this, "Pilih baris jasa yang akan dihapus.");
        });
        buttonPanel.add(btnHapusJasa);
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
        btnSimpanTrans.setText("Simpan Transaksi");
        btnSimpanTrans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnSimpanTransActionPerformed(evt); }
        });
        buttonPanel.add(btnSimpanTrans);
        btnPrint = new javax.swing.JButton();
        btnPrint.setText("Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { printTransaksi(); }
        });
        buttonPanel.add(btnPrint);
        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        setSize(900, 600);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddClientActionPerformed
        UIHelper.info(this, "Silakan tambahkan client baru lewat menu Data Client terlebih dahulu.");
    }//GEN-LAST:event_btnAddClientActionPerformed

    private void btnTambahDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahDetailActionPerformed
        addDetailRow();
    }//GEN-LAST:event_btnTambahDetailActionPerformed

    private void btnHapusDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusDetailActionPerformed
        int row = tblDetail.getSelectedRow();
        if (row >= 0) { detailModel.removeRow(row); hitungTotal(); }
        else UIHelper.warn(this, "Pilih baris detail yang akan dihapus.");
    }//GEN-LAST:event_btnHapusDetailActionPerformed

    private void btnSimpanTransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanTransActionPerformed
        simpanTransaksi();
    }//GEN-LAST:event_btnSimpanTransActionPerformed

    private void addJasaRow() {
        javax.swing.JTextField fNama = new javax.swing.JTextField(15);
        javax.swing.JTextField fHarga = new javax.swing.JTextField(10);
        javax.swing.JTextField fQty = new javax.swing.JTextField("1", 5);
        javax.swing.JPanel p = new javax.swing.JPanel(new java.awt.GridLayout(3, 2, 5, 5));
        p.add(new javax.swing.JLabel("Nama Jasa:")); p.add(fNama);
        p.add(new javax.swing.JLabel("Harga:")); p.add(fHarga);
        p.add(new javax.swing.JLabel("Qty:")); p.add(fQty);
        int result = javax.swing.JOptionPane.showConfirmDialog(this, p, "Tambah Jasa", javax.swing.JOptionPane.OK_CANCEL_OPTION);
        if (result == javax.swing.JOptionPane.OK_OPTION) {
            String nama = fNama.getText().trim();
            if (nama.isEmpty()) { UIHelper.warn(this, "Nama jasa tidak boleh kosong."); return; }
            try {
                double harga = Double.parseDouble(fHarga.getText().trim());
                int qty = Integer.parseInt(fQty.getText().trim());
                if (qty <= 0) { UIHelper.warn(this, "Qty harus > 0."); return; }
                jasaDetailModel.addRow(new Object[]{nama, harga, qty, harga * qty});
                hitungTotal();
            } catch (NumberFormatException ex) {
                UIHelper.warn(this, "Harga dan Qty harus berupa angka.");
            }
        }
    }

    private void addDetailRow() {
        if (sparepartList == null || sparepartList.isEmpty()) {
            UIHelper.warn(this, "Tidak ada data sparepart.");
            return;
        }

        SparepartChooserDialog dialog = new SparepartChooserDialog(this, sparepartList);
        dialog.setVisible(true);

        Sparepart s = dialog.getSelectedSparepart();
        int qty = dialog.getSelectedQty();
        if (s == null || qty <= 0) return;

        double harga = s.getHargaJual();
        double subtotal = harga * qty;
        detailModel.addRow(new Object[]{
            s.getSparepartId(), s.getNamaSparepart(), qty, harga, subtotal
        });

        hitungTotal();
    }

    private void hitungTotal() {
        double totalSparepart = 0;
        for (int i = 0; i < detailModel.getRowCount(); i++) {
            totalSparepart += Double.parseDouble(detailModel.getValueAt(i, 4).toString());
        }

        double totalJasa = 0;
        for (int i = 0; i < jasaDetailModel.getRowCount(); i++) {
            totalJasa += Double.parseDouble(jasaDetailModel.getValueAt(i, 3).toString());
        }

        double grandTotal = totalJasa + totalSparepart;
        txtTotalJasa.setText(String.valueOf(totalJasa));
        txtTotalSparepart.setText(String.valueOf(totalSparepart));
        txtGrandTotal.setText(String.valueOf(grandTotal));

        double bayar = 0;
        try {
            bayar = Double.parseDouble(txtBayar.getText().trim());
        } catch (NumberFormatException ignored) {
        }
        double kembali = Math.max(0, bayar - grandTotal);
        txtKembali.setText(String.valueOf(kembali));
    }

    private void simpanTransaksi() {
        if (viewOnly) { UIHelper.warn(this, "Transaksi sudah selesai, tidak bisa diedit."); return; }
        try {
            if (cbClient.getSelectedIndex() <= 0
             || cbVehicle.getSelectedIndex() <= 0
             || cbMekanik.getSelectedIndex() <= 0) {
                UIHelper.warn(this, "Pilih Client, Vehicle, dan Mekanik.");
                return;
            }

            int clientId = clientList.get(cbClient.getSelectedIndex() - 1).getClientId();

            String vSel = cbVehicle.getSelectedItem().toString();
            int vehicleId = Integer.parseInt(vSel.split(" - ")[0].trim());

            String mSel = cbMekanik.getSelectedItem().toString();
            int mekanikId = Integer.parseInt(mSel.split(" - ")[0].trim());

            ServiceTransaction t = new ServiceTransaction();
            t.setTanggal(new Date());
            t.setClientId(clientId);
            t.setVehicleId(vehicleId);
            t.setMekanikId(mekanikId);
            t.setKeluhan(txtKeluhan.getText().trim());
            t.setRegistrationId(sourceRegistrationId);
            t.setStatusServis("Dikerjakan");
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
            t.setKembali(Math.max(0, bayar - t.getGrandTotal()));
            t.setMetodeBayar(cbMetodeBayar.getSelectedItem().toString());
            t.setUserKasir("admin");

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

            // Build jasa details
            List<TransactionJasaDetail> jasaDetails = new ArrayList<>();
            for (int i = 0; i < jasaDetailModel.getRowCount(); i++) {
                TransactionJasaDetail jd = new TransactionJasaDetail();
                jd.setNamaJasa(jasaDetailModel.getValueAt(i, 0).toString());
                jd.setHarga(Double.parseDouble(jasaDetailModel.getValueAt(i, 1).toString()));
                jd.setQty(Integer.parseInt(jasaDetailModel.getValueAt(i, 2).toString()));
                jd.setSubtotal(Double.parseDouble(jasaDetailModel.getValueAt(i, 3).toString()));
                jasaDetails.add(jd);
            }
            t.setJasaDetails(jasaDetails);

            if (editTransId > 0) {
                t.setTransId(editTransId);
                transDAO.updateWithDetails(t);
                UIHelper.success(this, "Transaksi berhasil diupdate!");
            } else {
                int newId = transDAO.insertWithDetails(t);
                editTransId = newId;
                UIHelper.success(this, "Transaksi berhasil disimpan! ID: " + newId);
                if (sourceRegistrationId != null) {
                    ServiceRegistration reg = regDAO.findById(sourceRegistrationId);
                    if (reg != null) {
                        reg.setStatus("InProgress");
                        if (reg.getTanggalMulai() == null) {
                            reg.setTanggalMulai(new Date());
                        }
                        regDAO.update(reg);
                    }
                }
            }
        } catch (SQLException ex) {
            UIHelper.error(this, "Error simpan: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            UIHelper.error(this, "Format angka tidak valid: " + ex.getMessage());
        }
    }

    private void prosesBayar() {
        if (editTransId <= 0) {
            UIHelper.warn(this, "Simpan transaksi terlebih dahulu.");
            return;
        }
        try {
            hitungTotal();

            double grandTotal = Double.parseDouble(txtGrandTotal.getText().trim());
            double bayar = Double.parseDouble(txtBayar.getText().trim());

            if (bayar < grandTotal) {
                UIHelper.warn(this, "Pembayaran kurang!");
                return;
            }

            double kembali = bayar - grandTotal;
            transDAO.updateStatusPembayaran(editTransId, bayar, kembali);

            ServiceTransaction saved = transDAO.findById(editTransId);
            Integer regId = saved != null ? saved.getRegistrationId() : sourceRegistrationId;
            if (regId != null) {
                ServiceRegistration reg = regDAO.findById(regId);
                if (reg != null) {
                    reg.setStatus("Completed");
                    regDAO.update(reg);
                }
            }

            txtKembali.setText(String.valueOf(kembali));
            UIHelper.success(this, "Pembayaran berhasil! Kembali: " + kembali);
        } catch (SQLException ex) {
            UIHelper.error(this, "Error bayar: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            UIHelper.error(this, "Format angka tidak valid.");
        }
    }

    private void printTransaksi() {
        if (editTransId <= 0) {
            UIHelper.warn(this, "Simpan transaksi terlebih dahulu sebelum print nota.");
            return;
        }
        try {
            ServiceTransaction t = transDAO.findByIdFull(editTransId);
            if (t != null) {
                com.mycompany.garagemanagementsystem.util.ExportUtils.printServiceReceipt(t);
            } else {
                UIHelper.error(this, "Transaksi tidak ditemukan.");
            }
        } catch (java.sql.SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddClient;
    private javax.swing.JButton btnHapusDetail;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSimpanTrans;
    private javax.swing.JLabel lblMetodeBayar;
    private javax.swing.JComboBox cbMetodeBayar;
    private javax.swing.JButton btnTambahDetail;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JComboBox cbClient;
    private javax.swing.JComboBox cbMekanik;
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
