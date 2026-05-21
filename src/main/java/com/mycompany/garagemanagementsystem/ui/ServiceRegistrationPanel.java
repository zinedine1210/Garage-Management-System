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
import com.mycompany.garagemanagementsystem.model.TransactionDetail;
import com.mycompany.garagemanagementsystem.model.Vehicle;
import com.mycompany.garagemanagementsystem.util.StyledTable;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Panel Pendaftaran Servis - revamped dengan StyledTable.
 */
public class ServiceRegistrationPanel extends javax.swing.JPanel {

    private final ServiceRegistrationDAO regDAO = new ServiceRegistrationDAO();
    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MekanikDAO mekanikDAO = new MekanikDAO();

    private List<Client> clientList = new ArrayList<>();
    private List<Mekanik> mekanikList = new ArrayList<>();

    private StyledTable styledTable;
    private JComboBox<String> cbStatusFilter;
    private JTextField txtSearch;
    private JLabel lblInfo;
    private int selectedRegId = -1;
    private JButton btnDetail;

    public ServiceRegistrationPanel() {
        buildUI();
        loadTable();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        // ===== TOP =====
        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setOpaque(false);

        // Page header
        topPanel.add(UIHelper.createPageHeader("Pendaftaran Servis",
                "Kelola pendaftaran servis kendaraan pelanggan — dari registrasi hingga penyelesaian"), BorderLayout.NORTH);

        // Flow info + filter
        JPanel midPanel = new JPanel(new BorderLayout(0, 6));
        midPanel.setOpaque(false);

        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        flowPanel.setBackground(new Color(232, 245, 253));
        flowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 180, 246)),
                new EmptyBorder(6, 10, 6, 10)));
        JLabel lblFlow = new JLabel(
                "<html><b>Flow:</b> Customer Datang → <b>Daftar Baru</b> → <b>Mulai Servis</b> → "
                + "<b>Edit Transaksi</b> (isi detail) → <b>Selesaikan</b></html>");
        lblFlow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        flowPanel.add(lblFlow);
        midPanel.add(flowPanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(6, 12, 6, 12)));

        filterPanel.add(new JLabel("🔍 Cari:"));
        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        filterPanel.add(txtSearch);

        filterPanel.add(new JLabel("Status:"));
        cbStatusFilter = new JComboBox<>(new String[]{"Semua", "Registered", "InProgress", "Completed"});
        cbStatusFilter.addActionListener(e -> applyFilter());
        filterPanel.add(cbStatusFilter);

        lblInfo = new JLabel(" ");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(new Color(33, 100, 180));
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(lblInfo);

        midPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(midPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // ===== CENTER =====
        styledTable = new StyledTable();
        styledTable.addSelectionListener(e -> {
            Object[] row = styledTable.getSelectedRowData();
            if (row != null) {
                selectedRegId = (int) row[0];
                lblInfo.setText("Dipilih: #" + selectedRegId + " | Status: " + row[6]);
            }
        });
        add(styledTable, BorderLayout.CENTER);

        // ===== BOTTOM =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        buttonPanel.setBackground(new Color(243, 245, 249));

        JButton btnDaftar = UIHelper.createStyledButton("+ Daftar Baru", new Color(40, 167, 69));
        btnDaftar.addActionListener(e -> showFormDialog(null));
        buttonPanel.add(btnDaftar);

        JButton btnEdit = UIHelper.createStyledButton("Edit", new Color(0, 123, 255));
        btnEdit.addActionListener(e -> editRegistrasi());
        buttonPanel.add(btnEdit);

        JButton btnMulai = UIHelper.createStyledButton("▶ Mulai Servis", new Color(23, 162, 184));
        btnMulai.addActionListener(e -> mulaiServis());
        buttonPanel.add(btnMulai);

        JButton btnEditTrans = UIHelper.createStyledButton("✎ Edit Transaksi", new Color(108, 117, 125));
        btnEditTrans.addActionListener(e -> editTransaksi());
        buttonPanel.add(btnEditTrans);

        JButton btnSelesai = UIHelper.createStyledButton("✓ Selesaikan", new Color(139, 92, 246));
        btnSelesai.addActionListener(e -> selesaikan());
        buttonPanel.add(btnSelesai);

        JButton btnHapus = UIHelper.createStyledButton("Hapus", new Color(220, 53, 69));
        btnHapus.addActionListener(e -> hapusRegistrasi());
        buttonPanel.add(btnHapus);

        JButton btnRefresh = UIHelper.createStyledButton("Refresh", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> { selectedRegId = -1; loadTable(); lblInfo.setText(" "); });
        buttonPanel.add(btnRefresh);

        btnDetail = UIHelper.createStyledButton("\uD83D\uDCCB Lihat Detail", new Color(13, 110, 253));
        btnDetail.setVisible(false);
        btnDetail.addActionListener(e -> showDetail());
        buttonPanel.add(btnDetail);

        // Show Detail button only for Completed registrations
        styledTable.addSelectionListener(e -> {
            Object[] row = styledTable.getSelectedRowData();
            boolean show = row != null && row[6] != null && "Completed".equalsIgnoreCase(row[6].toString());
            btnDetail.setVisible(show);
        });

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // ==================== FILTER ====================
    private void applyFilter() {
        String text = txtSearch.getText().trim().toLowerCase();
        String status = cbStatusFilter.getSelectedItem().toString();

        List<Object[]> result = new ArrayList<>();
        for (Object[] row : styledTable.getAllData()) {
            if (!"Semua".equals(status)) {
                String rowStatus = row[6] != null ? row[6].toString() : "";
                if (!rowStatus.equalsIgnoreCase(status)) continue;
            }
            if (!text.isEmpty()) {
                boolean found = false;
                for (Object cell : row) {
                    if (cell != null && cell.toString().toLowerCase().contains(text)) { found = true; break; }
                }
                if (!found) continue;
            }
            result.add(row);
        }
        styledTable.setFilteredData(result);
    }

    // ==================== POPUP FORM DIALOG ====================
    private void showFormDialog(ServiceRegistration existing) {
        Window win = SwingUtilities.getWindowAncestor(this);
        String dlgTitle = existing == null ? "Pendaftaran Servis Baru" : "Edit Pendaftaran #" + existing.getRegistrationId();
        String dlgSub = "Isi form di bawah untuk mendaftarkan servis kendaraan";
        JDialog dialog = new JDialog(win instanceof Frame ? (Frame) win : null, dlgTitle, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 440);
        dialog.setLocationRelativeTo(this);

        dialog.add(UIHelper.createDialogHeader(dlgTitle, dlgSub), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 15, 10, 15));
        form.setBackground(Color.WHITE);
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

        try {
            clientList = clientDAO.findAll();
            mekanikList = mekanikDAO.findAll();
        } catch (SQLException ex) { /* ignore */ }

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

        if (existing != null) {
            for (int i = 0; i < cbClient.getItemCount(); i++) {
                if (cbClient.getItemAt(i).startsWith(existing.getClientId() + " - ")) { cbClient.setSelectedIndex(i); break; }
            }
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

        JPanel btnPanel = UIHelper.createDialogButtonPanel();

        JButton btnBatal = UIHelper.createStyledButton("Batal", new Color(108, 117, 125));
        btnBatal.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnBatal);

        JButton btnSimpan = UIHelper.createStyledButton("Simpan", new Color(40, 167, 69));
        btnSimpan.addActionListener(e -> {
            if (cbClient.getSelectedIndex() <= 0 || cbVehicle.getSelectedIndex() <= 0 || cbMekanik.getSelectedIndex() <= 0) {
                UIHelper.warn(dialog, "Pilih Client, Kendaraan, dan Mekanik.");
                return;
            }
            if (txtKeluhan.getText().trim().isEmpty()) {
                UIHelper.warn(dialog, "Keluhan tidak boleh kosong.");
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
                    UIHelper.success(dialog, "Pendaftaran berhasil disimpan.");
                } else {
                    existing.setClientId(clientId);
                    existing.setVehicleId(vehicleId);
                    existing.setMekanikId(mekanikId);
                    existing.setKeluhan(txtKeluhan.getText().trim());
                    existing.setCatatan(txtCatatan.getText().trim());
                    regDAO.update(existing);
                    UIHelper.success(dialog, "Pendaftaran berhasil diupdate.");
                }
                dialog.dispose();
                loadTable();
            } catch (SQLException ex) {
                UIHelper.error(dialog, "Error: " + ex.getMessage());
            }
        });
        btnPanel.add(btnSimpan);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ==================== ACTIONS ====================
    private void editRegistrasi() {
        if (selectedRegId < 0) { UIHelper.warn(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceRegistration reg = regDAO.findById(selectedRegId);
            if (reg == null) { UIHelper.warn(this, "Data tidak ditemukan."); return; }
            showFormDialog(reg);
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    private void mulaiServis() {
        if (selectedRegId < 0) { UIHelper.warn(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceRegistration reg = regDAO.findById(selectedRegId);
            if (reg == null) { UIHelper.warn(this, "Data tidak ditemukan."); return; }
            if (!"Registered".equals(reg.getStatus())) {
                UIHelper.warn(this, "Hanya status 'Registered' yang bisa dimulai servis.");
                return;
            }
            if (!UIHelper.confirm(this,
                    "Mulai servis untuk pendaftaran #" + selectedRegId + "?\nTransaksi akan otomatis dibuat.",
                    "Konfirmasi Mulai Servis")) return;

            ServiceTransaction t = new ServiceTransaction();
            t.setTanggal(new Date());
            t.setClientId(reg.getClientId());
            t.setVehicleId(reg.getVehicleId());
            t.setMekanikId(reg.getMekanikId());
            t.setKeluhan(reg.getKeluhan());
            t.setRegistrationId(reg.getRegistrationId());
            t.setStatusServis("Dikerjakan");
            t.setTotalJasa(0); t.setTotalSparepart(0); t.setGrandTotal(0);
            t.setBayar(0); t.setKembali(0);
            t.setMetodeBayar("Cash"); t.setUserKasir("admin");
            t.setDetails(new ArrayList<>());
            int transId = transDAO.insertWithDetails(t);

            reg.setStatus("InProgress");
            reg.setTanggalMulai(new Date());
            regDAO.update(reg);

            loadTable();
            selectedRegId = -1;
            lblInfo.setText("Servis dimulai! Transaksi #" + transId + " telah dibuat.");
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    private void editTransaksi() {
        if (selectedRegId < 0) { UIHelper.warn(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceRegistration reg = regDAO.findById(selectedRegId);
            if (reg == null) { UIHelper.warn(this, "Data tidak ditemukan."); return; }
            if (!"InProgress".equals(reg.getStatus())) {
                UIHelper.warn(this, "Hanya status 'InProgress' yang bisa diedit transaksinya.");
                return;
            }
            ServiceTransaction trans = transDAO.findByRegistrationId(selectedRegId);
            if (trans == null) {
                UIHelper.warn(this, "Transaksi belum dibuat. Klik 'Mulai Servis' terlebih dahulu.");
                return;
            }
            Window win = SwingUtilities.getWindowAncestor(this);
            Frame owner = (win instanceof Frame) ? (Frame) win : null;
            new ServiceTransactionFrame(owner, trans.getTransId()).setVisible(true);
            loadTable();
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    private void selesaikan() {
        if (selectedRegId < 0) { UIHelper.warn(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceRegistration reg = regDAO.findById(selectedRegId);
            if (reg == null) { UIHelper.warn(this, "Data tidak ditemukan."); return; }
            if ("Completed".equals(reg.getStatus())) { UIHelper.info(this, "Sudah selesai."); return; }
            if (!"InProgress".equals(reg.getStatus())) {
                UIHelper.warn(this, "Hanya status 'InProgress' yang bisa diselesaikan.");
                return;
            }
            ServiceTransaction trans = transDAO.findByRegistrationId(selectedRegId);
            if (trans == null) { UIHelper.warn(this, "Transaksi belum dibuat."); return; }

            Window win = SwingUtilities.getWindowAncestor(this);
            Frame owner = (win instanceof Frame) ? (Frame) win : null;
            new ServiceTransactionFrame(owner, trans.getTransId()).setVisible(true);

            if (UIHelper.confirm(this,
                    "Tandai pendaftaran #" + selectedRegId + " sebagai SELESAI?",
                    "Konfirmasi Penyelesaian")) {
                reg.setStatus("Completed");
                regDAO.update(reg);
                transDAO.updateStatusServis(trans.getTransId(), "Selesai Lunas");
            }
            loadTable();
            selectedRegId = -1;
            lblInfo.setText(" ");
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    private void hapusRegistrasi() {
        if (selectedRegId < 0) { UIHelper.warn(this, "Pilih pendaftaran dari tabel."); return; }
        if (!UIHelper.confirm(this, "Hapus pendaftaran #" + selectedRegId + "?", "Konfirmasi Hapus")) return;
        try {
            regDAO.delete(selectedRegId);
            loadTable();
            selectedRegId = -1;
            lblInfo.setText(" ");
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    private void showDetail() {
        if (selectedRegId < 0) { UIHelper.warn(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceTransaction t = transDAO.findByRegistrationId(selectedRegId);
            if (t == null) { UIHelper.warn(this, "Transaksi belum dibuat untuk pendaftaran ini."); return; }
            t = transDAO.findByIdFull(t.getTransId());
            if (t == null) { UIHelper.error(this, "Detail transaksi tidak ditemukan."); return; }

            StringBuilder sb = new StringBuilder();
            sb.append("══════════════════════════════\n");
            sb.append("   DETAIL PENDAFTARAN SERVIS\n");
            sb.append("══════════════════════════════\n\n");
            sb.append("No. Registrasi: REG-").append(String.format("%05d", selectedRegId)).append("\n");
            sb.append("No. Transaksi : TRX-").append(String.format("%05d", t.getTransId())).append("\n");
            sb.append("Tanggal       : ").append(t.getTanggal() != null ? new SimpleDateFormat("dd-MM-yyyy HH:mm").format(t.getTanggal()) : "-").append("\n");
            sb.append("Status        : ").append(t.getStatusServis()).append("\n");
            sb.append("Metode Bayar  : ").append(t.getMetodeBayar() != null ? t.getMetodeBayar() : "Cash").append("\n");

            sb.append("\n── Pelanggan & Kendaraan ──\n");
            sb.append("Pelanggan     : ").append(t.getClientNama() != null ? t.getClientNama() : "-").append("\n");
            sb.append("No. Polisi    : ").append(t.getNoPolisi() != null ? t.getNoPolisi() : "-").append("\n");
            sb.append("Mekanik       : ").append(t.getMekanikNama() != null ? t.getMekanikNama() : "-").append("\n");

            sb.append("\n── Keluhan ──\n");
            sb.append(t.getKeluhan() != null && !t.getKeluhan().isEmpty() ? t.getKeluhan() : "-").append("\n");

            if (t.getDetails() != null && !t.getDetails().isEmpty()) {
                sb.append("\n── Sparepart ──\n");
                for (int i = 0; i < t.getDetails().size(); i++) {
                    TransactionDetail d = t.getDetails().get(i);
                    sb.append(String.format("  %d. %s (Qty: %d)\n", i + 1,
                            d.getSparepartNama() != null ? d.getSparepartNama() : "Sparepart #" + d.getSparepartId(),
                            d.getQty()));
                }
            }

            sb.append("\n── Total ──\n");
            sb.append("Total Jasa      : Rp ").append(String.format("%,.0f", t.getTotalJasa())).append("\n");
            sb.append("Total Sparepart : Rp ").append(String.format("%,.0f", t.getTotalSparepart())).append("\n");
            sb.append("Grand Total     : Rp ").append(String.format("%,.0f", t.getGrandTotal())).append("\n");
            sb.append("Bayar           : Rp ").append(String.format("%,.0f", t.getBayar())).append("\n");
            sb.append("Kembali         : Rp ").append(String.format("%,.0f", Math.max(0, t.getKembali()))).append("\n");

            JTextArea ta = new JTextArea(sb.toString());
            ta.setEditable(false);
            ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
            ta.setBackground(new Color(245, 245, 250));
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(420, 450));
            Window win = SwingUtilities.getWindowAncestor(this);
            Frame owner = (win instanceof Frame) ? (Frame) win : null;
            JOptionPane.showMessageDialog(owner, sp, "Detail Pendaftaran - REG-" + String.format("%05d", selectedRegId), JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    // ==================== TABLE ====================
    private void loadTable() {
        try {
            List<ServiceRegistration> list = regDAO.findAll();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            List<Object[]> data = new ArrayList<>();
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

                data.add(new Object[]{
                        r.getRegistrationId(),
                        r.getTanggalDaftar() != null ? sdf.format(r.getTanggalDaftar()) : "",
                        clientNama, vehicleNopol, mekanikNama,
                        r.getKeluhan(), r.getStatus(), r.getCatatan()
                });
            }
            styledTable.setData(new String[]{"ID", "Tanggal Daftar", "Client", "Kendaraan", "Mekanik", "Keluhan", "Status", "Catatan"}, data);
            // Apply current filter
            applyFilter();
        } catch (SQLException ex) {
            UIHelper.error(this, "Error load: " + ex.getMessage());
        }
    }
}
