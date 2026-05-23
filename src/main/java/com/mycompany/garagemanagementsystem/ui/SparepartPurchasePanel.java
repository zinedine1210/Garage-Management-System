package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.SparepartDAO;
import com.mycompany.garagemanagementsystem.dao.SparepartPurchaseDAO;
import com.mycompany.garagemanagementsystem.dao.SupplierDAO;
import com.mycompany.garagemanagementsystem.model.Sparepart;
import com.mycompany.garagemanagementsystem.model.SparepartPurchase;
import com.mycompany.garagemanagementsystem.model.Supplier;
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

public class SparepartPurchasePanel extends javax.swing.JPanel {

    private final SparepartPurchaseDAO purchaseDAO = new SparepartPurchaseDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final SparepartDAO sparepartDAO = new SparepartDAO();
    private StyledTable styledTable;
    private JTextField txtSearch;

    public SparepartPurchasePanel() {
        buildUI();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        JPanel topWrap = new JPanel(new BorderLayout(0, 6));
        topWrap.setOpaque(false);
        topWrap.add(UIHelper.createPageHeader("Pembelian Sparepart",
                "Catat pembelian sparepart dari supplier untuk menambah stok bengkel"), BorderLayout.NORTH);

        // Filter bar
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

        // Buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(243, 245, 249));
        buttonPanel.setBorder(new EmptyBorder(6, 0, 6, 0));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftButtons.setOpaque(false);

        JButton btnTambah = createStyledButton("+ Pembelian Baru", new Color(40, 167, 69));
        btnTambah.addActionListener(e -> showPurchaseDialog());
        leftButtons.add(btnTambah);

        JButton btnHapus = createStyledButton("Hapus", new Color(220, 53, 69));
        btnHapus.addActionListener(e -> deletePurchase());
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
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Data Pembelian", options);
            if (choice == 0) ExportUtils.exportTableToPDF(styledTable.getTable(), "Data_Pembelian_Sparepart");
            else if (choice == 1) ExportUtils.exportTableToExcel(styledTable.getTable(), "Data_Pembelian_Sparepart");
        });
        rightButtons.add(btnExport);

        buttonPanel.add(rightButtons, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @SuppressWarnings("unchecked")
    private void showPurchaseDialog() {
        String dlgTitle = "Catat Pembelian Sparepart";
        String dlgSub = "Lengkapi informasi pembelian dari supplier";
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

        JComboBox<String> cbSupplier = new JComboBox<>();
        JComboBox<String> cbSparepart = new JComboBox<>();
        JTextField txtQty = new JTextField("1", 20);
        JTextField txtHargaBeli = new JTextField("0", 20);
        JTextField txtTotalHarga = new JTextField("0", 20);
        txtTotalHarga.setEditable(false);
        JTextField txtKeterangan = new JTextField(20);

        // Load combos
        List<Supplier> suppliers = new ArrayList<>();
        List<Sparepart> allSpareparts = new ArrayList<>();
        try {
            suppliers = supplierDAO.findAll();
            allSpareparts = sparepartDAO.findAll();
        } catch (SQLException ignored) {}

        cbSupplier.addItem("-- Pilih Supplier --");
        for (Supplier s : suppliers) cbSupplier.addItem(s.getSupplierId() + " - " + s.getNama());

        cbSparepart.addItem("-- Pilih Sparepart --");
        for (Sparepart sp : allSpareparts) {
            cbSparepart.addItem(sp.getSparepartId() + " - " + sp.getKodeSparepart() + " - " + sp.getNamaSparepart());
        }

        final List<Sparepart> sparepartsFinal = allSpareparts;
        cbSupplier.addActionListener(e -> {
            cbSparepart.removeAllItems();
            cbSparepart.addItem("-- Pilih Sparepart --");
            if (cbSupplier.getSelectedIndex() <= 0) return;
            try {
                int suppId = Integer.parseInt(cbSupplier.getSelectedItem().toString().split(" - ")[0]);
                List<Sparepart> filtered = sparepartDAO.findBySupplierId(suppId);
                for (Sparepart sp : filtered) {
                    cbSparepart.addItem(sp.getSparepartId() + " - " + sp.getKodeSparepart() + " - " + sp.getNamaSparepart() + " (Rp " + String.format("%,.0f", sp.getHargaBeli()) + ")");
                }
            } catch (Exception ignored2) {}
        });

        cbSparepart.addActionListener(e -> {
            if (cbSparepart.getSelectedIndex() <= 0) { txtHargaBeli.setText("0"); return; }
            try {
                int spId = Integer.parseInt(cbSparepart.getSelectedItem().toString().split(" - ")[0]);
                Sparepart sp = sparepartDAO.findById(spId);
                if (sp != null) txtHargaBeli.setText(String.valueOf(sp.getHargaBeli()));
            } catch (Exception ignored2) {}
        });

        // Auto calc
        javax.swing.event.DocumentListener calcListener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calc(); }
            void calc() {
                try {
                    int q = Integer.parseInt(txtQty.getText().trim());
                    double h = Double.parseDouble(txtHargaBeli.getText().trim());
                    txtTotalHarga.setText(String.format("%,.0f", q * h));
                } catch (NumberFormatException ignored2) {}
            }
        };
        txtQty.getDocument().addDocumentListener(calcListener);
        txtHargaBeli.getDocument().addDocumentListener(calcListener);

        int r = 0;
        addFormField(form, gbc, r++, "Supplier:", cbSupplier);
        addFormField(form, gbc, r++, "Sparepart:", cbSparepart);
        addFormField(form, gbc, r++, "Qty:", txtQty);
        addFormField(form, gbc, r++, "Harga Beli/pcs:", txtHargaBeli);
        addFormField(form, gbc, r++, "Total Harga:", txtTotalHarga);
        addFormField(form, gbc, r++, "Keterangan:", txtKeterangan);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = UIHelper.createDialogButtonPanel();

        JButton btnCancel = createStyledButton("Batal", new Color(108, 117, 125));
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);

        JButton btnSave = createStyledButton("Simpan", new Color(40, 167, 69));
        btnSave.addActionListener(e -> {
            if (cbSupplier.getSelectedIndex() <= 0 || cbSparepart.getSelectedIndex() <= 0) {
                UIHelper.warn(dialog, "Pilih Supplier dan Sparepart.");
                return;
            }
            try {
                int suppId = Integer.parseInt(cbSupplier.getSelectedItem().toString().split(" - ")[0]);
                int spId = Integer.parseInt(cbSparepart.getSelectedItem().toString().split(" - ")[0]);

                SparepartPurchase p = new SparepartPurchase();
                p.setTanggal(new Date());
                p.setSupplierId(suppId);
                p.setSparepartId(spId);
                p.setQty(Integer.parseInt(txtQty.getText().trim()));
                p.setHargaBeli(Double.parseDouble(txtHargaBeli.getText().trim()));
                p.setTotalHarga(p.getQty() * p.getHargaBeli());
                p.setKeterangan(txtKeterangan.getText().trim());

                purchaseDAO.insert(p);
                UIHelper.success(dialog, "Pembelian berhasil disimpan! Stok diperbarui.");
                dialog.dispose();
                loadData();
            } catch (SQLException ex) {
                UIHelper.error(dialog, "Error: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                UIHelper.error(dialog, "Format angka tidak valid.");
            }
        });
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(500, 380));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deletePurchase() {
        Object[] row = styledTable.getSelectedRowData();
        if (row == null) { UIHelper.warn(this, "Pilih data yang akan dihapus."); return; }
        if (UIHelper.confirm(this, "Hapus pembelian #" + row[0] + "?", "Konfirmasi Hapus")) {
            try {
                purchaseDAO.delete((int) row[0]);
                loadData();
            } catch (SQLException ex) {
                UIHelper.error(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void loadData() {
        try {
            List<SparepartPurchase> list = purchaseDAO.findAll();
            List<Object[]> data = new ArrayList<>();
            for (SparepartPurchase p : list) {
                data.add(new Object[]{p.getPurchaseId(),
                        p.getTanggal() != null ? p.getTanggal().toString() : "",
                        p.getSupplierNama(), p.getKodeSp(), p.getSparepartNama(),
                        p.getQty(), p.getHargaBeli(), p.getTotalHarga(), p.getKeterangan()});
            }
            styledTable.setData(new String[]{"ID", "Tanggal", "Supplier", "Kode SP", "Sparepart",
                    "Qty", "Harga Beli", "Total", "Keterangan"}, data);
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
}
