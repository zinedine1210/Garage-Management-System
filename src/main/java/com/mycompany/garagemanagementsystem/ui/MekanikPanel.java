package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.MekanikDAO;
import com.mycompany.garagemanagementsystem.model.Mekanik;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class MekanikPanel extends javax.swing.JPanel {

    private final MekanikDAO mekanikDAO = new MekanikDAO();

    public MekanikPanel() {
        initComponents();
        myInit();
    }

    private void myInit() {
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
        loadData();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        formPanel = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblNama = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        lblTelepon = new javax.swing.JLabel();
        txtTelepon = new javax.swing.JTextField();
        lblSpesialis = new javax.swing.JLabel();
        txtSpesialis = new javax.swing.JTextField();
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

        formPanel.setLayout(new java.awt.GridLayout(4, 2));
        lblId.setText("ID:"); formPanel.add(lblId);
        txtId.setColumns(5); txtId.setEnabled(false); formPanel.add(txtId);
        lblNama.setText("Nama:"); formPanel.add(lblNama);
        txtNama.setColumns(20); formPanel.add(txtNama);
        lblTelepon.setText("Telepon:"); formPanel.add(lblTelepon);
        txtTelepon.setColumns(15); formPanel.add(txtTelepon);
        lblSpesialis.setText("Spesialis:"); formPanel.add(lblSpesialis);
        txtSpesialis.setColumns(15); formPanel.add(txtSpesialis);

        topPanel.add(formPanel, java.awt.BorderLayout.CENTER);

        filterPanel.setLayout(new java.awt.BorderLayout());
        lblCari.setText(" Cari: ");
        filterPanel.add(lblCari, java.awt.BorderLayout.WEST);
        filterPanel.add(txtSearch, java.awt.BorderLayout.CENTER);
        topPanel.add(filterPanel, java.awt.BorderLayout.SOUTH);

        add(topPanel, java.awt.BorderLayout.NORTH);

        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(table);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btnBaru.setText("Baru");
        btnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnBaruActionPerformed(evt); }
        });
        buttonPanel.add(btnBaru);

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnSimpanActionPerformed(evt); }
        });
        buttonPanel.add(btnSimpan);

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnHapusActionPerformed(evt); }
        });
        buttonPanel.add(btnHapus);

        javax.swing.JButton btnRefresh = new javax.swing.JButton();
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { loadData(); }
        });
        buttonPanel.add(btnRefresh);
        
        javax.swing.JButton btnPrint = new javax.swing.JButton();
        btnPrint.setText("Export");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Object[] options = {"PDF", "Excel", "Batal"};
                int choice = javax.swing.JOptionPane.showOptionDialog(MekanikPanel.this, "Pilih format export:", "Export Data", javax.swing.JOptionPane.YES_NO_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice == 0) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToPDF(table, "Data_Mekanik");
                else if (choice == 1) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToExcel(table, "Data_Mekanik");
            }
        });
        buttonPanel.add(btnPrint);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruActionPerformed
        clearForm();
    }//GEN-LAST:event_btnBaruActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        saveMekanik();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        deleteMekanik();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void loadData() {
        try {
            List<Mekanik> list = mekanikDAO.findAll();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Nama", "Telepon", "Spesialis"}, 0);
            for (Mekanik m : list) {
                model.addRow(new Object[]{m.getMekanikId(), m.getNama(), m.getTelepon(), m.getSpesialis()});
            }
            table.setModel(model);
            table.setRowSorter(new javax.swing.table.TableRowSorter<>(model));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load data: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtNama.setText("");
        txtTelepon.setText("");
        txtSpesialis.setText("");
        table.clearSelection();
        txtNama.requestFocusInWindow();
    }

    private void saveMekanik() {
        try {
            Mekanik m = new Mekanik();
            if (!txtId.getText().isEmpty()) {
                m.setMekanikId(Integer.parseInt(txtId.getText()));
            }
            m.setNama(txtNama.getText());
            m.setTelepon(txtTelepon.getText());
            m.setSpesialis(txtSpesialis.getText());

            if (m.getMekanikId() == 0) {
                mekanikDAO.insert(m);
            } else {
                mekanikDAO.update(m);
            }
            loadData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error simpan: " + ex.getMessage());
        }
    }

    private void deleteMekanik() {
        if (txtId.getText().isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus mekanik ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                mekanikDAO.delete(Integer.parseInt(txtId.getText()));
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
            txtNama.setText(table.getValueAt(row, 1).toString());
            txtTelepon.setText(table.getValueAt(row, 2).toString());
            txtSpesialis.setText(table.getValueAt(row, 3).toString());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JPanel formPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblSpesialis;
    private javax.swing.JLabel lblTelepon;
    private javax.swing.JTable table;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSpesialis;
    private javax.swing.JTextField txtTelepon;
    // End of variables declaration//GEN-END:variables
}
