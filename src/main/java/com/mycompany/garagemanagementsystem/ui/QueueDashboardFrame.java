package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class QueueDashboardFrame extends javax.swing.JFrame {

    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();

    public QueueDashboardFrame() {
        initComponents();
        getContentPane().setBackground(new Color(20, 20, 30));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        lblClock.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        loadData();

        Timer dataTimer = new Timer(5000, e -> loadData());
        dataTimer.start();

        Timer clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();
    }

    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy  |  HH:mm:ss");
        lblClock.setText(sdf.format(new java.util.Date()));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblClock = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        wrapMenunggu = new javax.swing.JPanel();
        titleMenunggu = new javax.swing.JLabel();
        pnlMenunggu = new javax.swing.JPanel();
        wrapDikerjakan = new javax.swing.JPanel();
        titleDikerjakan = new javax.swing.JLabel();
        pnlDikerjakan = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Dashboard Antrian - Live Service Progress");
        getContentPane().setLayout(new java.awt.BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(20, 20, 30));

        lblTitle.setFont(new java.awt.Font("Arial", 1, 32));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("STATUS ANTRIAN SERVIS HARI INI");
        headerPanel.add(lblTitle, BorderLayout.NORTH);

        lblClock = new JLabel();
        lblClock.setFont(new Font("Arial", Font.PLAIN, 16));
        lblClock.setForeground(new Color(180, 180, 200));
        lblClock.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblClock, BorderLayout.SOUTH);

        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        mainPanel.setBackground(new java.awt.Color(20, 20, 30));
        mainPanel.setLayout(new java.awt.GridLayout(1, 2, 20, 20));

        wrapMenunggu.setBackground(new java.awt.Color(30, 30, 45));
        wrapMenunggu.setLayout(new java.awt.BorderLayout());
        wrapMenunggu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 200, 0), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        titleMenunggu.setFont(new java.awt.Font("Arial", 1, 22));
        titleMenunggu.setForeground(new java.awt.Color(255, 200, 0));
        titleMenunggu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleMenunggu.setText("MENUNGGU");
        titleMenunggu.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        wrapMenunggu.add(titleMenunggu, java.awt.BorderLayout.NORTH);
        pnlMenunggu.setBackground(new java.awt.Color(30, 30, 45));
        pnlMenunggu.setLayout(new BoxLayout(pnlMenunggu, BoxLayout.Y_AXIS));
        wrapMenunggu.add(pnlMenunggu, java.awt.BorderLayout.CENTER);
        mainPanel.add(wrapMenunggu);

        wrapDikerjakan.setBackground(new java.awt.Color(30, 30, 45));
        wrapDikerjakan.setLayout(new java.awt.BorderLayout());
        wrapDikerjakan.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 200, 100), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        titleDikerjakan.setFont(new java.awt.Font("Arial", 1, 22));
        titleDikerjakan.setForeground(new java.awt.Color(0, 200, 100));
        titleDikerjakan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleDikerjakan.setText("SEDANG DIKERJAKAN");
        titleDikerjakan.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        wrapDikerjakan.add(titleDikerjakan, java.awt.BorderLayout.NORTH);
        pnlDikerjakan.setBackground(new java.awt.Color(30, 30, 45));
        pnlDikerjakan.setLayout(new BoxLayout(pnlDikerjakan, BoxLayout.Y_AXIS));
        wrapDikerjakan.add(pnlDikerjakan, java.awt.BorderLayout.CENTER);
        mainPanel.add(wrapDikerjakan);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        setSize(1200, 700);
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void loadData() {
        try {
            List<ServiceTransaction> antrian = transDAO.getAntrian();

            pnlMenunggu.removeAll();
            pnlDikerjakan.removeAll();

            int menungguCount = 0;
            int dikerjakanCount = 0;

            for (ServiceTransaction t : antrian) {
                JPanel card = createCard(t);
                if ("Menunggu".equals(t.getStatusServis())) {
                    pnlMenunggu.add(card);
                    menungguCount++;
                } else if ("Dikerjakan".equals(t.getStatusServis())) {
                    pnlDikerjakan.add(card);
                    dikerjakanCount++;
                }
            }

            titleMenunggu.setText("MENUNGGU (" + menungguCount + ")");
            titleDikerjakan.setText("SEDANG DIKERJAKAN (" + dikerjakanCount + ")");

            pnlMenunggu.revalidate();
            pnlMenunggu.repaint();
            pnlDikerjakan.revalidate();
            pnlDikerjakan.repaint();
        } catch (SQLException ex) {
            System.err.println("Gagal memuat antrian: " + ex.getMessage());
        }
    }

    private JPanel createCard(ServiceTransaction t) {
        JPanel card = new JPanel(new BorderLayout(10, 2));
        card.setBackground(new Color(45, 45, 65));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 90), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            )
        ));

        JLabel lblPlat = new JLabel(t.getNoPolisi());
        lblPlat.setFont(new Font("Arial", Font.BOLD, 24));
        lblPlat.setForeground(Color.WHITE);

        String timeStr = "";
        if (t.getTanggal() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            timeStr = sdf.format(t.getTanggal());
        }

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        infoPanel.setOpaque(false);

        JLabel lblClient = new JLabel(t.getClientNama() != null ? t.getClientNama() : "-");
        lblClient.setFont(new Font("Arial", Font.PLAIN, 14));
        lblClient.setForeground(new Color(200, 200, 220));

        JLabel lblMekanik = new JLabel("Mekanik: " + (t.getMekanikNama() != null ? t.getMekanikNama() : "-"));
        lblMekanik.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMekanik.setForeground(new Color(150, 200, 255));

        JLabel lblKeluhan = new JLabel(t.getKeluhan() != null ? t.getKeluhan() : "-");
        lblKeluhan.setFont(new Font("Arial", Font.ITALIC, 12));
        lblKeluhan.setForeground(new Color(180, 180, 180));

        infoPanel.add(lblClient);
        infoPanel.add(lblMekanik);
        infoPanel.add(lblKeluhan);

        JLabel lblTime = new JLabel(timeStr);
        lblTime.setFont(new Font("Arial", Font.BOLD, 16));
        lblTime.setForeground(new Color(200, 200, 200));
        lblTime.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(lblPlat, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(lblTime, BorderLayout.EAST);

        return card;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblClock;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel pnlDikerjakan;
    private javax.swing.JPanel pnlMenunggu;
    private javax.swing.JLabel titleDikerjakan;
    private javax.swing.JLabel titleMenunggu;
    private javax.swing.JPanel wrapDikerjakan;
    private javax.swing.JPanel wrapMenunggu;
    // End of variables declaration//GEN-END:variables
}
