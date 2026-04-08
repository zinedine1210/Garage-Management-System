package com.mycompany.garagemanagementsystem.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenuFrame extends javax.swing.JFrame {

    private DashboardPanel dashboardPanel;

    public MainMenuFrame() {
        initComponents();
        styleSidebarButtons();
        dashboardPanel = new DashboardPanel();
        tabbedPane.addTab("Dashboard", dashboardPanel);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
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

        JPanel tabTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabTitle.setOpaque(false);
        tabTitle.add(new JLabel(title + " "));

        JButton btnClose = new JButton("X");
        btnClose.setMargin(new java.awt.Insets(0, 0, 0, 0));
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
