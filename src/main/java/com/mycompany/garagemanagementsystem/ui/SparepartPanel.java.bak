package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.SparepartDAO;
import com.mycompany.garagemanagementsystem.dao.SupplierDAO;
import com.mycompany.garagemanagementsystem.model.Sparepart;
import com.mycompany.garagemanagementsystem.model.Supplier;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class SparepartPanel extends javax.swing.JPanel {

    private final SparepartDAO sparepartDAO = new SparepartDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    public SparepartPanel() {
        initComponents();
        myInit();
    }

    private void myInit() {
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        table.setRowHeight(22);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> isiFormDariTabel());
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTabel(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTabel(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTabel(); }
            private void filterTabel() {
                String teks = txtSearch.getText();
                if (table.getRowSorter() == null) {
                    table.setRowSorter(new javax.swing.table.TableRowSorter<>((DefaultTableModel) table.getModel()));
                }
                javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                        (javax.swing.table.TableRowSorter<DefaultTableModel>) table.getRowSorter();
                if (teks.trim().isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + teks));
            }
        });
        loadSuppliers();
        loadData();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        formPanel = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblKode = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        lblNama = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        lblSatuan = new javax.swing.JLabel();
        txtSatuan = new javax.swing.JTextField();
        lblStok = new javax.swing.JLabel();
        txtStok = new javax.swing.JTextField();
        lblHargaBeli = new javax.swing.JLabel();
        txtHargaBeli = new javax.swing.JTextField();
        lblHargaJual = new javax.swing.JLabel();
        txtHargaJual = new javax.swing.JTextField();
        lblSupplier = new javax.swing.JLabel();
        cbSupplier = new javax.swing.JComboBox();
        filterPanel = new javax.swing.JPanel();
        lblCari = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        btnBaru = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());
        topPanel.setLayout(new java.awt.BorderLayout());
        formPanel.setLayout(new java.awt.GridLayout(8, 2));
        lblId.setText("ID:"); formPanel.add(lblId);
        txtId.setColumns(5); txtId.setEnabled(false); formPanel.add(txtId);
        lblKode.setText("Kode:"); formPanel.add(lblKode);
        txtKode.setColumns(15); formPanel.add(txtKode);
        lblNama.setText("Nama:"); formPanel.add(lblNama);
        txtNama.setColumns(20); formPanel.add(txtNama);
        lblSatuan.setText("Satuan:"); formPanel.add(lblSatuan);
        txtSatuan.setColumns(10); formPanel.add(txtSatuan);
        lblStok.setText("Stok:"); formPanel.add(lblStok);
        txtStok.setColumns(5); formPanel.add(txtStok);
        lblHargaBeli.setText("Harga Beli:"); formPanel.add(lblHargaBeli);
        txtHargaBeli.setColumns(10); formPanel.add(txtHargaBeli);
        lblHargaJual.setText("Harga Jual:"); formPanel.add(lblHargaJual);
        txtHargaJual.setColumns(10); formPanel.add(txtHargaJual);
        lblSupplier.setText("Supplier:"); formPanel.add(lblSupplier);
        formPanel.add(cbSupplier);
        topPanel.add(formPanel, java.awt.BorderLayout.CENTER);

        filterPanel.setLayout(new java.awt.BorderLayout());
        lblCari.setText(" Cari: "); filterPanel.add(lblCari, java.awt.BorderLayout.WEST);
        filterPanel.add(txtSearch, java.awt.BorderLayout.CENTER);
        topPanel.add(filterPanel, java.awt.BorderLayout.SOUTH);
        add(topPanel, java.awt.BorderLayout.NORTH);

        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(table);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btnBaru.setText("Baru");
        btnBaru.addActionListener(new java.awt.event.ActionListener() { public void actionPerformed(java.awt.event.ActionEvent evt) { btnBaruActionPerformed(evt); } });
        buttonPanel.add(btnBaru);
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() { public void actionPerformed(java.awt.event.ActionEvent evt) { btnSimpanActionPerformed(evt); } });
        buttonPanel.add(btnSimpan);
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() { public void actionPerformed(java.awt.event.ActionEvent evt) { btnHapusActionPerformed(evt); } });
        buttonPanel.add(btnHapus);

        javax.swing.JButton btnRefresh = new javax.swing.JButton();
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() { public void actionPerformed(java.awt.event.ActionEvent evt) { loadData(); } });
        buttonPanel.add(btnRefresh);

        javax.swing.JButton btnPrint = new javax.swing.JButton();
        btnPrint.setText("Export");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Object[] options = {"PDF", "Excel", "Batal"};
                int choice = javax.swing.JOptionPane.showOptionDialog(SparepartPanel.this, "Pilih format export:", "Export Data", javax.swing.JOptionPane.YES_NO_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice == 0) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToPDF(table, "Data_Sparepart");
                else if (choice == 1) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToExcel(table, "Data_Sparepart");
            }
        });
        buttonPanel.add(btnPrint);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruActionPerformed
        clearForm();
    }//GEN-LAST:event_btnBaruActionPerformed
    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        saveSparepart();
    }//GEN-LAST:event_btnSimpanActionPerformed
    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        deleteSparepart();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void loadSuppliers() {
        try {
            cbSupplier.removeAllItems();
            cbSupplier.addItem("-- Tanpa Supplier --");
            for (Supplier s : supplierDAO.findAll()) {
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
                    new Object[]{"ID", "Kode", "Nama", "Satuan", "Stok", "Harga Beli", "Harga Jual", "Supplier ID"}, 0);
            for (Sparepart s : list) {
                model.addRow(new Object[]{
                    s.getSparepartId(), s.getKodeSparepart(), s.getNamaSparepart(),
                    s.getSatuan(), s.getStok(), s.getHargaBeli(), s.getHargaJual(), s.getSupplierId()
                });
            }
            table.setModel(model);
            table.setRowSorter(new javax.swing.table.TableRowSorter<>(model));
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
        if (cbSupplier.getItemCount() > 0) cbSupplier.setSelectedIndex(0);
        table.clearSelection();
        txtKode.requestFocusInWindow();
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

            Object sel = cbSupplier.getSelectedItem();
            if (sel instanceof Supplier) {
                s.setSupplierId(((Supplier) sel).getSupplierId());
            } else {
                s.setSupplierId(0);
            }

            if (s.getSparepartId() == 0) sparepartDAO.insert(s);
            else sparepartDAO.update(s);
            loadData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error simpan: " + ex.getMessage());
        }
    }

    private void deleteSparepart() {
        if (txtId.getText().isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus sparepart ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
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

    private void isiFormDariTabel() {
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
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JComboBox cbSupplier;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JPanel formPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblHargaBeli;
    private javax.swing.JLabel lblHargaJual;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblKode;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblSatuan;
    private javax.swing.JLabel lblStok;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JTable table;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField txtHargaBeli;
    private javax.swing.JTextField txtHargaJual;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtSatuan;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtStok;
    // End of variables declaration//GEN-END:variables
}
