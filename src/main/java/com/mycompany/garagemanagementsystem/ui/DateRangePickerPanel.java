package com.mycompany.garagemanagementsystem.ui;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BiConsumer;

/**
 * Reusable date range picker with preset buttons and custom range.
 * Presets: Hari Ini, Minggu Ini, Minggu Lalu, Bulan Ini, Bulan Lalu
 * Custom: Start/End date choosers shown inline when "Tanggal Lainnya" is selected.
 */
public class DateRangePickerPanel extends JPanel {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd MMM yyyy");
    private static final Color BG = Color.WHITE;
    private static final Color ACTIVE_BG = new Color(59, 130, 246);
    private static final Color ACTIVE_FG = Color.WHITE;
    private static final Color INACTIVE_BG = new Color(243, 245, 249);
    private static final Color INACTIVE_FG = new Color(55, 65, 81);
    private static final Color BORDER_COLOR = new Color(220, 225, 235);

    private Date fromDate, toDate;
    private BiConsumer<Date, Date> onDateChange;
    private JButton[] presetButtons;
    private JButton btnCustom;
    private JPanel customPanel;
    private JDateChooser dateFrom, dateTo;
    private JLabel lblRange;
    private int activeIndex = -1;
    private boolean initialized = false;

    public DateRangePickerPanel(BiConsumer<Date, Date> onDateChange) {
        this.onDateChange = onDateChange;
        setLayout(new BorderLayout(0, 4));
        setBackground(BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(8, 12, 8, 12)));
        buildUI();
        selectPreset(3); // default: Bulan Ini
        initialized = true;
    }

    private void buildUI() {
        // Top row: preset buttons + range label
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        topRow.setOpaque(false);

        topRow.add(makeLabel("Periode:"));

        String[] presets = {"Hari Ini", "Minggu Ini", "Minggu Lalu", "Bulan Ini", "Bulan Lalu"};
        presetButtons = new JButton[presets.length];
        for (int i = 0; i < presets.length; i++) {
            final int idx = i;
            JButton btn = makePresetButton(presets[i]);
            btn.addActionListener(e -> selectPreset(idx));
            presetButtons[i] = btn;
            topRow.add(btn);
        }

        btnCustom = makePresetButton("Tanggal Lainnya");
        btnCustom.addActionListener(e -> showCustomRange());
        topRow.add(btnCustom);

        topRow.add(Box.createHorizontalStrut(12));
        lblRange = new JLabel("");
        lblRange.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRange.setForeground(new Color(59, 130, 246));
        topRow.add(lblRange);

        add(topRow, BorderLayout.NORTH);

        // Custom range row (hidden by default)
        customPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        customPanel.setOpaque(false);
        customPanel.setVisible(false);

        customPanel.add(makeLabel("Dari:"));
        dateFrom = new JDateChooser();
        dateFrom.setPreferredSize(new Dimension(150, 28));
        dateFrom.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customPanel.add(dateFrom);

        customPanel.add(makeLabel("Sampai:"));
        dateTo = new JDateChooser();
        dateTo.setPreferredSize(new Dimension(150, 28));
        dateTo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customPanel.add(dateTo);

        JButton btnApply = new JButton("Terapkan");
        btnApply.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnApply.setBackground(new Color(59, 130, 246));
        btnApply.setForeground(Color.WHITE);
        btnApply.setFocusPainted(false);
        btnApply.setBorderPainted(false);
        btnApply.setOpaque(true);
        btnApply.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnApply.setBorder(new EmptyBorder(6, 14, 6, 14));
        btnApply.addActionListener(e -> applyCustomRange());
        customPanel.add(btnApply);

        add(customPanel, BorderLayout.CENTER);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(107, 114, 128));
        return lbl;
    }

    private JButton makePresetButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(INACTIVE_BG);
        btn.setForeground(INACTIVE_FG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }

    private void highlightButton(int presetIndex, boolean isCustom) {
        for (int i = 0; i < presetButtons.length; i++) {
            boolean active = (!isCustom && i == presetIndex);
            presetButtons[i].setBackground(active ? ACTIVE_BG : INACTIVE_BG);
            presetButtons[i].setForeground(active ? ACTIVE_FG : INACTIVE_FG);
        }
        btnCustom.setBackground(isCustom ? ACTIVE_BG : INACTIVE_BG);
        btnCustom.setForeground(isCustom ? ACTIVE_FG : INACTIVE_FG);
    }

    private void selectPreset(int index) {
        activeIndex = index;
        customPanel.setVisible(false);
        highlightButton(index, false);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        switch (index) {
            case 0: // Hari Ini
                fromDate = cal.getTime();
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                toDate = cal.getTime();
                break;
            case 1: // Minggu Ini
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                fromDate = cal.getTime();
                cal.add(Calendar.DAY_OF_WEEK, 6);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                toDate = cal.getTime();
                break;
            case 2: // Minggu Lalu
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                cal.add(Calendar.WEEK_OF_YEAR, -1);
                fromDate = cal.getTime();
                cal.add(Calendar.DAY_OF_WEEK, 6);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                toDate = cal.getTime();
                break;
            case 3: // Bulan Ini
                cal.set(Calendar.DAY_OF_MONTH, 1);
                fromDate = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                toDate = cal.getTime();
                break;
            case 4: // Bulan Lalu
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.MONTH, -1);
                fromDate = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                toDate = cal.getTime();
                break;
        }
        updateLabel();
        if (initialized) onDateChange.accept(fromDate, toDate);
    }

    private void showCustomRange() {
        highlightButton(-1, true);
        customPanel.setVisible(true);
        dateFrom.setDate(fromDate);
        dateTo.setDate(toDate);
        revalidate();
    }

    private void applyCustomRange() {
        Date f = dateFrom.getDate();
        Date t = dateTo.getDate();
        if (f == null || t == null) {
            JOptionPane.showMessageDialog(this, "Pilih tanggal mulai dan akhir.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Set time boundaries
        Calendar cal = Calendar.getInstance();
        cal.setTime(f);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        fromDate = cal.getTime();

        cal.setTime(t);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        toDate = cal.getTime();

        activeIndex = -1;
        updateLabel();
        onDateChange.accept(fromDate, toDate);
    }

    private void updateLabel() {
        lblRange.setText(SDF.format(fromDate) + "  —  " + SDF.format(toDate));
    }

    public Date getFromDate() { return fromDate; }
    public Date getToDate() { return toDate; }
}
