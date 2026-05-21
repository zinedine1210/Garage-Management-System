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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        buttonPanel.setBackground(new Color(243, 245, 249));

        JButton btnTambah = createStyledButton("+ Tambah", new Color(40, 167, 69));
        btnTambah.addActionListener(e -> showFormDialog(null));
        buttonPanel.add(btnTambah);

        JButton btnEdit = createStyledButton("Edit", new Color(0, 123, 255));
        btnEdit.addActionListener(e -> {
            Object[] row = styledTable.getSelectedRowData();
            if (row == null) { UIHelper.warn(this, "Pilih data yang akan diedit."); return; }
            showFormDialog(row);
        });
        buttonPanel.add(btnEdit);

        JButton btnHapus = createStyledButton("Hapus", new Color(220, 53, 69));
        btnHapus.addActionListener(e -> deleteData());
        buttonPanel.add(btnHapus);

        JButton btnRefresh = createStyledButton("Refresh", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> loadData());
        buttonPanel.add(btnRefresh);

        JButton btnExport = createStyledButton("Export", new Color(23, 162, 184));
        btnExport.addActionListener(e -> {
            String[] options = {"PDF", "Excel", "Batal"};
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Data Kendaraan", options);
            if (choice == 0) ExportUtils.exportTableToPDF(styledTable.getTable(), "Data_Kendaraan");
            else if (choice == 1) ExportUtils.exportTableToExcel(styledTable.getTable(), "Data_Kendaraan");
        });
        buttonPanel.add(btnExport);
        add(buttonPanel, BorderLayout.SOUTH);
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
            txtNoPolisi.setText(str(existingData[2]));
            txtMerk.setText(str(existingData[3]));
            txtTipe.setText(str(existingData[4]));
            txtCc.setText(str(existingData[5]));
            if (existingData[6] != null) cbJenis.setSelectedItem(existingData[6].toString());
            txtTahun.setText(str(existingData[7]));
            txtNoRangka.setText(str(existingData[8]));
            txtNoMesin.setText(str(existingData[9]));
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
            List<Vehicle> list = vehicleDAO.findAll();
            List<Object[]> data = new ArrayList<>();
            for (Vehicle v : list) {
                data.add(new Object[]{v.getVehicleId(), v.getClientId(), v.getNoPolisi(), v.getMerk(),
                        v.getTipe(), v.getCc(), v.getTipeKendaraan(), v.getTahun(), v.getNoRangka(), v.getNoMesin()});
            }
            styledTable.setData(new String[]{"ID", "Client ID", "No Polisi", "Merk", "Tipe", "CC",
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
