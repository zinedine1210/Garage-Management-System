package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceHistoryItem;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ServiceHistoryFrame extends JDialog {

    private final JTextField txtNoPolisi;
    private final JTable table;
    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();

    public ServiceHistoryFrame(Frame owner) {
        super(owner, "Pencarian Riwayat Servis Digital", true);
        setSize(850, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        txtNoPolisi = new JTextField(15);
        JButton btnCari = new JButton("Cari Riwayat");
        btnCari.addActionListener(e -> cariRiwayat());

        JPanel searchPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        searchPanel.add(new JLabel(" Pencarian berdasarkan No Polisi:", JLabel.RIGHT));
        searchPanel.add(txtNoPolisi);
        searchPanel.add(btnCari);
        searchPanel.add(new JLabel()); // spacer

        table = new JTable();
        table.setFillsViewportHeight(true);

        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void cariRiwayat() {
        String nopol = txtNoPolisi.getText().trim();
        if (nopol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan No Polisi terlebih dahulu.");
            return;
        }

        try {
            List<ServiceHistoryItem> history = transDAO.findHistoryByNoPolisi(nopol);
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tidak ada riwayat servis untuk No Polisi tersebut.");
                table.setModel(new DefaultTableModel()); // clear table
                return;
            }

            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Tanggal", "Mekanik", "Keluhan/Pekerjaan", "Sparepart Diganti", "Total Biaya"}, 0);
            
            for (ServiceHistoryItem item : history) {
                model.addRow(new Object[]{
                    item.getTanggal(),
                    item.getMekanik(),
                    item.getKeluhan(),
                    item.getSpareparts(),
                    String.format("Rp %,.0f", item.getTotalBiaya())
                });
            }
            table.setModel(model);
            table.getColumnModel().getColumn(3).setPreferredWidth(250); // lebar untuk daftar sparepart

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error pencarian data: " + ex.getMessage());
        }
    }
}
