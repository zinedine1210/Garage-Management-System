package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.SparepartDAO;
import com.mycompany.garagemanagementsystem.dao.SupplierDAO;
import com.mycompany.garagemanagementsystem.model.Sparepart;
import com.mycompany.garagemanagementsystem.model.Supplier;
import com.mycompany.garagemanagementsystem.util.ExportUtils;
import com.mycompany.garagemanagementsystem.util.StyledTable;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SparepartPanel extends javax.swing.JPanel {

    private final SparepartDAO sparepartDAO = new SparepartDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private StyledTable styledTable;
    private JTextField txtSearch;
    private List<Supplier> supplierList = new ArrayList<>();

    public SparepartPanel() {
        buildUI();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        JPanel topWrap = new JPanel(new BorderLayout(0, 6));
        topWrap.setOpaque(false);
        topWrap.add(UIHelper.createPageHeader("Data Sparepart", "Kelola stok dan informasi sparepart bengkel"), BorderLayout.NORTH);

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
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Data Sparepart", options);
            if (choice == 0) ExportUtils.exportTableToPDF(styledTable.getTable(), "Data_Sparepart");
            else if (choice == 1) ExportUtils.exportTableToExcel(styledTable.getTable(), "Data_Sparepart");
        });
        buttonPanel.add(btnExport);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @SuppressWarnings("unchecked")
    private void showFormDialog(Object[] existingData) {
        loadSuppliers();
        String dlgTitle = existingData == null ? "Tambah Sparepart Baru" : "Edit Data Sparepart";
        String dlgSub = "Lengkapi informasi sparepart bengkel";
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

        JTextField txtKode = new JTextField(20);
        JTextField txtNama = new JTextField(20);
        JTextField txtSatuan = new JTextField(20);
        JTextField txtStok = new JTextField(20);
        JTextField txtHargaBeli = new JTextField(20);
        JTextField txtHargaJual = new JTextField(20);
        JComboBox<Object> cbSupplier = new JComboBox<>();
        cbSupplier.addItem("-- Tanpa Supplier --");
        for (Supplier s : supplierList) cbSupplier.addItem(s);

        int r = 0;
        addFormField(form, gbc, r++, "Kode:", txtKode);
        addFormField(form, gbc, r++, "Nama:", txtNama);
        addFormField(form, gbc, r++, "Satuan:", txtSatuan);
        addFormField(form, gbc, r++, "Stok:", txtStok);
        addFormField(form, gbc, r++, "Harga Beli:", txtHargaBeli);
        addFormField(form, gbc, r++, "Harga Jual:", txtHargaJual);
        addFormField(form, gbc, r++, "Supplier:", cbSupplier);

        if (existingData != null) {
            txtKode.setText(str(existingData[1]));
            txtNama.setText(str(existingData[2]));
            txtSatuan.setText(str(existingData[3]));
            txtStok.setText(str(existingData[4]));
            txtHargaBeli.setText(str(existingData[5]));
            txtHargaJual.setText(str(existingData[6]));
            int supplierId = (int) existingData[7];
            if (supplierId == 0) {
                cbSupplier.setSelectedIndex(0);
            } else {
                for (int i = 1; i < cbSupplier.getItemCount(); i++) {
                    Object item = cbSupplier.getItemAt(i);
                    if (item instanceof Supplier && ((Supplier) item).getSupplierId() == supplierId) {
                        cbSupplier.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = UIHelper.createDialogButtonPanel();

        JButton btnCancel = createStyledButton("Batal", new Color(108, 117, 125));
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);

        JButton btnSave = createStyledButton("Simpan", new Color(40, 167, 69));
        btnSave.addActionListener(e -> {
            try {
                Sparepart sp = new Sparepart();
                if (existingData != null) sp.setSparepartId((int) existingData[0]);
                sp.setKodeSparepart(txtKode.getText());
                sp.setNamaSparepart(txtNama.getText());
                sp.setSatuan(txtSatuan.getText());
                sp.setStok(Integer.parseInt(txtStok.getText()));
                sp.setHargaBeli(Double.parseDouble(txtHargaBeli.getText()));
                sp.setHargaJual(Double.parseDouble(txtHargaJual.getText()));
                Object sel = cbSupplier.getSelectedItem();
                sp.setSupplierId(sel instanceof Supplier ? ((Supplier) sel).getSupplierId() : 0);

                if (sp.getSparepartId() == 0) sparepartDAO.insert(sp);
                else sparepartDAO.update(sp);
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                UIHelper.error(dialog, "Error: " + ex.getMessage());
            }
        });
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(460, 400));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void loadSuppliers() {
        try { supplierList = supplierDAO.findAll(); } catch (SQLException ignored) {}
    }

    private void deleteData() {
        Object[] row = styledTable.getSelectedRowData();
        if (row == null) { UIHelper.warn(this, "Pilih data yang akan dihapus."); return; }
        if (UIHelper.confirm(this, "Hapus sparepart \"" + row[2] + "\"?", "Konfirmasi Hapus")) {
            try {
                sparepartDAO.delete((int) row[0]);
                loadData();
            } catch (SQLException ex) {
                UIHelper.error(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void loadData() {
        try {
            List<Sparepart> list = sparepartDAO.findAll();
            List<Object[]> data = new ArrayList<>();
            for (Sparepart s : list) {
                data.add(new Object[]{s.getSparepartId(), s.getKodeSparepart(), s.getNamaSparepart(),
                        s.getSatuan(), s.getStok(), s.getHargaBeli(), s.getHargaJual(), s.getSupplierId()});
            }
            styledTable.setData(new String[]{"ID", "Kode", "Nama", "Satuan", "Stok", "Harga Beli", "Harga Jual", "Supplier ID"}, data);
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
