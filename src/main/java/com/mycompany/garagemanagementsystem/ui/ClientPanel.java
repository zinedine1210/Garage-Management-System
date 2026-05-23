package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ClientDAO;
import com.mycompany.garagemanagementsystem.model.Client;
import com.mycompany.garagemanagementsystem.util.ExportUtils;
import com.mycompany.garagemanagementsystem.util.StyledTable;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ClientPanel extends javax.swing.JPanel {

    private final ClientDAO clientDAO = new ClientDAO();
    private StyledTable styledTable;
    private JTextField txtSearch;

    public ClientPanel() {
        buildUI();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        // ===== TOP =====
        JPanel topWrap = new JPanel(new BorderLayout(0, 6));
        topWrap.setOpaque(false);
        topWrap.add(UIHelper.createPageHeader("Data Client", "Kelola informasi pelanggan bengkel"), BorderLayout.NORTH);

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

        // ===== CENTER: Table =====
        styledTable = new StyledTable();
        add(styledTable, BorderLayout.CENTER);

        // ===== BOTTOM: Buttons =====
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
        btnHapus.addActionListener(e -> deleteClient());
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
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Data Client", options);
            if (choice == 0) ExportUtils.exportTableToPDF(styledTable.getTable(), "Data_Client");
            else if (choice == 1) ExportUtils.exportTableToExcel(styledTable.getTable(), "Data_Client");
        });
        rightButtons.add(btnExport);

        buttonPanel.add(rightButtons, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Static method to show Add Client dialog from anywhere.
     * Returns the newly created Client, or null if cancelled.
     */
    public static Client showAddClientDialog(Component parent) {
        ClientDAO dao = new ClientDAO();
        String dlgTitle = "Tambah Client Baru";
        String dlgSub = "Lengkapi informasi pelanggan di bawah ini";
        Window win = SwingUtilities.getWindowAncestor(parent);
        Frame frame = (win instanceof Frame) ? (Frame) win : null;
        JDialog dialog = new JDialog(frame, dlgTitle, true);
        dialog.setLayout(new BorderLayout());

        dialog.add(UIHelper.createDialogHeader(dlgTitle, dlgSub), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtNama = new JTextField(25);
        JTextField txtAlamat = new JTextField(25);
        JTextField txtTelepon = new JTextField(25);
        JTextField txtEmail = new JTextField(25);

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(new JLabel("Nama:"), gbc); gbc.gridx = 1; gbc.weightx = 1.0;
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 13)); form.add(txtNama, gbc); r++;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(new JLabel("Alamat:"), gbc); gbc.gridx = 1; gbc.weightx = 1.0;
        txtAlamat.setFont(new Font("Segoe UI", Font.PLAIN, 13)); form.add(txtAlamat, gbc); r++;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(new JLabel("Telepon:"), gbc); gbc.gridx = 1; gbc.weightx = 1.0;
        txtTelepon.setFont(new Font("Segoe UI", Font.PLAIN, 13)); form.add(txtTelepon, gbc); r++;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(new JLabel("Email:"), gbc); gbc.gridx = 1; gbc.weightx = 1.0;
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13)); form.add(txtEmail, gbc);

        dialog.add(form, BorderLayout.CENTER);

        final Client[] result = {null};
        JPanel btnPanel = UIHelper.createDialogButtonPanel();
        JButton btnCancel = UIHelper.createStyledButton("Batal", new Color(108, 117, 125));
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);

        JButton btnSave = UIHelper.createStyledButton("Simpan", new Color(40, 167, 69));
        btnSave.addActionListener(e -> {
            String nama = txtNama.getText().trim();
            if (nama.isEmpty()) { UIHelper.warn(dialog, "Nama tidak boleh kosong."); return; }
            try {
                Client c = new Client();
                c.setNama(nama);
                c.setAlamat(txtAlamat.getText().trim());
                c.setTelepon(txtTelepon.getText().trim());
                c.setEmail(txtEmail.getText().trim());
                c.setTanggalDaftar(new Date());
                dao.insert(c);
                // Fetch the newly inserted client (highest ID)
                List<Client> all = dao.findAll();
                result[0] = all.stream().max((a, b) -> Integer.compare(a.getClientId(), b.getClientId())).orElse(null);
                dialog.dispose();
            } catch (Exception ex) {
                UIHelper.error(dialog, "Error: " + ex.getMessage());
            }
        });
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(420, 300));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return result[0];
    }

    private void showFormDialog(Object[] existingData) {
        String dlgTitle = existingData == null ? "Tambah Client Baru" : "Edit Data Client";
        String dlgSub = "Lengkapi informasi pelanggan di bawah ini";
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), dlgTitle, true);
        dialog.setLayout(new BorderLayout());

        dialog.add(UIHelper.createDialogHeader(dlgTitle, dlgSub), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtNama = new JTextField(25);
        JTextField txtAlamat = new JTextField(25);
        JTextField txtTelepon = new JTextField(25);
        JTextField txtEmail = new JTextField(25);

        addFormField(form, gbc, 0, "Nama:", txtNama);
        addFormField(form, gbc, 1, "Alamat:", txtAlamat);
        addFormField(form, gbc, 2, "Telepon:", txtTelepon);
        addFormField(form, gbc, 3, "Email:", txtEmail);

        if (existingData != null) {
            txtNama.setText(str(existingData[1]));
            txtAlamat.setText(str(existingData[2]));
            txtTelepon.setText(str(existingData[3]));
            txtEmail.setText(str(existingData[4]));
        }

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = UIHelper.createDialogButtonPanel();

        JButton btnCancel = createStyledButton("Batal", new Color(108, 117, 125));
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);

        JButton btnSave = createStyledButton("Simpan", new Color(40, 167, 69));
        btnSave.addActionListener(e -> {
            try {
                Client c = new Client();
                if (existingData != null) c.setClientId((int) existingData[0]);
                c.setNama(txtNama.getText());
                c.setAlamat(txtAlamat.getText());
                c.setTelepon(txtTelepon.getText());
                c.setEmail(txtEmail.getText());
                c.setTanggalDaftar(new Date());

                if (c.getClientId() == 0) clientDAO.insert(c);
                else clientDAO.update(c);

                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                UIHelper.error(dialog, "Error: " + ex.getMessage());
            }
        });
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(420, 300));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteClient() {
        Object[] row = styledTable.getSelectedRowData();
        if (row == null) { UIHelper.warn(this, "Pilih data yang akan dihapus."); return; }
        if (UIHelper.confirm(this, "Hapus client \"" + row[1] + "\"?", "Konfirmasi Hapus")) {
            try {
                clientDAO.delete((int) row[0]);
                loadData();
            } catch (SQLException ex) {
                UIHelper.error(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void loadData() {
        try {
            List<Client> list = clientDAO.findAll();
            List<Object[]> data = new ArrayList<>();
            for (Client c : list) {
                data.add(new Object[]{c.getClientId(), c.getNama(), c.getAlamat(), c.getTelepon(), c.getEmail()});
            }
            styledTable.setData(new String[]{"ID", "Nama", "Alamat", "Telepon", "Email"}, data);
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
