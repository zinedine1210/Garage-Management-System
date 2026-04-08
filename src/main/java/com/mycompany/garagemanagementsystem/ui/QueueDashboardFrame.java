package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import java.awt.Color;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * QueueDashboardFrame = Layar antrian servis untuk ditampilkan di TV bengkel.
 *
 * Tampilan:
 * ┌──────────────────────────────────────────────┐
 * │         STATUS ANTRIAN SERVIS                 │
 * ├───────────────────┬──────────────────────────┤
 * │  MENUNGGU         │  SEDANG DIKERJAKAN        │
 * │  B 1234 XYZ       │  D 5678 ABC               │
 * │  B 9999 DEF       │                            │
 * └───────────────────┴──────────────────────────┘
 *
 * Background hitam, auto-refresh setiap 5 detik via Timer.
 * Data diambil dari ServiceTransactionDAO.getAntrian() yang mencari transaksi
 * hari ini dengan status "Menunggu" atau "Dikerjakan".
 */
public class QueueDashboardFrame extends javax.swing.JFrame {

    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();

    public QueueDashboardFrame() {
        initComponents();
        getContentPane().setBackground(Color.BLACK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Muat data pertama kali
        loadData();

        // Auto-refresh setiap 5 detik (5000 milidetik)
        Timer timer = new Timer(5000, e -> loadData());
        timer.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
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

        lblTitle.setFont(new java.awt.Font("Arial", 1, 36));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("STATUS ANTRIAN SERVIS");
        getContentPane().add(lblTitle, java.awt.BorderLayout.NORTH);

        mainPanel.setBackground(new java.awt.Color(0, 0, 0));
        mainPanel.setLayout(new java.awt.GridLayout(1, 2, 20, 20));

        wrapMenunggu.setBackground(new java.awt.Color(25, 25, 25));
        wrapMenunggu.setLayout(new java.awt.BorderLayout());
        titleMenunggu.setFont(new java.awt.Font("Arial", 1, 24));
        titleMenunggu.setForeground(new java.awt.Color(255, 255, 0));
        titleMenunggu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleMenunggu.setText("MENUNGGU (WAITING)");
        wrapMenunggu.add(titleMenunggu, java.awt.BorderLayout.NORTH);
        pnlMenunggu.setBackground(new java.awt.Color(25, 25, 25));
        pnlMenunggu.setLayout(new java.awt.GridLayout(10, 1, 5, 5));
        wrapMenunggu.add(pnlMenunggu, java.awt.BorderLayout.CENTER);
        mainPanel.add(wrapMenunggu);

        wrapDikerjakan.setBackground(new java.awt.Color(25, 25, 25));
        wrapDikerjakan.setLayout(new java.awt.BorderLayout());
        titleDikerjakan.setFont(new java.awt.Font("Arial", 1, 24));
        titleDikerjakan.setForeground(new java.awt.Color(0, 255, 0));
        titleDikerjakan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleDikerjakan.setText("SEDANG DIKERJAKAN (WORKING)");
        wrapDikerjakan.add(titleDikerjakan, java.awt.BorderLayout.NORTH);
        pnlDikerjakan.setBackground(new java.awt.Color(25, 25, 25));
        pnlDikerjakan.setLayout(new java.awt.GridLayout(10, 1, 5, 5));
        wrapDikerjakan.add(pnlDikerjakan, java.awt.BorderLayout.CENTER);
        mainPanel.add(wrapDikerjakan);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        setSize(1000, 600);
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Muat data antrian dari database dan tampilkan di panel.
     * Dipanggil pertama kali + setiap 5 detik oleh Timer.
     */
    private void loadData() {
        try {
            List<ServiceTransaction> antrian = transDAO.getAntrian();

            // Kosongkan kedua panel terlebih dahulu
            pnlMenunggu.removeAll();
            pnlDikerjakan.removeAll();

            // Isi panel sesuai status masing-masing transaksi
            for (ServiceTransaction t : antrian) {
                JLabel lblPlat = new JLabel(t.getKeluhan(), SwingConstants.CENTER);
                lblPlat.setFont(new Font("Arial", Font.BOLD, 30));
                lblPlat.setForeground(Color.WHITE);

                if ("Menunggu".equals(t.getStatusServis())) {
                    pnlMenunggu.add(lblPlat);
                } else if ("Dikerjakan".equals(t.getStatusServis())) {
                    pnlDikerjakan.add(lblPlat);
                }
            }

            // Refresh tampilan panel
            pnlMenunggu.revalidate();
            pnlMenunggu.repaint();
            pnlDikerjakan.revalidate();
            pnlDikerjakan.repaint();
        } catch (SQLException ex) {
            System.err.println("Gagal memuat antrian: " + ex.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
