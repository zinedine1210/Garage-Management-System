package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ClientDAO;
import com.mycompany.garagemanagementsystem.dao.MekanikDAO;
import com.mycompany.garagemanagementsystem.dao.ServiceRegistrationDAO;
import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.dao.VehicleDAO;
import com.mycompany.garagemanagementsystem.model.Client;
import com.mycompany.garagemanagementsystem.model.Mekanik;
import com.mycompany.garagemanagementsystem.model.ServiceRegistration;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import com.mycompany.garagemanagementsystem.model.Vehicle;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel Pendaftaran Servis.
 * 
 * FLOW BENGKEL:
 * 1. Customer datang -> Klik "Daftar Baru" -> Isi form popup -> Status: Registered
 * 2. Mulai Servis -> Otomatis buat transaksi & tandai InProgress
 * 3. Selama dikerjakan -> Klik "Edit Transaksi" -> Isi sparepart, jasa, dll
 * 4. Selesai -> Klik "Selesaikan" -> Review transaksi -> Status: Completed
 */
public class ServiceRegistrationPanel extends javax.swing.JPanel {

    private final ServiceRegistrationDAO regDAO = new ServiceRegistrationDAO();
    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MekanikDAO mekanikDAO = new MekanikDAO();

    private List<Client> clientList = new ArrayList<>();
    private List<Mekanik> mekanikList = new ArrayList<>();

    private JComboBox<String> cbStatusFilter;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblInfo;
    private int selectedRegId = -1;

    public ServiceRegistrationPanel() {
        buildUI();
        loadTable();
    }

