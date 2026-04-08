package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.UserDAO;
import com.mycompany.garagemanagementsystem.model.User;
import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 * LoginFrame = Halaman login (tampilan pertama saat aplikasi dibuka).
 *
 * Alur kerja:
 * 1. User memasukkan username & password
 * 2. Klik tombol "Login" → prosesLogin() dipanggil
 * 3. UserDAO.login() mengecek ke database
 * 4. Jika cocok → buka MainMenuFrame, tutup LoginFrame
 * 5. Jika tidak cocok → tampilkan pesan error
 *
 * Tombol "Display Antrian TV" langsung membuka layar antrian tanpa login.
 *
 * initDatabase() dipanggil saat halaman dibuka untuk memastikan tabel 'users'
 * sudah ada di database dan ada minimal 1 admin (username: admin, password: admin123).
 */
public class LoginFrame extends javax.swing.JFrame {

    // DAO untuk mengakses tabel users di database
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        initComponents();  // Buat semua komponen UI (di-generate NetBeans)
        initDatabase();    // Pastikan tabel users ada di database
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnAntrian = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login - Garage Management");

        panel.setLayout(new java.awt.GridLayout(4, 2, 10, 10));

        jLabel1.setText("Username:");
        panel.add(jLabel1);

        txtUsername.setColumns(15);
        txtUsername.addActionListener(this::txtUsernameActionPerformed);
        panel.add(txtUsername);

        jLabel2.setText(" Password:");
        panel.add(jLabel2);

        txtPassword.setColumns(15);
        panel.add(txtPassword);
        panel.add(jLabel3);

        btnLogin.setText("Login");
        btnLogin.addActionListener(this::btnLoginActionPerformed);
        panel.add(btnLogin);
        panel.add(jLabel4);

        btnAntrian.setText("Display Antrian TV");
        btnAntrian.addActionListener(this::btnAntrianActionPerformed);
        panel.add(btnAntrian);

        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        prosesLogin();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnAntrianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAntrianActionPerformed
        new QueueDashboardFrame().setVisible(true);
    }//GEN-LAST:event_btnAntrianActionPerformed

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

    private void initDatabase() {
        // SQL untuk membuat tabel users jika belum ada
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) UNIQUE NOT NULL," +
                "password_hash VARCHAR(255) NOT NULL," +
                "role VARCHAR(20) DEFAULT 'admin'," +
                "nama_lengkap VARCHAR(100)" +
                ")";

        // SQL untuk menambah user admin default (jika belum ada)
        // ON DUPLICATE KEY UPDATE = jika username sudah ada, tidak error
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

    /**
     * Proses login: ambil input user, cocokkan dengan database.
     */
    private void prosesLogin() {
        try {
            // Ambil input dari text field
            String uname = txtUsername.getText();
            String pwd = new String(txtPassword.getPassword());

            // Cek ke database via UserDAO
            User u = userDAO.login(uname, pwd);

            if (u != null) {
                // Login berhasil → buka menu utama, tutup halaman login
                JOptionPane.showMessageDialog(this, "Selamat datang, " + u.getNamaLengkap());
                new MainMenuFrame().setVisible(true);
                this.dispose(); // Tutup window login
            } else {
                // Login gagal → tampilkan pesan error
                JOptionPane.showMessageDialog(this, "Username atau Password salah!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAntrian;
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel panel;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
