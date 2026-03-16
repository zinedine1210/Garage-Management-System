package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.DashboardDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainMenuFrame extends JFrame {

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    
    private final JLabel lblTransaksi = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lblLaba = new JLabel("Rp 0", SwingConstants.CENTER);
    private final JLabel lblPelanggan = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lblSparepart = new JLabel("0", SwingConstants.CENTER);

    public MainMenuFrame() {
        setTitle("Garage Management System - Dashboard");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // --- SIDEBAR MENU ---
        JPanel sidebarPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.setBackground(Color.DARK_GRAY);

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
        sidebarPanel.add(btnRefresh);

        // --- DASHBOARD CONTENT ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(" Ringkasan Garasi Hari Ini", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        cardsPanel.add(createCard("Transaksi Selesai", lblTransaksi, new Color(173, 216, 230)));
        cardsPanel.add(createCard("Laba Kotor", lblLaba, new Color(144, 238, 144)));
        cardsPanel.add(createCard("Total Pelanggan", lblPelanggan, new Color(255, 253, 150)));
        cardsPanel.add(createCard("Sparepart Terjual", lblSparepart, new Color(255, 182, 193)));

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(cardsPanel, BorderLayout.CENTER);

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        loadDashboardData();
    }
    
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private void loadDashboardData() {
        try {
            lblTransaksi.setText(String.valueOf(dashboardDAO.getTransaksiHariIni()));
            lblLaba.setText(String.format("Rp %,.0f", dashboardDAO.getLabaHariIni()));
            lblPelanggan.setText(String.valueOf(dashboardDAO.getTotalPelanggan()));
            lblSparepart.setText(String.valueOf(dashboardDAO.getSparepartTerjualHariIni()));
        } catch (Exception ex) {
            System.err.println("Gagal meload dashboard: " + ex.getMessage());
        }
    }
}


