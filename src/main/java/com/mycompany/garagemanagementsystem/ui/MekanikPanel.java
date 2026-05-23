package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.MekanikDAO;
import com.mycompany.garagemanagementsystem.model.Mekanik;
import com.mycompany.garagemanagementsystem.util.ExportUtils;
import com.mycompany.garagemanagementsystem.util.StyledTable;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MekanikPanel extends javax.swing.JPanel {

    private final MekanikDAO mekanikDAO = new MekanikDAO();
    private StyledTable styledTable;
    private JTextField txtSearch;

    public MekanikPanel() {
        buildUI();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        JPanel topWrap = new JPanel(new BorderLayout(0, 6));
        topWrap.setOpaque(false);
        topWrap.add(UIHelper.createPageHeader("Data Mekanik", "Kelola informasi mekanik bengkel beserta spesialisasinya"), BorderLayout.NORTH);

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
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Data Mekanik", options);
            if (choice == 0) ExportUtils.exportTableToPDF(styledTable.getTable(), "Data_Mekanik");
            else if (choice == 1) ExportUtils.exportTableToExcel(styledTable.getTable(), "Data_Mekanik");
        });
        rightButtons.add(btnExport);

        buttonPanel.add(rightButtons, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showFormDialog(Object[] existingData) {
        String dlgTitle = existingData == null ? "Tambah Mekanik Baru" : "Edit Data Mekanik";
        String dlgSub = "Lengkapi informasi mekanik bengkel";
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
        JTextField txtTelepon = new JTextField(25);
        JTextField txtSpesialis = new JTextField(25);

        addFormField(form, gbc, 0, "Nama:", txtNama);
        addFormField(form, gbc, 1, "Telepon:", txtTelepon);
        addFormField(form, gbc, 2, "Spesialis:", txtSpesialis);

        if (existingData != null) {
            txtNama.setText(str(existingData[1]));
            txtTelepon.setText(str(existingData[2]));
            txtSpesialis.setText(str(existingData[3]));
        }

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = UIHelper.createDialogButtonPanel();

        JButton btnCancel = createStyledButton("Batal", new Color(108, 117, 125));
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);

        JButton btnSave = createStyledButton("Simpan", new Color(40, 167, 69));
        btnSave.addActionListener(e -> {
            try {
                Mekanik m = new Mekanik();
                if (existingData != null) m.setMekanikId((int) existingData[0]);
                m.setNama(txtNama.getText());
                m.setTelepon(txtTelepon.getText());
                m.setSpesialis(txtSpesialis.getText());
                if (m.getMekanikId() == 0) mekanikDAO.insert(m);
                else mekanikDAO.update(m);
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                UIHelper.error(dialog, "Error: " + ex.getMessage());
            }
        });
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(420, 260));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteData() {
        Object[] row = styledTable.getSelectedRowData();
        if (row == null) { UIHelper.warn(this, "Pilih data yang akan dihapus."); return; }
        if (UIHelper.confirm(this, "Hapus mekanik \"" + row[1] + "\"?", "Konfirmasi Hapus")) {
            try {
                mekanikDAO.delete((int) row[0]);
                loadData();
            } catch (SQLException ex) {
                UIHelper.error(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void loadData() {
        try {
            List<Mekanik> list = mekanikDAO.findAll();
            List<Object[]> data = new ArrayList<>();
            for (Mekanik m : list) {
                data.add(new Object[]{m.getMekanikId(), m.getNama(), m.getTelepon(), m.getSpesialis()});
            }
            styledTable.setData(new String[]{"ID", "Nama", "Telepon", "Spesialis"}, data);
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
