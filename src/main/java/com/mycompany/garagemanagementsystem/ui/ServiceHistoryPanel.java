package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceHistoryItem;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ServiceHistoryPanel extends javax.swing.JPanel {

    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();

    public ServiceHistoryPanel() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchPanel = new javax.swing.JPanel();
        lblNoPolisi = new javax.swing.JLabel();
        txtNoPolisi = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        lblSpacer = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        searchPanel.setLayout(new java.awt.GridLayout(1, 4, 10, 10));

        lblNoPolisi.setText(" Pencarian berdasarkan No Polisi:");
        lblNoPolisi.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        searchPanel.add(lblNoPolisi);

        txtNoPolisi.setColumns(15);
        searchPanel.add(txtNoPolisi);

        btnCari.setText("Cari Riwayat");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });
        searchPanel.add(btnCari);

        lblSpacer.setText("");
        searchPanel.add(lblSpacer);

        add(searchPanel, java.awt.BorderLayout.NORTH);

        table.setFillsViewportHeight(true);
        jScrollPane1.setViewportView(table);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        cariRiwayat();
    }//GEN-LAST:event_btnCariActionPerformed

    private void cariRiwayat() {
        String nopol = txtNoPolisi.getText().trim();
        if (nopol.isEmpty()) { JOptionPane.showMessageDialog(this, "Masukkan No Polisi terlebih dahulu."); return; }
        try {
            List<ServiceHistoryItem> history = transDAO.findHistoryByNoPolisi(nopol);
            if (history.isEmpty()) { JOptionPane.showMessageDialog(this, "Tidak ada riwayat servis untuk No Polisi tersebut."); table.setModel(new DefaultTableModel()); return; }
            DefaultTableModel model = new DefaultTableModel(new Object[]{"Tanggal", "Mekanik", "Keluhan/Pekerjaan", "Sparepart Diganti", "Total Biaya"}, 0);
            for (ServiceHistoryItem item : history) {
                model.addRow(new Object[]{item.getTanggal(), item.getMekanik(), item.getKeluhan(), item.getSpareparts(), String.format("Rp %,.0f", item.getTotalBiaya())});
            }
            table.setModel(model);
            table.getColumnModel().getColumn(3).setPreferredWidth(250);
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error pencarian data: " + ex.getMessage()); }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblNoPolisi;
    private javax.swing.JLabel lblSpacer;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtNoPolisi;
    // End of variables declaration//GEN-END:variables
}
