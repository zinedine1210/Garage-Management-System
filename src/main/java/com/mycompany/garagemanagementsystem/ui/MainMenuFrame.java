package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.DashboardDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

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

    public MainMenuFrame() {
        setTitle("Garage Management System - Executive Dashboard");
        setSize(1100, 700);
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

        btnClient.addActionListener(e -> new ClientFrame((Frame) this).setVisible(true));
        btnVehicle.addActionListener(e -> new VehicleFrame((Frame) this).setVisible(true));
        btnMekanik.addActionListener(e -> new MekanikFrame((Frame) this).setVisible(true));
        btnSparepart.addActionListener(e -> new SparepartFrame((Frame) this).setVisible(true));
        btnSupplier.addActionListener(e -> new SupplierFrame((Frame) this).setVisible(true));
        btnTransaksi.addActionListener(e -> new TransactionListFrame((Frame) this).setVisible(true));
        btnRiwayat.addActionListener(e -> new ServiceHistoryFrame((Frame) this).setVisible(true));
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
        // Refresh digabung di bawah atau direplace dengan listener timer, tapi kita letakkan di fitur menu juga
        
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

        // --- DASHBOARD CONTENT ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(240, 244, 248));

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
        row1.add(createModernCard("Stok Kritis", lblStokKritis, new Color(255, 228, 225), new Color(178, 34, 34)));

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
        
        mainScrollPanel.add(createSectionHeader("2. KEUANGAN & PERFORMA"));
        mainScrollPanel.add(row2);
        mainScrollPanel.add(Box.createVerticalStrut(20));
        
        mainScrollPanel.add(createSectionHeader("3. MONITORING & NOTIFIKASI"));
        mainScrollPanel.add(row3);

        JScrollPane scrollPane = new JScrollPane(mainScrollPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(sidebarWrap, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        loadDashboardData();
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

        } catch (Exception ex) {
            System.err.println("Gagal meload dashboard: " + ex.getMessage());
        }
    }
}
