package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.DashboardDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;


public class MainMenuFrame extends JFrame {

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    
    // Row 1: Statistik Utama
    private final JLabel lblAntrean = new JLabel("0");
    private final JLabel lblPengerjaan = new JLabel("0");
    private final JLabel lblSelesai = new JLabel("0");
    private final JLabel lblStokKritis = new JLabel("0");

    // Row 2: Keuangan & Performa
    private final JLabel lblOmzet = new JLabel("Rp 0");
    private final JProgressBar progressTarget = new JProgressBar(0, 100);
    private final JLabel lblTargetTeks = new JLabel("0% dari Rp50 Jt");
    private final JLabel lblMekanik = new JLabel("-");

    // Row 3: Monitoring & Notifikasi (Tabel)
    private final DefaultTableModel modelAntrean = new DefaultTableModel(new Object[]{"Nopol", "Status"}, 0);
    private final DefaultTableModel modelReminder = new DefaultTableModel(new Object[]{"Pelanggan", "Nopol", "Info"}, 0);
    private final DefaultTableModel modelSparepart = new DefaultTableModel(new Object[]{"Item", "Terjual"}, 0);

    // New Table Model for Top 3 Critical Stock
    private final DefaultTableModel modelTopKritis = new DefaultTableModel(new Object[]{"Sparepart", "Stok"}, 0);
    
    // Chart Panels
    private ChartPanel mechanicChartPanel;
    private ChartPanel revenueChartPanel;
    private ChartPanel vehicleTypeChartPanel;
    private ChartPanel serviceCategoryChartPanel;

    private JTabbedPane tabbedPane;

