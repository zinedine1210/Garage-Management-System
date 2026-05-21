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

public class LaporanPendapatanPanel extends javax.swing.JPanel {

    private final DashboardDAO dao = new DashboardDAO();
    private StyledTable styledTable;
    private JLabel lblDateFrom, lblDateTo, lblSummary;
    private JTextField txtSearch;
    private Date filterDateFrom, filterDateTo;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public LaporanPendapatanPanel() {
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

        topPanel.add(UIHelper.createPageHeader("Laporan Pendapatan",
                "Analisis pendapatan harian dari jasa servis dan penjualan sparepart"), BorderLayout.NORTH);

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
        txtSearch = new JTextField(15);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
        });
        filterPanel.add(txtSearch);

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
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Laporan Pendapatan", options);
            if (choice == 0) ExportUtils.exportTableToPDF(styledTable.getTable(), "Laporan_Pendapatan");
            else if (choice == 1) ExportUtils.exportTableToExcel(styledTable.getTable(), "Laporan_Pendapatan");
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
            List<Object[]> rawData = dao.getLaporanPendapatan(filterDateFrom, filterDateTo);
            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");
            List<Object[]> data = new ArrayList<>();
            for (Object[] row : rawData) {
                data.add(new Object[]{
                        row[0] != null ? dateFmt.format(row[0]) : "",
                        row[1], row[2], row[3], row[4]
                });
            }
            styledTable.setData(new String[]{"Tanggal", "Jumlah Transaksi", "Pendapatan Jasa",
                    "Pendapatan Sparepart", "Total Pendapatan"}, data);
            updateSummary();
        } catch (Exception ex) {
            UIHelper.error(this, "Error load data: " + ex.getMessage());
        }
    }

    private void updateSummary() {
        double sumJasa = 0, sumSparepart = 0, sumGrand = 0;
        int sumTransaksi = 0;
        int count = styledTable.getFilteredRowCount();
        for (Object[] row : styledTable.getFilteredData()) {
            try {
                sumTransaksi += Integer.parseInt(row[1].toString());
                sumJasa += Double.parseDouble(row[2].toString());
                sumSparepart += Double.parseDouble(row[3].toString());
                sumGrand += Double.parseDouble(row[4].toString());
            } catch (Exception ignored) {}
        }

        double totalPembelian = 0;
        try { totalPembelian = dao.getTotalPembelianByDateRange(filterDateFrom, filterDateTo); } catch (Exception ignored) {}
        double labaKotor = sumGrand - totalPembelian;

        lblSummary.setText(String.format(
                "%d hari  |  %d transaksi  |  Jasa: Rp %,.0f  |  Sparepart: Rp %,.0f  |  "
                + "Pendapatan: Rp %,.0f  |  Pembelian: Rp %,.0f  |  Laba Kotor: Rp %,.0f",
                count, sumTransaksi, sumJasa, sumSparepart, sumGrand, totalPembelian, labaKotor));
    }
}
