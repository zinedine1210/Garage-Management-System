package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class TransactionListPanel extends javax.swing.JPanel {

    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private JButton btnDateFrom;
    private JButton btnDateTo;
    private JLabel lblDateFrom;
    private JLabel lblDateTo;
    private Date filterDateFrom;
    private Date filterDateTo;
    private JLabel lblSummary;
    private Frame owner;

    public TransactionListPanel() {
        initComponents();
        myInit();
    }

    public TransactionListPanel(Frame owner) {
        this();
        this.owner = owner;
    }

    private void myInit() {
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        table.setRowHeight(22);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        cbStatus.addActionListener(e -> applyFilter());
        cbBulan.addActionListener(e -> applyFilter());

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        datePanel.add(new JLabel("Dari:"));
        lblDateFrom = new JLabel("---");
        datePanel.add(lblDateFrom);
        btnDateFrom = new JButton("Pilih");
        btnDateFrom.addActionListener(e -> pickDateFrom());
        datePanel.add(btnDateFrom);
        datePanel.add(new JLabel("Sampai:"));
        lblDateTo = new JLabel("---");
        datePanel.add(lblDateTo);
        btnDateTo = new JButton("Pilih");
        btnDateTo.addActionListener(e -> pickDateTo());
        datePanel.add(btnDateTo);
        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            setDefaultDateRangeThisMonth();
            loadData();
        });
        datePanel.add(btnReset);
        topPanel.add(datePanel, BorderLayout.SOUTH);

        lblSummary = new JLabel(" ");
        lblSummary.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        remove(topPanel);
        JPanel wrapTop = new JPanel(new BorderLayout());
        wrapTop.add(topPanel, BorderLayout.CENTER);
        wrapTop.add(lblSummary, BorderLayout.SOUTH);
        add(wrapTop, BorderLayout.NORTH);

        setDefaultDateRangeThisMonth();
        loadData();
    }

    private void setDefaultDateRangeThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        filterDateFrom = cal.getTime();

        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        filterDateTo = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        lblDateFrom.setText(sdf.format(filterDateFrom));
        lblDateTo.setText(sdf.format(filterDateTo));
    }

    private void pickDateFrom() {
        JCalendarDialog dialog = new JCalendarDialog(getOwnerFrame(), filterDateFrom);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            filterDateFrom = dialog.getSelectedDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            lblDateFrom.setText(sdf.format(filterDateFrom));
            loadData();
        }
    }

    private void pickDateTo() {
        JCalendarDialog dialog = new JCalendarDialog(getOwnerFrame(), filterDateTo);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            filterDateTo = dialog.getSelectedDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            lblDateTo.setText(sdf.format(filterDateTo));
            loadData();
        }
    }

    private void applyFilter() {
        if (table.getRowSorter() == null) {
            table.setRowSorter(new javax.swing.table.TableRowSorter<>((DefaultTableModel) table.getModel()));
        }
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                (javax.swing.table.TableRowSorter<DefaultTableModel>) table.getRowSorter();

        java.util.List<javax.swing.RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

        String text = txtSearch.getText().trim();
        if (text.length() > 0) {
            filters.add(javax.swing.RowFilter.regexFilter("(?i)" + text));
        }

        String status = cbStatus.getSelectedItem().toString();
        if (!status.equals("Semua")) {
            filters.add(javax.swing.RowFilter.regexFilter("(?i)^" + status + "$", 8));
        }

        String bulan = cbBulan.getSelectedItem().toString();
        if (!bulan.equals("Semua")) {
            filters.add(javax.swing.RowFilter.regexFilter("-[0]*" + bulan + "-", 1));
        }

        if (filters.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(javax.swing.RowFilter.andFilter(filters));

        updateSummary();
    }

    private void updateSummary() {
        double sumJasa = 0, sumSparepart = 0, sumGrand = 0;
        int count = table.getRowCount();

        for (int i = 0; i < count; i++) {
            try {
                Object jasaObj = table.getValueAt(i, 6);
                Object sparepartObj = table.getValueAt(i, 7);
                Object grandObj = table.getValueAt(i, 9);

                double jasa = jasaObj != null ? Double.parseDouble(jasaObj.toString()) : 0;
                double sparepart = sparepartObj != null ? Double.parseDouble(sparepartObj.toString()) : 0;
                double grand = grandObj != null ? Double.parseDouble(grandObj.toString()) : 0;

                sumJasa += jasa;
                sumSparepart += sparepart;
                sumGrand += grand;
            } catch (NumberFormatException e) {
                continue;
            }
        }

        if (lblSummary != null) {
            lblSummary.setText(String.format("Total %d transaksi  |  Jasa: Rp %,.0f  |  Sparepart: Rp %,.0f  |  Grand Total: Rp %,.0f",
                    count, sumJasa, sumSparepart, sumGrand));
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        filterPanel = new javax.swing.JPanel();
        lblCari = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        cbStatus = new javax.swing.JComboBox();
        lblBulan = new javax.swing.JLabel();
        cbBulan = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        btnBaru = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new java.awt.BorderLayout());
        filterPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        lblCari.setText("Cari:"); filterPanel.add(lblCari);
        txtSearch.setColumns(15); filterPanel.add(txtSearch);
        lblStatus.setText("Status:"); filterPanel.add(lblStatus);
        cbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Menunggu", "Dikerjakan", "Selesai Lunas", "Batal" }));
        filterPanel.add(cbStatus);
        lblBulan.setText("Bulan:"); filterPanel.add(lblBulan);
        cbBulan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));
        filterPanel.add(cbBulan);
        topPanel.add(filterPanel, java.awt.BorderLayout.CENTER);
        add(topPanel, java.awt.BorderLayout.NORTH);

        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(table);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btnBaru.setText("Transaksi Baru");
        btnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnBaruActionPerformed(evt); }
        });
        buttonPanel.add(btnBaru);
        btnEdit.setText("Edit Transaksi");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnEditActionPerformed(evt); }
        });
        buttonPanel.add(btnEdit);
        btnHapus.setText("Hapus Transaksi");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnHapusActionPerformed(evt); }
        });
        buttonPanel.add(btnHapus);
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnRefreshActionPerformed(evt); }
        });
        buttonPanel.add(btnRefresh);
        btnPrint = new javax.swing.JButton();
        btnPrint.setText("Print Laporan");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { printLaporan(); }
        });
        buttonPanel.add(btnPrint);
        javax.swing.JButton btnExport = new javax.swing.JButton();
        btnExport.setText("Export");
        btnExport.addActionListener(e -> {
            String[] options = {"PDF", "Excel"};
            int choice = javax.swing.JOptionPane.showOptionDialog(this, "Pilih format export:", "Export Data", javax.swing.JOptionPane.YES_NO_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (choice == 0) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToPDF(table, "Data_Transaksi_Servis");
            else if (choice == 1) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToExcel(table, "Data_Transaksi_Servis");
        });
        buttonPanel.add(btnExport);
        add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruActionPerformed
        Frame f = getOwnerFrame();
        new ServiceTransactionFrame(f).setVisible(true); loadData();
    }//GEN-LAST:event_btnBaruActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan diedit."); return; }
        int transId = (int) table.getValueAt(row, 0);
        Frame f = getOwnerFrame();
        new ServiceTransactionFrame(f, transId).setVisible(true); loadData();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan dihapus."); return; }
        int transId = (int) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus transaksi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try { transDAO.delete(transId); loadData(); }
            catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error hapus: " + ex.getMessage()); }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadData();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void loadData() {
        try {
            List<ServiceTransaction> list = transDAO.findAll();

            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID Transaksi", "Tanggal", "Pelanggan", "No. Polisi", "Keluhan", "Mekanik", "Total Jasa", "Total Sparepart", "Status", "Grand Total"}, 0);

            for (ServiceTransaction t : list) {
                if (filterDateFrom != null && t.getTanggal().before(filterDateFrom)) continue;
                if (filterDateTo != null && t.getTanggal().after(filterDateTo)) continue;

                model.addRow(new Object[]{t.getTransId(), t.getTanggal().toString(), t.getClientNama(), t.getNoPolisi(),
                    t.getKeluhan(), t.getMekanikNama(), t.getTotalJasa(), t.getTotalSparepart(), t.getStatusServis(), t.getGrandTotal()});
            }

            table.setModel(model);
            javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(model);
            table.setRowSorter(sorter);

            updateSummary();
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error load data: " + ex.getMessage()); }
    }

    private Frame getOwnerFrame() {
        if (owner != null) return owner;
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof Frame) return (Frame) w;
        return null;
    }

    private void printLaporan() {
        Object[] options = {"PDF", "Excel", "Batal"};
        int choice = JOptionPane.showOptionDialog(this, "Pilih format export:", "Export Laporan", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 0) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToPDF(table, "Laporan_Transaksi_Servis");
        else if (choice == 1) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToExcel(table, "Laporan_Transaksi_Servis");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JComboBox cbBulan;
    private javax.swing.JComboBox cbStatus;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBulan;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable table;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
