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
 * Laporan Kinerja Mekanik - menampilkan performa setiap mekanik dengan filter tanggal.
 */
public class LaporanKinerjaMekanikPanel extends javax.swing.JPanel {

    private final DashboardDAO dao = new DashboardDAO();
    private StyledTable styledTable;
    private JLabel lblSummary;
    private JTextField txtSearch;
    private Date filterDateFrom, filterDateTo;
    private DateRangePickerPanel dateRangePicker;

    public LaporanKinerjaMekanikPanel() {
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

        topPanel.add(UIHelper.createPageHeader("Laporan Kinerja Mekanik",
                "Evaluasi performa setiap mekanik berdasarkan jumlah servis dan pendapatan"), BorderLayout.NORTH);

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
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Laporan Kinerja", options);
            int[] totCols = {3, 4, 5, 6, 7};
            if (choice == 0) ExportUtils.exportTableToPDFWithTotals(styledTable.getTable(), "Laporan_Kinerja_Mekanik", totCols);
            else if (choice == 1) ExportUtils.exportTableToExcelWithTotals(styledTable.getTable(), "Laporan_Kinerja_Mekanik", totCols);
        });
        rightButtons.add(btnExport);

        buttonPanel.add(rightButtons, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            List<Object[]> data = dao.getLaporanKinerjaMekanik(filterDateFrom, filterDateTo);
            styledTable.setData(new String[]{"ID", "Nama Mekanik", "Spesialis", "Total Servis",
                    "Selesai", "Dalam Proses", "Total Jasa (Rp)", "Total Revenue (Rp)"}, data);
            updateSummary();
        } catch (Exception ex) {
            UIHelper.error(this, "Error load data: " + ex.getMessage());
        }
    }

    private void updateSummary() {
        int totalServis = 0, totalSelesai = 0;
        double totalJasa = 0, totalRevenue = 0;
        int mekanikCount = styledTable.getFilteredRowCount();
        for (Object[] row : styledTable.getFilteredData()) {
            try {
                totalServis += (int) row[3];
                totalSelesai += (int) row[4];
                totalJasa += (double) row[6];
                totalRevenue += (double) row[7];
            } catch (Exception ignored) {}
        }
        lblSummary.setText(String.format(
                "%d mekanik  |  Total Servis: %d  |  Selesai: %d  |  Total Jasa: Rp %,.0f  |  Revenue: Rp %,.0f",
                mekanikCount, totalServis, totalSelesai, totalJasa, totalRevenue));
    }

    private JButton createStyledButton(String text, Color bg) {
        return UIHelper.createStyledButton(text, bg);
    }
}
