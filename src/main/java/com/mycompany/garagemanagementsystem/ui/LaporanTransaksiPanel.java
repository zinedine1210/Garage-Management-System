package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.DashboardDAO;
import com.mycompany.garagemanagementsystem.util.ExportUtils;
import com.mycompany.garagemanagementsystem.util.StyledTable;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LaporanTransaksiPanel extends javax.swing.JPanel {

    private final DashboardDAO dao = new DashboardDAO();
    private StyledTable styledTable;
    private JLabel lblDateFrom, lblDateTo, lblSummary;
    private JTextField txtSearch;
    private JComboBox<String> cbStatus;
    private Date filterDateFrom, filterDateTo;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public LaporanTransaksiPanel() {
        buildUI();
        setDefaultDateRange();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        // ===== TOP =====
        JPanel topPanel = new JPanel(new BorderLayout(0, 4));
        topPanel.setOpaque(false);

        topPanel.add(UIHelper.createPageHeader("Laporan Transaksi Servis",
                "Rekapitulasi seluruh transaksi servis kendaraan berdasarkan periode tertentu"), BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(6, 12, 6, 12)));

        filterPanel.add(new JLabel("Dari:"));
        lblDateFrom = new JLabel("---");
        filterPanel.add(lblDateFrom);
        JButton btnPickFrom = new JButton("Pilih");
        btnPickFrom.addActionListener(e -> pickDate(true));
        filterPanel.add(btnPickFrom);

        filterPanel.add(new JLabel("Sampai:"));
        lblDateTo = new JLabel("---");
        filterPanel.add(lblDateTo);
        JButton btnPickTo = new JButton("Pilih");
        btnPickTo.addActionListener(e -> pickDate(false));
        filterPanel.add(btnPickTo);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> { setDefaultDateRange(); loadData(); });
        filterPanel.add(btnReset);

        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("🔍 Cari:"));
        txtSearch = new JTextField(12);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        filterPanel.add(txtSearch);

        filterPanel.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"Semua", "Menunggu", "Dikerjakan", "Selesai Lunas", "Batal"});
        cbStatus.addActionListener(e -> applyFilter());
        filterPanel.add(cbStatus);

        JPanel midWrap = new JPanel(new BorderLayout(0, 4));
        midWrap.setOpaque(false);
        midWrap.add(filterPanel, BorderLayout.CENTER);

        lblSummary = new JLabel(" ");
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummary.setBorder(new EmptyBorder(4, 10, 4, 10));
        midWrap.add(lblSummary, BorderLayout.SOUTH);

        topPanel.add(midWrap, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ===== CENTER =====
        styledTable = new StyledTable();
        add(styledTable, BorderLayout.CENTER);

        // ===== BOTTOM =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        buttonPanel.setBackground(new Color(243, 245, 249));

        JButton btnRefresh = UIHelper.createStyledButton("Refresh", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> loadData());
        buttonPanel.add(btnRefresh);

        JButton btnExport = UIHelper.createStyledButton("Export", new Color(23, 162, 184));
        btnExport.addActionListener(e -> {
            String[] options = {"PDF", "Excel", "Batal"};
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Laporan Transaksi", options);
            int[] totCols = {7, 8, 9};
            if (choice == 0) ExportUtils.exportTableToPDFWithTotals(styledTable.getTable(), "Laporan_Transaksi_Servis", totCols);
            else if (choice == 1) ExportUtils.exportTableToExcelWithTotals(styledTable.getTable(), "Laporan_Transaksi_Servis", totCols);
        });
        buttonPanel.add(btnExport);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setDefaultDateRange() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
        filterDateFrom = cal.getTime();
        cal.add(Calendar.MONTH, 1); cal.add(Calendar.DAY_OF_MONTH, -1);
        filterDateTo = cal.getTime();
        lblDateFrom.setText(sdf.format(filterDateFrom));
        lblDateTo.setText(sdf.format(filterDateTo));
    }

    private void pickDate(boolean isFrom) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        JCalendarDialog dialog = new JCalendarDialog(owner, isFrom ? filterDateFrom : filterDateTo);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (isFrom) { filterDateFrom = dialog.getSelectedDate(); lblDateFrom.setText(sdf.format(filterDateFrom)); }
            else { filterDateTo = dialog.getSelectedDate(); lblDateTo.setText(sdf.format(filterDateTo)); }
            loadData();
        }
    }

    private void loadData() {
        try {
            List<Object[]> data = dao.getLaporanTransaksiServis(filterDateFrom, filterDateTo);
            styledTable.setData(new String[]{"ID", "Tanggal", "Pelanggan", "No Polisi", "Mekanik",
                    "Keluhan", "Status", "Total Jasa", "Total Sparepart", "Grand Total", "Metode Bayar"}, data);
            applyFilter();
        } catch (Exception ex) {
            UIHelper.error(this, "Error load data: " + ex.getMessage());
        }
    }

    private void applyFilter() {
        String text = txtSearch.getText().trim().toLowerCase();
        String status = cbStatus.getSelectedItem().toString();

        List<Object[]> result = new ArrayList<>();
        for (Object[] row : styledTable.getAllData()) {
            if (!"Semua".equals(status)) {
                String rowStatus = row[6] != null ? row[6].toString() : "";
                if (!rowStatus.equalsIgnoreCase(status)) continue;
            }
            if (!text.isEmpty()) {
                boolean found = false;
                for (Object cell : row) {
                    if (cell != null && cell.toString().toLowerCase().contains(text)) { found = true; break; }
                }
                if (!found) continue;
            }
            result.add(row);
        }
        styledTable.setFilteredData(result);
        updateSummary();
    }

    private void updateSummary() {
        double sumJasa = 0, sumSparepart = 0, sumGrand = 0;
        int count = styledTable.getFilteredRowCount();
        for (Object[] row : styledTable.getFilteredData()) {
            try {
                sumJasa += row[7] != null ? Double.parseDouble(row[7].toString()) : 0;
                sumSparepart += row[8] != null ? Double.parseDouble(row[8].toString()) : 0;
                sumGrand += row[9] != null ? Double.parseDouble(row[9].toString()) : 0;
            } catch (Exception ignored) {}
        }
        lblSummary.setText(String.format("Total %d transaksi  |  Jasa: Rp %,.0f  |  Sparepart: Rp %,.0f  |  Grand Total: Rp %,.0f",
                count, sumJasa, sumSparepart, sumGrand));
    }
}
