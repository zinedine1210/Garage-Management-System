package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.DashboardDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class MainMenuFrame extends javax.swing.JFrame {

    private final DashboardDAO dashDAO = new DashboardDAO();

    // Dashboard cards
    private JLabel lblTransHariIni, lblOmzetHariIni, lblAntrian, lblDalamPengerjaan, lblSelesai, lblStokKritis;
    private JLabel lblOmzetBulan, lblOmzetMinggu, lblOmzetTahun, lblMekanikTerajin;

    // Charts
    private ChartPanel chartPanelTipe, chartPanelKategori, chartPanelOmzetBulanan;

    // Progress bars & tables
    private JPanel pnlKinerja;
    private DefaultTableModel tblAntrianModel, tblReminderModel, tblSparepartModel, tblStokKritisModel;

    public MainMenuFrame() {
        initComponents();
        styleSidebarButtons();
        buildDashboardTab();
        loadDashboardData();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    }

    private void styleSidebarButtons() {
        Color bg = new Color(43, 45, 66);
        Color fg = Color.WHITE;
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        for (Component c : sidebarPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                b.setBackground(bg); b.setForeground(fg); b.setFont(font);
                b.setFocusPainted(false); b.setBorderPainted(false);
            }
        }
        btnRefresh.setBackground(new Color(60, 60, 90));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(font);
        btnRefresh.setFocusPainted(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebarWrap = new javax.swing.JPanel();
        lblApp = new javax.swing.JLabel();
        sidebarPanel = new javax.swing.JPanel();
        btnClient = new javax.swing.JButton();
        btnVehicle = new javax.swing.JButton();
        btnMekanik = new javax.swing.JButton();
        btnSparepart = new javax.swing.JButton();
        btnSupplier = new javax.swing.JButton();
        btnTransaksi = new javax.swing.JButton();
        btnRiwayat = new javax.swing.JButton();
        btnAntrian = new javax.swing.JButton();
        bottomSidebar = new javax.swing.JPanel();
        btnRefresh = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Garage Management System - Executive Dashboard");

        sidebarWrap.setBackground(new java.awt.Color(43, 45, 66));
        sidebarWrap.setPreferredSize(new java.awt.Dimension(220, 0));
        sidebarWrap.setLayout(new java.awt.BorderLayout());

        lblApp.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblApp.setForeground(new java.awt.Color(255, 255, 255));
        lblApp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblApp.setText("Garage System");
        sidebarWrap.add(lblApp, java.awt.BorderLayout.NORTH);

        sidebarPanel.setBackground(new java.awt.Color(43, 45, 66));
        sidebarPanel.setLayout(new java.awt.GridLayout(8, 1, 5, 5));

        btnClient.setText("Data Client");
        btnClient.addActionListener(this::btnClientActionPerformed);
        sidebarPanel.add(btnClient);

        btnVehicle.setText("Data Vehicle");
        btnVehicle.addActionListener(this::btnVehicleActionPerformed);
        sidebarPanel.add(btnVehicle);

        btnMekanik.setText("Data Mekanik");
        btnMekanik.addActionListener(this::btnMekanikActionPerformed);
        sidebarPanel.add(btnMekanik);

        btnSparepart.setText("Data Sparepart");
        btnSparepart.addActionListener(this::btnSparepartActionPerformed);
        sidebarPanel.add(btnSparepart);

        btnSupplier.setText("Data Supplier");
        btnSupplier.addActionListener(this::btnSupplierActionPerformed);
        sidebarPanel.add(btnSupplier);

        btnTransaksi.setText("Transaksi Servis");
        btnTransaksi.addActionListener(this::btnTransaksiActionPerformed);
        sidebarPanel.add(btnTransaksi);

        btnRiwayat.setText("Riwayat Servis");
        btnRiwayat.addActionListener(this::btnRiwayatActionPerformed);
        sidebarPanel.add(btnRiwayat);

        btnAntrian.setText("Layar Antrian (TV)");
        btnAntrian.addActionListener(this::btnAntrianActionPerformed);
        sidebarPanel.add(btnAntrian);

        sidebarWrap.add(sidebarPanel, java.awt.BorderLayout.CENTER);

        bottomSidebar.setBackground(new java.awt.Color(43, 45, 66));
        bottomSidebar.setLayout(new java.awt.BorderLayout());

        btnRefresh.setText("Refresh Data");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);
        bottomSidebar.add(btnRefresh, java.awt.BorderLayout.CENTER);

        sidebarWrap.add(bottomSidebar, java.awt.BorderLayout.SOUTH);

        getContentPane().add(sidebarWrap, java.awt.BorderLayout.WEST);

        tabbedPane.setBackground(new java.awt.Color(240, 244, 248));
        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientActionPerformed
        openTab("Data Client", new ClientPanel());
    }//GEN-LAST:event_btnClientActionPerformed

    private void btnVehicleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVehicleActionPerformed
        openTab("Data Vehicle", new VehiclePanel());
    }//GEN-LAST:event_btnVehicleActionPerformed

    private void btnMekanikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMekanikActionPerformed
        openTab("Data Mekanik", new MekanikPanel());
    }//GEN-LAST:event_btnMekanikActionPerformed

    private void btnSparepartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSparepartActionPerformed
        openTab("Data Sparepart", new SparepartPanel());
    }//GEN-LAST:event_btnSparepartActionPerformed

    private void btnSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSupplierActionPerformed
        openTab("Data Supplier", new SupplierPanel());
    }//GEN-LAST:event_btnSupplierActionPerformed

    private void btnTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransaksiActionPerformed
        openTab("Transaksi Servis", new TransactionListPanel(this));
    }//GEN-LAST:event_btnTransaksiActionPerformed

    private void btnRiwayatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatActionPerformed
        openTab("Riwayat Servis", new ServiceHistoryPanel());
    }//GEN-LAST:event_btnRiwayatActionPerformed

    private void btnAntrianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAntrianActionPerformed
        new QueueDashboardFrame().setVisible(true);
    }//GEN-LAST:event_btnAntrianActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadDashboardData();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void openTab(String title, Component panel) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(title)) { tabbedPane.setSelectedIndex(i); return; }
        }
        tabbedPane.addTab(title, panel);
        int idx = tabbedPane.indexOfTab(title);
        JPanel tabTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabTitle.setOpaque(false);
        tabTitle.add(new JLabel(title + " "));
        JButton btnClose = new JButton("x");
        btnClose.setMargin(new java.awt.Insets(0, 4, 0, 4));
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.addActionListener(e -> {
            int i = tabbedPane.indexOfTab(title);
            if (i >= 0) tabbedPane.removeTabAt(i);
        });
        tabTitle.add(btnClose);
        tabbedPane.setTabComponentAt(idx, tabTitle);
        tabbedPane.setSelectedIndex(idx);
    }

    // === DASHBOARD TAB (built in code because JFreeChart can't be in .form) ===

    private void buildDashboardTab() {
        JPanel dashPanel = new JPanel(new BorderLayout(10, 10));
        dashPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        dashPanel.setBackground(new Color(240, 244, 248));

        // Top stats cards
        JPanel cardRow = new JPanel(new GridLayout(1, 6, 10, 10));
        cardRow.setOpaque(false);
        lblTransHariIni = new JLabel("0", SwingConstants.CENTER);
        lblOmzetHariIni = new JLabel("Rp 0", SwingConstants.CENTER);
        lblAntrian = new JLabel("0", SwingConstants.CENTER);
        lblDalamPengerjaan = new JLabel("0", SwingConstants.CENTER);
        lblSelesai = new JLabel("0", SwingConstants.CENTER);
        lblStokKritis = new JLabel("0", SwingConstants.CENTER);
        cardRow.add(createCard("Transaksi Hari Ini", lblTransHariIni, new Color(52, 152, 219)));
        cardRow.add(createCard("Omzet Hari Ini", lblOmzetHariIni, new Color(46, 204, 113)));
        cardRow.add(createCard("Antrian", lblAntrian, new Color(241, 196, 15)));
        cardRow.add(createCard("Dalam Pengerjaan", lblDalamPengerjaan, new Color(230, 126, 34)));
        cardRow.add(createCard("Selesai", lblSelesai, new Color(155, 89, 182)));
        cardRow.add(createCard("Stok Kritis", lblStokKritis, new Color(231, 76, 60)));
        dashPanel.add(cardRow, BorderLayout.NORTH);

        // Middle: charts
        JPanel midPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        midPanel.setOpaque(false);
        chartPanelTipe = new ChartPanel(null); chartPanelTipe.setPreferredSize(new Dimension(300, 250));
        chartPanelKategori = new ChartPanel(null); chartPanelKategori.setPreferredSize(new Dimension(300, 250));
        chartPanelOmzetBulanan = new ChartPanel(null); chartPanelOmzetBulanan.setPreferredSize(new Dimension(300, 250));
        midPanel.add(chartPanelTipe);
        midPanel.add(chartPanelKategori);
        midPanel.add(chartPanelOmzetBulanan);
        dashPanel.add(midPanel, BorderLayout.CENTER);

        // Bottom: Finance + tables
        JPanel bottomPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        bottomPanel.setOpaque(false);

        // Finance card
        JPanel financePanel = new JPanel();
        financePanel.setLayout(new BoxLayout(financePanel, BoxLayout.Y_AXIS));
        financePanel.setBackground(Color.WHITE);
        financePanel.setBorder(BorderFactory.createTitledBorder("Keuangan & Performa"));
        lblOmzetMinggu = new JLabel("Omzet Minggu: Rp 0");
        lblOmzetBulan = new JLabel("Omzet Bulan: Rp 0");
        lblOmzetTahun = new JLabel("Omzet Tahun: Rp 0");
        lblMekanikTerajin = new JLabel("Mekanik Terajin: -");
        financePanel.add(lblOmzetMinggu); financePanel.add(lblOmzetBulan);
        financePanel.add(lblOmzetTahun); financePanel.add(lblMekanikTerajin);

        // Kinerja Mekanik
        pnlKinerja = new JPanel();
        pnlKinerja.setLayout(new BoxLayout(pnlKinerja, BoxLayout.Y_AXIS));
        pnlKinerja.setBackground(Color.WHITE);
        pnlKinerja.setBorder(BorderFactory.createTitledBorder("Kinerja Mekanik"));

        // Antrian table
        tblAntrianModel = new DefaultTableModel(new Object[]{"No Polisi", "Status"}, 0);
        JTable tblAntrian = new JTable(tblAntrianModel);
        JPanel antrianWrap = new JPanel(new BorderLayout());
        antrianWrap.setBorder(BorderFactory.createTitledBorder("Antrian Hari Ini"));
        antrianWrap.add(new JScrollPane(tblAntrian), BorderLayout.CENTER);

        // Sparepart terlaris table
        tblSparepartModel = new DefaultTableModel(new Object[]{"Sparepart", "Qty"}, 0);
        JTable tblSparepart = new JTable(tblSparepartModel);
        JPanel spWrap = new JPanel(new BorderLayout());
        spWrap.setBorder(BorderFactory.createTitledBorder("Sparepart Terlaris"));
        spWrap.add(new JScrollPane(tblSparepart), BorderLayout.CENTER);

        bottomPanel.add(financePanel);
        bottomPanel.add(pnlKinerja);
        bottomPanel.add(antrianWrap);
        bottomPanel.add(spWrap);
        dashPanel.add(bottomPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Dashboard", dashPanel);
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void loadDashboardData() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        try {
            lblTransHariIni.setText(String.valueOf(dashDAO.getTransaksiHariIni()));
            lblOmzetHariIni.setText(nf.format(dashDAO.getOmzetHariIni()));
            lblAntrian.setText(String.valueOf(dashDAO.getTotalAntrean()));
            lblDalamPengerjaan.setText(String.valueOf(dashDAO.getDalamPengerjaan()));
            lblSelesai.setText(String.valueOf(dashDAO.getServisSelesai()));
            lblStokKritis.setText(String.valueOf(dashDAO.getStokKritis()));

            lblOmzetMinggu.setText("Omzet Minggu: " + nf.format(dashDAO.getOmzetMingguan()));
            lblOmzetBulan.setText("Omzet Bulan: " + nf.format(dashDAO.getOmzetBulanIni()));
            lblOmzetTahun.setText("Omzet Tahun: " + nf.format(dashDAO.getOmzetTahunan()));
            lblMekanikTerajin.setText("Mekanik Terajin: " + dashDAO.getMekanikTerajin());

            // Antrian table
            tblAntrianModel.setRowCount(0);
            for (Object[] row : dashDAO.getTabelAntrean()) tblAntrianModel.addRow(row);

            // Sparepart terlaris table
            tblSparepartModel.setRowCount(0);
            for (Object[] row : dashDAO.getSparepartTerlaris()) tblSparepartModel.addRow(row);

            // Kinerja Mekanik progress bars
            pnlKinerja.removeAll();
            List<Object[]> kinerja = dashDAO.getKinerjaMekanik();
            int maxServis = 1;
            for (Object[] k : kinerja) maxServis = Math.max(maxServis, (int) k[1]);
            for (Object[] k : kinerja) {
                JLabel lbl = new JLabel(k[0] + ": " + k[1] + " servis");
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                pnlKinerja.add(lbl);
                JProgressBar pb = new JProgressBar(0, maxServis);
                pb.setValue((int) k[1]);
                pb.setStringPainted(true);
                pnlKinerja.add(pb);
            }
            pnlKinerja.revalidate(); pnlKinerja.repaint();

            updateCharts();
        } catch (SQLException ex) {
            System.err.println("Dashboard error: " + ex.getMessage());
        }
    }

    private void updateCharts() throws SQLException {
        // Pie: Tipe Kendaraan
        DefaultPieDataset dsTipe = new DefaultPieDataset();
        for (Object[] r : dashDAO.getPerbandinganTipeKendaraan()) dsTipe.setValue(r[0].toString(), (int) r[1]);
        JFreeChart chartTipe = ChartFactory.createPieChart("Tipe Kendaraan", dsTipe, true, true, false);
        chartPanelTipe.setChart(chartTipe);

        // Pie: Kategori Servis
        DefaultPieDataset dsKat = new DefaultPieDataset();
        for (Object[] r : dashDAO.getKategoriServis()) dsKat.setValue(r[0].toString(), (int) r[1]);
        JFreeChart chartKat = ChartFactory.createPieChart("Kategori Servis", dsKat, true, true, false);
        chartPanelKategori.setChart(chartKat);

        // Bar: Omzet Bulanan
        updateRevenueChart();
    }

    private void updateRevenueChart() throws SQLException {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        String[] bulanNama = {"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        for (Object[] r : dashDAO.getOmzetPerBulanTahunIni()) {
            int bulan = (int) r[0];
            ds.addValue((double) r[1], "Omzet", bulanNama[bulan]);
        }
        JFreeChart chart = ChartFactory.createBarChart("Omzet Bulanan", "Bulan", "Rupiah", ds, PlotOrientation.VERTICAL, false, true, false);
        chartPanelOmzetBulanan.setChart(chart);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomSidebar;
    private javax.swing.JButton btnAntrian;
    private javax.swing.JButton btnClient;
    private javax.swing.JButton btnMekanik;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRiwayat;
    private javax.swing.JButton btnSparepart;
    private javax.swing.JButton btnSupplier;
    private javax.swing.JButton btnTransaksi;
    private javax.swing.JButton btnVehicle;
    private javax.swing.JLabel lblApp;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JPanel sidebarWrap;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
