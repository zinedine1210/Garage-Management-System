package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class TransactionListPanel extends JPanel {

    private final JTable table;
    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private final Frame owner;

    public TransactionListPanel(Frame owner) {
        this.owner = owner;

        JButton btnBaru = new JButton("Transaksi Baru");
        JButton btnEdit = new JButton("Edit Transaksi");
        JButton btnHapus = new JButton("Hapus Transaksi");
        JButton btnRefresh = new JButton("Refresh");

        btnBaru.addActionListener(e -> newTransaction());
        btnEdit.addActionListener(e -> editTransaction());
        btnHapus.addActionListener(e -> deleteTransaction());
        btnRefresh.addActionListener(e -> loadData());

        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnBaru);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnRefresh);

        // --- FILTER PANEL ---
        JPanel filterPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        
        filterPanel.add(new JLabel("Cari:"));
        javax.swing.JTextField txtSearch = new javax.swing.JTextField(15);
        filterPanel.add(txtSearch);
        
        filterPanel.add(new JLabel("Status:"));
        String[] statuses = {"Semua", "Menunggu", "Dikerjakan", "Selesai Lunas", "Batal"};
        javax.swing.JComboBox<String> cbStatus = new javax.swing.JComboBox<>(statuses);
        filterPanel.add(cbStatus);
        
        filterPanel.add(new JLabel("Bulan:"));
        String[] bulans = {"Semua", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        javax.swing.JComboBox<String> cbBulan = new javax.swing.JComboBox<>(bulans);
        filterPanel.add(cbBulan);
        
        Runnable applyFilter = () -> {
            if (table.getRowSorter() == null) {
                javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>((DefaultTableModel) table.getModel());
                table.setRowSorter(sorter);
            }
            javax.swing.table.TableRowSorter<DefaultTableModel> sorter = (javax.swing.table.TableRowSorter<DefaultTableModel>) table.getRowSorter();
            
            java.util.List<javax.swing.RowFilter<Object,Object>> filters = new java.util.ArrayList<>();
            
            // Text Filter (search across all columns)
            String text = txtSearch.getText().trim();
            if (text.length() > 0) {
                filters.add(javax.swing.RowFilter.regexFilter("(?i)" + text));
            }
            
            // Status Filter (Column index 8 for Status)
            String status = cbStatus.getSelectedItem().toString();
            if (!status.equals("Semua")) {
                filters.add(javax.swing.RowFilter.regexFilter("(?i)^" + status + "$", 8));
            }
            
            // Month Filter (Column index 1 for Tanggal, format YYYY-MM-DD...)
            String bulan = cbBulan.getSelectedItem().toString();
            if (!bulan.equals("Semua")) {
                filters.add(javax.swing.RowFilter.regexFilter("-[0]*" + bulan + "-", 1));
            }
            
            if (filters.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(javax.swing.RowFilter.andFilter(filters));
            }
        };

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter.run(); }
        });
        cbStatus.addActionListener(e -> applyFilter.run());
        cbBulan.addActionListener(e -> applyFilter.run());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        try {
            List<ServiceTransaction> list = transDAO.findAll();
            // Columns: ID (0), Tanggal (1), Pelanggan (2), No Polisi (3), Keluhan (4), Mekanik (5), Total Jasa (6), Total Sparepart (7), Status (8), Grand Total (9)
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID Transaksi", "Tanggal", "Pelanggan", "No. Polisi", "Keluhan", "Mekanik", "Total Jasa", "Total Sparepart", "Status", "Grand Total"}, 0);
            for (ServiceTransaction t : list) {
                model.addRow(new Object[]{
                    t.getTransId(),
                    t.getTanggal().toString(),
                    t.getClientNama(),
                    t.getNoPolisi(),
                    t.getKeluhan(),
                    t.getMekanikNama(),
                    t.getTotalJasa(),
                    t.getTotalSparepart(),
                    t.getStatusServis(),
                    t.getGrandTotal()
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

    private void newTransaction() {
        new ServiceTransactionFrame(owner).setVisible(true);
        loadData(); // refresh setelah close form input
    }

    private void editTransaction() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan diedit.");
            return;
        }
        int transId = (int) table.getValueAt(row, 0);
        new ServiceTransactionFrame(owner, transId).setVisible(true);
        loadData();
    }

    private void deleteTransaction() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan dihapus.");
            return;
        }
        
        int transId = (int) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus transaksi ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                transDAO.delete(transId);
                loadData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error hapus: " + ex.getMessage());
            }
        }
    }
}
