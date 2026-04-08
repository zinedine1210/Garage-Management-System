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

/**
 * =====================================================================
 * MainMenuFrame - Halaman Utama Aplikasi Garage Management System
 * =====================================================================
 *
 * Ini adalah frame utama setelah login berhasil. Tampilannya:
 *
 *   +-------------------+--------------------------------------------+
 *   | SIDEBAR (KIRI)    |  TABBED PANE (KANAN)                       |
 *   | ================= |  ======================================    |
 *   | Data Client       |  Tab 1: Dashboard (grafik + statistik)     |
 *   | Data Vehicle      |  Tab 2: (dibuka saat klik tombol sidebar)  |
 *   | Data Mekanik      |  Tab 3: (bisa tutup dengan tombol x)       |
 *   | Data Sparepart    |                                            |
 *   | Data Supplier     |  Dashboard berisi:                         |
 *   | Transaksi Servis  |  - 6 kartu statistik (atas)                |
 *   | Riwayat Servis    |  - 3 grafik: PieChart + BarChart (tengah)  |
 *   | Layar Antrian     |  - 4 panel: keuangan, kinerja mekanik,     |
 *   |                   |    antrian hari ini, sparepart terlaris     |
 *   | [Refresh Data]    |                                            |
 *   +-------------------+--------------------------------------------+
 *
 * Cara kerja:
 *   1. Sidebar = tombol navigasi yang membuka panel di tabbedPane
 *   2. Setiap tab bisa ditutup dengan tombol "x"
 *   3. Dashboard dibuat di code (bukan di Design tab) karena
 *      menggunakan library JFreeChart untuk grafik
 *   4. Tombol "Refresh Data" memuat ulang semua data dashboard
 */
public class MainMenuFrame extends javax.swing.JFrame {

    private final DashboardDAO dashDAO = new DashboardDAO();

    // --- Komponen Dashboard: Label untuk kartu statistik ---
    private JLabel lblTransHariIni, lblOmzetHariIni, lblAntrian,
                   lblDalamPengerjaan, lblSelesai, lblStokKritis;
    private JLabel lblOmzetBulan, lblOmzetMinggu, lblOmzetTahun, lblMekanikTerajin;

    // --- Komponen Dashboard: Panel grafik dari JFreeChart ---
    private ChartPanel chartPanelTipe, chartPanelKategori, chartPanelOmzetBulanan;

    // --- Komponen Dashboard: Progress bar kinerja mekanik dan tabel ---
    private JPanel pnlKinerja;
    private DefaultTableModel tblAntrianModel, tblReminderModel, tblSparepartModel, tblStokKritisModel;