    public MainMenuFrame() {
        setTitle("Garage Management System - Executive Dashboard");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // --- SIDEBAR MENU ---
        JPanel sidebarPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.setBackground(new Color(43, 45, 66));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));

        JButton btnClient = createMenuButton("Data Client");
        JButton btnVehicle = createMenuButton("Data Vehicle");
        JButton btnMekanik = createMenuButton("Data Mekanik");
        JButton btnSparepart = createMenuButton("Data Sparepart");
        JButton btnSupplier = createMenuButton("Data Supplier");
        JButton btnTransaksi = createMenuButton("Transaksi Servis");
        JButton btnRiwayat = createMenuButton("Riwayat Servis");
        JButton btnAntrian = createMenuButton("Layar Antrian (TV)");
        JButton btnRefresh = createMenuButton("Refresh Data");

        btnClient.addActionListener(e -> openTab("Data Client", new ClientPanel()));
        btnVehicle.addActionListener(e -> openTab("Data Vehicle", new VehiclePanel()));
        btnMekanik.addActionListener(e -> openTab("Data Mekanik", new MekanikPanel()));
        btnSparepart.addActionListener(e -> openTab("Data Sparepart", new SparepartPanel()));
        btnSupplier.addActionListener(e -> openTab("Data Supplier", new SupplierPanel()));
        btnTransaksi.addActionListener(e -> openTab("Transaksi Servis", new TransactionListPanel((Frame) this)));
        btnRiwayat.addActionListener(e -> openTab("Riwayat Servis", new ServiceHistoryPanel()));
        btnAntrian.addActionListener(e -> new QueueDashboardFrame().setVisible(true));
        btnRefresh.addActionListener(e -> loadDashboardData());

        sidebarPanel.add(btnClient);
        sidebarPanel.add(btnVehicle);
        sidebarPanel.add(btnMekanik);
        sidebarPanel.add(btnSparepart);
        sidebarPanel.add(btnSupplier);
        sidebarPanel.add(btnTransaksi);
        sidebarPanel.add(btnRiwayat); 
        sidebarPanel.add(btnAntrian); 
        
        JPanel sidebarWrap = new JPanel(new BorderLayout());
        sidebarWrap.setBackground(new Color(43, 45, 66));
        
        JLabel lblApp = new JLabel("BengkelPro", SwingConstants.CENTER);
        lblApp.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblApp.setForeground(Color.WHITE);
        lblApp.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        sidebarWrap.add(lblApp, BorderLayout.NORTH);
        sidebarWrap.add(sidebarPanel, BorderLayout.CENTER);
        
        JPanel bottomSidebar = new JPanel(new BorderLayout());
        bottomSidebar.setBackground(new Color(43, 45, 66));
        bottomSidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomSidebar.add(btnRefresh, BorderLayout.CENTER);
        sidebarWrap.add(bottomSidebar, BorderLayout.SOUTH);

        // --- MAIN WORKSPACE (JTabbedPane) ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(240, 244, 248));
        
        // --- DASHBOARD CONTENT ---
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(new Color(240, 244, 248));

        JLabel titleLabel = new JLabel("Executive Dashboard Overview", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JPanel mainScrollPanel = new JPanel();
        mainScrollPanel.setLayout(new BoxLayout(mainScrollPanel, BoxLayout.Y_AXIS));
        mainScrollPanel.setBackground(new Color(240, 244, 248));
        mainScrollPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // ROW 1: STATISTIK UTAMA (4 Cards)
        JPanel row1 = new JPanel(new GridLayout(1, 4, 15, 15));
        row1.setBackground(new Color(240, 244, 248));
        row1.add(createModernCard("Total Antrean", lblAntrean, new Color(255, 235, 205), new Color(210, 105, 30)));
        row1.add(createModernCard("Dalam Pengerjaan", lblPengerjaan, new Color(224, 255, 255), new Color(0, 139, 139)));
        row1.add(createModernCard("Servis Selesai", lblSelesai, new Color(240, 255, 240), new Color(34, 139, 34)));
        
        // Stok kritis Panel custom text label on top, table below
        JPanel stokKritisContainer = new JPanel(new BorderLayout());
        stokKritisContainer.setOpaque(false);
        stokKritisContainer.add(lblStokKritis, BorderLayout.NORTH);
        
        JTable tableTopKritis = new JTable(modelTopKritis);
        tableTopKritis.setRowHeight(20);
        tableTopKritis.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 10));
        tableTopKritis.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        tableTopKritis.setEnabled(false);
        JScrollPane spTopKritis = new JScrollPane(tableTopKritis);
        spTopKritis.setPreferredSize(new Dimension(0, 60));
        stokKritisContainer.add(spTopKritis, BorderLayout.CENTER);
        
        row1.add(createModernCard("Stok Kritis", stokKritisContainer, new Color(255, 228, 225), new Color(178, 34, 34)));

        // CHARTS ROW (New Row 1.5)
        JPanel chartRow = new JPanel(new GridLayout(1, 4, 15, 15));
        chartRow.setBackground(new Color(240, 244, 248));
        
        mechanicChartPanel = new ChartPanel(null);
        mechanicChartPanel.setPreferredSize(new Dimension(300, 250));
        
        // Revenue Chart with Filter
        JPanel revenueContainer = new JPanel(new BorderLayout());
        revenueContainer.setBackground(new Color(240, 244, 248));
        
        String[] revenueFilters = {"Pilih Filter", "Bulanan (Tahun Ini)", "Tahunan"};
        javax.swing.JComboBox<String> cbRevenueFilter = new javax.swing.JComboBox<>(revenueFilters);
        cbRevenueFilter.addActionListener(e -> {
            try {
                updateRevenueChart(cbRevenueFilter.getSelectedItem().toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        revenueChartPanel = new ChartPanel(null);
        revenueChartPanel.setPreferredSize(new Dimension(300, 220));
        
        revenueContainer.add(cbRevenueFilter, BorderLayout.NORTH);
        revenueContainer.add(revenueChartPanel, BorderLayout.CENTER);
        
        vehicleTypeChartPanel = new ChartPanel(null);
        vehicleTypeChartPanel.setPreferredSize(new Dimension(300, 250));
        
        serviceCategoryChartPanel = new ChartPanel(null);
        serviceCategoryChartPanel.setPreferredSize(new Dimension(300, 250));

        chartRow.add(mechanicChartPanel);
        chartRow.add(revenueContainer);
        chartRow.add(vehicleTypeChartPanel);
        chartRow.add(serviceCategoryChartPanel);

        // ROW 2: KEUANGAN & PERFORMA (3 Cards)
        JPanel row2 = new JPanel(new GridLayout(1, 3, 15, 15));
        row2.setBackground(new Color(240, 244, 248));
        
        // Setup Progress Target Panel
        JPanel targetPanel = new JPanel(new BorderLayout(0, 10));
        targetPanel.setOpaque(false);
        progressTarget.setStringPainted(true);
        progressTarget.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressTarget.setForeground(new Color(70, 130, 180));
        lblTargetTeks.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        targetPanel.add(progressTarget, BorderLayout.CENTER);
        targetPanel.add(lblTargetTeks, BorderLayout.SOUTH);

        row2.add(createModernCard("Omzet Hari Ini", lblOmzet, Color.WHITE, new Color(46, 139, 87)));
        row2.add(createModernCard("Pencapaian Target Bulanan", targetPanel, Color.WHITE, new Color(70, 130, 180)));
        row2.add(createModernCard("Mekanik Terajin (Bulan Ini)", lblMekanik, Color.WHITE, new Color(148, 0, 211)));

        // ROW 3: MONITORING & NOTIFIKASI TABEL (3 Panels)
        JPanel row3 = new JPanel(new GridLayout(1, 3, 15, 15));
        row3.setBackground(new Color(240, 244, 248));
        row3.add(createTablePanel("Tabel Antrean Hari Ini", modelAntrean));
        row3.add(createTablePanel("Reminder Servis Berkala", modelReminder));
        row3.add(createTablePanel("Sparepart Terlaris (Bulanan)", modelSparepart));

        // Add to Scroll Panel with styling
        mainScrollPanel.add(createSectionHeader("1. STATISTIK OPERASIONAL HARI INI"));
        mainScrollPanel.add(row1);
        mainScrollPanel.add(Box.createVerticalStrut(20));
        
        mainScrollPanel.add(createSectionHeader("1.5. VISUALISASI DATA"));
        mainScrollPanel.add(chartRow);
        mainScrollPanel.add(Box.createVerticalStrut(20));
        
        mainScrollPanel.add(createSectionHeader("2. KEUANGAN & PERFORMA"));
        mainScrollPanel.add(row2);
        mainScrollPanel.add(Box.createVerticalStrut(20));
        
        mainScrollPanel.add(createSectionHeader("3. MONITORING & NOTIFIKASI"));
        mainScrollPanel.add(row3);

        JScrollPane scrollPane = new JScrollPane(mainScrollPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Assemble Dashboard Panel
        dashboardPanel.add(titleLabel, BorderLayout.NORTH);
        dashboardPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Set TabbedPane Configuration
        tabbedPane.addTab("Dashboard", dashboardPanel);
        add(sidebarWrap, BorderLayout.WEST);
        add(tabbedPane, BorderLayout.CENTER);
        
        loadDashboardData();
    }
    
    private void openTab(String title, JPanel panel) {
        // Cek apakah tab sudah terbuka
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(title)) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }
        
        // Tambah tab baru
        tabbedPane.addTab(title, panel);
        int index = tabbedPane.getTabCount() - 1;
        tabbedPane.setSelectedIndex(index);
        
        // Custom Tab Header
        JPanel tabHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabHeader.setOpaque(false);
        JLabel lblTitle = new JLabel(title + "  ");
        JButton btnClose = new JButton("x");
        btnClose.setMargin(new java.awt.Insets(0, 2, 0, 2));
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setFont(new Font("Arial", Font.BOLD, 12));
        btnClose.setForeground(Color.RED);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnClose.addActionListener(e -> {
            int closeIndex = tabbedPane.indexOfTabComponent(tabHeader);
            if(closeIndex != -1) {
                tabbedPane.remove(closeIndex);
            }
        });
        
        tabHeader.add(lblTitle);
        tabHeader.add(btnClose);
        tabbedPane.setTabComponentAt(index, tabHeader);
    }
    
    private JLabel createSectionHeader(String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(105, 105, 105));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return lbl;
    }
    
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(60, 63, 88));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(43, 45, 66), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return btn;
    }

    private JPanel createModernCard(String title, Component valueComp, Color bgColor, Color fgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 224, 232), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(fgColor != null ? fgColor : Color.DARK_GRAY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        if (valueComp instanceof JLabel) {
            ((JLabel) valueComp).setFont(new Font("Segoe UI", Font.BOLD, 28));
            ((JLabel) valueComp).setForeground(fgColor != null ? fgColor : Color.BLACK);
            ((JLabel) valueComp).setHorizontalAlignment(SwingConstants.LEFT);
        }

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(valueComp, BorderLayout.CENTER);

        // Fixed height for cards
        panel.setPreferredSize(new Dimension(0, 120));
        panel.setMinimumSize(new Dimension(0, 120));
        return panel;
    }

    private JPanel createTablePanel(String title, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 224, 232), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setEnabled(false); // Read only
        table.setFillsViewportHeight(true);
        
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        
        // Fixed height for table panels
        panel.setPreferredSize(new Dimension(0, 180));
        panel.setMinimumSize(new Dimension(0, 180));
        return panel;
    }

    private void loadDashboardData() {
        try {
            // Row 1
            lblAntrean.setText(String.valueOf(dashboardDAO.getTotalAntrean()));
            lblPengerjaan.setText(String.valueOf(dashboardDAO.getDalamPengerjaan()));
            lblSelesai.setText(String.valueOf(dashboardDAO.getServisSelesai()));
            lblStokKritis.setText(String.valueOf(dashboardDAO.getStokKritis()));
            
            modelTopKritis.setRowCount(0);
            List<Object[]> topKritis = dashboardDAO.getTop3StokKritis();
            for (Object[] row : topKritis) modelTopKritis.addRow(row);

            // Row 2
            lblOmzet.setText(String.format("Rp %,.0f", dashboardDAO.getOmzetHariIni()));
            lblMekanik.setText(dashboardDAO.getMekanikTerajin());
            
            // Progress Target Bulanan (Target Semu = Rp 50 Juta)
            double targetSum = 50000000.0;
            double currentBulan = dashboardDAO.getOmzetBulanIni();
            int persentase = (int) ((currentBulan / targetSum) * 100);
            if (persentase > 100) persentase = 100;
            progressTarget.setValue(persentase);
            lblTargetTeks.setText(String.format("%d%% (Terkumpul Rp %,.0f dari Rp 50 Jt)", persentase, currentBulan));

            // Row 3
            modelAntrean.setRowCount(0);
            List<Object[]> antrean = dashboardDAO.getTabelAntrean();
            for (Object[] row : antrean) modelAntrean.addRow(row);

            modelReminder.setRowCount(0);
            List<Object[]> reminders = dashboardDAO.getReminderServis();
            for (Object[] row : reminders) modelReminder.addRow(row);

            modelSparepart.setRowCount(0);
            List<Object[]> terlaris = dashboardDAO.getSparepartTerlaris();
            for (Object[] row : terlaris) modelSparepart.addRow(row);

            // Update Charts
            updateCharts();

        } catch (Exception ex) {
            System.err.println("Gagal meload dashboard: " + ex.getMessage());
        }
    }

    private void updateCharts() throws Exception {
        // 1. Mechanic Chart (Bar)
        DefaultCategoryDataset mechanicDataset = new DefaultCategoryDataset();
        List<Object[]> mekanikData = dashboardDAO.getKinerjaMekanik();
        for (Object[] row : mekanikData) {
            mechanicDataset.addValue((Number) row[1], "Servis", (Comparable) row[0]);
        }
        JFreeChart mechanicChart = ChartFactory.createBarChart("Kinerja Mekanik", "Mekanik", "Jumlah Servis", mechanicDataset, PlotOrientation.VERTICAL, false, true, false);
        mechanicChartPanel.setChart(mechanicChart);

        // 2. Revenue Chart (Bar)
        updateRevenueChart("Pilih Filter"); // Default view

        // 3. Vehicle Type Chart (Pie)
        DefaultPieDataset vehicleDataset = new DefaultPieDataset();
        List<Object[]> vehicleData = dashboardDAO.getPerbandinganTipeKendaraan();
        for (Object[] row : vehicleData) {
            vehicleDataset.setValue((Comparable) row[0], (Number) row[1]);
        }
        JFreeChart vehicleChart = ChartFactory.createPieChart("Tipe Kendaraan", vehicleDataset, true, true, false);
        vehicleTypeChartPanel.setChart(vehicleChart);

        // 4. Service Category Chart (Pie)
        DefaultPieDataset categoryDataset = new DefaultPieDataset();
        List<Object[]> categoryData = dashboardDAO.getKategoriServis();
        for (Object[] row : categoryData) {
            categoryDataset.setValue((Comparable) row[0], (Number) row[1]);
        }
        JFreeChart categoryChart = ChartFactory.createPieChart("Kategori Servis", categoryDataset, true, true, false);
        serviceCategoryChartPanel.setChart(categoryChart);
    }
    
    private void updateRevenueChart(String filter) throws java.sql.SQLException {
        DefaultCategoryDataset revenueDataset = new DefaultCategoryDataset();
        String title = "Perbandingan Tren Pendapatan";
        String xAxis = "Periode";
        
        if ("Bulanan (Tahun Ini)".equals(filter)) {
            title = "Pendapatan Tiap Bulan (Tahun Ini)";
            xAxis = "Bulan";
            List<Object[]> data = dashboardDAO.getOmzetPerBulanTahunIni();
            String[] namaBulan = {"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
            for (Object[] row : data) {
                int bulanVal = (Integer) row[0];
                String bulanStr = (bulanVal >= 1 && bulanVal <= 12) ? namaBulan[bulanVal] : String.valueOf(bulanVal);
                revenueDataset.addValue((Number) row[1], "Omzet", bulanStr);
            }
        } else if ("Tahunan".equals(filter)) {
            title = "Pendapatan per Tahun";
            xAxis = "Tahun";
            List<Object[]> data = dashboardDAO.getOmzetPerTahun();
            for (Object[] row : data) {
                 revenueDataset.addValue((Number) row[1], "Omzet", (Comparable) row[0]);
            }
        } else {
            // Default: Show current week, month, year as before
            title = "Tren Pendapatan Terkini";
            revenueDataset.addValue(dashboardDAO.getOmzetMingguan(), "Omzet", "Minggu Ini");
            revenueDataset.addValue(dashboardDAO.getOmzetBulanIni(), "Omzet", "Bulan Ini");
            revenueDataset.addValue(dashboardDAO.getOmzetTahunan(), "Omzet", "Tahun Ini");
        }
        
        JFreeChart revenueChart = ChartFactory.createBarChart(title, xAxis, "Rupiah", revenueDataset, PlotOrientation.VERTICAL, false, true, false);
        revenueChartPanel.setChart(revenueChart);
    }
}
