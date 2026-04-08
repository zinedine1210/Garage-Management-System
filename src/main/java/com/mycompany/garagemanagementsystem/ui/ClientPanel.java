package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ClientDAO;
import com.mycompany.garagemanagementsystem.model.Client;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * ClientPanel = Panel CRUD untuk data pelanggan (client).
 *
 * Tampilan:
 * ┌─────────────────────────────────┐
 * │  Form Input (ID, Nama, dll)     │  ← NORTH (atas)
 * │  [Cari: _______________]        │
 * ├─────────────────────────────────┤
 * │  Tabel data client              │  ← CENTER (tengah)
 * ├─────────────────────────────────┤
 * │  [Baru] [Simpan] [Hapus]       │  ← SOUTH (bawah)
 * └─────────────────────────────────┘
 *
 * Alur kerja CRUD:
 * - BARU:   Klik "Baru" → form dikosongkan → isi data → klik "Simpan" → INSERT ke DB
 * - EDIT:   Klik baris di tabel → data muncul di form → ubah → klik "Simpan" → UPDATE
 * - HAPUS:  Klik baris di tabel → klik "Hapus" → konfirmasi → DELETE dari DB
 * - CARI:   Ketik di kolom Cari → tabel otomatis terfilter (tanpa query ulang ke DB)
 *
 * Pola ini SAMA PERSIS untuk semua panel CRUD:
 * MekanikPanel, SupplierPanel, VehiclePanel, SparepartPanel
 */
public class ClientPanel extends javax.swing.JPanel {

    // DAO untuk akses tabel client di database
    private final ClientDAO clientDAO = new ClientDAO();

    public ClientPanel() {
        initComponents();  // Buat komponen UI (di-generate NetBeans)
        myInit();          // Setup tambahan: event listener, fitur cari, load data
    }

