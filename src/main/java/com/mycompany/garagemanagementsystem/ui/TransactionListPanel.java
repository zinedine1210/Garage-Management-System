package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class TransactionListPanel extends javax.swing.JPanel {

    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private Frame owner;

    public TransactionListPanel() {
        initComponents();
        myInit();
    }

    public TransactionListPanel(Frame owner) {
        this();
        this.owner = owner;
    }

    private void myInit() {
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        cbStatus.addActionListener(e -> applyFilter());
        cbBulan.addActionListener(e -> applyFilter());
        loadData();
    }

    private void applyFilter() {
        if (table.getRowSorter() == null) {
            table.setRowSorter(new javax.swing.table.TableRowSorter<>((DefaultTableModel) table.getModel()));
        }
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                (javax.swing.table.TableRowSorter<DefaultTableModel>) table.getRowSorter();

        java.util.List<javax.swing.RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

        String text = txtSearch.getText().trim();
        if (text.length() > 0) {
            filters.add(javax.swing.RowFilter.regexFilter("(?i)" + text));
        }

        String status = cbStatus.getSelectedItem().toString();
        if (!status.equals("Semua")) {
            filters.add(javax.swing.RowFilter.regexFilter("(?i)^" + status + "$", 8));
        }

        String bulan = cbBulan.getSelectedItem().toString();
        if (!bulan.equals("Semua")) {
            filters.add(javax.swing.RowFilter.regexFilter("-[0]*" + bulan + "-", 1));
        }

        if (filters.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(javax.swing.RowFilter.andFilter(filters));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        filterPanel = new javax.swing.JPanel();
        lblCari = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        cbStatus = new javax.swing.JComboBox();
        lblBulan = new javax.swing.JLabel();
        cbBulan = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        btnBaru = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new java.awt.BorderLayout());
        filterPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        lblCari.setText("Cari:"); filterPanel.add(lblCari);
        txtSearch.setColumns(15); filterPanel.add(txtSearch);
        lblStatus.setText("Status:"); filterPanel.add(lblStatus);
        cbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Menunggu", "Dikerjakan", "Selesai Lunas", "Batal" }));
        filterPanel.add(cbStatus);
        lblBulan.setText("Bulan:"); filterPanel.add(lblBulan);
        cbBulan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));
        filterPanel.add(cbBulan);
        topPanel.add(filterPanel, java.awt.BorderLayout.CENTER);
        add(topPanel, java.awt.BorderLayout.NORTH);

        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(table);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btnBaru.setText("Transaksi Baru");
        btnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnBaruActionPerformed(evt); }
        });
        buttonPanel.add(btnBaru);
        btnEdit.setText("Edit Transaksi");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnEditActionPerformed(evt); }
        });
        buttonPanel.add(btnEdit);
        btnHapus.setText("Hapus Transaksi");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnHapusActionPerformed(evt); }
        });
        buttonPanel.add(btnHapus);
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnRefreshActionPerformed(evt); }
        });
        buttonPanel.add(btnRefresh);
        add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruActionPerformed
        new ServiceTransactionFrame(owner).setVisible(true); loadData();
    }//GEN-LAST:event_btnBaruActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan diedit."); return; }
        int transId = (int) table.getValueAt(row, 0);
        new ServiceTransactionFrame(owner, transId).setVisible(true); loadData();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan dihapus."); return; }
        int transId = (int) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus transaksi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try { transDAO.delete(transId); loadData(); }
            catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error hapus: " + ex.getMessage()); }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadData();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void loadData() {
        try {
            List<ServiceTransaction> list = transDAO.findAll();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID Transaksi", "Tanggal", "Pelanggan", "No. Polisi", "Keluhan", "Mekanik", "Total Jasa", "Total Sparepart", "Status", "Grand Total"}, 0);
            for (ServiceTransaction t : list) {
                model.addRow(new Object[]{t.getTransId(), t.getTanggal().toString(), t.getClientNama(), t.getNoPolisi(),
                    t.getKeluhan(), t.getMekanikNama(), t.getTotalJasa(), t.getTotalSparepart(), t.getStatusServis(), t.getGrandTotal()});
            }
            table.setModel(model);
            javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(model);
            table.setRowSorter(sorter);
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error load data: " + ex.getMessage()); }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JComboBox cbBulan;
    private javax.swing.JComboBox cbStatus;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBulan;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable table;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
