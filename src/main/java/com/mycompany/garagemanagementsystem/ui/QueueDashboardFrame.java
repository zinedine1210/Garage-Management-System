package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class QueueDashboardFrame extends JFrame {

    private final JPanel pnlMenunggu;
    private final JPanel pnlDikerjakan;
    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();

    public QueueDashboardFrame() {
        setTitle("Dashboard Antrian - Live Service Progress");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        JLabel lblTitle = new JLabel("STATUS ANTRIAN SERVIS", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel Kiri: Menunggu
        JPanel wrapMenunggu = new JPanel(new BorderLayout());
        wrapMenunggu.setBackground(new Color(25, 25, 25));
        JLabel titleMenunggu = new JLabel("MENUNGGU (WAITING)", SwingConstants.CENTER);
        titleMenunggu.setFont(new Font("Arial", Font.BOLD, 24));
        titleMenunggu.setForeground(Color.YELLOW);
        wrapMenunggu.add(titleMenunggu, BorderLayout.NORTH);

        pnlMenunggu = new JPanel(new GridLayout(10, 1, 5, 5));
        pnlMenunggu.setBackground(new Color(25, 25, 25));
        wrapMenunggu.add(pnlMenunggu, BorderLayout.CENTER);

        // Panel Kanan: Dikerjakan
        JPanel wrapDikerjakan = new JPanel(new BorderLayout());
        wrapDikerjakan.setBackground(new Color(25, 25, 25));
        JLabel titleDikerjakan = new JLabel("SEDANG DIKERJAKAN (WORKING)", SwingConstants.CENTER);
        titleDikerjakan.setFont(new Font("Arial", Font.BOLD, 24));
        titleDikerjakan.setForeground(Color.GREEN);
        wrapDikerjakan.add(titleDikerjakan, BorderLayout.NORTH);

        pnlDikerjakan = new JPanel(new GridLayout(10, 1, 5, 5));
        pnlDikerjakan.setBackground(new Color(25, 25, 25));
        wrapDikerjakan.add(pnlDikerjakan, BorderLayout.CENTER);

        mainPanel.add(wrapMenunggu);
        mainPanel.add(wrapDikerjakan);
        add(mainPanel, BorderLayout.CENTER);

        loadData();

        // Auto Refresh Setiap 5 Detik
        Timer timer = new Timer(5000, e -> loadData());
        timer.start();
    }

    private void loadData() {
        try {
            List<ServiceTransaction> antrian = transDAO.getAntrian();
            pnlMenunggu.removeAll();
            pnlDikerjakan.removeAll();

            for (ServiceTransaction t : antrian) {
                JLabel lblPlat = new JLabel(t.getKeluhan(), SwingConstants.CENTER); // numpang kolom keluhan
                lblPlat.setFont(new Font("Arial", Font.BOLD, 30));
                lblPlat.setForeground(Color.WHITE);

                if ("Menunggu".equals(t.getStatusServis())) {
                    pnlMenunggu.add(lblPlat);
                } else if ("Dikerjakan".equals(t.getStatusServis())) {
                    pnlDikerjakan.add(lblPlat);
                }
            }
            pnlMenunggu.revalidate();
            pnlMenunggu.repaint();
            pnlDikerjakan.revalidate();
            pnlDikerjakan.repaint();
            
        } catch (SQLException ex) {
            System.err.println("Gagal memuat antrian: " + ex.getMessage());
        }
    }
}
