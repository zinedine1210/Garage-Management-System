package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.model.User;
import com.mycompany.garagemanagementsystem.util.AppConfig;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainMenuFrame extends javax.swing.JFrame {

    private DashboardPanel dashboardPanel;
    private JPanel contentArea;
    private String currentPage = "Dashboard";
    private final User loggedInUser;

    // Colors
    private static final Color SIDEBAR_BG = new Color(24, 26, 42);
    private static final Color SIDEBAR_HEADER_BG = new Color(18, 20, 34);
    private static final Color SIDEBAR_BTN_HOVER = new Color(40, 44, 68);
    private static final Color SIDEBAR_BTN_ACTIVE = new Color(65, 105, 225);
    private static final Color SIDEBAR_TEXT = new Color(180, 185, 200);
    private static final Color SIDEBAR_TEXT_HOVER = new Color(255, 255, 255);
    private static final Color SECTION_TEXT = new Color(100, 110, 145);
    private static final Color APPBAR_BG = new Color(255, 255, 255);
    private static final Color APPBAR_BORDER = new Color(230, 232, 240);

    // Fonts
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    // Track active button for highlight
    private JButton activeMenuButton = null;

    public MainMenuFrame(User user) {
        this.loggedInUser = user;
        initUI();
        dashboardPanel = new DashboardPanel();
        showContent("Dashboard", dashboardPanel);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    }

    public MainMenuFrame() {
        this(null);
    }

    private String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 11) return "Selamat Pagi";
        if (hour < 15) return "Selamat Siang";
        if (hour < 18) return "Selamat Sore";
        return "Selamat Malam";
    }

    private void initUI() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(AppConfig.getAppName());
        setLayout(new BorderLayout());

        // ========== SIDEBAR ==========
        JPanel sidebarWrap = new JPanel(new BorderLayout());
        sidebarWrap.setBackground(SIDEBAR_BG);
        sidebarWrap.setPreferredSize(new Dimension(250, 0));

        // --- Header: Logo + App Name ---
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(SIDEBAR_HEADER_BG);
        headerPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        ImageIcon logoIcon = AppConfig.loadLogo(36, 36);
        if (logoIcon != null) {
            JLabel lblLogo = new JLabel(logoIcon);
            headerPanel.add(lblLogo, BorderLayout.WEST);
        }
        JLabel lblApp = new JLabel("<html>" + AppConfig.getAppName() + "</html>");
        lblApp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblApp.setForeground(Color.WHITE);
        headerPanel.add(lblApp, BorderLayout.CENTER);

        sidebarWrap.add(headerPanel, BorderLayout.NORTH);

        // --- Menu ---
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(SIDEBAR_BG);
        menuPanel.setBorder(new EmptyBorder(8, 0, 8, 0));

        addSection(menuPanel, "DASHBOARD");
        menuPanel.add(createMenuButton("\uD83D\uDCCA", "Dashboard", e -> {
            if (dashboardPanel != null) dashboardPanel.loadData();
            showContent("Dashboard", dashboardPanel);
        }));

        addSection(menuPanel, "MASTER DATA");
        menuPanel.add(createMenuButton("\uD83D\uDC64", "Data Client", e -> showContent("Data Client", new ClientPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDE97", "Data Kendaraan", e -> showContent("Data Kendaraan", new VehiclePanel())));
        menuPanel.add(createMenuButton("\uD83D\uDD27", "Data Mekanik", e -> showContent("Data Mekanik", new MekanikPanel())));
        menuPanel.add(createMenuButton("\u2699\uFE0F", "Data Sparepart", e -> showContent("Data Sparepart", new SparepartPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDCE6", "Data Supplier", e -> showContent("Data Supplier", new SupplierPanel())));

        addSection(menuPanel, "TRANSAKSI");
        menuPanel.add(createMenuButton("\uD83D\uDCCB", "Pendaftaran Servis", e -> showContent("Pendaftaran Servis", new ServiceRegistrationPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDEE0\uFE0F", "Transaksi Servis", e -> showContent("Transaksi Servis", new TransactionListPanel(this))));
        menuPanel.add(createMenuButton("\uD83D\uDED2", "Pembelian Sparepart", e -> showContent("Pembelian Sparepart", new SparepartPurchasePanel())));
        menuPanel.add(createMenuButton("\uD83D\uDCB3", "Penjualan Sparepart", e -> {
            ServiceRegistrationPanel srp = new ServiceRegistrationPanel();
            showContent("Penjualan Sparepart", srp);
            srp.openSparepartPurchaseWizard();
        }));

        addSection(menuPanel, "LAPORAN");
        menuPanel.add(createMenuButton("\uD83D\uDCC4", "Lap. Transaksi", e -> showContent("Lap. Transaksi Servis", new LaporanTransaksiPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDCE5", "Lap. Pembelian", e -> showContent("Lap. Pembelian", new LaporanPembelianPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDCC8", "Lap. Omzet", e -> showContent("Lap. Omzet & Pendapatan", new LaporanOmzetPanel())));
        menuPanel.add(createMenuButton("\uD83C\uDFC6", "Lap. Kinerja Mekanik", e -> showContent("Lap. Kinerja Mekanik", new LaporanKinerjaMekanikPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDD25", "Lap. Sparepart Terlaris", e -> showContent("Lap. Sparepart Terlaris", new LaporanSparepartTerlarisPanel())));

        addSection(menuPanel, "LAINNYA");
        menuPanel.add(createMenuButton("\uD83D\uDCDC", "Riwayat Servis", e -> showContent("Riwayat Servis", new ServiceHistoryPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDCFA", "Layar Antrian (TV)", e -> new QueueDashboardFrame().setVisible(true)));

        JScrollPane menuScroll = new JScrollPane(menuPanel);
        menuScroll.setBorder(null);
        menuScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.getVerticalScrollBar().setUnitIncrement(16);
        menuScroll.getViewport().setBackground(SIDEBAR_BG);
        sidebarWrap.add(menuScroll, BorderLayout.CENTER);

        add(sidebarWrap, BorderLayout.WEST);

        // ========== RIGHT SIDE ==========
        JPanel rightPanel = new JPanel(new BorderLayout());

        // ========== APPBAR ==========
        String userName = loggedInUser != null ? loggedInUser.getNamaLengkap() : "Administrator";

        JPanel appBar = new JPanel(new BorderLayout());
        appBar.setBackground(APPBAR_BG);
        appBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, APPBAR_BORDER),
                new EmptyBorder(0, 24, 0, 24)));
        appBar.setPreferredSize(new Dimension(0, 56));

        // Left: greeting
        JLabel lblGreeting = new JLabel("\uD83D\uDC4B " + getGreeting() + ", " + userName + "!");
        lblGreeting.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblGreeting.setForeground(new Color(60, 65, 80));
        appBar.add(lblGreeting, BorderLayout.WEST);

        // Center: live clock
        JLabel lblClock = new JLabel();
        lblClock.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblClock.setForeground(new Color(100, 105, 120));
        lblClock.setHorizontalAlignment(SwingConstants.CENTER);
        Runnable updateClock = () -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy  |  HH:mm:ss");
            lblClock.setText(sdf.format(new Date()));
        };
        updateClock.run();
        Timer clockTimer = new Timer(1000, e -> updateClock.run());
        clockTimer.start();
        appBar.add(lblClock, BorderLayout.CENTER);

        // Right: actions
        JPanel appBarRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        appBarRight.setOpaque(false);

        JButton btnRefreshDash = new JButton("Refresh");
        styleAppBarButton(btnRefreshDash, false);
        btnRefreshDash.addActionListener(e -> {
            if (dashboardPanel != null && "Dashboard".equals(currentPage)) {
                dashboardPanel.loadData();
            }
        });
        appBarRight.add(btnRefreshDash);

        JButton btnLogout = new JButton("Logout");
        styleAppBarButton(btnLogout, true);
        btnLogout.addActionListener(e -> {
            if (UIHelper.confirm(this, "Yakin ingin logout?", "Konfirmasi Logout")) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        appBarRight.add(btnLogout);

        appBar.add(appBarRight, BorderLayout.EAST);
        rightPanel.add(appBar, BorderLayout.NORTH);

        // ========== CONTENT AREA ==========
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(new Color(243, 245, 249));
        rightPanel.add(contentArea, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private void showContent(String title, Component panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
        currentPage = title;
    }

    private void styleAppBarButton(JButton btn, boolean danger) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 32));
        if (danger) {
            btn.setBackground(new Color(220, 53, 69));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else {
            btn.setBackground(new Color(243, 245, 249));
            btn.setForeground(new Color(70, 75, 90));
        }
    }

    private void addSection(JPanel menu, String text) {
        menu.add(Box.createVerticalStrut(10));
        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(SECTION_FONT);
        lbl.setForeground(SECTION_TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(4, 14, 4, 14));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        menu.add(lbl);
        menu.add(Box.createVerticalStrut(2));
    }

    private JButton createMenuButton(String icon, String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(SIDEBAR_TEXT);
        btn.setFont(MENU_FONT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setPreferredSize(new Dimension(250, 36));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(0, 16, 0, 12));
        btn.setMargin(new Insets(0, 0, 0, 0));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != activeMenuButton) {
                    btn.setBackground(SIDEBAR_BTN_HOVER);
                    btn.setForeground(SIDEBAR_TEXT_HOVER);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != activeMenuButton) {
                    btn.setBackground(SIDEBAR_BG);
                    btn.setForeground(SIDEBAR_TEXT);
                }
            }
        });

        btn.addActionListener(e -> {
            if (activeMenuButton != null) {
                activeMenuButton.setBackground(SIDEBAR_BG);
                activeMenuButton.setForeground(SIDEBAR_TEXT);
            }
            activeMenuButton = btn;
            btn.setBackground(SIDEBAR_BTN_ACTIVE);
            btn.setForeground(SIDEBAR_TEXT_HOVER);
            action.actionPerformed(e);
        });

        return btn;
    }
}
