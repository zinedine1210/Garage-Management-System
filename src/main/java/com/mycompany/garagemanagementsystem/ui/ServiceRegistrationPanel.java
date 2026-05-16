package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ClientDAO;
import com.mycompany.garagemanagementsystem.dao.MekanikDAO;
import com.mycompany.garagemanagementsystem.dao.ServiceRegistrationDAO;
import com.mycompany.garagemanagementsystem.dao.VehicleDAO;
import com.mycompany.garagemanagementsystem.model.Client;
import com.mycompany.garagemanagementsystem.model.Mekanik;
import com.mycompany.garagemanagementsystem.model.ServiceRegistration;
import com.mycompany.garagemanagementsystem.model.Vehicle;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ServiceRegistrationPanel extends javax.swing.JPanel {

    private final ServiceRegistrationDAO regDAO = new ServiceRegistrationDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MekanikDAO mekanikDAO = new MekanikDAO();

    private List<Client> clientList = new ArrayList<>();
    private List<Vehicle> vehicleList = new ArrayList<>();
    private List<Mekanik> mekanikList = new ArrayList<>();

    private JComboBox<String> cbClient, cbVehicle, cbMekanik, cbStatus;
    private JTextArea txtKeluhan, txtCatatan;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnDaftar, btnMulaiServis, btnSelesai, btnHapus, btnRefresh, btnReset;
    private JLabel lblInfo;
    private JTextField txtSearch;
    private int selectedRegId = -1;

    public ServiceRegistrationPanel() {
        buildUI();
        loadCombos();
        loadTable();
    }

    private void buildUI() {
        setLayout(new java.awt.BorderLayout(5, 5));

        // ---- TOP FORM ----
        JPanel formPanel = new JPanel(new java.awt.GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Pendaftaran Servis Baru"));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(4, 6, 4, 6);
        gbc.anchor = java.awt.GridBagConstraints.WEST;

        // Client
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Client:"), gbc);
        cbClient = new JComboBox<>();
        cbClient.setPreferredSize(new java.awt.Dimension(220, 26));
        cbClient.addActionListener(e -> filterVehicleByClient());
        gbc.gridx = 1; formPanel.add(cbClient, gbc);

        // Vehicle
        gbc.gridx = 2; formPanel.add(new JLabel("Kendaraan:"), gbc);
        cbVehicle = new JComboBox<>();
        cbVehicle.setPreferredSize(new java.awt.Dimension(220, 26));
        gbc.gridx = 3; formPanel.add(cbVehicle, gbc);

        // Mekanik
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Mekanik:"), gbc);
        cbMekanik = new JComboBox<>();
        cbMekanik.setPreferredSize(new java.awt.Dimension(220, 26));
        gbc.gridx = 1; formPanel.add(cbMekanik, gbc);

        // Status filter
        gbc.gridx = 2; formPanel.add(new JLabel("Filter Status:"), gbc);
        cbStatus = new JComboBox<>(new String[]{"Semua", "Registered", "InProgress", "Completed"});
        cbStatus.setPreferredSize(new java.awt.Dimension(150, 26));
        cbStatus.addActionListener(e -> loadTable());
        gbc.gridx = 3; formPanel.add(cbStatus, gbc);

        // Keluhan
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Keluhan:"), gbc);
        txtKeluhan = new JTextArea(3, 20);
        txtKeluhan.setLineWrap(true);
        JScrollPane scrollKeluhan = new JScrollPane(txtKeluhan);
        scrollKeluhan.setPreferredSize(new java.awt.Dimension(220, 60));
        gbc.gridx = 1; gbc.gridwidth = 1; formPanel.add(scrollKeluhan, gbc);

        // Catatan
        gbc.gridx = 2; formPanel.add(new JLabel("Catatan:"), gbc);
        txtCatatan = new JTextArea(3, 20);
        txtCatatan.setLineWrap(true);
        JScrollPane scrollCatatan = new JScrollPane(txtCatatan);
        scrollCatatan.setPreferredSize(new java.awt.Dimension(220, 60));
        gbc.gridx = 3; formPanel.add(scrollCatatan, gbc);

        add(formPanel, java.awt.BorderLayout.NORTH);

        // ---- TABLE ----
        tableModel = new DefaultTableModel(new Object[]{"ID", "Tanggal Daftar", "Client", "Kendaraan", "Mekanik", "Keluhan", "Status", "Catatan"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> isiFormDariTabel());
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setPreferredSize(new java.awt.Dimension(800, 280));
        add(scrollTable, java.awt.BorderLayout.CENTER);

        // ---- BOTTOM BUTTONS ----
        JPanel bottomPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Aksi"));

        btnDaftar = new JButton("Daftar Baru");
        btnDaftar.setBackground(new java.awt.Color(0, 150, 136));
        btnDaftar.setForeground(java.awt.Color.WHITE);
        btnDaftar.addActionListener(e -> daftarBaru());

        btnMulaiServis = new JButton("Mulai Servis");
        btnMulaiServis.setBackground(new java.awt.Color(33, 150, 243));
        btnMulaiServis.setForeground(java.awt.Color.WHITE);
        btnMulaiServis.addActionListener(e -> mulaiServis());

        btnSelesai = new JButton("Tandai Selesai");
        btnSelesai.setBackground(new java.awt.Color(76, 175, 80));
        btnSelesai.setForeground(java.awt.Color.WHITE);
        btnSelesai.addActionListener(e -> tandaiSelesai());

        btnHapus = new JButton("Hapus");
        btnHapus.setBackground(new java.awt.Color(244, 67, 54));
        btnHapus.setForeground(java.awt.Color.WHITE);
        btnHapus.addActionListener(e -> hapusRegistrasi());

        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> { loadTable(); clearForm(); });

        btnReset = new JButton("Bersihkan Form");
        btnReset.addActionListener(e -> clearForm());

        txtSearch = new JTextField(15);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        bottomPanel.add(btnDaftar);
        bottomPanel.add(btnMulaiServis);
        bottomPanel.add(btnSelesai);
        bottomPanel.add(btnHapus);
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnReset);
        bottomPanel.add(new JLabel("  Cari:"));
        bottomPanel.add(txtSearch);

        lblInfo = new JLabel(" ");
        lblInfo.setForeground(java.awt.Color.BLUE);
        bottomPanel.add(lblInfo);

        add(bottomPanel, java.awt.BorderLayout.SOUTH);
    }

    private void loadCombos() {
        try {
            cbClient.removeAllItems();
            cbClient.addItem("-- Pilih Client --");
            clientList = clientDAO.findAll();
            for (Client c : clientList) {
                cbClient.addItem(c.getClientId() + " - " + c.getNama());
            }

            cbMekanik.removeAllItems();
            cbMekanik.addItem("-- Pilih Mekanik --");
            mekanikList = mekanikDAO.findAll();
            for (Mekanik m : mekanikList) {
                cbMekanik.addItem(m.getMekanikId() + " - " + m.getNama());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage());
        }
    }

    private void filterVehicleByClient() {
        cbVehicle.removeAllItems();
        cbVehicle.addItem("-- Pilih Kendaraan --");
        if (cbClient.getSelectedIndex() <= 0) return;
        try {
            String sel = cbClient.getSelectedItem().toString();
            int clientId = Integer.parseInt(sel.split(" - ")[0].trim());
            vehicleList = vehicleDAO.findByClientId(clientId);
            for (Vehicle v : vehicleList) {
                cbVehicle.addItem(v.getVehicleId() + " - " + v.getNoPolisi() + " (" + v.getMerk() + ")");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat kendaraan: " + ex.getMessage());
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            String statusFilter = cbStatus.getSelectedItem().toString();
            List<ServiceRegistration> list;
            if ("Semua".equals(statusFilter)) {
                list = regDAO.findAll();
            } else {
                list = regDAO.findByStatus(statusFilter);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (ServiceRegistration r : list) {
                String clientNama = "";
                String vehicleNopol = "";
                String mekanikNama = "";
                try {
                    Client c = clientDAO.findById(r.getClientId());
                    if (c != null) clientNama = c.getNama();
                    List<Vehicle> vs = vehicleDAO.findByClientId(r.getClientId());
                    for (Vehicle v : vs) {
                        if (v.getVehicleId() == r.getVehicleId()) { vehicleNopol = v.getNoPolisi(); break; }
                    }
                    Mekanik m = mekanikDAO.findById(r.getMekanikId());
                    if (m != null) mekanikNama = m.getNama();
                } catch (SQLException ex) { /* ignore individual row errors */ }

                tableModel.addRow(new Object[]{
                    r.getRegistrationId(),
                    r.getTanggalDaftar() != null ? sdf.format(r.getTanggalDaftar()) : "",
                    clientNama,
                    vehicleNopol,
                    mekanikNama,
                    r.getKeluhan(),
                    r.getStatus(),
                    r.getCatatan()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data registrasi: " + ex.getMessage());
        }
    }

    private void isiFormDariTabel() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedRegId = (int) table.getValueAt(row, 0);
        lblInfo.setText("Dipilih: ID #" + selectedRegId + " | Status: " + table.getValueAt(row, 6));
    }

    private void daftarBaru() {
        if (cbClient.getSelectedIndex() <= 0 || cbVehicle.getSelectedIndex() <= 0 || cbMekanik.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Harap pilih Client, Kendaraan, dan Mekanik terlebih dahulu.");
            return;
        }
        if (txtKeluhan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keluhan tidak boleh kosong.");
            return;
        }
        try {
            int clientId = Integer.parseInt(cbClient.getSelectedItem().toString().split(" - ")[0].trim());
            int vehicleId = Integer.parseInt(cbVehicle.getSelectedItem().toString().split(" - ")[0].trim());
            int mekanikId = Integer.parseInt(cbMekanik.getSelectedItem().toString().split(" - ")[0].trim());

            ServiceRegistration reg = new ServiceRegistration();
            reg.setClientId(clientId);
            reg.setVehicleId(vehicleId);
            reg.setMekanikId(mekanikId);
            reg.setKeluhan(txtKeluhan.getText().trim());
            reg.setCatatan(txtCatatan.getText().trim());
            reg.setStatus("Registered");
            reg.setTanggalDaftar(new Date());

            regDAO.insert(reg);
            JOptionPane.showMessageDialog(this, "Pendaftaran servis berhasil disimpan.");
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage());
        }
    }

    private void mulaiServis() {
        if (selectedRegId < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pendaftaran dari tabel terlebih dahulu.");
            return;
        }
        try {
            ServiceRegistration reg = regDAO.findById(selectedRegId);
            if (reg == null) { JOptionPane.showMessageDialog(this, "Data tidak ditemukan."); return; }
            if (!"Registered".equals(reg.getStatus())) {
                JOptionPane.showMessageDialog(this, "Hanya pendaftaran berstatus 'Registered' yang bisa dimulai.");
                return;
            }

            java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(this);
            java.awt.Frame owner = (win instanceof java.awt.Frame) ? (java.awt.Frame) win : null;
            ServiceTransactionFrame frame = new ServiceTransactionFrame(owner, reg);
            frame.setVisible(true);

            ServiceRegistration latest = regDAO.findById(selectedRegId);
            if (latest != null && "Registered".equals(latest.getStatus())) {
                latest.setStatus("InProgress");
                latest.setTanggalMulai(new Date());
                regDAO.update(latest);
            }

            JOptionPane.showMessageDialog(this, "Flow transaksi dibuka dari pendaftaran. Status pendaftaran diperbarui otomatis.");
            loadTable();
            lblInfo.setText(" ");
            selectedRegId = -1;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah status: " + ex.getMessage());
        }
    }

    private void tandaiSelesai() {
        if (selectedRegId < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pendaftaran dari tabel terlebih dahulu.");
            return;
        }
        try {
            ServiceRegistration reg = regDAO.findById(selectedRegId);
            if (reg == null) { JOptionPane.showMessageDialog(this, "Data tidak ditemukan."); return; }
            if ("Completed".equals(reg.getStatus())) {
                JOptionPane.showMessageDialog(this, "Pendaftaran ini sudah berstatus 'Completed'.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Tandai pendaftaran #" + selectedRegId + " sebagai selesai?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            reg.setStatus("Completed");
            regDAO.update(reg);
            JOptionPane.showMessageDialog(this, "Status diubah menjadi 'Completed'.");
            loadTable();
            lblInfo.setText(" ");
            selectedRegId = -1;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah status: " + ex.getMessage());
        }
    }

    private void hapusRegistrasi() {
        if (selectedRegId < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pendaftaran dari tabel terlebih dahulu.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus pendaftaran #" + selectedRegId + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            regDAO.delete(selectedRegId);
            JOptionPane.showMessageDialog(this, "Pendaftaran dihapus.");
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus: " + ex.getMessage());
        }
    }

    private void clearForm() {
        cbClient.setSelectedIndex(0);
        cbVehicle.removeAllItems();
        cbVehicle.addItem("-- Pilih Kendaraan --");
        cbMekanik.setSelectedIndex(0);
        txtKeluhan.setText("");
        txtCatatan.setText("");
        selectedRegId = -1;
        lblInfo.setText(" ");
        table.clearSelection();
    }

    private void filterTable() {
        String text = txtSearch.getText().trim();
        if (table.getRowSorter() == null) {
            javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(tableModel);
            table.setRowSorter(sorter);
        }
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                (javax.swing.table.TableRowSorter<DefaultTableModel>) table.getRowSorter();
        if (text.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
    }
}
