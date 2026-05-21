package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.util.AppConfig;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class MainMenuFrame extends javax.swing.JFrame {

    private DashboardPanel dashboardPanel;
    private javax.swing.JTabbedPane tabbedPane;

    private static final Color SIDEBAR_BG = new Color(43, 45, 66);
    private static final Color SIDEBAR_SECTION_BG = new Color(35, 37, 56);
    private static final Color SIDEBAR_BTN_HOVER = new Color(58, 61, 90);
    private static final Color TAB_BG = new Color(240, 244, 248);
    private static final Color TAB_SELECTED_BG = Color.WHITE;
    private static final Color TAB_TEXT = new Color(60, 60, 80);
    private static final Color TAB_CLOSE_HOVER = new Color(220, 50, 50);
    private static final Font TAB_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public MainMenuFrame() {
        initUI();
        dashboardPanel = new DashboardPanel();
        tabbedPane.addTab("Dashboard", dashboardPanel);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    }

    private void initUI() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(AppConfig.getAppName() + " - Executive Dashboard");
        setLayout(new BorderLayout());

        // ========== SIDEBAR ==========
        JPanel sidebarWrap = new JPanel(new BorderLayout());
        sidebarWrap.setBackground(SIDEBAR_BG);
        sidebarWrap.setPreferredSize(new Dimension(230, 0));

        // --- Header: Logo + App Name ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SIDEBAR_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        String logoPath = AppConfig.getLogoPath();
        if (logoPath != null && !logoPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(logoPath);
                java.awt.Image img = icon.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
                JLabel lblLogo = new JLabel(new ImageIcon(img));
                lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
                headerPanel.add(lblLogo, BorderLayout.WEST);
            } catch (Exception ignored) {}
        }

        JLabel lblApp = new JLabel("<html><div style='text-align:center;'>" + AppConfig.getAppName() + "</div></html>");
        lblApp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblApp.setForeground(Color.WHITE);
        lblApp.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(lblApp, BorderLayout.CENTER);

        sidebarWrap.add(headerPanel, BorderLayout.NORTH);

        // --- Menu Sections ---
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(SIDEBAR_BG);

        // Dashboard
        menuPanel.add(createSectionLabel("DASHBOARD"));
        menuPanel.add(createMenuButton("Dashboard", e -> {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (tabbedPane.getTitleAt(i).equals("Dashboard")) {
                    tabbedPane.setSelectedIndex(i);
                    return;
                }
            }
        }));

        menuPanel.add(Box.createVerticalStrut(8));

        // Master Data
        menuPanel.add(createSectionLabel("MASTER DATA"));
        menuPanel.add(createMenuButton("Data Client", e -> openTab("Data Client", new ClientPanel())));
        menuPanel.add(createMenuButton("Data Kendaraan", e -> openTab("Data Kendaraan", new VehiclePanel())));
        menuPanel.add(createMenuButton("Data Mekanik", e -> openTab("Data Mekanik", new MekanikPanel())));
        menuPanel.add(createMenuButton("Data Sparepart", e -> openTab("Data Sparepart", new SparepartPanel())));
        menuPanel.add(createMenuButton("Data Supplier", e -> openTab("Data Supplier", new SupplierPanel())));

        menuPanel.add(Box.createVerticalStrut(8));

        // Transaksi
        menuPanel.add(createSectionLabel("TRANSAKSI"));
        menuPanel.add(createMenuButton("Pendaftaran Servis", e -> openTab("Pendaftaran Servis", new ServiceRegistrationPanel())));
        menuPanel.add(createMenuButton("Transaksi Servis", e -> openTab("Transaksi Servis", new TransactionListPanel(this))));
        menuPanel.add(createMenuButton("Pembelian Sparepart", e -> openTab("Pembelian Sparepart", new SparepartPurchasePanel())));

        menuPanel.add(Box.createVerticalStrut(8));

        // Laporan
        menuPanel.add(createSectionLabel("LAPORAN"));
        menuPanel.add(createMenuButton("Lap. Transaksi Servis", e -> openTab("Lap. Transaksi Servis", new LaporanTransaksiPanel())));
        menuPanel.add(createMenuButton("Lap. Pembelian", e -> openTab("Lap. Pembelian", new LaporanPembelianPanel())));
        menuPanel.add(createMenuButton("Lap. Pendapatan", e -> openTab("Lap. Pendapatan", new LaporanPendapatanPanel())));

        menuPanel.add(Box.createVerticalStrut(8));

        // Lainnya
        menuPanel.add(createSectionLabel("LAINNYA"));
        menuPanel.add(createMenuButton("Riwayat Servis", e -> openTab("Riwayat Servis", new ServiceHistoryPanel())));
        menuPanel.add(createMenuButton("Layar Antrian (TV)", e -> new QueueDashboardFrame().setVisible(true)));

        JScrollPane menuScroll = new JScrollPane(menuPanel);
        menuScroll.setBorder(null);
        menuScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.getVerticalScrollBar().setUnitIncrement(16);
        menuScroll.getViewport().setBackground(SIDEBAR_BG);
        sidebarWrap.add(menuScroll, BorderLayout.CENTER);

        // --- Bottom: Refresh ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(35, 37, 56));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.setBackground(new Color(60, 60, 90));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(MENU_FONT);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> {
            if (dashboardPanel != null) dashboardPanel.loadData();
        });
        bottomPanel.add(btnRefresh, BorderLayout.CENTER);
        sidebarWrap.add(bottomPanel, BorderLayout.SOUTH);

        add(sidebarWrap, BorderLayout.WEST);

        // ========== TABBED PANE ==========
        tabbedPane = new javax.swing.JTabbedPane();
        tabbedPane.setFont(TAB_FONT);
        tabbedPane.setBackground(TAB_BG);
        tabbedPane.setForeground(TAB_TEXT);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        UIManager.put("TabbedPane.selected", TAB_SELECTED_BG);
        UIManager.put("TabbedPane.contentBorderInsets", new java.awt.Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.tabAreaInsets", new java.awt.Insets(2, 6, 0, 6));
        UIManager.put("TabbedPane.tabInsets", new java.awt.Insets(6, 14, 6, 14));
        tabbedPane.updateUI();

        add(tabbedPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createSectionLabel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SIDEBAR_SECTION_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 12, 4, 12));

        JLabel lbl = new JLabel(text);
        lbl.setFont(SECTION_FONT);
        lbl.setForeground(new Color(140, 150, 180));
        panel.add(lbl, BorderLayout.WEST);

        return panel;
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton("  " + text);
        btn.setHorizontalAlignment(JButton.LEFT);
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(Color.WHITE);
        btn.setFont(MENU_FONT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setPreferredSize(new Dimension(220, 36));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(SIDEBAR_BTN_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(SIDEBAR_BG);
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
        tabHeader.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));

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
