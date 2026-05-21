package com.mycompany.garagemanagementsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class MainMenuFrame extends javax.swing.JFrame {

    private DashboardPanel dashboardPanel;

    private static final Color TAB_BG = new Color(240, 244, 248);
    private static final Color TAB_SELECTED_BG = Color.WHITE;
    private static final Color TAB_HOVER_BG = new Color(220, 228, 236);
    private static final Color TAB_TEXT = new Color(60, 60, 80);
    private static final Color TAB_CLOSE_HOVER = new Color(220, 50, 50);
    private static final Font TAB_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public MainMenuFrame() {
        initComponents();
        styleSidebarButtons();
        styleTabbedPane();
        dashboardPanel = new DashboardPanel();
        tabbedPane.addTab("Dashboard", dashboardPanel);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    }

    private void styleTabbedPane() {
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
    }

    private void styleSidebarButtons() {
        Color bg = new Color(43, 45, 66);
        Color fg = Color.WHITE;
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
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
        sidebarPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 2));

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

        btnPembelian = new javax.swing.JButton();
        btnPembelian.setText("Pembelian Sparepart");
        btnPembelian.addActionListener(this::btnPembelianActionPerformed);
        sidebarPanel.add(btnPembelian);

        javax.swing.JButton btnPendaftaran = new javax.swing.JButton();
        btnPendaftaran.setText("Pendaftaran Servis");
        btnPendaftaran.addActionListener(e -> openTab("Pendaftaran Servis", new ServiceRegistrationPanel()));
        sidebarPanel.add(btnPendaftaran);

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

    private void btnPembelianActionPerformed(java.awt.event.ActionEvent evt) {
        openTab("Pembelian Sparepart", new SparepartPurchasePanel());
    }

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        dashboardPanel.loadData();
    }//GEN-LAST:event_btnRefreshActionPerformed

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomSidebar;
    private javax.swing.JButton btnAntrian;
    private javax.swing.JButton btnClient;
    private javax.swing.JButton btnMekanik;
    private javax.swing.JButton btnPembelian;
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
