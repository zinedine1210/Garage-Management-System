package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ClientDAO;
import com.mycompany.garagemanagementsystem.dao.VehicleDAO;
import com.mycompany.garagemanagementsystem.model.Client;
import com.mycompany.garagemanagementsystem.model.Vehicle;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class VehiclePanel extends JPanel {

    private final JTextField txtId;
    private final JComboBox<Client> cbClient;
    private final JTextField txtNoPolisi;
    private final JTextField txtMerk;
    private final JTextField txtTipe;
    private final JTextField txtCc;
    private final JComboBox<String> cbTipeKendaraan;
    private final JTextField txtTahun;
    private final JTextField txtNoRangka;
    private final JTextField txtNoMesin;
    private final JTable table;
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final ClientDAO clientDAO = new ClientDAO();

    public VehiclePanel() {

        txtId = new JTextField(5);
        txtId.setEnabled(false);
        cbClient = new JComboBox<>();
        txtNoPolisi = new JTextField(10);
        txtMerk = new JTextField(10);
        txtTipe = new JTextField(10);
        txtCc = new JTextField(5);
        cbTipeKendaraan = new JComboBox<>(new String[]{"Roda 2", "Lebih dari Roda 2"});
        txtTahun = new JTextField(4);
        txtNoRangka = new JTextField(15);
        txtNoMesin = new JTextField(15);

        JButton btnBaru = new JButton("Baru");
        JButton btnSimpan = new JButton("Simpan");
        JButton btnHapus = new JButton("Hapus");

        btnBaru.addActionListener(e -> clearForm());
        btnSimpan.addActionListener(e -> saveVehicle());
        btnHapus.addActionListener(e -> deleteVehicle());

        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> tableSelectionChanged());

        JPanel formPanel = new JPanel(new GridLayout(10, 2));
        formPanel.add(new JLabel("ID:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Client:"));
        formPanel.add(cbClient);
        formPanel.add(new JLabel("No Polisi:"));
        formPanel.add(txtNoPolisi);
        formPanel.add(new JLabel("Merk:"));
        formPanel.add(txtMerk);
        formPanel.add(new JLabel("Tipe:"));
        formPanel.add(txtTipe);
        formPanel.add(new JLabel("CC:"));
        formPanel.add(txtCc);
        formPanel.add(new JLabel("Jenis:"));
        formPanel.add(cbTipeKendaraan);
        formPanel.add(new JLabel("Tahun:"));
        formPanel.add(txtTahun);
        formPanel.add(new JLabel("No Rangka (Opsional):"));
        formPanel.add(txtNoRangka);
        formPanel.add(new JLabel("No Mesin (Opsional):"));
        formPanel.add(txtNoMesin);

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

        loadClients();
        loadData();
    }

    private void loadClients() {
        try {
            cbClient.removeAllItems();
            List<Client> clients = clientDAO.findAll();
            for (Client c : clients) {
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
                    new Object[]{"ID", "Client ID", "No Polisi", "Merk", "Tipe", "CC", "Jenis", "Tahun", "No Rangka", "No Mesin"}, 0);
            for (Vehicle v : list) {
                model.addRow(new Object[]{
                    v.getVehicleId(),
                    v.getClientId(),
                    v.getNoPolisi(),
                    v.getMerk(),
                    v.getTipe(),
                    v.getCc(),
                    v.getTipeKendaraan(),
                    v.getTahun(),
                    v.getNoRangka(),
                    v.getNoMesin()
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
        if (cbClient.getItemCount() > 0) {
            cbClient.setSelectedIndex(0);
        }
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
            if (selectedClient != null) {
                v.setClientId(selectedClient.getClientId());
            } else {
                v.setClientId(0);
            }
            v.setNoPolisi(txtNoPolisi.getText());
            v.setMerk(txtMerk.getText());
            v.setTipe(txtTipe.getText());
            
            try {
                v.setCc(Integer.parseInt(txtCc.getText()));
            } catch(NumberFormatException e) {
                v.setCc(0);
            }
            
            v.setTipeKendaraan(cbTipeKendaraan.getSelectedItem().toString());
            v.setTahun(Integer.parseInt(txtTahun.getText()));
            v.setNoRangka(txtNoRangka.getText());
            v.setNoMesin(txtNoMesin.getText());

            if (v.getVehicleId() == 0) {
                vehicleDAO.insert(v);
            } else {
                vehicleDAO.update(v);
            }
            loadData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error simpan: " + ex.getMessage());
        }
    }

    private void deleteVehicle() {
        if (txtId.getText().isEmpty()) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus vehicle ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
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

    private void tableSelectionChanged() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(table.getValueAt(row, 0).toString());
            int clientId = Integer.parseInt(table.getValueAt(row, 1).toString());
            for (int i = 0; i < cbClient.getItemCount(); i++) {
                if (cbClient.getItemAt(i).getClientId() == clientId) {
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
            if(jenisObj != null) cbTipeKendaraan.setSelectedItem(jenisObj.toString());
            
            Object tahunObj = table.getValueAt(row, 7);
            txtTahun.setText(tahunObj != null ? tahunObj.toString() : "");
            
            Object noRangkaObj = table.getValueAt(row, 8);
            txtNoRangka.setText(noRangkaObj != null ? noRangkaObj.toString() : "");
            
            Object noMesinObj = table.getValueAt(row, 9);
            txtNoMesin.setText(noMesinObj != null ? noMesinObj.toString() : "");
        }
    }
}


