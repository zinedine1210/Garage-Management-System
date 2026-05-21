package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.ServiceRegistrationDAO;
import com.mycompany.garagemanagementsystem.dao.ServiceTransactionDAO;
import com.mycompany.garagemanagementsystem.model.ServiceRegistration;
import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import com.mycompany.garagemanagementsystem.util.AppConfig;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class QueueDashboardFrame extends javax.swing.JFrame {

    private final ServiceTransactionDAO transDAO = new ServiceTransactionDAO();
    private final ServiceRegistrationDAO regDAO = new ServiceRegistrationDAO();
    private JPanel pnlMenunggu, pnlDikerjakan;
    private JLabel titleMenunggu, titleDikerjakan, lblClock;

    private static final Color BG_DARK = new Color(15, 17, 28);
    private static final Color CARD_BG = new Color(28, 32, 50);
    private static final Color SECTION_BG = new Color(22, 25, 40);
    private static final Color YELLOW_ACCENT = new Color(250, 204, 21);
    private static final Color GREEN_ACCENT = new Color(34, 197, 94);
    private static final Color TEXT_WHITE = new Color(240, 240, 245);
    private static final Color TEXT_DIM = new Color(148, 163, 184);

    public QueueDashboardFrame() {
        buildUI();
        loadData();

        Timer dataTimer = new Timer(5000, e -> loadData());
        dataTimer.start();

        Timer clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();
    }

    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy   |   HH:mm:ss");
        lblClock.setText(sdf.format(new java.util.Date()));
    }

    private void buildUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Layar Antrian Servis - " + AppConfig.getAppName());
        setSize(1280, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        // ========== HEADER ==========
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 41, 82),
                        getWidth(), 0, new Color(17, 24, 48));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Bottom accent line
                g2.setColor(new Color(59, 130, 246));
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);
                g2.dispose();
            }
        };
        header.setLayout(new BorderLayout(16, 0));
        header.setBorder(new EmptyBorder(14, 24, 14, 24));
        header.setPreferredSize(new Dimension(0, 80));

        // Logo + Company info
        JPanel logoCompany = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        logoCompany.setOpaque(false);

        ImageIcon logo = AppConfig.loadLogo(44, 44);
        if (logo != null) {
            JLabel lblLogo = new JLabel(logo);
            logoCompany.add(lblLogo);
        }

        JPanel companyInfo = new JPanel();
        companyInfo.setLayout(new BoxLayout(companyInfo, BoxLayout.Y_AXIS));
        companyInfo.setOpaque(false);
        JLabel lblCompany = new JLabel(AppConfig.getCompanyName());
        lblCompany.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblCompany.setForeground(TEXT_WHITE);
        companyInfo.add(lblCompany);
        JLabel lblAddr = new JLabel(AppConfig.getCompanyAddress() + "  |  " + AppConfig.getCompanyPhoneFormatted());
        lblAddr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAddr.setForeground(TEXT_DIM);
        companyInfo.add(lblAddr);
        logoCompany.add(companyInfo);

        header.add(logoCompany, BorderLayout.WEST);

        // Clock
        lblClock = new JLabel();
        lblClock.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblClock.setForeground(TEXT_DIM);
        lblClock.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(lblClock, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ========== MAIN: Two Columns ==========
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(new EmptyBorder(16, 24, 24, 24));

        // --- MENUNGGU ---
        JPanel wrapMenunggu = createSectionPanel(YELLOW_ACCENT);
        titleMenunggu = createSectionTitle("MENUNGGU", YELLOW_ACCENT);
        wrapMenunggu.add(titleMenunggu, BorderLayout.NORTH);
        pnlMenunggu = new JPanel();
        pnlMenunggu.setLayout(new BoxLayout(pnlMenunggu, BoxLayout.Y_AXIS));
        pnlMenunggu.setOpaque(false);
        JScrollPane scrollMenunggu = new JScrollPane(pnlMenunggu);
        scrollMenunggu.setBorder(null);
        scrollMenunggu.setOpaque(false);
        scrollMenunggu.getViewport().setOpaque(false);
        scrollMenunggu.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollMenunggu.getVerticalScrollBar().setUnitIncrement(16);
        wrapMenunggu.add(scrollMenunggu, BorderLayout.CENTER);
        mainPanel.add(wrapMenunggu);

        // --- DIKERJAKAN ---
        JPanel wrapDikerjakan = createSectionPanel(GREEN_ACCENT);
        titleDikerjakan = createSectionTitle("SEDANG DIKERJAKAN", GREEN_ACCENT);
        wrapDikerjakan.add(titleDikerjakan, BorderLayout.NORTH);
        pnlDikerjakan = new JPanel();
        pnlDikerjakan.setLayout(new BoxLayout(pnlDikerjakan, BoxLayout.Y_AXIS));
        pnlDikerjakan.setOpaque(false);
        JScrollPane scrollDikerjakan = new JScrollPane(pnlDikerjakan);
        scrollDikerjakan.setBorder(null);
        scrollDikerjakan.setOpaque(false);
        scrollDikerjakan.getViewport().setOpaque(false);
        scrollDikerjakan.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollDikerjakan.getVerticalScrollBar().setUnitIncrement(16);
        wrapDikerjakan.add(scrollDikerjakan, BorderLayout.CENTER);
        mainPanel.add(wrapDikerjakan);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSectionPanel(Color accent) {
        JPanel panel = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SECTION_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                // Top accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 8, 8);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 16, 16, 16));
        return panel;
    }

    private JLabel createSectionTitle(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(color);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(new EmptyBorder(4, 0, 8, 0));
        return lbl;
    }

    private void loadData() {
        try {
            // Menunggu: from pendaftaran with status Registered
            List<ServiceRegistration> registered = regDAO.getRegisteredForQueue();
            pnlMenunggu.removeAll();
            for (ServiceRegistration r : registered) {
                pnlMenunggu.add(createCardReg(r, YELLOW_ACCENT));
                pnlMenunggu.add(Box.createVerticalStrut(8));
            }
            if (registered.isEmpty()) pnlMenunggu.add(createEmptyLabel());
            titleMenunggu.setText("MENUNGGU (" + registered.size() + ")");

            // Dikerjakan: from transaksi_servis with status Dikerjakan
            List<ServiceTransaction> antrian = transDAO.getAntrian();
            pnlDikerjakan.removeAll();
            int dikerjakanCount = 0;
            for (ServiceTransaction t : antrian) {
                if ("Dikerjakan".equals(t.getStatusServis())) {
                    pnlDikerjakan.add(createCard(t, GREEN_ACCENT));
                    pnlDikerjakan.add(Box.createVerticalStrut(8));
                    dikerjakanCount++;
                }
            }
            if (dikerjakanCount == 0) pnlDikerjakan.add(createEmptyLabel());
            titleDikerjakan.setText("SEDANG DIKERJAKAN (" + dikerjakanCount + ")");

            pnlMenunggu.revalidate(); pnlMenunggu.repaint();
            pnlDikerjakan.revalidate(); pnlDikerjakan.repaint();
        } catch (SQLException ex) {
            System.err.println("Gagal memuat antrian: " + ex.getMessage());
        }
    }

    private JPanel createCardReg(ServiceRegistration r, Color accent) {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(accent);
                g2.fillRoundRect(0, 4, 4, getHeight() - 8, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 18, 14, 18));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        String timeStr = "";
        if (r.getTanggalDaftar() != null) {
            timeStr = new SimpleDateFormat("HH:mm").format(r.getTanggalDaftar());
        }
        JLabel lblTime = new JLabel(timeStr);
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTime.setForeground(accent);
        lblTime.setPreferredSize(new Dimension(70, 0));
        card.add(lblTime, BorderLayout.WEST);

        JLabel lblPlat = new JLabel(r.getNoPolisi() != null ? r.getNoPolisi() : "-");
        lblPlat.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblPlat.setForeground(TEXT_WHITE);
        card.add(lblPlat, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        JLabel lblMekLabel = new JLabel("Mekanik");
        lblMekLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMekLabel.setForeground(TEXT_DIM);
        lblMekLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rightPanel.add(lblMekLabel, BorderLayout.NORTH);
        JLabel lblMekanik = new JLabel(r.getMekanikNama() != null ? r.getMekanikNama() : "-");
        lblMekanik.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMekanik.setForeground(new Color(147, 197, 253));
        lblMekanik.setHorizontalAlignment(SwingConstants.RIGHT);
        rightPanel.add(lblMekanik, BorderLayout.SOUTH);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createCard(ServiceTransaction t, Color accent) {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                // Left accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 4, 4, getHeight() - 8, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 18, 14, 18));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Time
        String timeStr = "";
        if (t.getTanggal() != null) {
            timeStr = new SimpleDateFormat("HH:mm").format(t.getTanggal());
        }
        JLabel lblTime = new JLabel(timeStr);
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTime.setForeground(accent);
        lblTime.setPreferredSize(new Dimension(70, 0));
        card.add(lblTime, BorderLayout.WEST);

        // Plat Nomer (center)
        JLabel lblPlat = new JLabel(t.getNoPolisi() != null ? t.getNoPolisi() : "-");
        lblPlat.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblPlat.setForeground(TEXT_WHITE);
        card.add(lblPlat, BorderLayout.CENTER);

        // Mekanik (right)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        JLabel lblMekLabel = new JLabel("Mekanik");
        lblMekLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMekLabel.setForeground(TEXT_DIM);
        lblMekLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rightPanel.add(lblMekLabel, BorderLayout.NORTH);
        JLabel lblMekanik = new JLabel(t.getMekanikNama() != null ? t.getMekanikNama() : "-");
        lblMekanik.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMekanik.setForeground(new Color(147, 197, 253));
        lblMekanik.setHorizontalAlignment(SwingConstants.RIGHT);
        rightPanel.add(lblMekanik, BorderLayout.SOUTH);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private JLabel createEmptyLabel() {
        JLabel lbl = new JLabel("Tidak ada antrian");
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        lbl.setForeground(TEXT_DIM);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(40, 0, 40, 0));
        return lbl;
    }
}
