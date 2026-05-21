package com.mycompany.garagemanagementsystem.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class UIHelper {

    private static final Color BG = new Color(243, 245, 249);
    private static final Color BLUE   = new Color(59, 130, 246);
    private static final Color GREEN  = new Color(40, 167, 69);
    private static final Color RED    = new Color(220, 53, 69);
    private static final Color AMBER  = new Color(245, 158, 11);
    private static final Color GRAY   = new Color(108, 117, 125);

    // ─── Page Header ───────────────────────────────────────────

    public static JPanel createPageHeader(String title, String description) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(30, 32, 48));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(lblTitle);
        header.add(Box.createVerticalStrut(2));
        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(107, 114, 128));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(lblDesc);
        return header;
    }

    // ─── Styled Button ─────────────────────────────────────────

    public static JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    // ─── Dialog Header (gradient blue banner) ──────────────────

    public static JPanel createDialogHeader(String title, String subtitle) {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(59, 130, 246),
                        getWidth(), 0, new Color(99, 102, 241));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(18, 22, 18, 22));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(lblTitle);
        if (subtitle != null && !subtitle.isEmpty()) {
            header.add(Box.createVerticalStrut(3));
            JLabel lblSub = new JLabel(subtitle);
            lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblSub.setForeground(new Color(219, 234, 254));
            lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
            header.add(lblSub);
        }
        return header;
    }

    // ─── Dialog Button Panel ───────────────────────────────────

    public static JPanel createDialogButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panel.setBackground(new Color(248, 249, 252));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 235)));
        return panel;
    }

    // ─── Alert Dialogs ─────────────────────────────────────────

    public static void info(Component parent, String message) {
        showAlert(parent, message, "Informasi", BLUE, "\u2139");
    }

    public static void success(Component parent, String message) {
        showAlert(parent, message, "Berhasil", GREEN, "\u2714");
    }

    public static void error(Component parent, String message) {
        showAlert(parent, message, "Error", RED, "\u2716");
    }

    public static void warn(Component parent, String message) {
        showAlert(parent, message, "Peringatan", AMBER, "\u26A0");
    }

    private static void showAlert(Component parent, String msg, String title, Color accent, String icon) {
        Window win = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        JDialog d = new JDialog(win instanceof Frame ? (Frame) win : null, title, true);
        d.setLayout(new BorderLayout());

        // Accent bar top
        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(accent);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(0, 5));
        d.add(bar, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new BorderLayout(14, 0));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(22, 24, 18, 24));

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        lblIcon.setForeground(accent);
        lblIcon.setPreferredSize(new Dimension(50, 50));
        content.add(lblIcon, BorderLayout.WEST);

        JPanel msgPanel = new JPanel();
        msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
        msgPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(accent);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        msgPanel.add(lblTitle);
        msgPanel.add(Box.createVerticalStrut(5));

        JLabel lblMsg = new JLabel("<html><body style='width:280px'>" + escapeHtml(msg) + "</body></html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(55, 65, 81));
        lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
        msgPanel.add(lblMsg);

        content.add(msgPanel, BorderLayout.CENTER);

        ImageIcon logo = AppConfig.loadLogo(36, 36);
        if (logo != null) {
            JLabel lblLogo = new JLabel(logo);
            lblLogo.setVerticalAlignment(SwingConstants.TOP);
            content.add(lblLogo, BorderLayout.EAST);
        }

        d.add(content, BorderLayout.CENTER);

        // Bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));
        JButton btnOk = createStyledButton("  OK  ", accent);
        btnOk.addActionListener(e -> d.dispose());
        bottom.add(btnOk);
        d.add(bottom, BorderLayout.SOUTH);

        d.pack();
        d.setMinimumSize(new Dimension(400, 190));
        d.setResizable(false);
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }

    // ─── Confirm Dialog ────────────────────────────────────────

    public static boolean confirm(Component parent, String message, String title) {
        Window win = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        JDialog d = new JDialog(win instanceof Frame ? (Frame) win : null, title, true);
        d.setLayout(new BorderLayout());

        boolean[] result = {false};

        // Accent bar
        JPanel bar = new JPanel();
        bar.setBackground(BLUE);
        bar.setPreferredSize(new Dimension(0, 5));
        d.add(bar, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new BorderLayout(14, 0));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(22, 24, 18, 24));

        JLabel lblIcon = new JLabel("?", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblIcon.setForeground(BLUE);
        lblIcon.setPreferredSize(new Dimension(50, 50));
        content.add(lblIcon, BorderLayout.WEST);

        JPanel msgPanel = new JPanel();
        msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
        msgPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(BLUE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        msgPanel.add(lblTitle);
        msgPanel.add(Box.createVerticalStrut(5));

        JLabel lblMsg = new JLabel("<html><body style='width:280px'>" + escapeHtml(message) + "</body></html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(55, 65, 81));
        lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
        msgPanel.add(lblMsg);

        content.add(msgPanel, BorderLayout.CENTER);

        ImageIcon logo = AppConfig.loadLogo(36, 36);
        if (logo != null) {
            JLabel lblLogo = new JLabel(logo);
            lblLogo.setVerticalAlignment(SwingConstants.TOP);
            content.add(lblLogo, BorderLayout.EAST);
        }

        d.add(content, BorderLayout.CENTER);

        // Bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        JButton btnNo = createStyledButton("Tidak", GRAY);
        btnNo.addActionListener(e -> d.dispose());
        bottom.add(btnNo);

        JButton btnYes = createStyledButton("  Ya  ", BLUE);
        btnYes.addActionListener(e -> { result[0] = true; d.dispose(); });
        bottom.add(btnYes);

        d.add(bottom, BorderLayout.SOUTH);
        d.pack();
        d.setMinimumSize(new Dimension(420, 190));
        d.setResizable(false);
        d.setLocationRelativeTo(parent);
        d.setVisible(true);

        return result[0];
    }

    // ─── Option Dialog (export choices etc) ────────────────────

    public static int showOptions(Component parent, String message, String title, String[] options) {
        Window win = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        JDialog d = new JDialog(win instanceof Frame ? (Frame) win : null, title, true);
        d.setLayout(new BorderLayout());

        int[] result = {-1};

        // Accent bar
        JPanel bar = new JPanel();
        bar.setBackground(BLUE);
        bar.setPreferredSize(new Dimension(0, 5));
        d.add(bar, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new BorderLayout(14, 0));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(22, 24, 18, 24));

        JLabel lblIcon = new JLabel("\uD83D\uDCC4", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        lblIcon.setPreferredSize(new Dimension(50, 50));
        content.add(lblIcon, BorderLayout.WEST);

        JPanel msgPanel = new JPanel();
        msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
        msgPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(BLUE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        msgPanel.add(lblTitle);
        msgPanel.add(Box.createVerticalStrut(5));

        JLabel lblMsg = new JLabel("<html><body style='width:280px'>" + escapeHtml(message) + "</body></html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(55, 65, 81));
        lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
        msgPanel.add(lblMsg);

        content.add(msgPanel, BorderLayout.CENTER);

        ImageIcon logo = AppConfig.loadLogo(36, 36);
        if (logo != null) {
            JLabel lblLogo = new JLabel(logo);
            lblLogo.setVerticalAlignment(SwingConstants.TOP);
            content.add(lblLogo, BorderLayout.EAST);
        }

        d.add(content, BorderLayout.CENTER);

        // Bottom with option buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        Color[] btnColors = {BLUE, GREEN, GRAY, AMBER};
        for (int i = 0; i < options.length; i++) {
            final int idx = i;
            String opt = options[i];
            boolean isCancel = opt.equalsIgnoreCase("Batal") || opt.equalsIgnoreCase("Cancel");
            JButton btn = createStyledButton(opt, isCancel ? GRAY : btnColors[i % btnColors.length]);
            btn.addActionListener(e -> { result[0] = idx; d.dispose(); });
            bottom.add(btn);
        }

        d.add(bottom, BorderLayout.SOUTH);
        d.pack();
        d.setMinimumSize(new Dimension(420, 190));
        d.setResizable(false);
        d.setLocationRelativeTo(parent);
        d.setVisible(true);

        return result[0];
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
