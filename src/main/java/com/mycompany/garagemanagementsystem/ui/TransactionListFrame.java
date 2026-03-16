package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class TransactionListFrame extends JDialog {

    private final JTable table;
    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private final Frame owner;

    public TransactionListFrame(Frame owner) {
        super(owner, "Daftar Transaksi Servis", true);
        this.owner = owner;
        setSize(800, 400);
        setLocationRelativeTo(owner);

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
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(new JLabel(" Cari: "), BorderLayout.WEST);
        javax.swing.JTextField txtSearch = new javax.swing.JTextField();
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
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID Transaksi", "Tanggal", "Keluhan", "Status", "Grand Total"}, 0);
            for (ServiceTransaction t : list) {
                model.addRow(new Object[]{
                    t.getTransId(),
                    t.getTanggal().toString(),
                    t.getKeluhan(),
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
