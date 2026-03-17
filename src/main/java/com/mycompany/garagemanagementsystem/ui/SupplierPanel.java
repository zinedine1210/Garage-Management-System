package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.SupplierDAO;
import com.mycompany.garagemanagementsystem.model.Supplier;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class SupplierPanel extends JPanel {

    private final JTextField txtId;
    private final JTextField txtNama;
    private final JTextField txtAlamat;
    private final JTextField txtTelepon;
    private final JTextField txtEmail;
    private final JTable table;
    private final SupplierDAO supplierDAO = new SupplierDAO();

    public SupplierPanel() {

        txtId = new JTextField(5);
        txtId.setEnabled(false);
        txtNama = new JTextField(20);
        txtAlamat = new JTextField(20);
        txtTelepon = new JTextField(15);
        txtEmail = new JTextField(20);

        JButton btnBaru = new JButton("Baru");
        JButton btnSimpan = new JButton("Simpan");
        JButton btnHapus = new JButton("Hapus");

        btnBaru.addActionListener(e -> clearForm());
        btnSimpan.addActionListener(e -> saveSupplier());
        btnHapus.addActionListener(e -> deleteSupplier());

        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> tableSelectionChanged());

        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        formPanel.add(new JLabel("ID:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Nama:"));
        formPanel.add(txtNama);
        formPanel.add(new JLabel("Alamat:"));
        formPanel.add(txtAlamat);
        formPanel.add(new JLabel("Telepon:"));
        formPanel.add(txtTelepon);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);

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

        loadData();
    }

    private void loadData() {
        try {
            List<Supplier> list = supplierDAO.findAll();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Nama", "Alamat", "Telepon", "Email"}, 0);
            for (Supplier s : list) {
                model.addRow(new Object[]{
                    s.getSupplierId(),
                    s.getNama(),
                    s.getAlamat(),
                    s.getTelepon(),
                    s.getEmail()
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
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelepon.setText("");
        txtEmail.setText("");
    }

    private void saveSupplier() {
        try {
            Supplier s = new Supplier();
            if (!txtId.getText().isEmpty()) {
                s.setSupplierId(Integer.parseInt(txtId.getText()));
            }
            s.setNama(txtNama.getText());
            s.setAlamat(txtAlamat.getText());
            s.setTelepon(txtTelepon.getText());
            s.setEmail(txtEmail.getText());

            if (s.getSupplierId() == 0) {
                supplierDAO.insert(s);
            } else {
                supplierDAO.update(s);
            }
            loadData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error simpan: " + ex.getMessage());
        }
    }

    private void deleteSupplier() {
        if (txtId.getText().isEmpty()) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus supplier ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                supplierDAO.delete(Integer.parseInt(txtId.getText()));
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
            txtNama.setText(table.getValueAt(row, 1).toString());
            txtAlamat.setText(table.getValueAt(row, 2).toString());
            txtTelepon.setText(table.getValueAt(row, 3).toString());
            txtEmail.setText(table.getValueAt(row, 4).toString());
        }
    }
}

