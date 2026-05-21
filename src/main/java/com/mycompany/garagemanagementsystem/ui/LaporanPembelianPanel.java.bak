package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.DashboardDAO;
import com.mycompany.garagemanagementsystem.util.ExportUtils;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class LaporanPembelianPanel extends javax.swing.JPanel {

    private final DashboardDAO dao = new DashboardDAO();
    private JTable table;
    private JLabel lblDateFrom, lblDateTo, lblSummary;
    private JTextField txtSearch;
    private Date filterDateFrom, filterDateTo;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public LaporanPembelianPanel() {
        buildUI();
        setDefaultDateRange();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ===== TOP: Filter =====
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
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
        filterPanel.add(new JLabel("Cari:"));
        txtSearch = new JTextField(15);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        filterPanel.add(txtSearch);

        topPanel.add(filterPanel, BorderLayout.CENTER);

        lblSummary = new JLabel(" ");
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummary.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        topPanel.add(lblSummary, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // ===== CENTER: Table =====
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BOTTOM: Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());
        buttonPanel.add(btnRefresh);

        JButton btnExport = new JButton("Export");
        btnExport.addActionListener(e -> {
            Object[] options = {"PDF", "Excel", "Batal"};
            int choice = JOptionPane.showOptionDialog(this, "Pilih format export:", "Export Laporan",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (choice == 0) ExportUtils.exportTableToPDF(table, "Laporan_Pembelian_Sparepart");
            else if (choice == 1) ExportUtils.exportTableToExcel(table, "Laporan_Pembelian_Sparepart");
        });
        buttonPanel.add(btnExport);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setDefaultDateRange() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        filterDateFrom = cal.getTime();

        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        filterDateTo = cal.getTime();

        lblDateFrom.setText(sdf.format(filterDateFrom));
        lblDateTo.setText(sdf.format(filterDateTo));
    }

    private void pickDate(boolean isFrom) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        JCalendarDialog dialog = new JCalendarDialog(owner, isFrom ? filterDateFrom : filterDateTo);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (isFrom) {
                filterDateFrom = dialog.getSelectedDate();
                lblDateFrom.setText(sdf.format(filterDateFrom));
            } else {
                filterDateTo = dialog.getSelectedDate();
                lblDateTo.setText(sdf.format(filterDateTo));
            }
            loadData();
        }
    }

    private void loadData() {
        try {
            List<Object[]> data = dao.getLaporanPembelian(filterDateFrom, filterDateTo);
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Tanggal", "Supplier", "Kode Sparepart",
                        "Nama Sparepart", "Qty", "Harga Beli", "Total Harga", "Keterangan"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Object[] row : data) {
                model.addRow(row);
            }
            table.setModel(model);
            table.setRowSorter(new TableRowSorter<>(model));
            updateSummary();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error load data: " + ex.getMessage());
        }
    }

    private void applyFilter() {
        if (table.getRowSorter() == null) return;
        TableRowSorter<DefaultTableModel> sorter =
                (TableRowSorter<DefaultTableModel>) table.getRowSorter();
        String text = txtSearch.getText().trim();
        if (text.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        updateSummary();
    }

    private void updateSummary() {
        double sumTotal = 0;
        int sumQty = 0;
        int count = table.getRowCount();
        for (int i = 0; i < count; i++) {
            try {
                sumQty += Integer.parseInt(table.getValueAt(i, 5).toString());
                sumTotal += Double.parseDouble(table.getValueAt(i, 7).toString());
            } catch (Exception ignored) {}
        }
        lblSummary.setText(String.format(
                "Total %d pembelian  |  Total Qty: %,d  |  Total Nilai: Rp %,.0f",
                count, sumQty, sumTotal));
    }
}
