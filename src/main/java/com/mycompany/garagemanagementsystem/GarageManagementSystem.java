package com.mycompany.garagemanagementsystem;

import com.mycompany.garagemanagementsystem.ui.LoginFrame;
import javax.swing.SwingUtilities;

public class GarageManagementSystem {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
