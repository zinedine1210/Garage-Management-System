package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.SparepartDAO;
import com.mycompany.garagemanagementsystem.dao.SupplierDAO;
import com.mycompany.garagemanagementsystem.model.Sparepart;
import com.mycompany.garagemanagementsystem.model.Supplier;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class SparepartFrame extends JDialog {

    private final JTextField txtId;
    private final JTextField txtKode;
    private final JTextField txtNama;
    private final JTextField txtSatuan;
    private final JTextField txtStok;
    private final JTextField txtHargaBeli;
    private final JTextField txtHargaJual;
    private final JComboBox<Supplier> cbSupplier;
    private final JTable table;
    private final SparepartDAO sparepartDAO = new SparepartDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    public SparepartFrame(Frame owner) {
        super(owner, "Master Sparepart", true);
        setSize(800, 450);
        setLocationRelativeTo(owner);

        txtId = new JTextField(5);
        txtId.setEnabled(false);
        txtKode = new JTextField(15);
        txtNama = new JTextField(20);
        txtSatuan = new JTextField(10);
        txtStok = new JTextField(5);
        txtHargaBeli = new JTextField(10);
        txtHargaJual = new JTextField(10);
        cbSupplier = new JComboBox<>();

        JButton btnBaru = new JButton("Baru");
        JButton btnSimpan = new JButton("Simpan");
        JButton btnHapus = new JButton("Hapus");

        btnBaru.addActionListener(e -> clearForm());
        btnSimpan.addActionListener(e -> saveSparepart());
        btnHapus.addActionListener(e -> deleteSparepart());

        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> tableSelectionChanged());

        JPanel formPanel = new JPanel(new GridLayout(8, 2));
        formPanel.add(new JLabel("ID:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Kode:"));
        formPanel.add(txtKode);
        formPanel.add(new JLabel("Nama:"));
        formPanel.add(txtNama);
        formPanel.add(new JLabel("Satuan:"));
        formPanel.add(txtSatuan);
        formPanel.add(new JLabel("Stok:"));
        formPanel.add(txtStok);
        formPanel.add(new JLabel("Harga Beli:"));
        formPanel.add(txtHargaBeli);
        formPanel.add(new JLabel("Harga Jual:"));
        formPanel.add(txtHargaJual);
        formPanel.add(new JLabel("Supplier:"));
        formPanel.add(cbSupplier);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnBaru);
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnHapus);

        // --- FILTER PANEL ---
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(new JLabel(" Cari: "), BorderLayout.WEST);
        JTextField txtSearch = new JTextField();
        filterPanel.add(txtSearch, BorderLayout.CENTER);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText();
                if (table.getRowSorter() == null) {
                    javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>((DefaultTableModel) table.getModel());
                    table.setRowSorter(sorter);
                }
                javax.swing.table.TableRowSorter<DefaultTableModel> sorter = (javax.swing.table.TableRowSorter<DefaultTableModel>) table.getRowSorter();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadSuppliers();
        loadData();
    }

    private void loadSuppliers() {
        try {
            cbSupplier.removeAllItems();
            List<Supplier> suppliers = supplierDAO.findAll();
            for (Supplier s : suppliers) {
                cbSupplier.addItem(s);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load suppliers: " + ex.getMessage());
        }
    }

    private void loadData() {
        try {
            List<Sparepart> list = sparepartDAO.findAll();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Kode", "Nama", "Satuan", "Stok",
                        "Harga Beli", "Harga Jual", "Supplier ID"}, 0);
            for (Sparepart s : list) {
                model.addRow(new Object[]{
                    s.getSparepartId(),
                    s.getKodeSparepart(),
                    s.getNamaSparepart(),
                    s.getSatuan(),
                    s.getStok(),
                    s.getHargaBeli(),
                    s.getHargaJual(),
                    s.getSupplierId()
                });
            }
            table.setModel(model);
            
            // Reapply sorter
            javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(model);
            table.setRowSorter(sorter);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load data: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtKode.setText("");
        txtNama.setText("");
        txtSatuan.setText("");
        txtStok.setText("");
        txtHargaBeli.setText("");
        txtHargaJual.setText("");
        if (cbSupplier.getItemCount() > 0) {
            cbSupplier.setSelectedIndex(0);
        }
    }

    private void saveSparepart() {
        try {
            Sparepart s = new Sparepart();
            if (!txtId.getText().isEmpty()) {
                s.setSparepartId(Integer.parseInt(txtId.getText()));
            }
            s.setKodeSparepart(txtKode.getText());
            s.setNamaSparepart(txtNama.getText());
            s.setSatuan(txtSatuan.getText());
            s.setStok(Integer.parseInt(txtStok.getText()));
            s.setHargaBeli(Double.parseDouble(txtHargaBeli.getText()));
            s.setHargaJual(Double.parseDouble(txtHargaJual.getText()));
            Supplier selectedSupplier = (Supplier) cbSupplier.getSelectedItem();
            if (selectedSupplier != null) {
                s.setSupplierId(selectedSupplier.getSupplierId());
            } else {
                s.setSupplierId(0);
            }

            if (s.getSparepartId() == 0) {
                sparepartDAO.insert(s);
            } else {
                sparepartDAO.update(s);
            }
            loadData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error simpan: " + ex.getMessage());
        }
    }

    private void deleteSparepart() {
        if (txtId.getText().isEmpty()) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus sparepart ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                sparepartDAO.delete(Integer.parseInt(txtId.getText()));
                loadData();
                clearForm();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error hapus: " + ex.getMessage());
            }
        }
    }

    private void tableSelectionChanged() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(table.getValueAt(row, 0).toString());
            txtKode.setText(table.getValueAt(row, 1).toString());
            txtNama.setText(table.getValueAt(row, 2).toString());
            txtSatuan.setText(table.getValueAt(row, 3).toString());
            txtStok.setText(table.getValueAt(row, 4).toString());
            txtHargaBeli.setText(table.getValueAt(row, 5).toString());
            txtHargaJual.setText(table.getValueAt(row, 6).toString());
            int supplierId = Integer.parseInt(table.getValueAt(row, 7).toString());
            for (int i = 0; i < cbSupplier.getItemCount(); i++) {
                if (cbSupplier.getItemAt(i).getSupplierId() == supplierId) {
                    cbSupplier.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
}

