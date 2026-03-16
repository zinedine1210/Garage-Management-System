package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.UserDAO;
import com.mycompany.garagemanagementsystem.model.User;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {
    
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("Login - Garage Management");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);

        JButton btnLogin = new JButton("Login");
        JButton btnAntrian = new JButton("Display Antrian TV");
        btnLogin.addActionListener(e -> prosesLogin());
        btnAntrian.addActionListener(e -> new QueueDashboardFrame().setVisible(true));

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel(" Username:"));
        panel.add(txtUsername);
        panel.add(new JLabel(" Password:"));
        panel.add(txtPassword);
        panel.add(new JLabel("")); // Spacer
        panel.add(btnLogin);
        panel.add(new JLabel("")); // Spacer
        panel.add(btnAntrian);

        add(panel);

        // Pastikan tabel users ada saat login frame terbuka
        initDatabase();
    }

    private void initDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) UNIQUE NOT NULL," +
                "password_hash VARCHAR(255) NOT NULL," +
                "role VARCHAR(20) DEFAULT 'admin'," +
                "nama_lengkap VARCHAR(100)" +
                ")";
        String insertAdminSQL = "INSERT INTO users (username, password_hash, role, nama_lengkap) " +
                "VALUES ('admin', 'admin123', 'admin', 'Administrator Utama') " +
                "ON DUPLICATE KEY UPDATE username=username";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {
            st.execute(createTableSQL);
            st.execute(insertAdminSQL);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menginisialisasi tabel admin: " + ex.getMessage());
        }
    }

    private void prosesLogin() {
        try {
            String uname = txtUsername.getText();
            String pwd = new String(txtPassword.getPassword());
            
            User u = userDAO.login(uname, pwd);
            if (u != null) {
                JOptionPane.showMessageDialog(this, "Selamat datang, " + u.getNamaLengkap());
                new MainMenuFrame().setVisible(true);
                this.dispose(); // Tutup halaman login
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