    private void buildUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ============ TOP: Flow info + Filter ============
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        // Flow info banner
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        flowPanel.setBackground(new Color(232, 245, 253));
        flowPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 246)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        JLabel lblFlow = new JLabel(
            "<html><b>Flow:</b> Customer Datang \u2192 <b>Daftar Baru</b> \u2192 <b>Mulai Servis</b> (tandai mulai) \u2192 " +
            "<b>Edit Transaksi</b> (isi detail) \u2192 <b>Selesaikan</b> (review & selesai)</html>");
        lblFlow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        flowPanel.add(lblFlow);
        topPanel.add(flowPanel, BorderLayout.NORTH);

        // Filter bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Cari:"));
        txtSearch = new JTextField(15);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        filterPanel.add(txtSearch);
        filterPanel.add(new JLabel("Status:"));
        cbStatusFilter = new JComboBox<>(new String[]{"Semua", "Registered", "InProgress", "Completed"});
        cbStatusFilter.addActionListener(e -> loadTable());
        filterPanel.add(cbStatusFilter);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // ============ CENTER: Table ============
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Tanggal Daftar", "Client", "Kendaraan", "Mekanik", "Keluhan", "Status", "Catatan"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedRegId = (int) table.getValueAt(row, 0);
                String status = table.getValueAt(row, 6).toString();
                lblInfo.setText("Dipilih: #" + selectedRegId + " | Status: " + status);
            }
        });
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(6).setMaxWidth(100);
        JScrollPane scrollTable = new JScrollPane(table);
        add(scrollTable, BorderLayout.CENTER);

        // ============ BOTTOM: Action buttons ============
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        JButton btnDaftar = new JButton("+ Daftar Baru");
        btnDaftar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDaftar.addActionListener(e -> showFormDialog(null));
        bottomPanel.add(btnDaftar);

        JButton btnEdit = new JButton("Edit Pendaftaran");
        btnEdit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnEdit.addActionListener(e -> editRegistrasi());
        bottomPanel.add(btnEdit);

        JButton btnMulaiServis = new JButton("\u25B6 Mulai Servis");
        btnMulaiServis.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMulaiServis.addActionListener(e -> mulaiServis());
        bottomPanel.add(btnMulaiServis);

        JButton btnEditTrans = new JButton("\u270E Edit Transaksi");
        btnEditTrans.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnEditTrans.addActionListener(e -> editTransaksi());
        bottomPanel.add(btnEditTrans);

        JButton btnSelesai = new JButton("\u2713 Selesaikan");
        btnSelesai.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSelesai.addActionListener(e -> selesaikan());
        bottomPanel.add(btnSelesai);

        JButton btnHapus = new JButton("Hapus");
        btnHapus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnHapus.addActionListener(e -> hapusRegistrasi());
        bottomPanel.add(btnHapus);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.addActionListener(e -> { selectedRegId = -1; loadTable(); lblInfo.setText(" "); });
        bottomPanel.add(btnRefresh);

        bottomPanel.add(Box.createHorizontalStrut(20));
        lblInfo = new JLabel(" ");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(new Color(33, 100, 180));
        bottomPanel.add(lblInfo);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ==================== POPUP FORM DIALOG ====================
    private void showFormDialog(ServiceRegistration existing) {
        Window win = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(win instanceof Frame ? (Frame) win : null,
            existing == null ? "Daftar Pendaftaran Baru" : "Edit Pendaftaran #" + existing.getRegistrationId(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 380);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cbClient = new JComboBox<>();
        JComboBox<String> cbVehicle = new JComboBox<>();
        JComboBox<String> cbMekanik = new JComboBox<>();
        JTextArea txtKeluhan = new JTextArea(3, 20);
        txtKeluhan.setLineWrap(true);
        JTextArea txtCatatan = new JTextArea(2, 20);
        txtCatatan.setLineWrap(true);

        // Load combos
        try {
            clientList = clientDAO.findAll();
            mekanikList = mekanikDAO.findAll();
        } catch (SQLException ex) { /* already loaded or empty */ }

        cbClient.addItem("-- Pilih Client --");
        for (Client c : clientList) cbClient.addItem(c.getClientId() + " - " + c.getNama());

        cbVehicle.addItem("-- Pilih Kendaraan --");
        cbClient.addActionListener(e -> {
            cbVehicle.removeAllItems();
            cbVehicle.addItem("-- Pilih Kendaraan --");
            if (cbClient.getSelectedIndex() <= 0) return;
            try {
                int clientId = Integer.parseInt(cbClient.getSelectedItem().toString().split(" - ")[0].trim());
                List<Vehicle> vehicles = vehicleDAO.findByClientId(clientId);
                for (Vehicle v : vehicles) cbVehicle.addItem(v.getVehicleId() + " - " + v.getNoPolisi() + " (" + v.getMerk() + ")");
            } catch (SQLException ex) { /* ignore */ }
        });

        cbMekanik.addItem("-- Pilih Mekanik --");
        for (Mekanik m : mekanikList) cbMekanik.addItem(m.getMekanikId() + " - " + m.getNama());

        // Prefill if editing
        if (existing != null) {
            for (int i = 0; i < cbClient.getItemCount(); i++) {
                if (cbClient.getItemAt(i).startsWith(existing.getClientId() + " - ")) { cbClient.setSelectedIndex(i); break; }
            }
            // Trigger vehicle load then select
            SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < cbVehicle.getItemCount(); i++) {
                    if (cbVehicle.getItemAt(i).startsWith(existing.getVehicleId() + " - ")) { cbVehicle.setSelectedIndex(i); break; }
                }
            });
            for (int i = 0; i < cbMekanik.getItemCount(); i++) {
                if (cbMekanik.getItemAt(i).startsWith(existing.getMekanikId() + " - ")) { cbMekanik.setSelectedIndex(i); break; }
            }
            txtKeluhan.setText(existing.getKeluhan() != null ? existing.getKeluhan() : "");
            txtCatatan.setText(existing.getCatatan() != null ? existing.getCatatan() : "");
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Client:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(cbClient, gbc); gbc.weightx = 0;
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Kendaraan:"), gbc);
        gbc.gridx = 1; form.add(cbVehicle, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Mekanik:"), gbc);
        gbc.gridx = 1; form.add(cbMekanik, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Keluhan:"), gbc);
        gbc.gridx = 1; form.add(new JScrollPane(txtKeluhan), gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Catatan:"), gbc);
        gbc.gridx = 1; form.add(new JScrollPane(txtCatatan), gbc);

        dialog.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.addActionListener(e -> {
            if (cbClient.getSelectedIndex() <= 0 || cbVehicle.getSelectedIndex() <= 0 || cbMekanik.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(dialog, "Pilih Client, Kendaraan, dan Mekanik.");
                return;
            }
            if (txtKeluhan.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Keluhan tidak boleh kosong.");
                return;
            }
            try {
                int clientId = Integer.parseInt(cbClient.getSelectedItem().toString().split(" - ")[0].trim());
                int vehicleId = Integer.parseInt(cbVehicle.getSelectedItem().toString().split(" - ")[0].trim());
                int mekanikId = Integer.parseInt(cbMekanik.getSelectedItem().toString().split(" - ")[0].trim());

                if (existing == null) {
                    ServiceRegistration reg = new ServiceRegistration();
                    reg.setClientId(clientId);
                    reg.setVehicleId(vehicleId);
                    reg.setMekanikId(mekanikId);
                    reg.setKeluhan(txtKeluhan.getText().trim());
                    reg.setCatatan(txtCatatan.getText().trim());
                    reg.setStatus("Registered");
                    reg.setTanggalDaftar(new Date());
                    regDAO.insert(reg);
                    JOptionPane.showMessageDialog(dialog, "Pendaftaran berhasil disimpan.");
                } else {
                    existing.setClientId(clientId);
                    existing.setVehicleId(vehicleId);
                    existing.setMekanikId(mekanikId);
                    existing.setKeluhan(txtKeluhan.getText().trim());
                    existing.setCatatan(txtCatatan.getText().trim());
                    regDAO.update(existing);
                    JOptionPane.showMessageDialog(dialog, "Pendaftaran berhasil diupdate.");
                }
                dialog.dispose();
                loadTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnSimpan);
        btnPanel.add(btnBatal);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ==================== ACTIONS ====================
    private void editRegistrasi() {
        if (selectedRegId < 0) { JOptionPane.showMessageDialog(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceRegistration reg = regDAO.findById(selectedRegId);
            if (reg == null) { JOptionPane.showMessageDialog(this, "Data tidak ditemukan."); return; }
            showFormDialog(reg);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void mulaiServis() {
        if (selectedRegId < 0) { JOptionPane.showMessageDialog(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceRegistration reg = regDAO.findById(selectedRegId);
            if (reg == null) { JOptionPane.showMessageDialog(this, "Data tidak ditemukan."); return; }
            if (!"Registered".equals(reg.getStatus())) {
                JOptionPane.showMessageDialog(this, "Hanya status 'Registered' yang bisa dimulai servis.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                "Mulai servis untuk pendaftaran #" + selectedRegId + "?\n" +
                "Transaksi akan otomatis dibuat.", "Konfirmasi Mulai Servis", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            // Auto-create transaction from registration data
            ServiceTransaction t = new ServiceTransaction();
            t.setTanggal(new Date());
            t.setClientId(reg.getClientId());
            t.setVehicleId(reg.getVehicleId());
            t.setMekanikId(reg.getMekanikId());
            t.setKeluhan(reg.getKeluhan());
            t.setRegistrationId(reg.getRegistrationId());
            t.setStatusServis("Dikerjakan");
            t.setTotalJasa(0);
            t.setTotalSparepart(0);
            t.setGrandTotal(0);
            t.setBayar(0);
            t.setKembali(0);
            t.setMetodeBayar("Cash");
            t.setUserKasir("admin");
            t.setDetails(new java.util.ArrayList<>());
            int transId = transDAO.insertWithDetails(t);

            // Update registration status
            reg.setStatus("InProgress");
            reg.setTanggalMulai(new Date());
            regDAO.update(reg);

            loadTable();
            selectedRegId = -1;
            lblInfo.setText("Servis dimulai! Transaksi #" + transId + " telah dibuat.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void editTransaksi() {
        if (selectedRegId < 0) { JOptionPane.showMessageDialog(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceRegistration reg = regDAO.findById(selectedRegId);
            if (reg == null) { JOptionPane.showMessageDialog(this, "Data tidak ditemukan."); return; }
            if (!"InProgress".equals(reg.getStatus())) {
                JOptionPane.showMessageDialog(this, "Hanya status 'InProgress' yang bisa diedit transaksinya.");
                return;
            }
            ServiceTransaction trans = transDAO.findByRegistrationId(selectedRegId);
            if (trans == null) {
                JOptionPane.showMessageDialog(this, "Transaksi belum dibuat. Klik 'Mulai Servis' terlebih dahulu.");
                return;
            }
            Window win = SwingUtilities.getWindowAncestor(this);
            Frame owner = (win instanceof Frame) ? (Frame) win : null;
            ServiceTransactionFrame frame = new ServiceTransactionFrame(owner, trans.getTransId());
            frame.setVisible(true);
            loadTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void selesaikan() {
        if (selectedRegId < 0) { JOptionPane.showMessageDialog(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceRegistration reg = regDAO.findById(selectedRegId);
            if (reg == null) { JOptionPane.showMessageDialog(this, "Data tidak ditemukan."); return; }
            if ("Completed".equals(reg.getStatus())) { JOptionPane.showMessageDialog(this, "Sudah selesai."); return; }
            if (!"InProgress".equals(reg.getStatus())) {
                JOptionPane.showMessageDialog(this, "Hanya status 'InProgress' yang bisa diselesaikan.");
                return;
            }
            ServiceTransaction trans = transDAO.findByRegistrationId(selectedRegId);
            if (trans == null) {
                JOptionPane.showMessageDialog(this, "Transaksi belum dibuat.");
                return;
            }
            // Open transaction for final review
            Window win = SwingUtilities.getWindowAncestor(this);
            Frame owner = (win instanceof Frame) ? (Frame) win : null;
            ServiceTransactionFrame frame = new ServiceTransactionFrame(owner, trans.getTransId());
            frame.setVisible(true);

            // After review, confirm completion
            int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah semua detail transaksi sudah benar?\nTandai pendaftaran #" + selectedRegId + " sebagai SELESAI?",
                "Konfirmasi Penyelesaian", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                reg.setStatus("Completed");
                regDAO.update(reg);
                transDAO.updateStatusServis(trans.getTransId(), "Selesai Lunas");
            }
            loadTable();
            selectedRegId = -1;
            lblInfo.setText(" ");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void hapusRegistrasi() {
        if (selectedRegId < 0) { JOptionPane.showMessageDialog(this, "Pilih pendaftaran dari tabel."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus pendaftaran #" + selectedRegId + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            regDAO.delete(selectedRegId);
            loadTable();
            selectedRegId = -1;
            lblInfo.setText(" ");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ==================== TABLE ====================
    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            String statusFilter = cbStatusFilter.getSelectedItem().toString();
            List<ServiceRegistration> list;
            if ("Semua".equals(statusFilter)) list = regDAO.findAll();
            else list = regDAO.findByStatus(statusFilter);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (ServiceRegistration r : list) {
                String clientNama = "", vehicleNopol = "", mekanikNama = "";
                try {
                    Client c = clientDAO.findById(r.getClientId());
                    if (c != null) clientNama = c.getNama();
                    List<Vehicle> vs = vehicleDAO.findByClientId(r.getClientId());
                    for (Vehicle v : vs) if (v.getVehicleId() == r.getVehicleId()) { vehicleNopol = v.getNoPolisi(); break; }
                    Mekanik m = mekanikDAO.findById(r.getMekanikId());
                    if (m != null) mekanikNama = m.getNama();
                } catch (SQLException ex) { /* skip */ }

                tableModel.addRow(new Object[]{
                    r.getRegistrationId(),
                    r.getTanggalDaftar() != null ? sdf.format(r.getTanggalDaftar()) : "",
                    clientNama, vehicleNopol, mekanikNama,
                    r.getKeluhan(), r.getStatus(), r.getCatatan()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load: " + ex.getMessage());
        }
    }

    private void filterTable() {
        String text = txtSearch.getText().trim();
        if (table.getRowSorter() == null) {
            table.setRowSorter(new javax.swing.table.TableRowSorter<>(tableModel));
        }
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                (javax.swing.table.TableRowSorter<DefaultTableModel>) table.getRowSorter();
        if (text.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
    }
}
