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
 * Laporan Omzet - menampilkan omzet per hari/minggu/bulan dengan filter tanggal lengkap.
 */
public class LaporanOmzetPanel extends javax.swing.JPanel {

    private final DashboardDAO dao = new DashboardDAO();
    private StyledTable styledTable;
    private JLabel lblDateFrom, lblDateTo, lblSummary;
    private JComboBox<String> cbGroupBy;
    private JTextField txtSearch;
    private Date filterDateFrom, filterDateTo;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public LaporanOmzetPanel() {
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

        topPanel.add(UIHelper.createPageHeader("Laporan Omzet",
                "Analisis omzet bengkel per hari, minggu, atau bulan dalam periode tertentu"), BorderLayout.NORTH);

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
        filterPanel.add(new JLabel("Group by:"));
        cbGroupBy = new JComboBox<>(new String[]{"Harian", "Mingguan", "Bulanan"});
        cbGroupBy.addActionListener(e -> loadData());
        filterPanel.add(cbGroupBy);

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
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Laporan Omzet", options);
            if (choice == 0) ExportUtils.exportTableToPDF(styledTable.getTable(), "Laporan_Omzet");
            else if (choice == 1) ExportUtils.exportTableToExcel(styledTable.getTable(), "Laporan_Omzet");
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
            String groupBy;
            switch (cbGroupBy.getSelectedIndex()) {
                case 1: groupBy = "WEEK"; break;
                case 2: groupBy = "MONTH"; break;
                default: groupBy = "DAY"; break;
            }
            List<Object[]> data = dao.getLaporanOmzet(filterDateFrom, filterDateTo, groupBy);
            styledTable.setData(new String[]{"Periode", "Jml Transaksi", "Pendapatan Jasa", "Pendapatan Sparepart", "Total Omzet"}, data);
            updateSummary();
        } catch (Exception ex) {
            UIHelper.error(this, "Error load data: " + ex.getMessage());
        }
    }

    private void updateSummary() {
        double sumJasa = 0, sumSp = 0, sumTotal = 0;
        int sumTrx = 0;
        for (Object[] row : styledTable.getFilteredData()) {
            try {
                sumTrx += (int) row[1];
                sumJasa += (double) row[2];
                sumSp += (double) row[3];
                sumTotal += (double) row[4];
            } catch (Exception ignored) {}
        }
        lblSummary.setText(String.format("Total %d transaksi  |  Jasa: Rp %,.0f  |  Sparepart: Rp %,.0f  |  Omzet: Rp %,.0f",
                sumTrx, sumJasa, sumSp, sumTotal));
    }

    private JButton createStyledButton(String text, Color bg) {
        return UIHelper.createStyledButton(text, bg);
    }
}
