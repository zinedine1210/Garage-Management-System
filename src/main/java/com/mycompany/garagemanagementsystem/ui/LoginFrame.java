package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.UserDAO;
import com.mycompany.garagemanagementsystem.model.User;
import com.mycompany.garagemanagementsystem.util.AppConfig;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Login frame - desain modern split-screen dengan gambar di samping.
 */
public class LoginFrame extends javax.swing.JFrame {

    private final UserDAO userDAO = new UserDAO();
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    // Ukuran gambar login yang direkomendasikan: 600 x 700 px
    // Set path di app.properties: app.login.image.path=/path/to/gambar.png
    private static final int FRAME_W = 900;
    private static final int FRAME_H = 580;
    private static final int IMAGE_PANEL_W = 420;

    public LoginFrame() {
        initFrame();
        initDatabase();
    }

    private void initFrame() {
        setTitle("Login - " + AppConfig.getAppName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(FRAME_W, FRAME_H);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // ====== LEFT: Image Panel ======
        root.add(buildImagePanel(), BorderLayout.WEST);

        // ====== RIGHT: Login Form ======
        root.add(buildFormPanel(), BorderLayout.CENTER);
    }

    // ===================== IMAGE PANEL =====================
    private JPanel buildImagePanel() {
        JPanel imagePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Try loading user's image
                ImageIcon img = AppConfig.loadLoginImage(getWidth(), getHeight());
                if (img != null) {
                    g2.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
                    // Semi-transparent overlay at bottom for company info
                    int overlayH = 90;
                    int overlayY = getHeight() - overlayH;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.setColor(new Color(20, 22, 36));
                    g2.fillRect(0, overlayY, getWidth(), overlayH);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    // Company text on overlay
                    g2.setColor(new Color(255, 255, 255, 230));
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    drawCenteredString(g2, AppConfig.getCompanyName(), getWidth(), overlayY + 28);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    g2.setColor(new Color(255, 255, 255, 180));
                    drawCenteredString(g2, AppConfig.getCompanyAddress(), getWidth(), overlayY + 48);
                    drawCenteredString(g2, AppConfig.getCompanyPhoneFormatted(), getWidth(), overlayY + 66);
                } else {
                    // Fallback: gradient background with decorative shapes
                    GradientPaint gp = new GradientPaint(0, 0, new Color(30, 32, 48),
                            getWidth(), getHeight(), new Color(59, 130, 246));
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    // Decorative circles
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
                    g2.setColor(Color.WHITE);
                    g2.fillOval(-60, -60, 300, 300);
                    g2.fillOval(getWidth() - 150, getHeight() - 200, 280, 280);
                    g2.fillOval(50, getHeight() - 120, 180, 180);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                    // Text on gradient
                    g2.setColor(new Color(255, 255, 255, 220));
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                    drawCenteredString(g2, AppConfig.getAppName(), getWidth(), getHeight() / 2 - 30);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    g2.setColor(new Color(255, 255, 255, 160));
                    drawCenteredString(g2, AppConfig.getCompanyName(), getWidth(), getHeight() / 2 + 10);
                    drawCenteredString(g2, AppConfig.getCompanyAddress(), getWidth(), getHeight() / 2 + 32);
                    drawCenteredString(g2, AppConfig.getCompanyPhoneFormatted(), getWidth(), getHeight() / 2 + 52);
                }
                g2.dispose();
            }

            private void drawCenteredString(Graphics2D g2, String text, int w, int y) {
                FontMetrics fm = g2.getFontMetrics();
                int x = (w - fm.stringWidth(text)) / 2;
                g2.drawString(text, x, y);
            }
        };
        imagePanel.setPreferredSize(new Dimension(IMAGE_PANEL_W, FRAME_H));
        imagePanel.setBackground(new Color(30, 32, 48));
        return imagePanel;
    }

    // ===================== FORM PANEL =====================
    private JPanel buildFormPanel() {
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setBackground(Color.WHITE);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(30, 50, 30, 50));
        inner.setMaximumSize(new Dimension(380, 500));

        // Logo
        ImageIcon logo = AppConfig.loadLogo(56, 56);
        if (logo != null) {
            JLabel logoLabel = new JLabel(logo);
            logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            inner.add(logoLabel);
            inner.add(Box.createVerticalStrut(14));
        }

        // Title
        JLabel lblTitle = new JLabel("Selamat Datang 👋");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(30, 32, 48));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(lblTitle);
        inner.add(Box.createVerticalStrut(4));

        JLabel lblSubtitle = new JLabel("Silahkan login untuk melanjutkan");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(107, 114, 128));
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(lblSubtitle);
        inner.add(Box.createVerticalStrut(30));

        // Username
        inner.add(fieldLabel("Username"));
        inner.add(Box.createVerticalStrut(6));
        txtUsername = createStyledTextField();
        inner.add(txtUsername);
        inner.add(Box.createVerticalStrut(16));

        // Password
        inner.add(fieldLabel("Password"));
        inner.add(Box.createVerticalStrut(6));
        txtPassword = createStyledPasswordField();
        inner.add(txtPassword);
        inner.add(Box.createVerticalStrut(28));

        // Login button
        JButton btnLogin = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setPaint(new Color(37, 99, 235));
                } else if (getModel().isRollover()) {
                    g2.setPaint(new Color(79, 150, 255));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(59, 130, 246), 0, getHeight(), new Color(37, 99, 235)));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(0, 44));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> prosesLogin());
        inner.add(btnLogin);
        inner.add(Box.createVerticalStrut(14));

        // Separator
        JPanel sepRow = new JPanel(new BorderLayout(8, 0));
        sepRow.setOpaque(false);
        sepRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        sepRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        sepRow.add(new JSeparator(), BorderLayout.CENTER);
        inner.add(sepRow);
        inner.add(Box.createVerticalStrut(14));

        // Antrian TV button
        JButton btnAntrian = new JButton("📺 Display Antrian TV");
        btnAntrian.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAntrian.setForeground(new Color(59, 130, 246));
        btnAntrian.setBackground(Color.WHITE);
        btnAntrian.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(8, 16, 8, 16)));
        btnAntrian.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnAntrian.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAntrian.setFocusPainted(false);
        btnAntrian.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAntrian.addActionListener(e -> {
            new QueueDashboardFrame().setVisible(true);
        });
        inner.add(btnAntrian);

        // Enter key binding
        getRootPane().setDefaultButton(btnLogin);

        form.add(inner);
        return form;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(55, 65, 81));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? new Color(59, 130, 246) : new Color(209, 213, 219));
                g2.setStroke(new BasicStroke(isFocusOwner() ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(6, 12, 6, 12));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { field.repaint(); }
            public void focusLost(FocusEvent e) { field.repaint(); }
        });
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? new Color(59, 130, 246) : new Color(209, 213, 219));
                g2.setStroke(new BasicStroke(isFocusOwner() ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(6, 12, 6, 12));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { field.repaint(); }
            public void focusLost(FocusEvent e) { field.repaint(); }
        });
        return field;
    }

    // ===================== LOGIN LOGIC =====================
    private void prosesLogin() {
        try {
            String uname = txtUsername.getText();
            String pwd = new String(txtPassword.getPassword());

            User u = userDAO.login(uname, pwd);
            if (u != null) {
                UIHelper.success(this, "Selamat datang, " + u.getNamaLengkap());
                new MainMenuFrame(u).setVisible(true);
                this.dispose();
            } else {
                UIHelper.error(this, "Username atau Password salah!");
            }
        } catch (Exception ex) {
            UIHelper.error(this, "Terjadi kesalahan: " + ex.getMessage());
        }
    }

    private void initDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "user_id INT AUTO_INCREMENT PRIMARY KEY,"
                + "username VARCHAR(50) UNIQUE NOT NULL,"
                + "password_hash VARCHAR(255) NOT NULL,"
                + "role VARCHAR(20) DEFAULT 'admin',"
                + "nama_lengkap VARCHAR(100)"
                + ")";
        String insertAdminSQL = "INSERT INTO users (username, password_hash, role, nama_lengkap) "
                + "VALUES ('admin', 'admin123', 'admin', 'Administrator Utama') "
                + "ON DUPLICATE KEY UPDATE username=username";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {
            st.execute(createTableSQL);
            st.execute(insertAdminSQL);
        } catch (Exception ex) {
            UIHelper.error(this, "Gagal menginisialisasi tabel admin: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
