package com.mycompany.garagemanagementsystem;

import com.mycompany.garagemanagementsystem.ui.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * Kelas utama (Main Class) - titik awal aplikasi dijalankan.
 *
 * Alur: main() → buat LoginFrame → user login → buka MainMenuFrame.
 * SwingUtilities.invokeLater() memastikan GUI dibuat di thread yang benar.
 */
public class GarageManagementSystem {

    public static void main(String[] args) {
        // Jalankan aplikasi dengan menampilkan halaman Login
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
