package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ClientDAO;
import com.mycompany.garagemanagementsystem.dao.VehicleDAO;
import com.mycompany.garagemanagementsystem.model.Client;
import com.mycompany.garagemanagementsystem.model.Vehicle;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class VehiclePanel extends javax.swing.JPanel {

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final ClientDAO clientDAO = new ClientDAO();

    public VehiclePanel() {
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
        loadClients();
        loadData();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        formPanel = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblClient = new javax.swing.JLabel();
        cbClient = new javax.swing.JComboBox();
        lblNoPolisi = new javax.swing.JLabel();
        txtNoPolisi = new javax.swing.JTextField();
        lblMerk = new javax.swing.JLabel();
        txtMerk = new javax.swing.JTextField();
        lblTipe = new javax.swing.JLabel();
        txtTipe = new javax.swing.JTextField();
        lblCc = new javax.swing.JLabel();
        txtCc = new javax.swing.JTextField();
        lblJenis = new javax.swing.JLabel();
        cbTipeKendaraan = new javax.swing.JComboBox();
        lblTahun = new javax.swing.JLabel();
        txtTahun = new javax.swing.JTextField();
        lblNoRangka = new javax.swing.JLabel();
        txtNoRangka = new javax.swing.JTextField();
        lblNoMesin = new javax.swing.JLabel();
        txtNoMesin = new javax.swing.JTextField();
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
        formPanel.setLayout(new java.awt.GridLayout(10, 2));

        lblId.setText("ID:"); formPanel.add(lblId);
        txtId.setColumns(5); txtId.setEnabled(false); formPanel.add(txtId);
        lblClient.setText("Client:"); formPanel.add(lblClient);
        formPanel.add(cbClient);
        lblNoPolisi.setText("No Polisi:"); formPanel.add(lblNoPolisi);
        txtNoPolisi.setColumns(10); formPanel.add(txtNoPolisi);
        lblMerk.setText("Merk:"); formPanel.add(lblMerk);
        txtMerk.setColumns(10); formPanel.add(txtMerk);
        lblTipe.setText("Tipe:"); formPanel.add(lblTipe);
        txtTipe.setColumns(10); formPanel.add(txtTipe);
        lblCc.setText("CC:"); formPanel.add(lblCc);
        txtCc.setColumns(5); formPanel.add(txtCc);
        lblJenis.setText("Jenis:"); formPanel.add(lblJenis);
        cbTipeKendaraan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Roda 2", "Lebih dari Roda 2" }));
        formPanel.add(cbTipeKendaraan);
        lblTahun.setText("Tahun:"); formPanel.add(lblTahun);
        txtTahun.setColumns(4); formPanel.add(txtTahun);
        lblNoRangka.setText("No Rangka (Opsional):"); formPanel.add(lblNoRangka);
        txtNoRangka.setColumns(15); formPanel.add(txtNoRangka);
        lblNoMesin.setText("No Mesin (Opsional):"); formPanel.add(lblNoMesin);
        txtNoMesin.setColumns(15); formPanel.add(txtNoMesin);

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
        add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruActionPerformed
        clearForm();
    }//GEN-LAST:event_btnBaruActionPerformed
    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        saveVehicle();
    }//GEN-LAST:event_btnSimpanActionPerformed
    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        deleteVehicle();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void loadClients() {
        try {
            cbClient.removeAllItems();
            for (Client c : clientDAO.findAll()) {
                cbClient.addItem(c);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load clients: " + ex.getMessage());
        }
    }

    private void loadData() {
        try {
            List<Vehicle> list = vehicleDAO.findAll();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Client ID", "No Polisi", "Merk", "Tipe", "CC",
                        "Jenis", "Tahun", "No Rangka", "No Mesin"}, 0);
            for (Vehicle v : list) {
                model.addRow(new Object[]{
                    v.getVehicleId(), v.getClientId(), v.getNoPolisi(), v.getMerk(),
                    v.getTipe(), v.getCc(), v.getTipeKendaraan(), v.getTahun(),
                    v.getNoRangka(), v.getNoMesin()
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
        if (cbClient.getItemCount() > 0) cbClient.setSelectedIndex(0);
        txtNoPolisi.setText("");
        txtMerk.setText("");
        txtTipe.setText("");
        txtCc.setText("0");
        cbTipeKendaraan.setSelectedIndex(0);
        txtTahun.setText("");
        txtNoRangka.setText("");
        txtNoMesin.setText("");
    }

    private void saveVehicle() {
        try {
            Vehicle v = new Vehicle();
            if (!txtId.getText().isEmpty()) {
                v.setVehicleId(Integer.parseInt(txtId.getText()));
            }

            Client selectedClient = (Client) cbClient.getSelectedItem();
            v.setClientId(selectedClient != null ? selectedClient.getClientId() : 0);

            v.setNoPolisi(txtNoPolisi.getText());
            v.setMerk(txtMerk.getText());
            v.setTipe(txtTipe.getText());
            try { v.setCc(Integer.parseInt(txtCc.getText())); }
            catch (NumberFormatException e) { v.setCc(0); }
            v.setTipeKendaraan(cbTipeKendaraan.getSelectedItem().toString());
            v.setTahun(Integer.parseInt(txtTahun.getText()));
            v.setNoRangka(txtNoRangka.getText());
            v.setNoMesin(txtNoMesin.getText());

            if (v.getVehicleId() == 0) vehicleDAO.insert(v);
            else vehicleDAO.update(v);
            loadData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error simpan: " + ex.getMessage());
        }
    }

    private void deleteVehicle() {
        if (txtId.getText().isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus vehicle ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                vehicleDAO.delete(Integer.parseInt(txtId.getText()));
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

            int clientId = Integer.parseInt(table.getValueAt(row, 1).toString());
            for (int i = 0; i < cbClient.getItemCount(); i++) {
                if (((Client) cbClient.getItemAt(i)).getClientId() == clientId) {
                    cbClient.setSelectedIndex(i);
                    break;
                }
            }

            txtNoPolisi.setText(table.getValueAt(row, 2).toString());
            txtMerk.setText(table.getValueAt(row, 3).toString());
            txtTipe.setText(table.getValueAt(row, 4).toString());
            Object ccObj = table.getValueAt(row, 5);
            txtCc.setText(ccObj != null ? ccObj.toString() : "0");
            Object jenisObj = table.getValueAt(row, 6);
            if (jenisObj != null) cbTipeKendaraan.setSelectedItem(jenisObj.toString());
            Object tahunObj = table.getValueAt(row, 7);
            txtTahun.setText(tahunObj != null ? tahunObj.toString() : "");
            Object noRangkaObj = table.getValueAt(row, 8);
            txtNoRangka.setText(noRangkaObj != null ? noRangkaObj.toString() : "");
            Object noMesinObj = table.getValueAt(row, 9);
            txtNoMesin.setText(noMesinObj != null ? noMesinObj.toString() : "");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JComboBox cbClient;
    private javax.swing.JComboBox cbTipeKendaraan;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JPanel formPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblCc;
    private javax.swing.JLabel lblClient;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblJenis;
    private javax.swing.JLabel lblMerk;
    private javax.swing.JLabel lblNoMesin;
    private javax.swing.JLabel lblNoPolisi;
    private javax.swing.JLabel lblNoRangka;
    private javax.swing.JLabel lblTahun;
    private javax.swing.JLabel lblTipe;
    private javax.swing.JTable table;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField txtCc;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtMerk;
    private javax.swing.JTextField txtNoMesin;
    private javax.swing.JTextField txtNoPolisi;
    private javax.swing.JTextField txtNoRangka;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTahun;
    private javax.swing.JTextField txtTipe;
    // End of variables declaration//GEN-END:variables
}
