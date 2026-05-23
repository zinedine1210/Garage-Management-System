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
 * Laporan Sparepart Terlaris - menampilkan sparepart yang paling banyak terjual.
 */
public class LaporanSparepartTerlarisPanel extends javax.swing.JPanel {

    private final DashboardDAO dao = new DashboardDAO();
    private StyledTable styledTable;
    private JLabel lblSummary;
    private JTextField txtSearch;
    private Date filterDateFrom, filterDateTo;
    private DateRangePickerPanel dateRangePicker;

    public LaporanSparepartTerlarisPanel() {
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

        topPanel.add(UIHelper.createPageHeader("Laporan Sparepart Terlaris",
                "Ranking sparepart berdasarkan jumlah penjualan dalam periode tertentu"), BorderLayout.NORTH);

        dateRangePicker = new DateRangePickerPanel((from, to) -> {
            filterDateFrom = from;
            filterDateTo = to;
            loadData();
        });

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterRow.setBackground(Color.WHITE);
        filterRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(6, 12, 6, 12)));

        filterRow.add(new JLabel("\uD83D\uDD0D Cari:"));
        txtSearch = new JTextField(12);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); updateSummary(); }
        });
        filterRow.add(txtSearch);

        JPanel filterWrap = new JPanel(new GridLayout(2, 1, 0, 4));
        filterWrap.setOpaque(false);
        filterWrap.add(dateRangePicker);
        filterWrap.add(filterRow);

        JPanel midWrap = new JPanel(new BorderLayout(0, 4));
        midWrap.setOpaque(false);
        midWrap.add(filterWrap, BorderLayout.CENTER);

        lblSummary = new JLabel(" ");
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummary.setBorder(new EmptyBorder(4, 10, 4, 10));
        midWrap.add(lblSummary, BorderLayout.SOUTH);

        topPanel.add(midWrap, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        styledTable = new StyledTable();
        add(styledTable, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(243, 245, 249));
        buttonPanel.setBorder(new EmptyBorder(6, 0, 6, 0));

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setOpaque(false);

        JButton btnRefresh = createStyledButton("Refresh", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> loadData());
        rightButtons.add(btnRefresh);

        JButton btnExport = createStyledButton("Export", new Color(23, 162, 184));
        btnExport.addActionListener(e -> {
            String[] options = {"PDF", "Excel", "Batal"};
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Laporan Sparepart", options);
            int[] totCols = {3, 4, 5};
            if (choice == 0) ExportUtils.exportTableToPDFWithTotals(styledTable.getTable(), "Laporan_Sparepart_Terlaris", totCols);
            else if (choice == 1) ExportUtils.exportTableToExcelWithTotals(styledTable.getTable(), "Laporan_Sparepart_Terlaris", totCols);
        });
        rightButtons.add(btnExport);

        buttonPanel.add(rightButtons, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
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
