package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.model.User;
import com.mycompany.garagemanagementsystem.util.AppConfig;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainMenuFrame extends javax.swing.JFrame {

    private DashboardPanel dashboardPanel;
    private javax.swing.JTabbedPane tabbedPane;
    private final User loggedInUser;

    // Colors
    private static final Color SIDEBAR_BG = new Color(30, 32, 48);
    private static final Color SIDEBAR_SECTION_BG = new Color(25, 27, 42);
    private static final Color SIDEBAR_BTN_HOVER = new Color(50, 53, 78);
    private static final Color SIDEBAR_BTN_ACTIVE = new Color(65, 105, 225);
    private static final Color APPBAR_BG = new Color(255, 255, 255);
    private static final Color APPBAR_BORDER = new Color(228, 231, 240);
    private static final Color TAB_BG = new Color(243, 245, 249);
    private static final Color TAB_SELECTED_BG = Color.WHITE;
    private static final Color TAB_TEXT = new Color(55, 60, 80);
    private static final Color TAB_CLOSE_HOVER = new Color(220, 50, 50);
    private static final Color ACCENT = new Color(65, 105, 225);

    // Fonts
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font TAB_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public MainMenuFrame(User user) {
        this.loggedInUser = user;
        initUI();
        dashboardPanel = new DashboardPanel();
        tabbedPane.addTab("Dashboard", dashboardPanel);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    }

    // Backward compat
    public MainMenuFrame() {
        this(null);
    }

    private void initUI() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(AppConfig.getAppName());
        setLayout(new BorderLayout());

        // ========== SIDEBAR ==========
        JPanel sidebarWrap = new JPanel(new BorderLayout());
        sidebarWrap.setBackground(SIDEBAR_BG);
        sidebarWrap.setPreferredSize(new Dimension(260, 0));

        // --- Header: Logo + App Name ---
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(new Color(24, 26, 40));
        headerPanel.setBorder(new EmptyBorder(18, 16, 18, 16));

        ImageIcon logoIcon = AppConfig.loadLogo(38, 38);
        if (logoIcon != null) {
            JLabel lblLogo = new JLabel(logoIcon);
            headerPanel.add(lblLogo, BorderLayout.WEST);
        }

        JLabel lblApp = new JLabel("<html><b>" + AppConfig.getAppName() + "</b></html>");
        lblApp.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblApp.setForeground(Color.WHITE);
        headerPanel.add(lblApp, BorderLayout.CENTER);

        sidebarWrap.add(headerPanel, BorderLayout.NORTH);

        // --- Menu Sections ---
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(SIDEBAR_BG);
        menuPanel.setBorder(new EmptyBorder(6, 0, 6, 0));

        // Dashboard
        menuPanel.add(createSectionLabel("DASHBOARD"));
        menuPanel.add(createMenuButton("\uD83D\uDCCA", "Dashboard", e -> {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (tabbedPane.getTitleAt(i).equals("Dashboard")) {
                    tabbedPane.setSelectedIndex(i);
                    return;
                }
            }
        }));

        menuPanel.add(Box.createVerticalStrut(6));

        // Master Data
        menuPanel.add(createSectionLabel("MASTER DATA"));
        menuPanel.add(createMenuButton("\uD83D\uDC64", "Data Client", e -> openTab("Data Client", new ClientPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDE97", "Data Kendaraan", e -> openTab("Data Kendaraan", new VehiclePanel())));
        menuPanel.add(createMenuButton("\uD83D\uDD27", "Data Mekanik", e -> openTab("Data Mekanik", new MekanikPanel())));
        menuPanel.add(createMenuButton("\u2699\uFE0F", "Data Sparepart", e -> openTab("Data Sparepart", new SparepartPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDCE6", "Data Supplier", e -> openTab("Data Supplier", new SupplierPanel())));

        menuPanel.add(Box.createVerticalStrut(6));

        // Transaksi
        menuPanel.add(createSectionLabel("TRANSAKSI"));
        menuPanel.add(createMenuButton("\uD83D\uDCCB", "Pendaftaran Servis", e -> openTab("Pendaftaran Servis", new ServiceRegistrationPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDEE0\uFE0F", "Transaksi Servis", e -> openTab("Transaksi Servis", new TransactionListPanel(this))));
        menuPanel.add(createMenuButton("\uD83D\uDED2", "Pembelian Sparepart", e -> openTab("Pembelian Sparepart", new SparepartPurchasePanel())));
        menuPanel.add(createMenuButton("\uD83D\uDCB2", "Penjualan Langsung", e -> openTab("Penjualan Langsung", new DirectSalePanel())));

        menuPanel.add(Box.createVerticalStrut(6));

        // Laporan
        menuPanel.add(createSectionLabel("LAPORAN"));
        menuPanel.add(createMenuButton("\uD83D\uDCC4", "Lap. Transaksi Servis", e -> openTab("Lap. Transaksi Servis", new LaporanTransaksiPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDCE5", "Lap. Pembelian", e -> openTab("Lap. Pembelian", new LaporanPembelianPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDCB0", "Lap. Pendapatan", e -> openTab("Lap. Pendapatan", new LaporanPendapatanPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDCC8", "Lap. Omzet", e -> openTab("Lap. Omzet", new LaporanOmzetPanel())));
        menuPanel.add(createMenuButton("\uD83C\uDFC6", "Lap. Kinerja Mekanik", e -> openTab("Lap. Kinerja Mekanik", new LaporanKinerjaMekanikPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDD25", "Lap. Sparepart Terlaris", e -> openTab("Lap. Sparepart Terlaris", new LaporanSparepartTerlarisPanel())));

        menuPanel.add(Box.createVerticalStrut(6));

        // Lainnya
        menuPanel.add(createSectionLabel("LAINNYA"));
        menuPanel.add(createMenuButton("\uD83D\uDCDC", "Riwayat Servis", e -> openTab("Riwayat Servis", new ServiceHistoryPanel())));
        menuPanel.add(createMenuButton("\uD83D\uDCFA", "Layar Antrian (TV)", e -> new QueueDashboardFrame().setVisible(true)));

        JScrollPane menuScroll = new JScrollPane(menuPanel);
        menuScroll.setBorder(null);
        menuScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.getVerticalScrollBar().setUnitIncrement(16);
        menuScroll.getViewport().setBackground(SIDEBAR_BG);
        sidebarWrap.add(menuScroll, BorderLayout.CENTER);

        add(sidebarWrap, BorderLayout.WEST);

        // ========== RIGHT SIDE (APPBAR + CONTENT) ==========
        JPanel rightPanel = new JPanel(new BorderLayout());

        // ========== APPBAR ==========
        JPanel appBar = new JPanel(new BorderLayout());
        appBar.setBackground(APPBAR_BG);
        appBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, APPBAR_BORDER),
                new EmptyBorder(8, 20, 8, 20)));
        appBar.setPreferredSize(new Dimension(0, 48));

        String userName = loggedInUser != null ? loggedInUser.getNamaLengkap() : "Administrator";
        JLabel lblWelcome = new JLabel("\uD83D\uDC4B  Selamat datang, " + userName);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(new Color(70, 75, 90));
        appBar.add(lblWelcome, BorderLayout.WEST);

        JPanel appBarRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        appBarRight.setOpaque(false);

        JButton btnRefreshDash = new JButton("Refresh");
        styleAppBarButton(btnRefreshDash);
        btnRefreshDash.addActionListener(e -> {
            if (dashboardPanel != null) dashboardPanel.loadData();
        });
        appBarRight.add(btnRefreshDash);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new Dimension(90, 32));
        btnLogout.addActionListener(e -> {
            if (UIHelper.confirm(this, "Yakin ingin logout?", "Konfirmasi Logout")) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        appBarRight.add(btnLogout);
        appBar.add(appBarRight, BorderLayout.EAST);

        rightPanel.add(appBar, BorderLayout.NORTH);

        // ========== TABBED PANE ==========
        tabbedPane = new javax.swing.JTabbedPane();
        tabbedPane.setFont(TAB_FONT);
        tabbedPane.setBackground(TAB_BG);
        tabbedPane.setForeground(TAB_TEXT);
        tabbedPane.setBorder(new EmptyBorder(4, 0, 0, 0));
        tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        UIManager.put("TabbedPane.selected", TAB_SELECTED_BG);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(2, 6, 0, 6));
        UIManager.put("TabbedPane.tabInsets", new Insets(6, 14, 6, 14));
        tabbedPane.updateUI();

        rightPanel.add(tabbedPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void styleAppBarButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(new Color(240, 243, 248));
        btn.setForeground(new Color(70, 75, 90));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 32));
    }

    private JPanel createSectionLabel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SIDEBAR_SECTION_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.setBorder(new EmptyBorder(8, 18, 4, 18));

        JLabel lbl = new JLabel(text);
        lbl.setFont(SECTION_FONT);
        lbl.setForeground(new Color(120, 130, 165));
        panel.add(lbl, BorderLayout.WEST);

        return panel;
    }

    private JButton createMenuButton(String icon, String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(new Color(200, 205, 220));
        btn.setFont(MENU_FONT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setPreferredSize(new Dimension(250, 38));
        btn.setBorder(new EmptyBorder(0, 18, 0, 12));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(SIDEBAR_BTN_HOVER);
                btn.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(SIDEBAR_BG);
                btn.setForeground(new Color(200, 205, 220));
            }
        });

        btn.addActionListener(action);
        return btn;
    }

    private void openTab(String title, Component panel) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(title)) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }

        tabbedPane.addTab(title, panel);
        int idx = tabbedPane.indexOfTab(title);

        JPanel tabHeader = new JPanel(new BorderLayout(8, 0));
        tabHeader.setOpaque(false);
        tabHeader.setBorder(new EmptyBorder(1, 0, 1, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(TAB_FONT);
        lblTitle.setForeground(TAB_TEXT);
        tabHeader.add(lblTitle, BorderLayout.CENTER);

        JLabel btnClose = new JLabel("\u2715") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getForeground().equals(TAB_CLOSE_HOVER)) {
                    g2.setColor(new Color(255, 220, 220));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnClose.setForeground(new Color(160, 160, 170));
        btnClose.setPreferredSize(new Dimension(20, 20));
        btnClose.setHorizontalAlignment(JLabel.CENTER);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setToolTipText("Tutup tab");
        btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnClose.setForeground(TAB_CLOSE_HOVER);
                btnClose.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnClose.setForeground(new Color(160, 160, 170));
                btnClose.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = tabbedPane.indexOfTab(title);
                if (i >= 0) tabbedPane.removeTabAt(i);
            }
        });
        tabHeader.add(btnClose, BorderLayout.EAST);

        tabbedPane.setTabComponentAt(idx, tabHeader);
        tabbedPane.setSelectedIndex(idx);
    }
}
