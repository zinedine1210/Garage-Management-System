package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.DashboardDAO;
import java.awt.BorderLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class DashboardPanel extends javax.swing.JPanel {

    private final DashboardDAO dashDAO = new DashboardDAO();
    private ChartPanel chartPanelTipe, chartPanelKategori, chartPanelOmzetBulanan;
    private DefaultTableModel tblAntrianModel, tblSparepartModel;
    private javax.swing.JButton btnDateFrom, btnDateTo, btnRefresh;
    private javax.swing.JLabel lblDateFrom, lblDateTo;
    private Date filterDateFrom, filterDateTo;

    public DashboardPanel() {
        initComponents();
        myInit();
    }

    private void myInit() {
        tblAntrianModel = new DefaultTableModel(new Object[]{"No Polisi", "Status"}, 0);
        tblAntrian.setModel(tblAntrianModel);

        tblSparepartModel = new DefaultTableModel(new Object[]{"Sparepart", "Qty"}, 0);
        tblSparepart.setModel(tblSparepartModel);

        chartPanelTipe = new ChartPanel(null);
        pnlChart1.add(chartPanelTipe, BorderLayout.CENTER);

        chartPanelKategori = new ChartPanel(null);
        pnlChart2.add(chartPanelKategori, BorderLayout.CENTER);

        chartPanelOmzetBulanan = new ChartPanel(null);
        pnlChart3.add(chartPanelOmzetBulanan, BorderLayout.CENTER);

        javax.swing.JPanel filterPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Filter Tanggal:"));
        filterPanel.add(new JLabel("Dari:"));
        lblDateFrom = new JLabel("---");
        filterPanel.add(lblDateFrom);
        btnDateFrom = new javax.swing.JButton("Pilih");
        btnDateFrom.addActionListener(e -> pickDateFrom());
        filterPanel.add(btnDateFrom);
        filterPanel.add(new JLabel("Sampai:"));
        lblDateTo = new JLabel("---");
        filterPanel.add(lblDateTo);
        btnDateTo = new javax.swing.JButton("Pilih");
        btnDateTo.addActionListener(e -> pickDateTo());
        filterPanel.add(btnDateTo);
        btnRefresh = new javax.swing.JButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());
        filterPanel.add(btnRefresh);
        javax.swing.JButton btnReset = new javax.swing.JButton("Reset");
        btnReset.addActionListener(e -> {
            setDefaultDateRangeThisMonth();
            loadData();
        });
        filterPanel.add(btnReset);
        
        javax.swing.JButton btnPrint = new javax.swing.JButton("Print Dashboard");
        btnPrint.addActionListener(e -> printDashboard());
        filterPanel.add(btnPrint);

        add(filterPanel, BorderLayout.NORTH);

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
        java.awt.Frame owner = javax.swing.SwingUtilities.getWindowAncestor(this) instanceof java.awt.Frame ? (java.awt.Frame)javax.swing.SwingUtilities.getWindowAncestor(this) : null;
        JCalendarDialog dialog = new JCalendarDialog(owner, filterDateFrom);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            filterDateFrom = dialog.getSelectedDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            lblDateFrom.setText(sdf.format(filterDateFrom));
            loadData();
        }
    }

    private void pickDateTo() {
        java.awt.Frame owner = javax.swing.SwingUtilities.getWindowAncestor(this) instanceof java.awt.Frame ? (java.awt.Frame)javax.swing.SwingUtilities.getWindowAncestor(this) : null;
        JCalendarDialog dialog = new JCalendarDialog(owner, filterDateTo);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            filterDateTo = dialog.getSelectedDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            lblDateTo.setText(sdf.format(filterDateTo));
            loadData();
        }
    }

    private void printDashboard() {
        Object[] options = {"PDF", "Excel", "Batal"};
        int choice = javax.swing.JOptionPane.showOptionDialog(this, "Pilih format export:", "Export Dashboard", javax.swing.JOptionPane.YES_NO_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 0) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToPDF(tblAntrian, "Dashboard_Antrian");
        else if (choice == 1) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToExcel(tblAntrian, "Dashboard_Antrian");
    }

    public void loadData() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        try {
            if (filterDateFrom != null && filterDateTo != null) {
                lblTransHariIni.setText(String.valueOf(dashDAO.getTransaksiByDateRange(filterDateFrom, filterDateTo)));
                lblOmzetHariIni.setText(nf.format(dashDAO.getOmzetByDateRange(filterDateFrom, filterDateTo)));
            } else {
                lblTransHariIni.setText(String.valueOf(dashDAO.getTransaksiHariIni()));
                lblOmzetHariIni.setText(nf.format(dashDAO.getOmzetHariIni()));
            }
            lblAntrian.setText(String.valueOf(dashDAO.getTotalAntrean()));
            lblDalamPengerjaan.setText(String.valueOf(dashDAO.getDalamPengerjaan()));
            lblSelesai.setText(String.valueOf(dashDAO.getServisSelesai()));
            lblStokKritis.setText(String.valueOf(dashDAO.getStokKritis()));

            lblOmzetMinggu.setText("Omzet Minggu: " + nf.format(dashDAO.getOmzetMingguan()));
            lblOmzetBulan.setText("Omzet Bulan: " + nf.format(dashDAO.getOmzetBulanIni()));
            lblOmzetTahun.setText("Omzet Tahun: " + nf.format(dashDAO.getOmzetTahunan()));
            lblMekanikTerajin.setText("Mekanik Terajin: " + dashDAO.getMekanikTerajin());

            tblAntrianModel.setRowCount(0);
            for (Object[] row : dashDAO.getTabelAntrean()) {
                tblAntrianModel.addRow(row);
            }

            tblSparepartModel.setRowCount(0);
            for (Object[] row : dashDAO.getSparepartTerlaris()) {
                tblSparepartModel.addRow(row);
            }

            pnlKinerja.removeAll();
            List<Object[]> kinerja = dashDAO.getKinerjaMekanik();
            int maxServis = 1;
            for (Object[] k : kinerja) {
                maxServis = Math.max(maxServis, (int) k[1]);
            }
            for (Object[] k : kinerja) {
                JLabel lbl = new JLabel(k[0] + ": " + k[1] + " servis");
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                pnlKinerja.add(lbl);
                JProgressBar pb = new JProgressBar(0, maxServis);
                pb.setValue((int) k[1]);
                pb.setStringPainted(true);
                pnlKinerja.add(pb);
            }
            pnlKinerja.revalidate();
            pnlKinerja.repaint();

            updateCharts();
        } catch (SQLException ex) {
            System.err.println("Dashboard error: " + ex.getMessage());
        }
    }

    private void updateCharts() throws SQLException {
        DefaultPieDataset dsTipe = new DefaultPieDataset();
        for (Object[] r : dashDAO.getPerbandinganTipeKendaraan()) {
            dsTipe.setValue(r[0].toString(), (int) r[1]);
        }
        chartPanelTipe.setChart(ChartFactory.createPieChart("Tipe Kendaraan", dsTipe, true, true, false));

        DefaultPieDataset dsKat = new DefaultPieDataset();
        for (Object[] r : dashDAO.getKategoriServis()) {
            dsKat.setValue(r[0].toString(), (int) r[1]);
        }
        chartPanelKategori.setChart(ChartFactory.createPieChart("Kategori Servis", dsKat, true, true, false));

        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        String[] bulanNama = {"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        for (Object[] r : dashDAO.getOmzetPerBulanTahunIni()) {
            int bulan = (int) r[0];
            ds.addValue((double) r[1], "Omzet", bulanNama[bulan]);
        }
        chartPanelOmzetBulanan.setChart(ChartFactory.createBarChart(
            "Omzet Bulanan", "Bulan", "Rupiah", ds, PlotOrientation.VERTICAL, false, true, false));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cardRow = new javax.swing.JPanel();
        cardTransaksi = new javax.swing.JPanel();
        lblCardTransaksi = new javax.swing.JLabel();
        lblTransHariIni = new javax.swing.JLabel();
        cardOmzet = new javax.swing.JPanel();
        lblCardOmzet = new javax.swing.JLabel();
        lblOmzetHariIni = new javax.swing.JLabel();
        cardAntrian = new javax.swing.JPanel();
        lblCardAntrian = new javax.swing.JLabel();
        lblAntrian = new javax.swing.JLabel();
        cardPengerjaan = new javax.swing.JPanel();
        lblCardPengerjaan = new javax.swing.JLabel();
        lblDalamPengerjaan = new javax.swing.JLabel();
        cardSelesai = new javax.swing.JPanel();
        lblCardSelesai = new javax.swing.JLabel();
        lblSelesai = new javax.swing.JLabel();
        cardStokKritis = new javax.swing.JPanel();
        lblCardStokKritis = new javax.swing.JLabel();
        lblStokKritis = new javax.swing.JLabel();
        chartRow = new javax.swing.JPanel();
        pnlChart1 = new javax.swing.JPanel();
        pnlChart2 = new javax.swing.JPanel();
        pnlChart3 = new javax.swing.JPanel();
        bottomRow = new javax.swing.JPanel();
        financePanel = new javax.swing.JPanel();
        lblOmzetMinggu = new javax.swing.JLabel();
        lblOmzetBulan = new javax.swing.JLabel();
        lblOmzetTahun = new javax.swing.JLabel();
        lblMekanikTerajin = new javax.swing.JLabel();
        pnlKinerja = new javax.swing.JPanel();
        antrianWrap = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAntrian = new javax.swing.JTable();
        sparepartWrap = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSparepart = new javax.swing.JTable();

        setBackground(new java.awt.Color(240, 244, 248));
        setLayout(new java.awt.BorderLayout(10, 10));

        cardRow.setOpaque(false);
        cardRow.setLayout(new java.awt.GridLayout(1, 6, 10, 10));

        cardTransaksi.setBackground(new java.awt.Color(52, 152, 219));
        cardTransaksi.setLayout(new java.awt.BorderLayout());

        lblCardTransaksi.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblCardTransaksi.setForeground(new java.awt.Color(255, 255, 255));
        lblCardTransaksi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCardTransaksi.setText("Transaksi Hari Ini");
        cardTransaksi.add(lblCardTransaksi, java.awt.BorderLayout.NORTH);

        lblTransHariIni.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTransHariIni.setForeground(new java.awt.Color(255, 255, 255));
        lblTransHariIni.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTransHariIni.setText("0");
        cardTransaksi.add(lblTransHariIni, java.awt.BorderLayout.CENTER);

        cardRow.add(cardTransaksi);

        cardOmzet.setBackground(new java.awt.Color(46, 204, 113));
        cardOmzet.setLayout(new java.awt.BorderLayout());

        lblCardOmzet.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblCardOmzet.setForeground(new java.awt.Color(255, 255, 255));
        lblCardOmzet.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCardOmzet.setText("Omzet Hari Ini");
        cardOmzet.add(lblCardOmzet, java.awt.BorderLayout.NORTH);

        lblOmzetHariIni.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblOmzetHariIni.setForeground(new java.awt.Color(255, 255, 255));
        lblOmzetHariIni.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOmzetHariIni.setText("Rp 0");
        cardOmzet.add(lblOmzetHariIni, java.awt.BorderLayout.CENTER);

        cardRow.add(cardOmzet);

        cardAntrian.setBackground(new java.awt.Color(241, 196, 15));
        cardAntrian.setLayout(new java.awt.BorderLayout());

        lblCardAntrian.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblCardAntrian.setForeground(new java.awt.Color(255, 255, 255));
        lblCardAntrian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCardAntrian.setText("Antrian");
        cardAntrian.add(lblCardAntrian, java.awt.BorderLayout.NORTH);

        lblAntrian.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblAntrian.setForeground(new java.awt.Color(255, 255, 255));
        lblAntrian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAntrian.setText("0");
        cardAntrian.add(lblAntrian, java.awt.BorderLayout.CENTER);

        cardRow.add(cardAntrian);

        cardPengerjaan.setBackground(new java.awt.Color(230, 126, 34));
        cardPengerjaan.setLayout(new java.awt.BorderLayout());

        lblCardPengerjaan.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblCardPengerjaan.setForeground(new java.awt.Color(255, 255, 255));
        lblCardPengerjaan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCardPengerjaan.setText("Dalam Pengerjaan");
        cardPengerjaan.add(lblCardPengerjaan, java.awt.BorderLayout.NORTH);

        lblDalamPengerjaan.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblDalamPengerjaan.setForeground(new java.awt.Color(255, 255, 255));
        lblDalamPengerjaan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDalamPengerjaan.setText("0");
        cardPengerjaan.add(lblDalamPengerjaan, java.awt.BorderLayout.CENTER);

        cardRow.add(cardPengerjaan);

        cardSelesai.setBackground(new java.awt.Color(155, 89, 182));
        cardSelesai.setLayout(new java.awt.BorderLayout());

        lblCardSelesai.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblCardSelesai.setForeground(new java.awt.Color(255, 255, 255));
        lblCardSelesai.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCardSelesai.setText("Selesai");
        cardSelesai.add(lblCardSelesai, java.awt.BorderLayout.NORTH);

        lblSelesai.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblSelesai.setForeground(new java.awt.Color(255, 255, 255));
        lblSelesai.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSelesai.setText("0");
        cardSelesai.add(lblSelesai, java.awt.BorderLayout.CENTER);

        cardRow.add(cardSelesai);

        cardStokKritis.setBackground(new java.awt.Color(231, 76, 60));
        cardStokKritis.setLayout(new java.awt.BorderLayout());

        lblCardStokKritis.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblCardStokKritis.setForeground(new java.awt.Color(255, 255, 255));
        lblCardStokKritis.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCardStokKritis.setText("Stok Kritis");
        cardStokKritis.add(lblCardStokKritis, java.awt.BorderLayout.NORTH);

        lblStokKritis.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblStokKritis.setForeground(new java.awt.Color(255, 255, 255));
        lblStokKritis.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStokKritis.setText("0");
        cardStokKritis.add(lblStokKritis, java.awt.BorderLayout.CENTER);

        cardRow.add(cardStokKritis);

        add(cardRow, java.awt.BorderLayout.NORTH);

        chartRow.setOpaque(false);
        chartRow.setLayout(new java.awt.GridLayout(1, 3, 10, 10));

        pnlChart1.setPreferredSize(new java.awt.Dimension(300, 250));
        pnlChart1.setLayout(new java.awt.BorderLayout());
        chartRow.add(pnlChart1);

        pnlChart2.setPreferredSize(new java.awt.Dimension(300, 250));
        pnlChart2.setLayout(new java.awt.BorderLayout());
        chartRow.add(pnlChart2);

        pnlChart3.setPreferredSize(new java.awt.Dimension(300, 250));
        pnlChart3.setLayout(new java.awt.BorderLayout());
        chartRow.add(pnlChart3);

        add(chartRow, java.awt.BorderLayout.CENTER);

        bottomRow.setOpaque(false);
        bottomRow.setLayout(new java.awt.GridLayout(1, 4, 10, 10));

        financePanel.setBackground(new java.awt.Color(255, 255, 255));
        financePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Keuangan & Performa"));
        financePanel.setLayout(new javax.swing.BoxLayout(financePanel, javax.swing.BoxLayout.Y_AXIS));

        lblOmzetMinggu.setText("Omzet Minggu: Rp 0");
        financePanel.add(lblOmzetMinggu);

        lblOmzetBulan.setText("Omzet Bulan: Rp 0");
        financePanel.add(lblOmzetBulan);

        lblOmzetTahun.setText("Omzet Tahun: Rp 0");
        financePanel.add(lblOmzetTahun);

        lblMekanikTerajin.setText("Mekanik Terajin: -");
        financePanel.add(lblMekanikTerajin);

        bottomRow.add(financePanel);

        pnlKinerja.setBackground(new java.awt.Color(255, 255, 255));
        pnlKinerja.setBorder(javax.swing.BorderFactory.createTitledBorder("Kinerja Mekanik"));
        pnlKinerja.setLayout(new javax.swing.BoxLayout(pnlKinerja, javax.swing.BoxLayout.Y_AXIS));
        bottomRow.add(pnlKinerja);

        antrianWrap.setBorder(javax.swing.BorderFactory.createTitledBorder("Antrian Hari Ini"));
        antrianWrap.setLayout(new java.awt.BorderLayout());

        tblAntrian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Polisi", "Status"
            }
        ));
        jScrollPane1.setViewportView(tblAntrian);

        antrianWrap.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        bottomRow.add(antrianWrap);

        sparepartWrap.setBorder(javax.swing.BorderFactory.createTitledBorder("Sparepart Terlaris"));
        sparepartWrap.setLayout(new java.awt.BorderLayout());

        tblSparepart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sparepart", "Qty"
            }
        ));
        jScrollPane2.setViewportView(tblSparepart);

        sparepartWrap.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        bottomRow.add(sparepartWrap);

        add(bottomRow, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel antrianWrap;
    private javax.swing.JPanel bottomRow;
    private javax.swing.JPanel cardAntrian;
    private javax.swing.JPanel cardOmzet;
    private javax.swing.JPanel cardPengerjaan;
    private javax.swing.JPanel cardRow;
    private javax.swing.JPanel cardSelesai;
    private javax.swing.JPanel cardStokKritis;
    private javax.swing.JPanel cardTransaksi;
    private javax.swing.JPanel chartRow;
    private javax.swing.JPanel financePanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAntrian;
    private javax.swing.JLabel lblCardAntrian;
    private javax.swing.JLabel lblCardOmzet;
    private javax.swing.JLabel lblCardPengerjaan;
    private javax.swing.JLabel lblCardSelesai;
    private javax.swing.JLabel lblCardStokKritis;
    private javax.swing.JLabel lblCardTransaksi;
    private javax.swing.JLabel lblDalamPengerjaan;
    private javax.swing.JLabel lblMekanikTerajin;
    private javax.swing.JLabel lblOmzetBulan;
    private javax.swing.JLabel lblOmzetHariIni;
    private javax.swing.JLabel lblOmzetMinggu;
    private javax.swing.JLabel lblOmzetTahun;
    private javax.swing.JLabel lblSelesai;
    private javax.swing.JLabel lblStokKritis;
    private javax.swing.JLabel lblTransHariIni;
    private javax.swing.JPanel pnlChart1;
    private javax.swing.JPanel pnlChart2;
    private javax.swing.JPanel pnlChart3;
    private javax.swing.JPanel pnlKinerja;
    private javax.swing.JPanel sparepartWrap;
    private javax.swing.JTable tblAntrian;
    private javax.swing.JTable tblSparepart;
    // End of variables declaration//GEN-END:variables
}
