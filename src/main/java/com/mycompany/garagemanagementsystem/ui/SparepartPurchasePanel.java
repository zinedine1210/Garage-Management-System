package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.SparepartDAO;
import com.mycompany.garagemanagementsystem.dao.SparepartPurchaseDAO;
import com.mycompany.garagemanagementsystem.dao.SupplierDAO;
import com.mycompany.garagemanagementsystem.model.Sparepart;
import com.mycompany.garagemanagementsystem.model.SparepartPurchase;
import com.mycompany.garagemanagementsystem.model.Supplier;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SparepartPurchasePanel extends javax.swing.JPanel {

    private final SparepartPurchaseDAO purchaseDAO = new SparepartPurchaseDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final SparepartDAO sparepartDAO = new SparepartDAO();

    private JComboBox<Object> cbSupplier;
    private JComboBox<Object> cbSparepart;
    private JTextField txtQty;
    private JTextField txtHargaBeli;
    private JTextField txtTotalHarga;
    private JTextField txtKeterangan;
    private JTextField txtSearch;
    private JTable table;
    private JButton btnSimpan;
    private JButton btnHapus;

    private List<Supplier> supplierList;
    private List<Sparepart> sparepartList;

    public SparepartPurchasePanel() {
        initUI();
        loadCombos();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Pembelian Sparepart Baru"));

        formPanel.add(new JLabel("Supplier:"));
        cbSupplier = new JComboBox<>();
        formPanel.add(cbSupplier);

        formPanel.add(new JLabel("Sparepart:"));
        cbSparepart = new JComboBox<>();
        formPanel.add(cbSparepart);

        formPanel.add(new JLabel("Qty:"));
        txtQty = new JTextField("1");
        formPanel.add(txtQty);

        formPanel.add(new JLabel("Harga Beli/pcs:"));
        txtHargaBeli = new JTextField("0");
        formPanel.add(txtHargaBeli);

        formPanel.add(new JLabel("Total Harga:"));
        txtTotalHarga = new JTextField("0");
        txtTotalHarga.setEditable(false);
        formPanel.add(txtTotalHarga);

        formPanel.add(new JLabel("Keterangan:"));
        txtKeterangan = new JTextField();
        formPanel.add(txtKeterangan);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(new JLabel(" Cari: "), BorderLayout.WEST);
        txtSearch = new JTextField();
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        filterPanel.add(txtSearch, BorderLayout.CENTER);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnSimpan = new JButton("Simpan Pembelian");
        btnSimpan.addActionListener(e -> simpanPembelian());
        buttonPanel.add(btnSimpan);

        btnHapus = new JButton("Hapus");
        btnHapus.addActionListener(e -> hapusPembelian());
        buttonPanel.add(btnHapus);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.SOUTH);

        txtQty.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { hitungTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { hitungTotal(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { hitungTotal(); }
        });
        txtHargaBeli.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { hitungTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { hitungTotal(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { hitungTotal(); }
        });
    }

    private void loadCombos() {
        try {
            supplierList = supplierDAO.findAll();
            cbSupplier.removeAllItems();
            cbSupplier.addItem("-- Pilih Supplier --");
            for (Supplier s : supplierList) {
                cbSupplier.addItem(s.getSupplierId() + " - " + s.getNama());
            }

            sparepartList = sparepartDAO.findAll();
            cbSparepart.removeAllItems();
            cbSparepart.addItem("-- Pilih Sparepart --");
            for (Sparepart sp : sparepartList) {
                cbSparepart.addItem(sp.getSparepartId() + " - " + sp.getKodeSparepart() + " - " + sp.getNamaSparepart());
            }

            cbSupplier.addActionListener(e -> filterSparepartBySupplier());
            cbSparepart.addActionListener(e -> autofillHargaFromSparepart());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load data: " + ex.getMessage());
        }
    }

    private void filterSparepartBySupplier() {
        try {
            Object selected = cbSupplier.getSelectedItem();
            if (selected == null || selected.toString().startsWith("--")) {
                cbSparepart.removeAllItems();
                cbSparepart.addItem("-- Pilih Sparepart --");
                return;
            }

            String[] parts = selected.toString().split(" - ", 2);
            int supplierId = Integer.parseInt(parts[0]);

            List<Sparepart> filtered = sparepartDAO.findBySupplierId(supplierId);
            cbSparepart.removeAllItems();
            cbSparepart.addItem("-- Pilih Sparepart --");
            for (Sparepart sp : filtered) {
                cbSparepart.addItem(sp.getSparepartId() + " - " + sp.getKodeSparepart() + " - " + sp.getNamaSparepart() + " (Rp " + String.format("%,.0f", sp.getHargaBeli()) + ")");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error filter sparepart: " + ex.getMessage());
        }
    }

    private void autofillHargaFromSparepart() {
        try {
            Object selected = cbSparepart.getSelectedItem();
            if (selected == null || selected.toString().startsWith("--")) {
                txtHargaBeli.setText("0");
                return;
            }

            String[] parts = selected.toString().split(" - ");
            int sparepartId = Integer.parseInt(parts[0]);

            Sparepart sp = sparepartDAO.findById(sparepartId);
            if (sp != null) {
                txtHargaBeli.setText(String.valueOf(sp.getHargaBeli()));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error autofill harga: " + ex.getMessage());
        }
    }

    private void hitungTotal() {
        try {
            int qty = Integer.parseInt(txtQty.getText().trim());
            double harga = Double.parseDouble(txtHargaBeli.getText().trim());
            txtTotalHarga.setText(String.valueOf(qty * harga));
        } catch (NumberFormatException ignored) {
        }
    }

    private void simpanPembelian() {
        try {
            if (cbSupplier.getSelectedIndex() <= 0 || cbSparepart.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, "Pilih Supplier dan Sparepart.");
                return;
            }

            String supplierStr = cbSupplier.getSelectedItem().toString();
            String sparepartStr = cbSparepart.getSelectedItem().toString();

            int supplierId = Integer.parseInt(supplierStr.split(" - ")[0]);
            int sparepartId = Integer.parseInt(sparepartStr.split(" - ")[0]);

            SparepartPurchase p = new SparepartPurchase();
            p.setTanggal(new Date());
            p.setSupplierId(supplierId);
            p.setSparepartId(sparepartId);
            p.setQty(Integer.parseInt(txtQty.getText().trim()));
            p.setHargaBeli(Double.parseDouble(txtHargaBeli.getText().trim()));
            p.setTotalHarga(p.getQty() * p.getHargaBeli());
            p.setKeterangan(txtKeterangan.getText().trim());

            purchaseDAO.insert(p);
            JOptionPane.showMessageDialog(this, "Pembelian berhasil disimpan! Stok diperbarui.");
            clearForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error simpan: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format angka tidak valid.");
        }
    }

    private void hapusPembelian() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus.");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus pembelian ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                purchaseDAO.delete(id);
                loadData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error hapus: " + ex.getMessage());
            }
        }
    }

    private void clearForm() {
        cbSupplier.setSelectedIndex(0);
        cbSparepart.setSelectedIndex(0);
        txtQty.setText("1");
        txtHargaBeli.setText("0");
        txtTotalHarga.setText("0");
        txtKeterangan.setText("");
    }

    private void loadData() {
        try {
            List<SparepartPurchase> list = purchaseDAO.findAll();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Tanggal", "Supplier", "Kode SP", "Sparepart", "Qty", "Harga Beli", "Total", "Keterangan"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            for (SparepartPurchase p : list) {
                model.addRow(new Object[]{
                    p.getPurchaseId(),
                    p.getTanggal() != null ? p.getTanggal().toString() : "",
                    p.getSupplierNama(),
                    p.getKodeSp(),
                    p.getSparepartNama(),
                    p.getQty(),
                    p.getHargaBeli(),
                    p.getTotalHarga(),
                    p.getKeterangan()
                });
            }
            table.setModel(model);
            table.setRowSorter(new javax.swing.table.TableRowSorter<>(model));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load data: " + ex.getMessage());
        }
    }

    private void filterTable() {
        if (table.getRowSorter() == null) return;
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                (javax.swing.table.TableRowSorter<DefaultTableModel>) table.getRowSorter();
        String text = txtSearch.getText().trim();
        if (text.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
    }
}