    /**
     * Setup tambahan yang tidak bisa dilakukan di Design tab:
     * 1. Saat klik baris tabel → isi form dengan data baris tersebut
     * 2. Saat ketik di kolom Cari → filter tabel secara realtime
     * 3. Load data dari database ke tabel
     */
    private void myInit() {
        // Tabel hanya boleh pilih 1 baris
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        // Ketika user klik baris di tabel, isi form dengan data dari baris tsb
        table.getSelectionModel().addListSelectionListener(e -> isiFormDariTabel());

        // Fitur pencarian realtime: ketika user mengetik, tabel langsung terfilter
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTabel(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTabel(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTabel(); }

            private void filterTabel() {
                String teks = txtSearch.getText();
                // Buat sorter jika belum ada
                if (table.getRowSorter() == null) {
                    javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                            new javax.swing.table.TableRowSorter<>((DefaultTableModel) table.getModel());
                    table.setRowSorter(sorter);
                }
                javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                        (javax.swing.table.TableRowSorter<DefaultTableModel>) table.getRowSorter();

                if (teks.trim().length() == 0) {
                    sorter.setRowFilter(null);  // Tampilkan semua data
                } else {
                    // Filter case-insensitive (huruf besar/kecil sama)
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + teks));
                }
            }
        });

        // Muat semua data client dari database ke tabel
        loadData();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        formPanel = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblNama = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        lblAlamat = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        lblTelepon = new javax.swing.JLabel();
        txtTelepon = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        filterPanel = new javax.swing.JPanel();
        lblCari = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        btnBaru = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new java.awt.BorderLayout());

        formPanel.setLayout(new java.awt.GridLayout(5, 2));

        lblId.setText("ID:");
        formPanel.add(lblId);

        txtId.setColumns(5);
        txtId.setEnabled(false);
        formPanel.add(txtId);

        lblNama.setText("Nama:");
        formPanel.add(lblNama);

        txtNama.setColumns(20);
        formPanel.add(txtNama);

        lblAlamat.setText("Alamat:");
        formPanel.add(lblAlamat);

        txtAlamat.setColumns(20);
        formPanel.add(txtAlamat);

        lblTelepon.setText("Telepon:");
        formPanel.add(lblTelepon);

        txtTelepon.setColumns(15);
        formPanel.add(txtTelepon);

        lblEmail.setText("Email:");
        formPanel.add(lblEmail);

        txtEmail.setColumns(20);
        formPanel.add(txtEmail);

        topPanel.add(formPanel, java.awt.BorderLayout.CENTER);

        filterPanel.setLayout(new java.awt.BorderLayout());

        lblCari.setText(" Cari: ");
        filterPanel.add(lblCari, java.awt.BorderLayout.WEST);
        filterPanel.add(txtSearch, java.awt.BorderLayout.CENTER);

        topPanel.add(filterPanel, java.awt.BorderLayout.SOUTH);

        add(topPanel, java.awt.BorderLayout.NORTH);

        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(table);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btnBaru.setText("Baru");
        btnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaruActionPerformed(evt);
            }
        });
        buttonPanel.add(btnBaru);

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        buttonPanel.add(btnSimpan);

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        buttonPanel.add(btnHapus);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruActionPerformed
        clearForm();
    }//GEN-LAST:event_btnBaruActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        saveClient();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        deleteClient();
    }//GEN-LAST:event_btnHapusActionPerformed

    // ================================================================
    // BAGIAN LOGIC (boleh diedit manual, ini yang perlu kamu pahami)
    // ================================================================

    /**
     * Muat semua data client dari database dan tampilkan di tabel.
     */
    private void loadData() {
        try {
            // Ambil semua client dari database via DAO
            List<Client> list = clientDAO.findAll();

            // Buat model tabel baru dengan kolom yang ditentukan
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Nama", "Alamat", "Telepon", "Email"}, 0);

            // Masukkan setiap client ke baris tabel
            for (Client c : list) {
                model.addRow(new Object[]{
                    c.getClientId(), c.getNama(), c.getAlamat(),
                    c.getTelepon(), c.getEmail()
                });
            }

            // Pasang model ke tabel dan aktifkan sorter
            table.setModel(model);
            table.setRowSorter(new javax.swing.table.TableRowSorter<>(model));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load data: " + ex.getMessage());
        }
    }

    /**
     * Kosongkan semua field form (untuk input data baru).
     */
    private void clearForm() {
        txtId.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelepon.setText("");
        txtEmail.setText("");
    }

    /**
     * Simpan data client ke database.
     * Jika ID kosong → INSERT (data baru).
     * Jika ID ada → UPDATE (edit data yang sudah ada).
     */
    private void saveClient() {
        try {
            // Buat object Client dan isi dari form
            Client c = new Client();
            if (!txtId.getText().isEmpty()) {
                c.setClientId(Integer.parseInt(txtId.getText()));
            }
            c.setNama(txtNama.getText());
            c.setAlamat(txtAlamat.getText());
            c.setTelepon(txtTelepon.getText());
            c.setEmail(txtEmail.getText());
            c.setTanggalDaftar(new Date()); // Tanggal hari ini

            // Cek: jika ID = 0 berarti data baru, jika tidak berarti edit
            if (c.getClientId() == 0) {
                clientDAO.insert(c);  // INSERT ke database
            } else {
                clientDAO.update(c);  // UPDATE di database
            }

            loadData();   // Refresh tabel
            clearForm();  // Kosongkan form
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error simpan: " + ex.getMessage());
        }
    }

    /**
     * Hapus client yang dipilih dari database.
     */
    private void deleteClient() {
        if (txtId.getText().isEmpty()) return; // Tidak ada yang dipilih

        // Tampilkan dialog konfirmasi
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus client ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                clientDAO.delete(Integer.parseInt(txtId.getText()));
                loadData();
                clearForm();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error hapus: " + ex.getMessage());
            }
        }
    }

    /**
     * Ketika user klik baris di tabel, isi form dengan data dari baris tersebut.
     * Ini memungkinkan user untuk edit atau hapus data yang dipilih.
     */
    private void isiFormDariTabel() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(table.getValueAt(row, 0).toString());
            txtNama.setText(table.getValueAt(row, 1).toString());
            txtAlamat.setText(table.getValueAt(row, 2).toString());
            txtTelepon.setText(table.getValueAt(row, 3).toString());
            txtEmail.setText(table.getValueAt(row, 4).toString());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JPanel formPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAlamat;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblTelepon;
    private javax.swing.JTable table;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTelepon;
    // End of variables declaration//GEN-END:variables
}
