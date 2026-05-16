package com.mycompany.garagemanagementsystem.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class DatePickerDialog extends JDialog {

    private Calendar calendar = Calendar.getInstance();
    private Date selectedDate;
    private boolean confirmed = false;

    private JSpinner spinYear;
    private JComboBox<String> cbMonth;
    private JComboBox<Integer> cbDay;

    public DatePickerDialog(Window owner, Date initialDate) {
        super(owner, "Pilih Tanggal", Dialog.ModalityType.APPLICATION_MODAL);
        if (initialDate != null) {
            calendar.setTime(initialDate);
        }
        buildUI();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(300, 180);
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        datePanel.add(new JLabel("Tahun:"));
        spinYear = new JSpinner(new SpinnerNumberModel(calendar.get(Calendar.YEAR), 2000, 2100, 1));
        spinYear.setPreferredSize(new java.awt.Dimension(70, 25));
        datePanel.add(spinYear);

        datePanel.add(new JLabel("Bulan:"));
        cbMonth = new JComboBox<>(new String[]{
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        });
        cbMonth.setSelectedIndex(calendar.get(Calendar.MONTH));
        cbMonth.addActionListener(e -> updateDays());
        datePanel.add(cbMonth);

        datePanel.add(new JLabel("Hari:"));
        cbDay = new JComboBox<>();
        updateDays();
        cbDay.setSelectedItem(calendar.get(Calendar.DAY_OF_MONTH));
        datePanel.add(cbDay);

        mainPanel.add(datePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(e -> confirm());
        buttonPanel.add(btnOK);

        JButton btnCancel = new JButton("Batal");
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnCancel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        getRootPane().setDefaultButton(btnOK);
    }

    private void updateDays() {
        int year = (Integer) spinYear.getValue();
        int month = cbMonth.getSelectedIndex();
        calendar.set(year, month, 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Object selected = cbDay.getSelectedItem();
        cbDay.removeAllItems();
        for (int i = 1; i <= maxDay; i++) {
            cbDay.addItem(i);
        }
        if (selected != null && (Integer) selected <= maxDay) {
            cbDay.setSelectedItem(selected);
        } else {
            cbDay.setSelectedIndex(0);
        }
    }

    private void confirm() {
        calendar.set(
            (Integer) spinYear.getValue(),
            cbMonth.getSelectedIndex(),
            (Integer) cbDay.getSelectedItem()
        );
        selectedDate = calendar.getTime();
        confirmed = true;
        dispose();
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
