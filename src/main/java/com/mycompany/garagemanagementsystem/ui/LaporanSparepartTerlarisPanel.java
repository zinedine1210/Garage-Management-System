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

/**
 * Laporan Sparepart Terlaris - menampilkan sparepart yang paling banyak terjual.
 */
public class LaporanSparepartTerlarisPanel extends javax.swing.JPanel {

    private final DashboardDAO dao = new DashboardDAO();
    private StyledTable styledTable;
    private JLabel lblDateFrom, lblDateTo, lblSummary;
    private JTextField txtSearch;
    private Date filterDateFrom, filterDateTo;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public LaporanSparepartTerlarisPanel() {
        buildUI();
        setDefaultDateRange();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        // ===== TOP: Filter =====
        JPanel topPanel = new JPanel(new BorderLayout(0, 4));
        topPanel.setOpaque(false);

        topPanel.add(UIHelper.createPageHeader("Laporan Sparepart Terlaris",
                "Ranking sparepart berdasarkan jumlah penjualan dalam periode tertentu"), BorderLayout.NORTH);

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
        filterPanel.add(new JLabel("\uD83D\uDD0D Cari:"));
        txtSearch = new JTextField(12);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
        });
        filterPanel.add(txtSearch);

        topPanel.add(filterPanel, BorderLayout.CENTER);

        lblSummary = new JLabel(" ");
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummary.setBorder(new EmptyBorder(4, 10, 4, 10));
        topPanel.add(lblSummary, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // ===== CENTER =====
        styledTable = new StyledTable();
        add(styledTable, BorderLayout.CENTER);

        // ===== BOTTOM =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        buttonPanel.setBackground(new Color(243, 245, 249));

        JButton btnRefresh = createStyledButton("Refresh", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> loadData());
        buttonPanel.add(btnRefresh);

        JButton btnExport = createStyledButton("Export", new Color(23, 162, 184));
        btnExport.addActionListener(e -> {
            String[] options = {"PDF", "Excel", "Batal"};
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Laporan Sparepart", options);
            int[] totCols = {3, 4, 5};
            if (choice == 0) ExportUtils.exportTableToPDFWithTotals(styledTable.getTable(), "Laporan_Sparepart_Terlaris", totCols);
            else if (choice == 1) ExportUtils.exportTableToExcelWithTotals(styledTable.getTable(), "Laporan_Sparepart_Terlaris", totCols);
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
            List<Object[]> data = dao.getLaporanSparepartTerlaris(filterDateFrom, filterDateTo);
            styledTable.setData(new String[]{"Kode Sparepart", "Nama Sparepart", "Satuan",
                    "Total Qty Terjual", "Total Nilai (Rp)", "Jml Transaksi"}, data);
            updateSummary();
        } catch (Exception ex) {
            UIHelper.error(this, "Error load data: " + ex.getMessage());
        }
    }

    private void updateSummary() {
        int totalQty = 0, totalTrx = 0;
        double totalNilai = 0;
        int items = styledTable.getFilteredRowCount();
        for (Object[] row : styledTable.getFilteredData()) {
            try {
                totalQty += (int) row[3];
                totalNilai += (double) row[4];
                totalTrx += (int) row[5];
            } catch (Exception ignored) {}
        }
        lblSummary.setText(String.format(
                "%d jenis sparepart  |  Total Qty: %,d  |  Total Nilai: Rp %,.0f  |  Dari %d transaksi",
                items, totalQty, totalNilai, totalTrx));
    }

    private JButton createStyledButton(String text, Color bg) {
        return UIHelper.createStyledButton(text, bg);
    }
}
