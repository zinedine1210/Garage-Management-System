package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import com.mycompany.garagemanagementsystem.util.ExportUtils;
import com.mycompany.garagemanagementsystem.util.StyledTable;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TransactionListPanel extends javax.swing.JPanel {

    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private StyledTable styledTable;
    private JTextField txtSearch;
    private JComboBox<String> cbStatus, cbBulan;
    private JLabel lblDateFrom, lblDateTo, lblSummary;
    private Date filterDateFrom, filterDateTo;
    private Frame owner;

    public TransactionListPanel() {
        buildUI();
        setDefaultDateRange();
        loadData();
    }

    public TransactionListPanel(Frame owner) {
        this();
        this.owner = owner;
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        // ===== TOP =====
        JPanel topPanel = new JPanel(new BorderLayout(0, 4));
        topPanel.setOpaque(false);

        topPanel.add(UIHelper.createPageHeader("Transaksi Servis",
                "Kelola transaksi servis kendaraan — buat, edit, atau hapus transaksi"), BorderLayout.NORTH);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterBar.setBackground(Color.WHITE);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(6, 12, 6, 12)));

        filterBar.add(new JLabel("\uD83D\uDD0D Cari:"));
        txtSearch = new JTextField(14);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        filterBar.add(txtSearch);

        filterBar.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"Semua", "Menunggu", "Dikerjakan", "Selesai Lunas", "Batal"});
        cbStatus.addActionListener(e -> applyFilter());
        filterBar.add(cbStatus);

        filterBar.add(new JLabel("Bulan:"));
        cbBulan = new JComboBox<>(new String[]{"Semua", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
        cbBulan.addActionListener(e -> applyFilter());
        filterBar.add(cbBulan);

        // Date range
        filterBar.add(Box.createHorizontalStrut(10));
        filterBar.add(new JLabel("Dari:"));
        lblDateFrom = new JLabel("---");
        filterBar.add(lblDateFrom);
        JButton btnPickFrom = new JButton("Pilih");
        btnPickFrom.addActionListener(e -> pickDate(true));
        filterBar.add(btnPickFrom);

        filterBar.add(new JLabel("Sampai:"));
        lblDateTo = new JLabel("---");
        filterBar.add(lblDateTo);
        JButton btnPickTo = new JButton("Pilih");
        btnPickTo.addActionListener(e -> pickDate(false));
        filterBar.add(btnPickTo);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> { setDefaultDateRange(); loadData(); });
        filterBar.add(btnReset);

        topPanel.add(filterBar, BorderLayout.CENTER);

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

        JButton btnBaru = createStyledButton("+ Transaksi Baru", new Color(40, 167, 69));
        btnBaru.addActionListener(e -> {
            new ServiceTransactionFrame(getOwnerFrame()).setVisible(true);
            loadData();
        });
        buttonPanel.add(btnBaru);

        JButton btnEdit = createStyledButton("Edit Transaksi", new Color(0, 123, 255));
        btnEdit.addActionListener(e -> {
            Object[] row = styledTable.getSelectedRowData();
            if (row == null) { UIHelper.warn(this, "Pilih transaksi yang akan diedit."); return; }
            new ServiceTransactionFrame(getOwnerFrame(), (int) row[0]).setVisible(true);
            loadData();
        });
        buttonPanel.add(btnEdit);

        JButton btnHapus = createStyledButton("Hapus", new Color(220, 53, 69));
        btnHapus.addActionListener(e -> {
            Object[] row = styledTable.getSelectedRowData();
            if (row == null) { UIHelper.warn(this, "Pilih transaksi yang akan dihapus."); return; }
            if (UIHelper.confirm(this, "Hapus transaksi #" + row[0] + "?", "Konfirmasi Hapus")) {
                try { transDAO.delete((int) row[0]); loadData(); }
                catch (SQLException ex) { UIHelper.error(this, "Error: " + ex.getMessage()); }
            }
        });
        buttonPanel.add(btnHapus);

        JButton btnRefresh = createStyledButton("Refresh", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> loadData());
        buttonPanel.add(btnRefresh);

        JButton btnExport = createStyledButton("Export", new Color(23, 162, 184));
        btnExport.addActionListener(e -> {
            String[] options = {"PDF", "Excel", "Batal"};
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Transaksi Servis", options);
            if (choice == 0) ExportUtils.exportTableToPDF(styledTable.getTable(), "Data_Transaksi_Servis");
            else if (choice == 1) ExportUtils.exportTableToExcel(styledTable.getTable(), "Data_Transaksi_Servis");
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        lblDateFrom.setText(sdf.format(filterDateFrom));
        lblDateTo.setText(sdf.format(filterDateTo));
    }

    private void pickDate(boolean isFrom) {
        JCalendarDialog dialog = new JCalendarDialog(getOwnerFrame(), isFrom ? filterDateFrom : filterDateTo);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (isFrom) { filterDateFrom = dialog.getSelectedDate(); lblDateFrom.setText(sdf.format(filterDateFrom)); }
            else { filterDateTo = dialog.getSelectedDate(); lblDateTo.setText(sdf.format(filterDateTo)); }
            loadData();
        }
    }

    private void loadData() {
        try {
            List<ServiceTransaction> list = transDAO.findAll();
            List<Object[]> data = new ArrayList<>();
            for (ServiceTransaction t : list) {
                if (filterDateFrom != null && t.getTanggal().before(filterDateFrom)) continue;
                if (filterDateTo != null && t.getTanggal().after(filterDateTo)) continue;
                data.add(new Object[]{t.getTransId(), t.getTanggal().toString(), t.getClientNama(), t.getNoPolisi(),
                        t.getKeluhan(), t.getMekanikNama(), t.getTotalJasa(), t.getTotalSparepart(),
                        t.getStatusServis(), t.getGrandTotal()});
            }
            styledTable.setData(new String[]{"ID", "Tanggal", "Pelanggan", "No. Polisi", "Keluhan",
                    "Mekanik", "Total Jasa", "Total Sparepart", "Status", "Grand Total"}, data);
            updateSummary();
        } catch (SQLException ex) {
            UIHelper.error(this, "Error load data: " + ex.getMessage());
        }
    }

    private void applyFilter() {
        String text = txtSearch.getText().trim().toLowerCase();
        String status = cbStatus.getSelectedItem().toString();
        String bulan = cbBulan.getSelectedItem().toString();

        // Reset to all data, then filter
        java.util.List<Object[]> result = new java.util.ArrayList<>();
        for (Object[] row : styledTable.getAllData()) {
            // Status filter
            if (!"Semua".equals(status)) {
                String rowStatus = row[8] != null ? row[8].toString() : "";
                if (!rowStatus.equalsIgnoreCase(status)) continue;
            }
            // Bulan filter
            if (!"Semua".equals(bulan)) {
                String tgl = row[1] != null ? row[1].toString() : "";
                if (!tgl.contains("-" + bulan + "-") && !tgl.startsWith(bulan + "-")) continue;
            }
            // Text filter
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
                sumJasa += row[6] != null ? Double.parseDouble(row[6].toString()) : 0;
                sumSparepart += row[7] != null ? Double.parseDouble(row[7].toString()) : 0;
                sumGrand += row[9] != null ? Double.parseDouble(row[9].toString()) : 0;
            } catch (NumberFormatException ignored) {}
        }
        lblSummary.setText(String.format("Total %d transaksi  |  Jasa: Rp %,.0f  |  Sparepart: Rp %,.0f  |  Grand Total: Rp %,.0f",
                count, sumJasa, sumSparepart, sumGrand));
    }

    private Frame getOwnerFrame() {
        if (owner != null) return owner;
        Window w = SwingUtilities.getWindowAncestor(this);
        return w instanceof Frame ? (Frame) w : null;
    }

    private JButton createStyledButton(String text, Color bg) {
        return UIHelper.createStyledButton(text, bg);
    }
}
