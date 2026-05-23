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

public class LaporanPembelianPanel extends javax.swing.JPanel {

    private final DashboardDAO dao = new DashboardDAO();
    private StyledTable styledTable;
    private JLabel lblSummary;
    private JTextField txtSearch;
    private Date filterDateFrom, filterDateTo;
    private DateRangePickerPanel dateRangePicker;

    public LaporanPembelianPanel() {
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

        topPanel.add(UIHelper.createPageHeader("Laporan Pembelian Sparepart",
                "Rekap pembelian sparepart dari supplier berdasarkan periode tertentu"), BorderLayout.NORTH);

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
        txtSearch = new JTextField(15);
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

        JButton btnRefresh = UIHelper.createStyledButton("Refresh", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> loadData());
        rightButtons.add(btnRefresh);

        JButton btnExport = UIHelper.createStyledButton("Export", new Color(23, 162, 184));
        btnExport.addActionListener(e -> {
            String[] options = {"PDF", "Excel", "Batal"};
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Laporan Pembelian", options);
            int[] totCols = {5, 6, 7};
            if (choice == 0) ExportUtils.exportTableToPDFWithTotals(styledTable.getTable(), "Laporan_Pembelian_Sparepart", totCols);
            else if (choice == 1) ExportUtils.exportTableToExcelWithTotals(styledTable.getTable(), "Laporan_Pembelian_Sparepart", totCols);
        });
        rightButtons.add(btnExport);

        buttonPanel.add(rightButtons, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            List<Object[]> data = dao.getLaporanPembelian(filterDateFrom, filterDateTo);
            styledTable.setData(new String[]{"ID", "Tanggal", "Supplier", "Kode Sparepart",
                    "Nama Sparepart", "Qty", "Harga Beli", "Total Harga", "Keterangan"}, data);
            updateSummary();
        } catch (Exception ex) {
            UIHelper.error(this, "Error load data: " + ex.getMessage());
        }
    }

    private void updateSummary() {
        double sumTotal = 0;
        int sumQty = 0;
        int count = styledTable.getFilteredRowCount();
        for (Object[] row : styledTable.getFilteredData()) {
            try {
                sumQty += Integer.parseInt(row[5].toString());
                sumTotal += Double.parseDouble(row[7].toString());
            } catch (Exception ignored) {}
        }
        lblSummary.setText(String.format("Total %d pembelian  |  Total Qty: %,d  |  Total Nilai: Rp %,.0f",
                count, sumQty, sumTotal));
    }
}
