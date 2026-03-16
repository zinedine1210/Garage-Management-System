package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ClientDAO;
import com.mycompany.garagemanagementsystem.dao.MekanikDAO;
import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.dao.SparepartDAO;
import com.mycompany.garagemanagementsystem.dao.VehicleDAO;
import com.mycompany.garagemanagementsystem.model.Client;
import com.mycompany.garagemanagementsystem.model.Mekanik;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import com.mycompany.garagemanagementsystem.model.Sparepart;
import com.mycompany.garagemanagementsystem.model.TransactionDetail;
import com.mycompany.garagemanagementsystem.model.Vehicle;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ServiceTransactionFrame extends JDialog {

    private final JComboBox<Client> cbClient;
    private final JComboBox<Vehicle> cbVehicle;
    private final JComboBox<Mekanik> cbMekanik;
    private final JComboBox<String> cbStatusServis;
    private final JTextArea txtKeluhan;
    private final JTextField txtTotalJasa;
    private final JTextField txtTotalSparepart;
    private final JTextField txtGrandTotal;
    private final JTextField txtBayar;
    private final JTextField txtKembali;
    private final JTable tblDetail;
    private final DefaultTableModel detailModel;
    private int currentTransId = 0;

    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MekanikDAO mekanikDAO = new MekanikDAO();
    private final SparepartDAO sparepartDAO = new SparepartDAO();

    public ServiceTransactionFrame(Frame owner) {
        super(owner, "Transaksi Servis", true);
        setSize(900, 500);
        setLocationRelativeTo(owner);

        cbClient = new JComboBox<>();
        cbVehicle = new JComboBox<>();
        cbMekanik = new JComboBox<>();
        txtKeluhan = new JTextArea(3, 30);

        cbStatusServis = new JComboBox<>(new String[]{"Menunggu", "Dikerjakan", "Selesai Lunas"});

        txtTotalJasa = new JTextField("0", 10);
        txtTotalSparepart = new JTextField("0", 10);
        txtGrandTotal = new JTextField("0", 10);
        txtBayar = new JTextField("0", 10);
        txtKembali = new JTextField("0", 10);

        txtTotalSparepart.setEditable(false);
        txtGrandTotal.setEditable(false);
        txtKembali.setEditable(false);

        detailModel = new DefaultTableModel(
                new Object[]{"Sparepart ID", "Nama Sparepart", "Qty", "Harga", "Subtotal"}, 0);
        tblDetail = new JTable(detailModel);

        JButton btnTambahDetail = new JButton("Tambah Sparepart");
        JButton btnHapusDetail = new JButton("Hapus Detail");
        JButton btnHitungTotal = new JButton("Hitung Total");
        JButton btnSimpanTrans = new JButton("Simpan Transaksi");
        JButton btnBayar = new JButton("Bayar");
        
        JButton btnAddClient = new JButton("+ New Client");
        btnAddClient.addActionListener(e -> {
            new ClientFrame((Frame) this.getParent()).setVisible(true);
            loadComboBoxData(); // reload setelah form client ditutup
        });

        btnTambahDetail.addActionListener(e -> addDetailRow());
        btnHapusDetail.addActionListener(e -> removeDetailRow());
        btnHitungTotal.addActionListener(e -> hitungTotal());
        btnSimpanTrans.addActionListener(e -> simpanTransaksi());
        btnBayar.addActionListener(e -> prosesBayar());

        cbClient.addActionListener(e -> filterVehicleByClient());
        cbVehicle.addActionListener(e -> autofillClientByVehicle());

        JPanel headerPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        headerPanel.add(new JLabel("Client:"));
        headerPanel.add(cbClient);
        headerPanel.add(btnAddClient);
        
        headerPanel.add(new JLabel("Vehicle:"));
        headerPanel.add(cbVehicle);
        headerPanel.add(new JLabel("")); // spacer
        
        headerPanel.add(new JLabel("Mekanik:"));
        headerPanel.add(cbMekanik);
        headerPanel.add(new JLabel("")); // spacer
        
        headerPanel.add(new JLabel("Keluhan:"));
        headerPanel.add(new JScrollPane(txtKeluhan));
        headerPanel.add(new JLabel("")); // spacer
        
        headerPanel.add(new JLabel("Status Antrian:"));
        headerPanel.add(cbStatusServis);
        headerPanel.add(new JLabel("")); // spacer

        JPanel totalPanel = new JPanel(new GridLayout(5, 2));
        totalPanel.add(new JLabel("Total Jasa:"));
        totalPanel.add(txtTotalJasa);
        totalPanel.add(new JLabel("Total Sparepart:"));
        totalPanel.add(txtTotalSparepart);
        totalPanel.add(new JLabel("Grand Total:"));
        totalPanel.add(txtGrandTotal);
        totalPanel.add(new JLabel("Bayar:"));
        totalPanel.add(txtBayar);
        totalPanel.add(new JLabel("Kembali:"));
        totalPanel.add(txtKembali);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnTambahDetail);
        buttonPanel.add(btnHapusDetail);
        buttonPanel.add(btnHitungTotal);
        buttonPanel.add(btnSimpanTrans);
        buttonPanel.add(btnBayar);

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(tblDetail), BorderLayout.CENTER);
        add(totalPanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);

        loadComboBoxData();
    }

    private void loadComboBoxData() {
        try {
            cbClient.removeAllItems();
            for (Client c : clientDAO.findAll()) {
                cbClient.addItem(c);
            }

            cbVehicle.removeAllItems();
            for (Vehicle v : vehicleDAO.findAll()) {
                cbVehicle.addItem(v);
            }

            cbMekanik.removeAllItems();
            for (Mekanik m : mekanikDAO.findAll()) {
                cbMekanik.addItem(m);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error load combobox data: " + ex.getMessage());
        }
    }

    private boolean isFiltering = false;

    private void filterVehicleByClient() {
        if (isFiltering) return;
        Client selectedClient = (Client) cbClient.getSelectedItem();
        if (selectedClient == null) return;
        
        isFiltering = true;
        try {
            cbVehicle.removeAllItems();
            for (Vehicle v : vehicleDAO.findAll()) {
                if (v.getClientId() == selectedClient.getClientId()) {
                    cbVehicle.addItem(v);
                }
            }
        } catch (SQLException ex) {
        }
        isFiltering = false;
    }

    private void autofillClientByVehicle() {
        if (isFiltering) return;
        Vehicle selectedVehicle = (Vehicle) cbVehicle.getSelectedItem();
        if (selectedVehicle == null) return;
        
        isFiltering = true;
        for (int i = 0; i < cbClient.getItemCount(); i++) {
            if (cbClient.getItemAt(i).getClientId() == selectedVehicle.getClientId()) {
                cbClient.setSelectedIndex(i);
                break;
            }
        }
        isFiltering = false;
    }

    private void addDetailRow() {
        try {
            List<Sparepart> spareparts = sparepartDAO.findAll();
            JComboBox<Sparepart> cbSparepart = new JComboBox<>();
            for (Sparepart s : spareparts) {
                cbSparepart.addItem(s);
            }

            JTextField txtQty = new JTextField(5);
            txtQty.setText("1");

            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("Pilih Sparepart:"));
            panel.add(cbSparepart);
            panel.add(new JLabel("Qty:"));
            panel.add(txtQty);

            int result = JOptionPane.showConfirmDialog(this, panel, "Tambah Sparepart",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                Sparepart selected = (Sparepart) cbSparepart.getSelectedItem();
                int qty = Integer.parseInt(txtQty.getText());
                if (selected != null && qty > 0) {
                    double harga = selected.getHargaJual();
                    double subtotal = qty * harga;
                    detailModel.addRow(new Object[]{selected.getSparepartId(), selected.getNamaSparepart(), qty, harga, subtotal});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void removeDetailRow() {
        int row = tblDetail.getSelectedRow();
        if (row >= 0) {
            detailModel.removeRow(row);
        }
    }

    private void hitungTotal() {
        double totalSparepart = 0;
        for (int i = 0; i < detailModel.getRowCount(); i++) {
            totalSparepart += Double.parseDouble(detailModel.getValueAt(i, 4).toString());
        }
        txtTotalSparepart.setText(String.valueOf(totalSparepart));

        double totalJasa = Double.parseDouble(txtTotalJasa.getText());
        double grandTotal = totalJasa + totalSparepart;
        txtGrandTotal.setText(String.valueOf(grandTotal));
    }

    private void simpanTransaksi() {
        try {
            hitungTotal();

            ServiceTransaction t = new ServiceTransaction();
            t.setTanggal(new Date());

            Client client = (Client) cbClient.getSelectedItem();
            t.setClientId(client != null ? client.getClientId() : 0);

            Vehicle vehicle = (Vehicle) cbVehicle.getSelectedItem();
            t.setVehicleId(vehicle != null ? vehicle.getVehicleId() : 0);

            Mekanik mekanik = (Mekanik) cbMekanik.getSelectedItem();
            t.setMekanikId(mekanik != null ? mekanik.getMekanikId() : 0);

            t.setKeluhan(txtKeluhan.getText());
            t.setStatusServis(cbStatusServis.getSelectedItem().toString());
            t.setTotalJasa(Double.parseDouble(txtTotalJasa.getText()));
            t.setTotalSparepart(Double.parseDouble(txtTotalSparepart.getText()));
            t.setGrandTotal(Double.parseDouble(txtGrandTotal.getText()));
            t.setBayar(0);
            t.setKembali(0);
            t.setUserKasir("kasir1");

            List<TransactionDetail> details = new ArrayList<>();
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                TransactionDetail d = new TransactionDetail();
                d.setSparepartId(Integer.parseInt(detailModel.getValueAt(i, 0).toString()));
                d.setQty(Integer.parseInt(detailModel.getValueAt(i, 2).toString()));
                d.setHarga(Double.parseDouble(detailModel.getValueAt(i, 3).toString()));
                d.setSubtotal(Double.parseDouble(detailModel.getValueAt(i, 4).toString()));
                details.add(d);
            }
            t.setDetails(details);

            currentTransId = transDAO.insertWithDetails(t);
            JOptionPane.showMessageDialog(this, "Transaksi tersimpan. ID: " + currentTransId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error simpan transaksi: " + ex.getMessage());
        }
    }

    private void prosesBayar() {
        if (currentTransId == 0) {
            JOptionPane.showMessageDialog(this, "Simpan transaksi dulu.");
            return;
        }
        try {
            double grandTotal = Double.parseDouble(txtGrandTotal.getText());
            double bayar = Double.parseDouble(txtBayar.getText());
            double kembali = bayar - grandTotal;
            if (kembali < 0) {
                JOptionPane.showMessageDialog(this, "Bayar kurang.");
                return;
            }
            txtKembali.setText(String.valueOf(kembali));
            transDAO.updateStatusPembayaran(currentTransId, bayar, kembali);
            JOptionPane.showMessageDialog(this, "Pembayaran berhasil, status Lunas.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error pembayaran: " + ex.getMessage());
        }
    }
}

