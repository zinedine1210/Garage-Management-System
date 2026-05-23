package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceHistoryItem;
import com.mycompany.garagemanagementsystem.util.StyledTable;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ServiceHistoryPanel extends javax.swing.JPanel {

    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private StyledTable styledTable;
    private JTextField txtNoPolisi;

    public ServiceHistoryPanel() {
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        // ===== TOP =====
        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setOpaque(false);

        topPanel.add(UIHelper.createPageHeader("Riwayat Servis Kendaraan",
                "Cari dan lihat riwayat servis kendaraan berdasarkan nomor polisi"), BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(6, 12, 6, 12)));

        searchPanel.add(new JLabel("🔍 No Polisi:"));
        txtNoPolisi = new JTextField(18);
        txtNoPolisi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNoPolisi.addActionListener(e -> cariRiwayat());
        searchPanel.add(txtNoPolisi);

        JButton btnCari = UIHelper.createStyledButton("Cari Riwayat", new Color(59, 130, 246));
        btnCari.addActionListener(e -> cariRiwayat());
        searchPanel.add(btnCari);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ===== CENTER =====
        styledTable = new StyledTable();
        add(styledTable, BorderLayout.CENTER);
    }

    private void cariRiwayat() {
        String nopol = txtNoPolisi.getText().trim();
        if (nopol.isEmpty()) {
            UIHelper.warn(this, "Masukkan No Polisi terlebih dahulu.");
            return;
        }

        try {
            List<ServiceHistoryItem> history = transDAO.findHistoryByNoPolisi(nopol);
            if (history.isEmpty()) {
                UIHelper.info(this, "Tidak ada riwayat servis untuk No Polisi tersebut.");
                styledTable.setData(new String[]{"Tanggal", "Mekanik", "Keluhan/Pekerjaan", "Jasa/Layanan", "Sparepart Diganti", "Total Biaya"}, new ArrayList<>());
                return;
            }
            List<Object[]> data = new ArrayList<>();
            for (ServiceHistoryItem item : history) {
                data.add(new Object[]{
                        item.getTanggal(), item.getMekanik(), item.getKeluhan(),
                        item.getJasaList(), item.getSpareparts(),
                        String.format("Rp %,.0f", item.getTotalBiaya())
                });
            }
            styledTable.setData(new String[]{"Tanggal", "Mekanik", "Keluhan/Pekerjaan", "Jasa/Layanan", "Sparepart Diganti", "Total Biaya"}, data);
        } catch (SQLException ex) {
            UIHelper.error(this, "Error pencarian data: " + ex.getMessage());
        }
    }
}
