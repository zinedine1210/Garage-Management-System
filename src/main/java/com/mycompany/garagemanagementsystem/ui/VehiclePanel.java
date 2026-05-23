package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ClientDAO;
import com.mycompany.garagemanagementsystem.dao.VehicleDAO;
import com.mycompany.garagemanagementsystem.model.Client;
import com.mycompany.garagemanagementsystem.model.Vehicle;
import com.mycompany.garagemanagementsystem.util.ExportUtils;
import com.mycompany.garagemanagementsystem.util.StyledTable;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class VehiclePanel extends javax.swing.JPanel {

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private StyledTable styledTable;
    private JTextField txtSearch;
    private List<Client> clientList = new ArrayList<>();

    public VehiclePanel() {
        buildUI();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        JPanel topWrap = new JPanel(new BorderLayout(0, 6));
        topWrap.setOpaque(false);
        topWrap.add(UIHelper.createPageHeader("Data Kendaraan", "Kelola data kendaraan milik pelanggan bengkel"), BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setBackground(Color.WHITE);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(10, 16, 10, 16)));
        JLabel lblSearch = new JLabel("\uD83D\uDD0D Cari:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterBar.add(lblSearch, BorderLayout.WEST);
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215)),
                new EmptyBorder(6, 10, 6, 10)));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); }
        });
        filterBar.add(txtSearch, BorderLayout.CENTER);
        topWrap.add(filterBar, BorderLayout.CENTER);
        add(topWrap, BorderLayout.NORTH);

        styledTable = new StyledTable();
        add(styledTable, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(243, 245, 249));
        buttonPanel.setBorder(new EmptyBorder(6, 0, 6, 0));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftButtons.setOpaque(false);

        JButton btnTambah = createStyledButton("+ Tambah", new Color(40, 167, 69));
        btnTambah.addActionListener(e -> showFormDialog(null));
        leftButtons.add(btnTambah);

        JButton btnEdit = createStyledButton("Edit", new Color(0, 123, 255));
        btnEdit.addActionListener(e -> {
            Object[] row = styledTable.getSelectedRowData();
            if (row == null) { UIHelper.warn(this, "Pilih data yang akan diedit."); return; }
            showFormDialog(row);
        });
        leftButtons.add(btnEdit);

        JButton btnHapus = createStyledButton("Hapus", new Color(220, 53, 69));
        btnHapus.addActionListener(e -> deleteData());
        leftButtons.add(btnHapus);

        buttonPanel.add(leftButtons, BorderLayout.WEST);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setOpaque(false);

        JButton btnRefresh = createStyledButton("Refresh", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> loadData());
        rightButtons.add(btnRefresh);

        JButton btnExport = createStyledButton("Export", new Color(23, 162, 184));
        btnExport.addActionListener(e -> {
            String[] options = {"PDF", "Excel", "Batal"};
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Data Kendaraan", options);
            if (choice == 0) ExportUtils.exportTableToPDF(styledTable.getTable(), "Data_Kendaraan");
            else if (choice == 1) ExportUtils.exportTableToExcel(styledTable.getTable(), "Data_Kendaraan");
        });
        rightButtons.add(btnExport);

        buttonPanel.add(rightButtons, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Static method to show Add Vehicle dialog from anywhere.
     * Returns the newly created Vehicle, or null if cancelled.
     * @param preselectedClientId if > 0, pre-selects that client in the dropdown.
     */
    public static Vehicle showAddVehicleDialog(Component parent, int preselectedClientId) {
        VehicleDAO vDAO = new VehicleDAO();
        ClientDAO cDAO = new ClientDAO();
        List<Client> clients;
        try { clients = cDAO.findAll(); } catch (SQLException ex) { clients = new ArrayList<>(); }

        String dlgTitle = "Tambah Kendaraan Baru";
        String dlgSub = "Lengkapi informasi kendaraan pelanggan";
        Window win = SwingUtilities.getWindowAncestor(parent);
        Frame frame = (win instanceof Frame) ? (Frame) win : null;
        JDialog dialog = new JDialog(frame, dlgTitle, true);
        dialog.setLayout(new BorderLayout());

        dialog.add(UIHelper.createDialogHeader(dlgTitle, dlgSub), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JComboBox<Object> cbClient = new JComboBox<>();
        for (Client c : clients) cbClient.addItem(c);
        JTextField txtNoPolisi = new JTextField(20);
        JTextField txtMerk = new JTextField(20);
        JTextField txtTipe = new JTextField(20);
        JTextField txtCc = new JTextField(20);
        JComboBox<String> cbJenis = new JComboBox<>(new String[]{"Roda 2", "Lebih dari Roda 2"});
        JTextField txtTahun = new JTextField("2024", 20);
        JTextField txtNoRangka = new JTextField(20);
        JTextField txtNoMesin = new JTextField(20);

        int r = 0;
        String[] labels = {"Client:", "No Polisi:", "Merk:", "Tipe:", "CC:", "Jenis:", "Tahun:", "No Rangka (Opsional):", "No Mesin (Opsional):"};
        JComponent[] fields = {cbClient, txtNoPolisi, txtMerk, txtTipe, txtCc, cbJenis, txtTahun, txtNoRangka, txtNoMesin};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            form.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            form.add(fields[i], gbc);
            r++;
        }

        // Pre-select client
        if (preselectedClientId > 0) {
            for (int i = 0; i < cbClient.getItemCount(); i++) {
                if (((Client) cbClient.getItemAt(i)).getClientId() == preselectedClientId) {
                    cbClient.setSelectedIndex(i);
                    break;
                }
            }
        }

        dialog.add(form, BorderLayout.CENTER);

        final Vehicle[] result = {null};
        JPanel btnPanel = UIHelper.createDialogButtonPanel();
        JButton btnCancel = UIHelper.createStyledButton("Batal", new Color(108, 117, 125));
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);

        JButton btnSave = UIHelper.createStyledButton("Simpan", new Color(40, 167, 69));
        btnSave.addActionListener(e -> {
            try {
                Vehicle v = new Vehicle();
                Client sel = (Client) cbClient.getSelectedItem();
                if (sel == null) { UIHelper.warn(dialog, "Pilih client terlebih dahulu."); return; }
                v.setClientId(sel.getClientId());
                v.setNoPolisi(txtNoPolisi.getText().trim());
                if (v.getNoPolisi().isEmpty()) { UIHelper.warn(dialog, "No Polisi wajib diisi."); return; }
                v.setMerk(txtMerk.getText().trim());
                v.setTipe(txtTipe.getText().trim());
                try { v.setCc(Integer.parseInt(txtCc.getText().trim())); } catch (NumberFormatException ex2) { v.setCc(0); }
                v.setTipeKendaraan(cbJenis.getSelectedItem().toString());
                v.setTahun(Integer.parseInt(txtTahun.getText().trim()));
                v.setNoRangka(txtNoRangka.getText().trim());
                v.setNoMesin(txtNoMesin.getText().trim());
                vDAO.insert(v);
                // Fetch newly inserted vehicle
                List<Vehicle> vList = vDAO.findByClientId(sel.getClientId());
                result[0] = vList.stream().max((a, b) -> Integer.compare(a.getVehicleId(), b.getVehicleId())).orElse(null);
                dialog.dispose();
            } catch (Exception ex) {
                UIHelper.error(dialog, "Error: " + ex.getMessage());
            }
        });
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(480, 440));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return result[0];
    }

    @SuppressWarnings("unchecked")
    private void showFormDialog(Object[] existingData) {
        loadClients();
        String dlgTitle = existingData == null ? "Tambah Kendaraan Baru" : "Edit Data Kendaraan";
        String dlgSub = "Lengkapi informasi kendaraan pelanggan";
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), dlgTitle, true);
        dialog.setLayout(new BorderLayout());

        dialog.add(UIHelper.createDialogHeader(dlgTitle, dlgSub), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JComboBox<Object> cbClient = new JComboBox<>();
        for (Client c : clientList) cbClient.addItem(c);
        JTextField txtNoPolisi = new JTextField(20);
        JTextField txtMerk = new JTextField(20);
        JTextField txtTipe = new JTextField(20);
        JTextField txtCc = new JTextField(20);
        JComboBox<String> cbJenis = new JComboBox<>(new String[]{"Roda 2", "Lebih dari Roda 2"});
        JTextField txtTahun = new JTextField(20);
        JTextField txtNoRangka = new JTextField(20);
        JTextField txtNoMesin = new JTextField(20);

        int r = 0;
        addFormField(form, gbc, r++, "Client:", cbClient);
        addFormField(form, gbc, r++, "No Polisi:", txtNoPolisi);
        addFormField(form, gbc, r++, "Merk:", txtMerk);
        addFormField(form, gbc, r++, "Tipe:", txtTipe);
        addFormField(form, gbc, r++, "CC:", txtCc);
        addFormField(form, gbc, r++, "Jenis:", cbJenis);
        addFormField(form, gbc, r++, "Tahun:", txtTahun);
        addFormField(form, gbc, r++, "No Rangka (Opsional):", txtNoRangka);
        addFormField(form, gbc, r++, "No Mesin (Opsional):", txtNoMesin);

        if (existingData != null) {
            int clientId = (int) existingData[1];
            for (int i = 0; i < cbClient.getItemCount(); i++) {
                if (((Client) cbClient.getItemAt(i)).getClientId() == clientId) {
                    cbClient.setSelectedIndex(i);
                    break;
                }
            }
            txtNoPolisi.setText(str(existingData[3]));
            txtMerk.setText(str(existingData[4]));
            txtTipe.setText(str(existingData[5]));
            txtCc.setText(str(existingData[6]));
            if (existingData[7] != null) cbJenis.setSelectedItem(existingData[7].toString());
            txtTahun.setText(str(existingData[8]));
            txtNoRangka.setText(str(existingData[9]));
            txtNoMesin.setText(str(existingData[10]));
        }

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = UIHelper.createDialogButtonPanel();

        JButton btnCancel = createStyledButton("Batal", new Color(108, 117, 125));
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);

        JButton btnSave = createStyledButton("Simpan", new Color(40, 167, 69));
        btnSave.addActionListener(e -> {
            try {
                Vehicle v = new Vehicle();
                if (existingData != null) v.setVehicleId((int) existingData[0]);
                Client sel = (Client) cbClient.getSelectedItem();
                v.setClientId(sel != null ? sel.getClientId() : 0);
                v.setNoPolisi(txtNoPolisi.getText());
                v.setMerk(txtMerk.getText());
                v.setTipe(txtTipe.getText());
                try { v.setCc(Integer.parseInt(txtCc.getText())); } catch (NumberFormatException ex2) { v.setCc(0); }
                v.setTipeKendaraan(cbJenis.getSelectedItem().toString());
                v.setTahun(Integer.parseInt(txtTahun.getText()));
                v.setNoRangka(txtNoRangka.getText());
                v.setNoMesin(txtNoMesin.getText());

                if (v.getVehicleId() == 0) vehicleDAO.insert(v);
                else vehicleDAO.update(v);
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                UIHelper.error(dialog, "Error: " + ex.getMessage());
            }
        });
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(480, 440));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void loadClients() {
        try { clientList = clientDAO.findAll(); } catch (SQLException ignored) {}
    }

    private void deleteData() {
        Object[] row = styledTable.getSelectedRowData();
        if (row == null) { UIHelper.warn(this, "Pilih data yang akan dihapus."); return; }
        if (UIHelper.confirm(this, "Hapus kendaraan \"" + row[2] + "\"?", "Konfirmasi Hapus")) {
            try {
                vehicleDAO.delete((int) row[0]);
                loadData();
            } catch (SQLException ex) {
                UIHelper.error(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void loadData() {
        try {
            loadClients();
            List<Vehicle> list = vehicleDAO.findAll();
            List<Object[]> data = new ArrayList<>();
            for (Vehicle v : list) {
                String clientNama = "";
                for (Client c : clientList) {
                    if (c.getClientId() == v.getClientId()) { clientNama = c.getNama(); break; }
                }
                data.add(new Object[]{v.getVehicleId(), v.getClientId(), clientNama, v.getNoPolisi(), v.getMerk(),
                        v.getTipe(), v.getCc(), v.getTipeKendaraan(), v.getTahun(), v.getNoRangka(), v.getNoMesin()});
            }
            styledTable.setData(new String[]{"ID", "Client ID", "Nama Client", "No Polisi", "Merk", "Tipe", "CC",
                    "Jenis", "Tahun", "No Rangka", "No Mesin"}, data);
        } catch (SQLException ex) {
            UIHelper.error(this, "Error load data: " + ex.getMessage());
        }
    }

    private JButton createStyledButton(String text, Color bg) {
        return UIHelper.createStyledButton(text, bg);
    }

    private void addFormField(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(field, gbc);
    }

    private String str(Object o) { return o != null ? o.toString() : ""; }
}
