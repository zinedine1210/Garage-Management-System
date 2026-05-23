package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.*;
import com.mycompany.garagemanagementsystem.model.*;
import com.mycompany.garagemanagementsystem.util.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Revamped Service Registration Panel with step-by-step wizard flow.
 * Jasa items are free-form (nama + harga + qty), no master data needed.
 * Also handles sparepart-only purchases (tanpa servis).
 */
public class ServiceRegistrationPanel extends javax.swing.JPanel {

    private final ServiceRegistrationDAO regDAO = new ServiceRegistrationDAO();
    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MekanikDAO mekanikDAO = new MekanikDAO();
    private final SparepartDAO sparepartDAO = new SparepartDAO();

    private StyledTable styledTable;
    private JComboBox<String> cbStatusFilter;
    private JTextField txtSearch;
    private JLabel lblInfo;
    private int selectedRegId = -1;

    // Contextual action buttons
    private JButton btnMulai, btnEditDetail, btnSelesai, btnLihatDetail, btnPrintNota;

    public ServiceRegistrationPanel() {
        buildUI();
        loadTable();
    }

    /** Opens the sparepart-only purchase wizard directly */
    public void openSparepartPurchaseWizard() {
        SwingUtilities.invokeLater(() -> openServiceWizard(null, false, true));
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        // ===== TOP =====
        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setOpaque(false);

        topPanel.add(UIHelper.createPageHeader("Pendaftaran & Servis",
                "Kelola servis kendaraan dari pendaftaran hingga selesai — step by step"), BorderLayout.NORTH);

        // Flow info
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        flowPanel.setBackground(new Color(232, 245, 253));
        flowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 180, 246)),
                new EmptyBorder(6, 10, 6, 10)));
        JLabel lblFlow = new JLabel(
                "<html><b>Flow:</b> <span style='color:#28a745'>Daftar Baru</span> \u2192 "
                + "<span style='color:#17a2b8'>Mulai Servis</span> \u2192 "
                + "<span style='color:#6c757d'>Edit Detail (jika perlu)</span> \u2192 "
                + "<span style='color:#8b5cf6'>Selesaikan & Bayar</span> \u2192 "
                + "<span style='color:#0d6efd'>Lihat / Print</span></html>");
        lblFlow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        flowPanel.add(lblFlow);

        JPanel midPanel = new JPanel(new BorderLayout(0, 6));
        midPanel.setOpaque(false);
        midPanel.add(flowPanel, BorderLayout.NORTH);

        // Filter bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(6, 12, 6, 12)));

        filterPanel.add(new JLabel("\uD83D\uDD0D Cari:"));
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

        // ===== CENTER - Table =====
        styledTable = new StyledTable();
        styledTable.addSelectionListener(e -> {
            Object[] row = styledTable.getSelectedRowData();
            if (row != null) {
                selectedRegId = (int) row[0];
                String status = row[6] != null ? row[6].toString() : "";
                lblInfo.setText("Dipilih: REG-" + String.format("%05d", selectedRegId) + " | Status: " + status);
                updateButtonVisibility(status);
            } else {
                updateButtonVisibility("");
            }
        });
        add(styledTable, BorderLayout.CENTER);

        // ===== BOTTOM - Action Buttons =====
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(243, 245, 249));
        buttonPanel.setBorder(new EmptyBorder(6, 0, 6, 0));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftButtons.setOpaque(false);

        // Always visible
        JButton btnDaftar = UIHelper.createStyledButton("+ Daftar Servis Baru", new Color(40, 167, 69));
        btnDaftar.addActionListener(e -> openServiceWizard(null, false, false));
        leftButtons.add(btnDaftar);

        JButton btnBeliSp = UIHelper.createStyledButton("+ Pembelian Sparepart", new Color(23, 162, 184));
        btnBeliSp.addActionListener(e -> openServiceWizard(null, false, true));
        leftButtons.add(btnBeliSp);

        // Contextual buttons - shown based on status
        btnMulai = UIHelper.createStyledButton("\u25B6 Mulai Servis", new Color(23, 162, 184));
        btnMulai.setVisible(false);
        btnMulai.addActionListener(e -> mulaiServis());
        leftButtons.add(btnMulai);

        btnEditDetail = UIHelper.createStyledButton("\u270E Edit Detail", new Color(108, 117, 125));
        btnEditDetail.setVisible(false);
        btnEditDetail.addActionListener(e -> openServiceWizard(getSelectedRegistration(), true, false));
        leftButtons.add(btnEditDetail);

        btnSelesai = UIHelper.createStyledButton("\u2713 Selesaikan & Bayar", new Color(139, 92, 246));
        btnSelesai.setVisible(false);
        btnSelesai.addActionListener(e -> selesaikanServis());
        leftButtons.add(btnSelesai);

        btnLihatDetail = UIHelper.createStyledButton("\uD83D\uDCCB Lihat Detail", new Color(13, 110, 253));
        btnLihatDetail.setVisible(false);
        btnLihatDetail.addActionListener(e -> lihatDetail());
        leftButtons.add(btnLihatDetail);

        btnPrintNota = UIHelper.createStyledButton("\uD83D\uDDA8 Print Nota", new Color(139, 92, 246));
        btnPrintNota.setVisible(false);
        btnPrintNota.addActionListener(e -> printNota());
        leftButtons.add(btnPrintNota);

        // Always visible utilities
        JButton btnHapus = UIHelper.createStyledButton("Hapus", new Color(220, 53, 69));
        btnHapus.addActionListener(e -> hapusRegistrasi());
        leftButtons.add(btnHapus);

        buttonPanel.add(leftButtons, BorderLayout.WEST);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setOpaque(false);

        JButton btnRefresh = UIHelper.createStyledButton("Refresh", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> { selectedRegId = -1; loadTable(); lblInfo.setText(" "); updateButtonVisibility(""); });
        rightButtons.add(btnRefresh);

        buttonPanel.add(rightButtons, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateButtonVisibility(String status) {
        boolean isRegistered = "Registered".equalsIgnoreCase(status);
        boolean isInProgress = "InProgress".equalsIgnoreCase(status);
        boolean isCompleted = "Completed".equalsIgnoreCase(status);

        btnMulai.setVisible(isRegistered);
        btnEditDetail.setVisible(isRegistered || isInProgress);
        btnSelesai.setVisible(isInProgress);
        btnLihatDetail.setVisible(isCompleted);
        btnPrintNota.setVisible(isCompleted);
    }

    private ServiceRegistration getSelectedRegistration() {
        if (selectedRegId < 0) return null;
        try {
            return regDAO.findById(selectedRegId);
        } catch (SQLException ex) {
            return null;
        }
    }

    // ==================== WIZARD DIALOG ====================

    /**
     * @param existing   null for new, or existing registration for edit
     * @param editMode   if true, start at step 2
     * @param sparepartOnly  if true, simplified wizard for sparepart purchase only
     */
    private void openServiceWizard(ServiceRegistration existing, boolean editMode, boolean sparepartOnly) {
        Window win = SwingUtilities.getWindowAncestor(this);
        Frame owner = (win instanceof Frame) ? (Frame) win : null;

        boolean isNew = (existing == null);
        String title;
        if (sparepartOnly) {
            title = "Pembelian Sparepart";
        } else {
            title = isNew ? "Pendaftaran Servis Baru" : "Edit Detail Servis #" + existing.getRegistrationId();
        }

        JDialog dialog = new JDialog(owner, title, true);
        dialog.setSize(950, 680);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Load master data
        List<Client> clientList = new ArrayList<>();
        List<Mekanik> mekanikList = new ArrayList<>();
        List<Sparepart> sparepartList = new ArrayList<>();
        try {
            clientList = clientDAO.findAll();
            mekanikList = mekanikDAO.findAll();
            sparepartList = sparepartDAO.findAll();
        } catch (SQLException ex) { /* ignore */ }

        // ===== STEP PANEL with CardLayout =====
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        int totalSteps = sparepartOnly ? 2 : 3;

        // Step indicators
        JLabel lblStep = new JLabel("Langkah 1 dari " + totalSteps);
        lblStep.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStep.setForeground(new Color(13, 110, 253));
        JLabel lblStepTitle = new JLabel("");
        lblStepTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStepTitle.setForeground(new Color(108, 117, 125));

        JPanel stepIndicator = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        stepIndicator.setBackground(new Color(232, 245, 253));
        stepIndicator.setBorder(new EmptyBorder(4, 12, 4, 12));
        stepIndicator.add(lblStep);
        stepIndicator.add(lblStepTitle);
        dialog.add(stepIndicator, BorderLayout.NORTH);

        // ---------- STEP 1: Customer, Vehicle, Mekanik ----------
        JPanel step1 = new JPanel(new BorderLayout(8, 8));
        step1.setBorder(new EmptyBorder(10, 15, 10, 15));
        step1.setBackground(Color.WHITE);

        // Tracking selected IDs (mutable via array for closure)
        final int[] selectedClientId = {0};
        final int[] selectedVehicleId = {0};

        // --- Top half: Customer table ---
        JPanel customerSection = new JPanel(new BorderLayout(4, 4));
        customerSection.setOpaque(false);

        JPanel customerHeader = new JPanel(new BorderLayout(6, 0));
        customerHeader.setOpaque(false);
        JLabel lblCust = new JLabel("Pilih Customer");
        lblCust.setFont(new Font("Segoe UI", Font.BOLD, 13));
        customerHeader.add(lblCust, BorderLayout.WEST);

        JPanel custSearchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        custSearchRow.setOpaque(false);
        JTextField txtCustSearch = new JTextField(16);
        txtCustSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCustSearch.setToolTipText("Cari nama / telepon");
        custSearchRow.add(new JLabel("Cari:"));
        custSearchRow.add(txtCustSearch);

        JButton btnNewClient = new JButton("+");
        btnNewClient.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNewClient.setForeground(Color.WHITE);
        btnNewClient.setBackground(new Color(40, 167, 69));
        btnNewClient.setFocusPainted(false); btnNewClient.setBorderPainted(false); btnNewClient.setOpaque(true);
        btnNewClient.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNewClient.setToolTipText("Tambah Customer Baru");
        btnNewClient.setPreferredSize(new Dimension(36, 26));
        custSearchRow.add(btnNewClient);
        customerHeader.add(custSearchRow, BorderLayout.EAST);
        customerSection.add(customerHeader, BorderLayout.NORTH);

        DefaultTableModel custModel = new DefaultTableModel(
                new Object[]{"ID", "Nama", "Telepon", "Alamat"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblCustomer = new JTable(custModel);
        tblCustomer.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCustomer.setRowHeight(24);
        tblCustomer.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tblCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCustomer.getColumnModel().getColumn(0).setMaxWidth(50);

        // Populate customer table
        final List<Client> fClientList = clientList;
        Runnable refreshCustomerTable = () -> {
            String q = txtCustSearch.getText().trim().toLowerCase();
            custModel.setRowCount(0);
            for (Client c : fClientList) {
                String searchStr = (c.getNama() + " " + (c.getTelepon() != null ? c.getTelepon() : "")).toLowerCase();
                if (q.isEmpty() || searchStr.contains(q)) {
                    custModel.addRow(new Object[]{c.getClientId(), c.getNama(),
                            c.getTelepon() != null ? c.getTelepon() : "", c.getAlamat() != null ? c.getAlamat() : ""});
                }
            }
        };
        refreshCustomerTable.run();

        txtCustSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshCustomerTable.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshCustomerTable.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshCustomerTable.run(); }
        });

        JScrollPane custScroll = new JScrollPane(tblCustomer);
        custScroll.setPreferredSize(new Dimension(0, 140));
        customerSection.add(custScroll, BorderLayout.CENTER);

        JLabel lblSelectedCust = new JLabel("Customer: belum dipilih");
        lblSelectedCust.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSelectedCust.setForeground(new Color(108, 117, 125));
        customerSection.add(lblSelectedCust, BorderLayout.SOUTH);

        // --- Bottom part: Vehicle table + mekanik + keluhan ---
        JPanel vehicleSection = new JPanel(new BorderLayout(4, 4));
        vehicleSection.setOpaque(false);

        JPanel vehicleHeader = new JPanel(new BorderLayout(6, 0));
        vehicleHeader.setOpaque(false);
        JLabel lblVeh = new JLabel("Pilih Kendaraan");
        lblVeh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        vehicleHeader.add(lblVeh, BorderLayout.WEST);

        JButton btnNewVehicle = new JButton("+");
        btnNewVehicle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNewVehicle.setForeground(Color.WHITE);
        btnNewVehicle.setBackground(new Color(40, 167, 69));
        btnNewVehicle.setFocusPainted(false); btnNewVehicle.setBorderPainted(false); btnNewVehicle.setOpaque(true);
        btnNewVehicle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNewVehicle.setToolTipText("Tambah Kendaraan Baru");
        btnNewVehicle.setPreferredSize(new Dimension(36, 26));
        JPanel vehBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        vehBtnRow.setOpaque(false);
        vehBtnRow.add(btnNewVehicle);
        vehicleHeader.add(vehBtnRow, BorderLayout.EAST);
        vehicleSection.add(vehicleHeader, BorderLayout.NORTH);

        DefaultTableModel vehModel = new DefaultTableModel(
                new Object[]{"ID", "No Polisi", "Merk", "Tipe", "Tahun"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblVehicle = new JTable(vehModel);
        tblVehicle.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVehicle.setRowHeight(24);
        tblVehicle.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tblVehicle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblVehicle.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane vehScroll = new JScrollPane(tblVehicle);
        vehScroll.setPreferredSize(new Dimension(0, 120));
        vehicleSection.add(vehScroll, BorderLayout.CENTER);

        JLabel lblSelectedVeh = new JLabel("Kendaraan: belum dipilih");
        lblSelectedVeh.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSelectedVeh.setForeground(new Color(108, 117, 125));
        vehicleSection.add(lblSelectedVeh, BorderLayout.SOUTH);

        // Reload vehicles for selected customer
        Runnable refreshVehicleTable = () -> {
            vehModel.setRowCount(0);
            selectedVehicleId[0] = 0;
            lblSelectedVeh.setText("Kendaraan: belum dipilih");
            if (selectedClientId[0] <= 0) return;
            try {
                List<Vehicle> vehicles = vehicleDAO.findByClientId(selectedClientId[0]);
                for (Vehicle v : vehicles) {
                    vehModel.addRow(new Object[]{v.getVehicleId(), v.getNoPolisi(), v.getMerk(),
                            v.getTipe() != null ? v.getTipe() : "", v.getTahun()});
                }
                if (vehModel.getRowCount() == 1) {
                    tblVehicle.setRowSelectionInterval(0, 0);
                    selectedVehicleId[0] = (int) vehModel.getValueAt(0, 0);
                    lblSelectedVeh.setText("Kendaraan: " + vehModel.getValueAt(0, 1) + " (" + vehModel.getValueAt(0, 2) + ")");
                }
            } catch (SQLException ex) { /* ignore */ }
        };

        // Customer table selection
        tblCustomer.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            int selRow = tblCustomer.getSelectedRow();
            if (selRow >= 0) {
                selectedClientId[0] = (int) custModel.getValueAt(selRow, 0);
                lblSelectedCust.setText("Customer: " + custModel.getValueAt(selRow, 1) + " (" + custModel.getValueAt(selRow, 2) + ")");
                lblSelectedCust.setForeground(new Color(40, 167, 69));
                refreshVehicleTable.run();
            }
        });

        // Vehicle table selection
        tblVehicle.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            int selRow = tblVehicle.getSelectedRow();
            if (selRow >= 0) {
                selectedVehicleId[0] = (int) vehModel.getValueAt(selRow, 0);
                lblSelectedVeh.setText("Kendaraan: " + vehModel.getValueAt(selRow, 1) + " (" + vehModel.getValueAt(selRow, 2) + ")");
                lblSelectedVeh.setForeground(new Color(40, 167, 69));
            }
        });

        // New client button - uses same dialog as ClientPanel
        btnNewClient.addActionListener(ev -> {
            Client newClient = ClientPanel.showAddClientDialog(dialog);
            if (newClient != null) {
                try {
                    fClientList.clear();
                    fClientList.addAll(clientDAO.findAll());
                } catch (SQLException ex) { /* ignore */ }
                refreshCustomerTable.run();
                // Select new client
                for (int i = 0; i < custModel.getRowCount(); i++) {
                    if ((int) custModel.getValueAt(i, 0) == newClient.getClientId()) {
                        tblCustomer.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
        });

        // New vehicle button - uses same dialog as VehiclePanel
        btnNewVehicle.addActionListener(ev -> {
            if (selectedClientId[0] <= 0) {
                UIHelper.warn(dialog, "Pilih customer terlebih dahulu sebelum menambah kendaraan.");
                return;
            }
            Vehicle newVehicle = VehiclePanel.showAddVehicleDialog(dialog, selectedClientId[0]);
            if (newVehicle != null) {
                refreshVehicleTable.run();
                for (int i = 0; i < vehModel.getRowCount(); i++) {
                    if ((int) vehModel.getValueAt(i, 0) == newVehicle.getVehicleId()) {
                        tblVehicle.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
        });

        // Mekanik, keluhan, catatan
        JComboBox<String> cbMekanik = new JComboBox<>();
        JTextArea txtKeluhan = new JTextArea(2, 30);
        txtKeluhan.setLineWrap(true); txtKeluhan.setWrapStyleWord(true);
        JTextArea txtCatatan = new JTextArea(2, 30);
        txtCatatan.setLineWrap(true); txtCatatan.setWrapStyleWord(true);

        cbMekanik.addItem("-- Tidak Ada (Pembelian Saja) --");
        for (Mekanik m : mekanikList) cbMekanik.addItem(m.getMekanikId() + " - " + m.getNama() + " [" + m.getSpesialis() + "]");

        // Extra fields panel
        JPanel extraFields = new JPanel(new GridBagLayout());
        extraFields.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int eRow = 0;
        if (!sparepartOnly) {
            g.gridx = 0; g.gridy = eRow; extraFields.add(styleLabel("Mekanik:"), g);
            g.gridx = 1; g.weightx = 1; extraFields.add(cbMekanik, g); g.weightx = 0;
            eRow++;
            g.gridx = 0; g.gridy = eRow; extraFields.add(styleLabel("Keluhan:"), g);
            g.gridx = 1; g.weightx = 1; extraFields.add(new JScrollPane(txtKeluhan), g); g.weightx = 0;
            eRow++;
        } else {
            txtKeluhan.setText("Pembelian Sparepart");
        }
        g.gridx = 0; g.gridy = eRow; extraFields.add(styleLabel("Catatan:"), g);
        g.gridx = 1; g.weightx = 1; extraFields.add(new JScrollPane(txtCatatan), g); g.weightx = 0;

        // Assemble step1
        JPanel step1Top = new JPanel(new GridLayout(1, 2, 10, 0));
        step1Top.setOpaque(false);
        step1Top.add(customerSection);
        step1Top.add(vehicleSection);

        step1.add(step1Top, BorderLayout.CENTER);
        step1.add(extraFields, BorderLayout.SOUTH);

        cardPanel.add(step1, "step1");

        // ---------- STEP 2: Jasa Items (only for servis, not sparepart-only) ----------
        DefaultTableModel jasaModel = new DefaultTableModel(
                new Object[]{"Nama Jasa", "Harga", "Qty", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        if (!sparepartOnly) {
            JPanel step2 = new JPanel(new BorderLayout(8, 8));
            step2.setBorder(new EmptyBorder(10, 15, 10, 15));
            step2.setBackground(Color.WHITE);

            JLabel lblJasaTitle = new JLabel("Daftar Jasa / Layanan Servis");
            lblJasaTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
            step2.add(lblJasaTitle, BorderLayout.NORTH);

            JTable tblJasa = new JTable(jasaModel);
            step2.add(new JScrollPane(tblJasa), BorderLayout.CENTER);

            // Free-form entry panel
            JPanel jasaEntryPanel = new JPanel(new BorderLayout(8, 4));
            jasaEntryPanel.setBackground(Color.WHITE);

            JPanel jasaFields = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            jasaFields.setBackground(Color.WHITE);
            jasaFields.add(new JLabel("Nama Jasa:"));
            JTextField txtJasaNama = new JTextField(18);
            txtJasaNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            jasaFields.add(txtJasaNama);
            jasaFields.add(new JLabel("Harga:"));
            JTextField txtJasaHarga = new JTextField(10);
            txtJasaHarga.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            jasaFields.add(txtJasaHarga);
            jasaFields.add(new JLabel("Qty:"));
            JTextField txtJasaQty = new JTextField("1", 4);
            txtJasaQty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            jasaFields.add(txtJasaQty);

            JButton btnAddJasa = UIHelper.createStyledButton("+ Tambah", new Color(40, 167, 69));
            btnAddJasa.addActionListener(e -> {
                String nama = txtJasaNama.getText().trim();
                if (nama.isEmpty()) { UIHelper.warn(dialog, "Nama jasa tidak boleh kosong."); return; }
                try {
                    double harga = Double.parseDouble(txtJasaHarga.getText().trim());
                    int qty = Integer.parseInt(txtJasaQty.getText().trim());
                    if (qty <= 0) { UIHelper.warn(dialog, "Qty harus lebih dari 0."); return; }
                    jasaModel.addRow(new Object[]{nama, harga, qty, harga * qty});
                    txtJasaNama.setText(""); txtJasaHarga.setText(""); txtJasaQty.setText("1");
                    txtJasaNama.requestFocus();
                } catch (NumberFormatException ex) {
                    UIHelper.warn(dialog, "Harga dan Qty harus berupa angka.");
                }
            });
            jasaFields.add(btnAddJasa);
            jasaEntryPanel.add(jasaFields, BorderLayout.CENTER);

            JPanel jasaBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            jasaBtnPanel.setBackground(Color.WHITE);
            JButton btnRemoveJasa = UIHelper.createStyledButton("Hapus Jasa", new Color(220, 53, 69));
            btnRemoveJasa.addActionListener(e -> {
                int sel = tblJasa.getSelectedRow();
                if (sel >= 0) jasaModel.removeRow(sel);
                else UIHelper.warn(dialog, "Pilih baris jasa yang akan dihapus.");
            });
            jasaBtnPanel.add(btnRemoveJasa);
            jasaEntryPanel.add(jasaBtnPanel, BorderLayout.SOUTH);

            step2.add(jasaEntryPanel, BorderLayout.SOUTH);
            cardPanel.add(step2, "step2");
        }

        // ---------- STEP 3 (or Step 2 for sparepart-only): Sparepart Items ----------
        JPanel stepSp = new JPanel(new BorderLayout(8, 8));
        stepSp.setBorder(new EmptyBorder(10, 15, 10, 15));
        stepSp.setBackground(Color.WHITE);

        JLabel lblSpTitle = new JLabel("Daftar Sparepart yang Digunakan");
        lblSpTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stepSp.add(lblSpTitle, BorderLayout.NORTH);

        DefaultTableModel spModel = new DefaultTableModel(
                new Object[]{"Sparepart ID", "Nama Sparepart", "Harga", "Qty", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblSparepart = new JTable(spModel);
        stepSp.add(new JScrollPane(tblSparepart), BorderLayout.CENTER);

        JPanel spBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        spBtnPanel.setBackground(Color.WHITE);
        final List<Sparepart> fSpList = sparepartList;
        JButton btnAddSp = UIHelper.createStyledButton("+ Tambah Sparepart", new Color(40, 167, 69));
        btnAddSp.addActionListener(e -> {
            SparepartChooserDialog scd = new SparepartChooserDialog(dialog, fSpList);
            scd.setVisible(true);
            Sparepart s = scd.getSelectedSparepart();
            int qty = scd.getSelectedQty();
            if (s != null && qty > 0) {
                spModel.addRow(new Object[]{s.getSparepartId(), s.getNamaSparepart(), s.getHargaJual(), qty, s.getHargaJual() * qty});
            }
        });
        spBtnPanel.add(btnAddSp);

        JButton btnRemoveSp = UIHelper.createStyledButton("Hapus Sparepart", new Color(220, 53, 69));
        btnRemoveSp.addActionListener(e -> {
            int sel = tblSparepart.getSelectedRow();
            if (sel >= 0) spModel.removeRow(sel);
            else UIHelper.warn(dialog, "Pilih baris sparepart yang akan dihapus.");
        });
        spBtnPanel.add(btnRemoveSp);
        stepSp.add(spBtnPanel, BorderLayout.SOUTH);

        String spStepKey = sparepartOnly ? "step2" : "step3";
        cardPanel.add(stepSp, spStepKey);

        dialog.add(cardPanel, BorderLayout.CENTER);

        // ===== NAVIGATION BUTTONS =====
        final int[] currentStep = {1};
        JButton btnPrev = UIHelper.createStyledButton("\u25C0 Sebelumnya", new Color(108, 117, 125));
        JButton btnNext = UIHelper.createStyledButton("Selanjutnya \u25B6", new Color(13, 110, 253));
        JButton btnSimpan = UIHelper.createStyledButton("\u2713 Simpan", new Color(40, 167, 69));
        JButton btnBatal = UIHelper.createStyledButton("Batal", new Color(108, 117, 125));

        String[] stepTitles = sparepartOnly
                ? new String[]{"Customer & Kendaraan", "Sparepart"}
                : new String[]{"Customer & Kendaraan", "Jasa / Layanan", "Sparepart"};

        Runnable updateNav = () -> {
            int step = currentStep[0];
            btnPrev.setVisible(step > 1);
            btnNext.setVisible(step < totalSteps);
            btnSimpan.setVisible(step == totalSteps);
            lblStep.setText("Langkah " + step + " dari " + totalSteps);
            lblStepTitle.setText("\u2014 " + stepTitles[step - 1]);
        };

        btnPrev.addActionListener(e -> {
            if (currentStep[0] > 1) {
                currentStep[0]--;
                cardLayout.show(cardPanel, "step" + currentStep[0]);
                updateNav.run();
            }
        });

        btnNext.addActionListener(e -> {
            // Validate step 1
            if (currentStep[0] == 1) {
                if (selectedClientId[0] <= 0 || selectedVehicleId[0] <= 0) {
                    UIHelper.warn(dialog, "Pilih Customer dan Kendaraan terlebih dahulu.");
                    return;
                }
                if (!sparepartOnly && cbMekanik.getSelectedIndex() <= 0) {
                    UIHelper.warn(dialog, "Pilih Mekanik terlebih dahulu.");
                    return;
                }
                if (!sparepartOnly && txtKeluhan.getText().trim().isEmpty()) {
                    UIHelper.warn(dialog, "Keluhan tidak boleh kosong.");
                    return;
                }
            }
            if (currentStep[0] < totalSteps) {
                currentStep[0]++;
                cardLayout.show(cardPanel, "step" + currentStep[0]);
                updateNav.run();
            }
        });

        btnBatal.addActionListener(e -> dialog.dispose());

        btnSimpan.addActionListener(e -> {
            try {
                int clientId = selectedClientId[0];
                int vehicleId = selectedVehicleId[0];
                if (clientId <= 0 || vehicleId <= 0) {
                    UIHelper.warn(dialog, "Pilih Customer dan Kendaraan terlebih dahulu.");
                    return;
                }
                int mekanikId = 0;
                if (!sparepartOnly && cbMekanik.getSelectedIndex() > 0) {
                    mekanikId = Integer.parseInt(cbMekanik.getSelectedItem().toString().split(" - ")[0].trim());
                }

                // Build jasa details
                List<TransactionJasaDetail> jasaDetails = new ArrayList<>();
                double totalJasa = 0;
                for (int i = 0; i < jasaModel.getRowCount(); i++) {
                    TransactionJasaDetail jd = new TransactionJasaDetail();
                    jd.setNamaJasa(jasaModel.getValueAt(i, 0).toString());
                    jd.setHarga(Double.parseDouble(jasaModel.getValueAt(i, 1).toString()));
                    jd.setQty(Integer.parseInt(jasaModel.getValueAt(i, 2).toString()));
                    jd.setSubtotal(Double.parseDouble(jasaModel.getValueAt(i, 3).toString()));
                    jasaDetails.add(jd);
                    totalJasa += jd.getSubtotal();
                }

                // Build sparepart details
                List<TransactionDetail> spDetails = new ArrayList<>();
                double totalSparepart = 0;
                for (int i = 0; i < spModel.getRowCount(); i++) {
                    TransactionDetail d = new TransactionDetail();
                    d.setSparepartId((int) spModel.getValueAt(i, 0));
                    d.setHarga(Double.parseDouble(spModel.getValueAt(i, 2).toString()));
                    d.setQty(Integer.parseInt(spModel.getValueAt(i, 3).toString()));
                    d.setSubtotal(Double.parseDouble(spModel.getValueAt(i, 4).toString()));
                    spDetails.add(d);
                    totalSparepart += d.getSubtotal();
                }

                double grandTotal = totalJasa + totalSparepart;

                if (isNew) {
                    // Create registration
                    ServiceRegistration reg = new ServiceRegistration();
                    reg.setClientId(clientId);
                    reg.setVehicleId(vehicleId);
                    reg.setMekanikId(mekanikId);
                    reg.setKeluhan(txtKeluhan.getText().trim());
                    reg.setCatatan(txtCatatan.getText().trim());

                    if (sparepartOnly) {
                        // Sparepart-only: directly completed
                        reg.setStatus("Completed");
                        reg.setTanggalDaftar(new Date());
                        reg.setTanggalMulai(new Date());
                    } else {
                        reg.setStatus("Registered");
                        reg.setTanggalDaftar(new Date());
                    }
                    regDAO.insert(reg);

                    // Get the new registration ID
                    List<ServiceRegistration> regs = regDAO.findAll();
                    int newRegId = regs.isEmpty() ? 0 : regs.get(0).getRegistrationId();

                    // Create transaction
                    ServiceTransaction t = new ServiceTransaction();
                    t.setTanggal(new Date());
                    t.setClientId(clientId);
                    t.setVehicleId(vehicleId);
                    t.setMekanikId(mekanikId);
                    t.setKeluhan(txtKeluhan.getText().trim());
                    t.setRegistrationId(newRegId);
                    t.setTotalJasa(totalJasa);
                    t.setTotalSparepart(totalSparepart);
                    t.setGrandTotal(grandTotal);
                    t.setMetodeBayar("Cash");
                    t.setUserKasir("admin");
                    t.setDetails(spDetails);
                    t.setJasaDetails(jasaDetails);

                    if (sparepartOnly) {
                        t.setStatusServis("Selesai Lunas");
                        // Show payment dialog immediately
                        t.setBayar(0);
                        t.setKembali(0);
                        int transId = transDAO.insertWithDetails(t);
                        t.setTransId(transId);
                        dialog.dispose();
                        // Load full transaction for payment
                        ServiceTransaction fullT = transDAO.findByIdFull(transId);
                        ServiceRegistration savedReg = regDAO.findById(newRegId);
                        showPaymentDialog(fullT, savedReg);
                    } else {
                        t.setStatusServis("Menunggu");
                        t.setBayar(0);
                        t.setKembali(0);
                        int transId = transDAO.insertWithDetails(t);
                        UIHelper.success(dialog, "Pendaftaran berhasil! Transaksi #" + transId + " telah dibuat.\nSilakan klik 'Mulai Servis' ketika mekanik siap.");
                        dialog.dispose();
                    }
                } else {
                    // Update existing registration
                    existing.setClientId(clientId);
                    existing.setVehicleId(vehicleId);
                    existing.setMekanikId(mekanikId);
                    existing.setKeluhan(txtKeluhan.getText().trim());
                    existing.setCatatan(txtCatatan.getText().trim());
                    regDAO.update(existing);

                    // Update linked transaction
                    ServiceTransaction trans = transDAO.findByRegistrationId(existing.getRegistrationId());
                    if (trans != null) {
                        trans.setClientId(clientId);
                        trans.setVehicleId(vehicleId);
                        trans.setMekanikId(mekanikId);
                        trans.setKeluhan(txtKeluhan.getText().trim());
                        trans.setTotalJasa(totalJasa);
                        trans.setTotalSparepart(totalSparepart);
                        trans.setGrandTotal(grandTotal);
                        trans.setDetails(spDetails);
                        trans.setJasaDetails(jasaDetails);
                        transDAO.updateWithDetails(trans);
                    }

                    UIHelper.success(dialog, "Detail servis berhasil diupdate.");
                    dialog.dispose();
                }

                loadTable();
                selectedRegId = -1;
                lblInfo.setText(" ");
                updateButtonVisibility("");
            } catch (SQLException ex) {
                UIHelper.error(dialog, "Error: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                UIHelper.error(dialog, "Format angka tidak valid.");
            }
        });

        // Prefill if editing existing
        if (existing != null) {
            // Select customer in table
            for (int i = 0; i < custModel.getRowCount(); i++) {
                if ((int) custModel.getValueAt(i, 0) == existing.getClientId()) {
                    tblCustomer.setRowSelectionInterval(i, i);
                    break;
                }
            }
            // Vehicle will auto-load from customer selection; select after a short delay
            SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < vehModel.getRowCount(); i++) {
                    if ((int) vehModel.getValueAt(i, 0) == existing.getVehicleId()) {
                        tblVehicle.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            });
            for (int i = 0; i < cbMekanik.getItemCount(); i++) {
                if (cbMekanik.getItemAt(i).startsWith(existing.getMekanikId() + " - ")) { cbMekanik.setSelectedIndex(i); break; }
            }
            txtKeluhan.setText(existing.getKeluhan() != null ? existing.getKeluhan() : "");
            txtCatatan.setText(existing.getCatatan() != null ? existing.getCatatan() : "");

            // Load existing jasa & sparepart details from transaction
            try {
                ServiceTransaction trans = transDAO.findByRegistrationId(existing.getRegistrationId());
                if (trans != null) {
                    ServiceTransaction fullTrans = transDAO.findByIdFull(trans.getTransId());
                    if (fullTrans != null) {
                        if (fullTrans.getJasaDetails() != null) {
                            for (TransactionJasaDetail jd : fullTrans.getJasaDetails()) {
                                jasaModel.addRow(new Object[]{jd.getNamaJasa(), jd.getHarga(), jd.getQty(), jd.getSubtotal()});
                            }
                        }
                        if (fullTrans.getDetails() != null) {
                            for (TransactionDetail d : fullTrans.getDetails()) {
                                spModel.addRow(new Object[]{d.getSparepartId(), d.getSparepartNama() != null ? d.getSparepartNama() : "Sparepart #" + d.getSparepartId(), d.getHarga(), d.getQty(), d.getSubtotal()});
                            }
                        }
                    }
                }
            } catch (SQLException ex) { /* ignore */ }

            if (editMode) {
                currentStep[0] = sparepartOnly ? 2 : 2;
                cardLayout.show(cardPanel, "step2");
            }
        }

        JPanel navPanel = UIHelper.createDialogButtonPanel();
        navPanel.add(btnBatal);
        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        navPanel.add(btnSimpan);
        dialog.add(navPanel, BorderLayout.SOUTH);

        updateNav.run();
        dialog.setVisible(true);
    }

    // ==================== ACTIONS ====================

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
                    "Mulai servis untuk REG-" + String.format("%05d", selectedRegId) + "?\nStatus akan berubah menjadi 'InProgress'.",
                    "Konfirmasi Mulai Servis")) return;

            reg.setStatus("InProgress");
            reg.setTanggalMulai(new Date());
            regDAO.update(reg);

            ServiceTransaction trans = transDAO.findByRegistrationId(selectedRegId);
            if (trans != null) {
                transDAO.updateStatusServis(trans.getTransId(), "Dikerjakan");
            }

            loadTable();
            selectedRegId = -1;
            lblInfo.setText("Servis dimulai!");
            updateButtonVisibility("");
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    private void selesaikanServis() {
        if (selectedRegId < 0) { UIHelper.warn(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceRegistration reg = regDAO.findById(selectedRegId);
            if (reg == null) { UIHelper.warn(this, "Data tidak ditemukan."); return; }
            if (!"InProgress".equals(reg.getStatus())) {
                UIHelper.warn(this, "Hanya status 'InProgress' yang bisa diselesaikan.");
                return;
            }

            ServiceTransaction trans = transDAO.findByRegistrationId(selectedRegId);
            if (trans == null) { UIHelper.warn(this, "Transaksi tidak ditemukan."); return; }

            ServiceTransaction fullTrans = transDAO.findByIdFull(trans.getTransId());
            if (fullTrans == null) { UIHelper.error(this, "Detail transaksi tidak ditemukan."); return; }

            showPaymentDialog(fullTrans, reg);
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    private void showPaymentDialog(ServiceTransaction t, ServiceRegistration reg) {
        Window win = SwingUtilities.getWindowAncestor(this);
        Frame owner = (win instanceof Frame) ? (Frame) win : null;

        JDialog dialog = new JDialog(owner, "Selesaikan & Pembayaran", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        dialog.add(UIHelper.createDialogHeader("Penyelesaian Servis",
                "Review total dan proses pembayaran"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(15, 20, 15, 20));
        form.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTotalJasa = new JLabel(String.format("Rp %,.0f", t.getTotalJasa()));
        lblTotalJasa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblTotalSp = new JLabel(String.format("Rp %,.0f", t.getTotalSparepart()));
        lblTotalSp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblGrand = new JLabel(String.format("Rp %,.0f", t.getGrandTotal()));
        lblGrand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGrand.setForeground(new Color(220, 53, 69));

        JTextField txtBayar = new JTextField("0", 15);
        txtBayar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel lblKembali = new JLabel("Rp 0");
        lblKembali.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKembali.setForeground(new Color(40, 167, 69));
        JComboBox<String> cbMetode = new JComboBox<>(new String[]{"Cash", "QRIS", "Transfer Bank", "Debit", "Lainnya"});

        txtBayar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void calc() {
                try {
                    double bayar = Double.parseDouble(txtBayar.getText().trim());
                    double kembali = Math.max(0, bayar - t.getGrandTotal());
                    lblKembali.setText(String.format("Rp %,.0f", kembali));
                } catch (NumberFormatException ex) {
                    lblKembali.setText("Rp 0");
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calc(); }
        });

        int r = 0;
        g.gridx = 0; g.gridy = r; form.add(styleLabel("Total Jasa:"), g);
        g.gridx = 1; g.weightx = 1; form.add(lblTotalJasa, g); g.weightx = 0;
        r++;
        g.gridx = 0; g.gridy = r; form.add(styleLabel("Total Sparepart:"), g);
        g.gridx = 1; form.add(lblTotalSp, g);
        r++;
        JSeparator sep = new JSeparator();
        g.gridx = 0; g.gridy = r; g.gridwidth = 2; form.add(sep, g); g.gridwidth = 1;
        r++;
        g.gridx = 0; g.gridy = r; form.add(styleLabel("GRAND TOTAL:"), g);
        g.gridx = 1; form.add(lblGrand, g);
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel(" "), g);
        r++;
        g.gridx = 0; g.gridy = r; form.add(styleLabel("Metode Bayar:"), g);
        g.gridx = 1; form.add(cbMetode, g);
        r++;
        g.gridx = 0; g.gridy = r; form.add(styleLabel("Bayar:"), g);
        g.gridx = 1; form.add(txtBayar, g);
        r++;
        g.gridx = 0; g.gridy = r; form.add(styleLabel("Kembali:"), g);
        g.gridx = 1; form.add(lblKembali, g);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = UIHelper.createDialogButtonPanel();
        JButton btnBatal = UIHelper.createStyledButton("Batal", new Color(108, 117, 125));
        btnBatal.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnBatal);

        JButton btnBayar = UIHelper.createStyledButton("\u2713 Selesaikan & Bayar", new Color(40, 167, 69));
        btnBayar.addActionListener(e -> {
            try {
                double bayar = Double.parseDouble(txtBayar.getText().trim());
                if (bayar < t.getGrandTotal()) {
                    UIHelper.warn(dialog, "Pembayaran kurang! Grand Total: " + String.format("Rp %,.0f", t.getGrandTotal()));
                    return;
                }
                double kembali = bayar - t.getGrandTotal();

                t.setBayar(bayar);
                t.setKembali(kembali);
                t.setMetodeBayar(cbMetode.getSelectedItem().toString());
                transDAO.updateStatusPembayaran(t.getTransId(), bayar, kembali);
                transDAO.updateStatusServis(t.getTransId(), "Selesai Lunas");

                reg.setStatus("Completed");
                regDAO.update(reg);

                dialog.dispose();
                loadTable();
                selectedRegId = -1;
                lblInfo.setText("Servis REG-" + String.format("%05d", reg.getRegistrationId()) + " selesai!");
                updateButtonVisibility("");
                UIHelper.success(this, "Pembayaran berhasil!\nKembali: " + String.format("Rp %,.0f", kembali));
            } catch (SQLException ex) {
                UIHelper.error(dialog, "Error: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                UIHelper.warn(dialog, "Format bayar tidak valid.");
            }
        });
        btnPanel.add(btnBayar);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void lihatDetail() {
        if (selectedRegId < 0) { UIHelper.warn(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceTransaction trans = transDAO.findByRegistrationId(selectedRegId);
            if (trans == null) { UIHelper.warn(this, "Transaksi tidak ditemukan."); return; }
            ServiceTransaction t = transDAO.findByIdFull(trans.getTransId());
            if (t == null) { UIHelper.error(this, "Detail tidak ditemukan."); return; }

            StringBuilder sb = new StringBuilder();
            sb.append("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n");
            sb.append("     DETAIL PENDAFTARAN SERVIS\n");
            sb.append("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n\n");
            sb.append("No. Registrasi : REG-").append(String.format("%05d", selectedRegId)).append("\n");
            sb.append("No. Transaksi  : TRX-").append(String.format("%05d", t.getTransId())).append("\n");
            sb.append("Tanggal        : ").append(t.getTanggal() != null ? new SimpleDateFormat("dd-MM-yyyy HH:mm").format(t.getTanggal()) : "-").append("\n");
            sb.append("Status         : ").append(t.getStatusServis()).append("\n");
            sb.append("Metode Bayar   : ").append(t.getMetodeBayar() != null ? t.getMetodeBayar() : "Cash").append("\n");

            sb.append("\n\u2500\u2500 Pelanggan & Kendaraan \u2500\u2500\n");
            sb.append("Pelanggan      : ").append(t.getClientNama() != null ? t.getClientNama() : "-").append("\n");
            sb.append("No. Polisi     : ").append(t.getNoPolisi() != null ? t.getNoPolisi() : "-").append("\n");
            sb.append("Mekanik        : ").append(t.getMekanikNama() != null ? t.getMekanikNama() : "-").append("\n");

            sb.append("\n\u2500\u2500 Keluhan \u2500\u2500\n");
            sb.append(t.getKeluhan() != null && !t.getKeluhan().isEmpty() ? t.getKeluhan() : "-").append("\n");

            if (t.getJasaDetails() != null && !t.getJasaDetails().isEmpty()) {
                sb.append("\n\u2500\u2500 Jasa / Layanan \u2500\u2500\n");
                for (int i = 0; i < t.getJasaDetails().size(); i++) {
                    TransactionJasaDetail jd = t.getJasaDetails().get(i);
                    sb.append(String.format("  %d. %s  @Rp %,.0f x%d = Rp %,.0f\n", i + 1,
                            jd.getNamaJasa(), jd.getHarga(), jd.getQty(), jd.getSubtotal()));
                }
            }

            if (t.getDetails() != null && !t.getDetails().isEmpty()) {
                sb.append("\n\u2500\u2500 Sparepart \u2500\u2500\n");
                for (int i = 0; i < t.getDetails().size(); i++) {
                    TransactionDetail d = t.getDetails().get(i);
                    sb.append(String.format("  %d. %s  @Rp %,.0f x%d = Rp %,.0f\n", i + 1,
                            d.getSparepartNama() != null ? d.getSparepartNama() : "Sparepart #" + d.getSparepartId(),
                            d.getHarga(), d.getQty(), d.getSubtotal()));
                }
            }

            sb.append("\n\u2500\u2500 Total \u2500\u2500\n");
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
            sp.setPreferredSize(new Dimension(500, 500));
            Window w = SwingUtilities.getWindowAncestor(this);
            Frame frm = (w instanceof Frame) ? (Frame) w : null;
            JOptionPane.showMessageDialog(frm, sp, "Detail - REG-" + String.format("%05d", selectedRegId), JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    private void printNota() {
        if (selectedRegId < 0) { UIHelper.warn(this, "Pilih pendaftaran dari tabel."); return; }
        try {
            ServiceTransaction trans = transDAO.findByRegistrationId(selectedRegId);
            if (trans == null) { UIHelper.warn(this, "Transaksi tidak ditemukan."); return; }
            ServiceTransaction t = transDAO.findByIdFull(trans.getTransId());
            if (t == null) { UIHelper.error(this, "Detail tidak ditemukan."); return; }
            ExportUtils.printServiceReceipt(t);
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    private void hapusRegistrasi() {
        if (selectedRegId < 0) { UIHelper.warn(this, "Pilih pendaftaran dari tabel."); return; }

        Object[] rowData = styledTable.getSelectedRowData();
        if (rowData != null && "Completed".equalsIgnoreCase(rowData[6].toString())) {
            UIHelper.warn(this, "Tidak bisa menghapus pendaftaran yang sudah selesai.");
            return;
        }

        if (!UIHelper.confirm(this, "Hapus pendaftaran REG-" + String.format("%05d", selectedRegId) + "?\nTransaksi terkait juga akan dihapus.", "Konfirmasi Hapus")) return;
        try {
            ServiceTransaction trans = transDAO.findByRegistrationId(selectedRegId);
            if (trans != null) {
                transDAO.delete(trans.getTransId());
            }
            regDAO.delete(selectedRegId);
            loadTable();
            selectedRegId = -1;
            lblInfo.setText(" ");
            updateButtonVisibility("");
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
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
                    if (r.getMekanikId() > 0) {
                        Mekanik m = mekanikDAO.findById(r.getMekanikId());
                        if (m != null) mekanikNama = m.getNama();
                    }
                } catch (SQLException ex) { /* skip */ }

                data.add(new Object[]{
                        r.getRegistrationId(),
                        r.getTanggalDaftar() != null ? sdf.format(r.getTanggalDaftar()) : "",
                        clientNama, vehicleNopol, mekanikNama,
                        r.getKeluhan(), r.getStatus(), r.getCatatan()
                });
            }
            styledTable.setData(new String[]{"ID", "Tanggal Daftar", "Client", "Kendaraan", "Mekanik", "Keluhan", "Status", "Catatan"}, data);
            applyFilter();
        } catch (SQLException ex) {
            UIHelper.error(this, "Error load: " + ex.getMessage());
        }
    }

    private JLabel styleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }
}
