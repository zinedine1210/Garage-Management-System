package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.DashboardDAO;
import com.mycompany.garagemanagementsystem.util.ExportUtils;
import com.mycompany.garagemanagementsystem.util.StyledTable;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Laporan Omzet & Pendapatan - menampilkan omzet per hari/minggu/bulan
 * dengan filter tanggal, status, metode bayar, dan ringkasan laba kotor.
 */
public class LaporanOmzetPanel extends javax.swing.JPanel {

    private final DashboardDAO dao = new DashboardDAO();
    private StyledTable styledTable;
    private JLabel lblSummary, lblSummary2;
    private JComboBox<String> cbGroupBy, cbStatus, cbMetodeBayar;
    private JTextField txtSearch;
    private Date filterDateFrom, filterDateTo;
    private DateRangePickerPanel dateRangePicker;

    public LaporanOmzetPanel() {
        buildUI();
        filterDateFrom = dateRangePicker.getFromDate();
        filterDateTo = dateRangePicker.getToDate();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        JPanel topPanel = new JPanel(new BorderLayout(0, 4));
        topPanel.setOpaque(false);

        topPanel.add(UIHelper.createPageHeader("Laporan Omzet & Pendapatan",
                "Analisis omzet dan pendapatan bengkel per hari, minggu, atau bulan — dengan filter lengkap"), BorderLayout.NORTH);

        dateRangePicker = new DateRangePickerPanel((from, to) -> {
            filterDateFrom = from;
            filterDateTo = to;
            loadData();
        });

        // Filter Row: Group by, Status, Metode Bayar, Search
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterRow.setBackground(Color.WHITE);
        filterRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(6, 12, 6, 12)));

        filterRow.add(new JLabel("Group by:"));
        cbGroupBy = new JComboBox<>(new String[]{"Harian", "Mingguan", "Bulanan"});
        cbGroupBy.addActionListener(e -> loadData());
        filterRow.add(cbGroupBy);

        filterRow.add(Box.createHorizontalStrut(8));
        filterRow.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"Semua", "Selesai Lunas", "Dikerjakan", "Menunggu", "Batal"});
        cbStatus.addActionListener(e -> loadData());
        filterRow.add(cbStatus);

        filterRow.add(Box.createHorizontalStrut(8));
        filterRow.add(new JLabel("Metode Bayar:"));
        cbMetodeBayar = new JComboBox<>(new String[]{"Semua", "Cash", "QRIS", "Transfer Bank", "Debit", "Lainnya"});
        cbMetodeBayar.addActionListener(e -> loadData());
        filterRow.add(cbMetodeBayar);

        filterRow.add(Box.createHorizontalStrut(8));
        filterRow.add(new JLabel("\uD83D\uDD0D Cari:"));
        txtSearch = new JTextField(14);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
        });
        filterRow.add(txtSearch);

        JButton btnReset = new JButton("Reset Filter");
        btnReset.addActionListener(e -> {
            cbStatus.setSelectedIndex(0);
            cbMetodeBayar.setSelectedIndex(0);
            cbGroupBy.setSelectedIndex(0);
            txtSearch.setText("");
        });
        filterRow.add(btnReset);

        JPanel filterWrap = new JPanel(new GridLayout(2, 1, 0, 4));
        filterWrap.setOpaque(false);
        filterWrap.add(dateRangePicker);
        filterWrap.add(filterRow);

        topPanel.add(filterWrap, BorderLayout.CENTER);

        // Summary panel with 2 lines
        JPanel summaryPanel = new JPanel(new GridLayout(2, 1));
        summaryPanel.setOpaque(false);
        lblSummary = new JLabel(" ");
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummary.setBorder(new EmptyBorder(4, 10, 0, 10));
        summaryPanel.add(lblSummary);
        lblSummary2 = new JLabel(" ");
        lblSummary2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummary2.setForeground(new Color(40, 167, 69));
        lblSummary2.setBorder(new EmptyBorder(0, 10, 4, 10));
        summaryPanel.add(lblSummary2);
        topPanel.add(summaryPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // ===== CENTER =====
        styledTable = new StyledTable();
        add(styledTable, BorderLayout.CENTER);

        // ===== BOTTOM =====
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(243, 245, 249));
        buttonPanel.setBorder(new EmptyBorder(6, 0, 6, 0));

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setOpaque(false);

        JButton btnRefresh = UIHelper.createStyledButton("Refresh", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> loadData());
        rightButtons.add(btnRefresh);

        JButton btnExport = UIHelper.createStyledButton("Export", new Color(23, 162, 184));
        btnExport.addActionListener(e -> {
            String[] options = {"PDF", "Excel", "Batal"};
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Laporan Omzet & Pendapatan", options);
            int[] totCols = {1, 2, 3, 4, 5};
            if (choice == 0) ExportUtils.exportTableToPDFWithTotals(styledTable.getTable(), "Laporan_Omzet_Pendapatan", totCols);
            else if (choice == 1) ExportUtils.exportTableToExcelWithTotals(styledTable.getTable(), "Laporan_Omzet_Pendapatan", totCols);
        });
        rightButtons.add(btnExport);

        buttonPanel.add(rightButtons, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            String groupBy;
            switch (cbGroupBy.getSelectedIndex()) {
                case 1: groupBy = "WEEK"; break;
                case 2: groupBy = "MONTH"; break;
                default: groupBy = "DAY"; break;
            }
            String statusFilter = null;
            if (cbStatus.getSelectedIndex() > 0) statusFilter = cbStatus.getSelectedItem().toString();
            String metodeFilter = null;
            if (cbMetodeBayar.getSelectedIndex() > 0) metodeFilter = cbMetodeBayar.getSelectedItem().toString();

            List<Object[]> data = dao.getLaporanOmzet(filterDateFrom, filterDateTo, groupBy, statusFilter, metodeFilter);
            styledTable.setData(new String[]{"Periode", "Jml Transaksi", "Pendapatan Jasa",
                    "Pendapatan Sparepart", "Total Omzet", "Total Dibayar"}, data);
            updateSummary();
        } catch (Exception ex) {
            UIHelper.error(this, "Error load data: " + ex.getMessage());
        }
    }

    private void updateSummary() {
        double sumJasa = 0, sumSp = 0, sumTotal = 0, sumBayar = 0;
        int sumTrx = 0;
        for (Object[] row : styledTable.getFilteredData()) {
            try {
                sumTrx += Integer.parseInt(row[1].toString());
                sumJasa += Double.parseDouble(row[2].toString());
                sumSp += Double.parseDouble(row[3].toString());
                sumTotal += Double.parseDouble(row[4].toString());
                sumBayar += Double.parseDouble(row[5].toString());
            } catch (Exception ignored) {}
        }

        double totalPembelian = 0;
        try { totalPembelian = dao.getTotalPembelianByDateRange(filterDateFrom, filterDateTo); } catch (Exception ignored) {}
        double labaKotor = sumTotal - totalPembelian;

        lblSummary.setText(String.format(
                "%d periode  |  %d transaksi  |  Jasa: Rp %,.0f  |  Sparepart: Rp %,.0f  |  Omzet: Rp %,.0f  |  Dibayar: Rp %,.0f",
                styledTable.getFilteredRowCount(), sumTrx, sumJasa, sumSp, sumTotal, sumBayar));
        lblSummary2.setText(String.format(
                "Pembelian Stok: Rp %,.0f  |  Laba Kotor: Rp %,.0f  |  Margin: %.1f%%",
                totalPembelian, labaKotor, sumTotal > 0 ? (labaKotor / sumTotal * 100) : 0));
    }
}
