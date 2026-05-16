package com.mycompany.garagemanagementsystem.ui;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class JCalendarDialog extends JDialog {
    private JDateChooser dateChooser;
    private Date selectedDate;
    private boolean confirmed = false;

    public JCalendarDialog(Window owner, Date initialDate) {
        super((Frame) owner, "Pilih Tanggal", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        initComponents(initialDate);
    }

    private void initComponents(Date initialDate) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dateChooser = new JDateChooser();
        dateChooser.setDate(initialDate != null ? initialDate : new Date());
        mainPanel.add(dateChooser, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(e -> {
            selectedDate = dateChooser.getDate();
            confirmed = true;
            dispose();
        });
        buttonPanel.add(btnOK);

        JButton btnCancel = new JButton("Batal");
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnCancel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
