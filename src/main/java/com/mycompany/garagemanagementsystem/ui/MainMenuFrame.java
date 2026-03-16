package com.mycompany.garagemanagementsystem.ui;

import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainMenuFrame extends JFrame {

    public MainMenuFrame() {
        setTitle("Garage Management System");
        setSize(700, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton btnClient = new JButton("Master Client");
        JButton btnVehicle = new JButton("Master Vehicle");
        JButton btnMekanik = new JButton("Master Mekanik");
        JButton btnSparepart = new JButton("Master Sparepart");
        JButton btnSupplier = new JButton("Master Supplier");
        JButton btnTransaksi = new JButton("Transaksi Servis");

        btnClient.addActionListener(e -> new ClientFrame((Frame) this).setVisible(true));
        btnVehicle.addActionListener(e -> new VehicleFrame((Frame) this).setVisible(true));
        btnMekanik.addActionListener(e -> new MekanikFrame((Frame) this).setVisible(true));
        btnSparepart.addActionListener(e -> new SparepartFrame((Frame) this).setVisible(true));
        btnSupplier.addActionListener(e -> new SupplierFrame((Frame) this).setVisible(true));
        btnTransaksi.addActionListener(e -> new ServiceTransactionFrame((Frame) this).setVisible(true));

        JPanel panel = new JPanel();
        panel.add(btnClient);
        panel.add(btnVehicle);
        panel.add(btnMekanik);
        panel.add(btnSparepart);
        panel.add(btnSupplier);
        panel.add(btnTransaksi);

        add(panel);
    }
}


