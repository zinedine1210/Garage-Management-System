/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.garagemanagementsystem;

import com.mycompany.garagemanagementsystem.ui.MainMenuFrame;
import javax.swing.SwingUtilities;

public class GarageManagementSystem {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenuFrame().setVisible(true);
        });
    }
}