    public MainMenuFrame() {
        initComponents();       // 1. Bangun GUI sidebar + tabbedPane (oleh NetBeans)
        styleSidebarButtons();  // 2. Beri warna gelap pada tombol sidebar
        buildDashboardTab();    // 3. Buat tab Dashboard dengan grafik
        loadDashboardData();    // 4. Muat data statistik dari database
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH); // Fullscreen
    }

    /** Beri warna & font seragam pada semua tombol di sidebar. */
    private void styleSidebarButtons() {
        Color bg = new Color(43, 45, 66);   // Warna gelap abu
        Color fg = Color.WHITE;
        Font font = new Font("Segoe UI", Font.PLAIN, 14);

        // Loop semua komponen di sidebarPanel, jika JButton → ubah warnanya
        for (Component c : sidebarPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                b.setBackground(bg);
                b.setForeground(fg);
                b.setFont(font);
                b.setFocusPainted(false);
                b.setBorderPainted(false);
            }
        }

        // Tombol Refresh sedikit berbeda warnanya
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

    /**
     * Buka tab baru di tabbedPane.
     * Jika tab dengan judul yang sama sudah ada, langsung pindah ke tab itu.
     * Setiap tab punya tombol "x" kecil untuk menutupnya.
     *
     * @param title  Judul tab (contoh: "Data Client")
     * @param panel  Panel yang ditampilkan di dalam tab
     */
    private void openTab(String title, Component panel) {
        // Cek apakah tab sudah ada → kalau ada, langsung pindah
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(title)) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }

        // Tambah tab baru
        tabbedPane.addTab(title, panel);
        int idx = tabbedPane.indexOfTab(title);

        // Buat header tab custom: "Judul [x]"
        JPanel tabTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabTitle.setOpaque(false);
        tabTitle.add(new JLabel(title + " "));

        // Tombol close "x"
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

    // =====================================================================
    //   BAGIAN DASHBOARD (dibuat di code, bukan di Design tab)
    //   Karena menggunakan JFreeChart yang tidak bisa di-drag-drop
    // =====================================================================

    /**
     * Bangun tab Dashboard dengan layout:
     *   NORTH  = 6 kartu statistik (baris atas)
     *   CENTER = 3 grafik (PieChart tipe, PieChart kategori, BarChart omzet)
     *   SOUTH  = 4 panel (keuangan, kinerja, antrian, sparepart terlaris)
     */
    private void buildDashboardTab() {
        JPanel dashPanel = new JPanel(new BorderLayout(10, 10));
        dashPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        dashPanel.setBackground(new Color(240, 244, 248));

        // ---- BARIS ATAS: 6 kartu statistik berwarna ----
        JPanel cardRow = new JPanel(new GridLayout(1, 6, 10, 10));
        cardRow.setOpaque(false);

        lblTransHariIni  = new JLabel("0", SwingConstants.CENTER);
        lblOmzetHariIni  = new JLabel("Rp 0", SwingConstants.CENTER);
        lblAntrian       = new JLabel("0", SwingConstants.CENTER);
        lblDalamPengerjaan = new JLabel("0", SwingConstants.CENTER);
        lblSelesai       = new JLabel("0", SwingConstants.CENTER);
        lblStokKritis    = new JLabel("0", SwingConstants.CENTER);

        cardRow.add(createCard("Transaksi Hari Ini", lblTransHariIni,   new Color(52, 152, 219)));  // Biru
        cardRow.add(createCard("Omzet Hari Ini",     lblOmzetHariIni,   new Color(46, 204, 113)));  // Hijau
        cardRow.add(createCard("Antrian",            lblAntrian,        new Color(241, 196, 15)));  // Kuning
        cardRow.add(createCard("Dalam Pengerjaan",   lblDalamPengerjaan,new Color(230, 126, 34)));  // Oranye
        cardRow.add(createCard("Selesai",            lblSelesai,        new Color(155, 89, 182)));  // Ungu
        cardRow.add(createCard("Stok Kritis",        lblStokKritis,     new Color(231, 76, 60)));   // Merah

        dashPanel.add(cardRow, BorderLayout.NORTH);

        // ---- BARIS TENGAH: 3 grafik JFreeChart ----
        JPanel midPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        midPanel.setOpaque(false);

        chartPanelTipe = new ChartPanel(null);
        chartPanelTipe.setPreferredSize(new Dimension(300, 250));

        chartPanelKategori = new ChartPanel(null);
        chartPanelKategori.setPreferredSize(new Dimension(300, 250));

        chartPanelOmzetBulanan = new ChartPanel(null);
        chartPanelOmzetBulanan.setPreferredSize(new Dimension(300, 250));

        midPanel.add(chartPanelTipe);          // PieChart: Motor vs Mobil
        midPanel.add(chartPanelKategori);      // PieChart: Kategori servis
        midPanel.add(chartPanelOmzetBulanan);  // BarChart: Omzet per bulan

        dashPanel.add(midPanel, BorderLayout.CENTER);

        // ---- BARIS BAWAH: 4 panel info ----
        JPanel bottomPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        bottomPanel.setOpaque(false);

        // Panel 1: Ringkasan Keuangan
        JPanel financePanel = new JPanel();
        financePanel.setLayout(new BoxLayout(financePanel, BoxLayout.Y_AXIS));
        financePanel.setBackground(Color.WHITE);
        financePanel.setBorder(BorderFactory.createTitledBorder("Keuangan & Performa"));
        lblOmzetMinggu     = new JLabel("Omzet Minggu: Rp 0");
        lblOmzetBulan      = new JLabel("Omzet Bulan: Rp 0");
        lblOmzetTahun      = new JLabel("Omzet Tahun: Rp 0");
        lblMekanikTerajin  = new JLabel("Mekanik Terajin: -");
        financePanel.add(lblOmzetMinggu);
        financePanel.add(lblOmzetBulan);
        financePanel.add(lblOmzetTahun);
        financePanel.add(lblMekanikTerajin);

        // Panel 2: Kinerja Mekanik (progress bar)
        pnlKinerja = new JPanel();
        pnlKinerja.setLayout(new BoxLayout(pnlKinerja, BoxLayout.Y_AXIS));
        pnlKinerja.setBackground(Color.WHITE);
        pnlKinerja.setBorder(BorderFactory.createTitledBorder("Kinerja Mekanik"));

        // Panel 3: Tabel Antrian Hari Ini
        tblAntrianModel = new DefaultTableModel(new Object[]{"No Polisi", "Status"}, 0);
        JTable tblAntrian = new JTable(tblAntrianModel);
        JPanel antrianWrap = new JPanel(new BorderLayout());
        antrianWrap.setBorder(BorderFactory.createTitledBorder("Antrian Hari Ini"));
        antrianWrap.add(new JScrollPane(tblAntrian), BorderLayout.CENTER);

        // Panel 4: Tabel Sparepart Terlaris
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

        // Tambahkan sebagai tab pertama
        tabbedPane.addTab("Dashboard", dashPanel);
    }

    /**
     * Buat satu kartu statistik berwarna untuk dashboard.
     * Setiap kartu berisi: judul (atas) dan angka besar (tengah).
     */
    private JPanel createCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Judul kartu (font kecil, putih)
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Angka besar (font bold, putih)
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Muat semua data dashboard dari database, lalu tampilkan.
     * Dipanggil saat pertama kali dan saat klik tombol "Refresh Data".
     */
    private void loadDashboardData() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        try {
            // === KARTU STATISTIK ===
            lblTransHariIni.setText(String.valueOf(dashDAO.getTransaksiHariIni()));
            lblOmzetHariIni.setText(nf.format(dashDAO.getOmzetHariIni()));
            lblAntrian.setText(String.valueOf(dashDAO.getTotalAntrean()));
            lblDalamPengerjaan.setText(String.valueOf(dashDAO.getDalamPengerjaan()));
            lblSelesai.setText(String.valueOf(dashDAO.getServisSelesai()));
            lblStokKritis.setText(String.valueOf(dashDAO.getStokKritis()));

            // === LABEL KEUANGAN ===
            lblOmzetMinggu.setText("Omzet Minggu: " + nf.format(dashDAO.getOmzetMingguan()));
            lblOmzetBulan.setText("Omzet Bulan: " + nf.format(dashDAO.getOmzetBulanIni()));
            lblOmzetTahun.setText("Omzet Tahun: " + nf.format(dashDAO.getOmzetTahunan()));
            lblMekanikTerajin.setText("Mekanik Terajin: " + dashDAO.getMekanikTerajin());

            // === TABEL ANTRIAN HARI INI ===
            tblAntrianModel.setRowCount(0);
            for (Object[] row : dashDAO.getTabelAntrean()) {
                tblAntrianModel.addRow(row);
            }

            // === TABEL SPAREPART TERLARIS ===
            tblSparepartModel.setRowCount(0);
            for (Object[] row : dashDAO.getSparepartTerlaris()) {
                tblSparepartModel.addRow(row);
            }

            // === KINERJA MEKANIK (progress bar) ===
            pnlKinerja.removeAll();
            List<Object[]> kinerja = dashDAO.getKinerjaMekanik();

            // Cari jumlah servis tertinggi untuk skala progress bar
            int maxServis = 1;
            for (Object[] k : kinerja) {
                maxServis = Math.max(maxServis, (int) k[1]);
            }

            // Buat label + progress bar untuk setiap mekanik
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

            // === GRAFIK ===
            updateCharts();
        } catch (SQLException ex) {
            System.err.println("Dashboard error: " + ex.getMessage());
        }
    }

    /** Update ketiga grafik dashboard dari data database. */
    private void updateCharts() throws SQLException {
        // Grafik 1: PieChart - Perbandingan Tipe Kendaraan (Motor vs Mobil)
        DefaultPieDataset dsTipe = new DefaultPieDataset();
        for (Object[] r : dashDAO.getPerbandinganTipeKendaraan()) {
            dsTipe.setValue(r[0].toString(), (int) r[1]);
        }
        JFreeChart chartTipe = ChartFactory.createPieChart(
            "Tipe Kendaraan", dsTipe, true, true, false
        );
        chartPanelTipe.setChart(chartTipe);

        // Grafik 2: PieChart - Kategori Servis
        DefaultPieDataset dsKat = new DefaultPieDataset();
        for (Object[] r : dashDAO.getKategoriServis()) {
            dsKat.setValue(r[0].toString(), (int) r[1]);
        }
        JFreeChart chartKat = ChartFactory.createPieChart(
            "Kategori Servis", dsKat, true, true, false
        );
        chartPanelKategori.setChart(chartKat);

        // Grafik 3: BarChart - Omzet Bulanan
        updateRevenueChart();
    }

    /** Buat BarChart omzet per bulan untuk tahun ini. */
    private void updateRevenueChart() throws SQLException {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        // Nama bulan dalam bahasa Indonesia (index 0 tidak dipakai)
        String[] bulanNama = {
            "", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
            "Jul", "Ags", "Sep", "Okt", "Nov", "Des"
        };

        // Ambil data omzet per bulan dari database
        for (Object[] r : dashDAO.getOmzetPerBulanTahunIni()) {
            int bulan = (int) r[0];
            ds.addValue((double) r[1], "Omzet", bulanNama[bulan]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Omzet Bulanan", "Bulan", "Rupiah", ds,
            PlotOrientation.VERTICAL, false, true, false
        );
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
