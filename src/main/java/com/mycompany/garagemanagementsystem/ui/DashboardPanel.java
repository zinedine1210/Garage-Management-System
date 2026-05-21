package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.DashboardDAO;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Dashboard utama - revamped dengan desain modern.
 */
public class DashboardPanel extends javax.swing.JPanel {

    private final DashboardDAO dashDAO = new DashboardDAO();
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // Stat card labels
    private JLabel lblTransVal, lblOmzetVal, lblAntrianVal, lblPengerjaanVal, lblSelesaiVal, lblStokKritisVal;
    // Finance labels
    private JLabel lblOmzetMinggu, lblOmzetBulan, lblOmzetTahun, lblMekanikTerajin;
    // Charts
    private ChartPanel chartPanelTipe, chartPanelKategori, chartPanelOmzetBulanan;
    // Tables
    private DefaultTableModel tblAntrianModel, tblSparepartModel;
    private JTable tblAntrian, tblSparepart;
    // Kinerja panel
    private JPanel pnlKinerjaContent;
    // Filter
    private JLabel lblDateFrom, lblDateTo;
    private Date filterDateFrom, filterDateTo;

    // Color palette
    private static final Color BG = new Color(243, 245, 249);
    private static final Color CARD_SHADOW = new Color(0, 0, 0, 20);

    public DashboardPanel() {
        buildUI();
        setDefaultDateRange();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);

        // Main scrollable content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1) Filter bar
        content.add(buildFilterBar());
        content.add(Box.createVerticalStrut(14));

        // 2) Stat cards row
        content.add(buildStatCards());
        content.add(Box.createVerticalStrut(14));

        // 3) Charts row
        content.add(buildChartsRow());
        content.add(Box.createVerticalStrut(14));

        // 4) Bottom row
        content.add(buildBottomRow());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    // ===================== FILTER BAR =====================
    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(6, 14, 6, 14)));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JLabel icon = new JLabel("📅");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        bar.add(icon);
        bar.add(label("Filter:", true));

        bar.add(label("Dari:", false));
        lblDateFrom = new JLabel("---");
        lblDateFrom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bar.add(lblDateFrom);
        bar.add(smallButton("Pilih", e -> pickDate(true)));

        bar.add(label("Sampai:", false));
        lblDateTo = new JLabel("---");
        lblDateTo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bar.add(lblDateTo);
        bar.add(smallButton("Pilih", e -> pickDate(false)));

        bar.add(Box.createHorizontalStrut(6));
        JButton btnRefresh = smallButton("🔄 Refresh", e -> loadData());
        btnRefresh.setBackground(new Color(59, 130, 246));
        btnRefresh.setForeground(Color.WHITE);
        bar.add(btnRefresh);

        JButton btnReset = smallButton("Reset", e -> { setDefaultDateRange(); loadData(); });
        bar.add(btnReset);

        JButton btnPrint = smallButton("📄 Export", e -> printDashboard());
        bar.add(btnPrint);

        return bar;
    }

    // ===================== STAT CARDS =====================
    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 6, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        lblTransVal = new JLabel("0");
        row.add(createStatCard("📊", "Transaksi", lblTransVal, new Color(59, 130, 246), new Color(37, 99, 235)));

        lblOmzetVal = new JLabel("Rp 0");
        row.add(createStatCard("💰", "Omzet", lblOmzetVal, new Color(16, 185, 129), new Color(5, 150, 105)));

        lblAntrianVal = new JLabel("0");
        row.add(createStatCard("⏳", "Antrian", lblAntrianVal, new Color(245, 158, 11), new Color(217, 119, 6)));

        lblPengerjaanVal = new JLabel("0");
        row.add(createStatCard("🔧", "Pengerjaan", lblPengerjaanVal, new Color(249, 115, 22), new Color(234, 88, 12)));

        lblSelesaiVal = new JLabel("0");
        row.add(createStatCard("✅", "Selesai", lblSelesaiVal, new Color(139, 92, 246), new Color(109, 40, 217)));

        lblStokKritisVal = new JLabel("0");
        row.add(createStatCard("⚠️", "Stok Kritis", lblStokKritisVal, new Color(239, 68, 68), new Color(220, 38, 38)));

        return row;
    }

    private JPanel createStatCard(String emoji, String title, JLabel valueLabel, Color color1, Color color2) {
        JPanel card = new JPanel(new BorderLayout(0, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(CARD_SHADOW);
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 16, 16);
                // Gradient
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Top: emoji + title
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        top.setOpaque(false);
        JLabel emojiLbl = new JLabel(emoji);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        top.add(emojiLbl);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(255, 255, 255, 200));
        top.add(titleLbl);
        card.add(top, BorderLayout.NORTH);

        // Center: value
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);
        valueLabel.setBorder(new EmptyBorder(0, 6, 0, 0));
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // ===================== CHARTS ROW =====================
    private JPanel buildChartsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        row.setPreferredSize(new Dimension(0, 280));

        chartPanelTipe = createStyledChartPanel();
        row.add(wrapChart(chartPanelTipe, "Tipe Kendaraan"));

        chartPanelKategori = createStyledChartPanel();
        row.add(wrapChart(chartPanelKategori, "Kategori Servis"));

        chartPanelOmzetBulanan = createStyledChartPanel();
        row.add(wrapChart(chartPanelOmzetBulanan, "Omzet Bulanan"));

        return row;
    }

    private ChartPanel createStyledChartPanel() {
        ChartPanel cp = new ChartPanel(null);
        cp.setMouseWheelEnabled(false);
        cp.setPopupMenu(null);
        return cp;
    }

    private JPanel wrapChart(ChartPanel cp, String title) {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_SHADOW);
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 14, 14);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 14, 14);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(55, 65, 81));
        lbl.setBorder(new EmptyBorder(0, 4, 6, 0));
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(cp, BorderLayout.CENTER);
        return wrapper;
    }

    // ===================== BOTTOM ROW =====================
    private JPanel buildBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        row.setPreferredSize(new Dimension(0, 240));

        // Finance panel
        row.add(buildFinanceCard());
        // Kinerja mekanik
        row.add(buildKinerjaCard());
        // Antrian table
        row.add(buildTableCard("Antrian Hari Ini", true));
        // Sparepart terlaris
        row.add(buildTableCard("Sparepart Terlaris", false));

        return row;
    }

    private JPanel buildFinanceCard() {
        JPanel card = createWhiteCard();
        card.setLayout(new BorderLayout());
        JLabel titleLbl = cardTitle("💵 Keuangan & Performa");
        card.add(titleLbl, BorderLayout.NORTH);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(8, 4, 4, 4));

        lblOmzetMinggu = financeLabel("Omzet Minggu: Rp 0");
        inner.add(lblOmzetMinggu);
        inner.add(Box.createVerticalStrut(8));
        lblOmzetBulan = financeLabel("Omzet Bulan: Rp 0");
        inner.add(lblOmzetBulan);
        inner.add(Box.createVerticalStrut(8));
        lblOmzetTahun = financeLabel("Omzet Tahun: Rp 0");
        inner.add(lblOmzetTahun);
        inner.add(Box.createVerticalStrut(12));
        inner.add(new JSeparator());
        inner.add(Box.createVerticalStrut(8));
        lblMekanikTerajin = financeLabel("🏆 Mekanik Terajin: -");
        lblMekanikTerajin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        inner.add(lblMekanikTerajin);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    private JLabel financeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(55, 65, 81));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel buildKinerjaCard() {
        JPanel card = createWhiteCard();
        card.setLayout(new BorderLayout());
        card.add(cardTitle("🏗️ Kinerja Mekanik"), BorderLayout.NORTH);

        pnlKinerjaContent = new JPanel();
        pnlKinerjaContent.setOpaque(false);
        pnlKinerjaContent.setLayout(new BoxLayout(pnlKinerjaContent, BoxLayout.Y_AXIS));
        pnlKinerjaContent.setBorder(new EmptyBorder(6, 4, 4, 4));

        JScrollPane sp = new JScrollPane(pnlKinerjaContent);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTableCard(String title, boolean isAntrian) {
        JPanel card = createWhiteCard();
        card.setLayout(new BorderLayout());
        card.add(cardTitle((isAntrian ? "📋 " : "🔥 ") + title), BorderLayout.NORTH);

        JTable tbl = new JTable();
        styleSmallTable(tbl);
        if (isAntrian) {
            tblAntrianModel = new DefaultTableModel(new Object[]{"No Polisi", "Status"}, 0);
            tbl.setModel(tblAntrianModel);
            tblAntrian = tbl;
        } else {
            tblSparepartModel = new DefaultTableModel(new Object[]{"Sparepart", "Qty"}, 0);
            tbl.setModel(tblSparepartModel);
            tblSparepart = tbl;
        }

        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(null);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private void styleSmallTable(JTable tbl) {
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl.setRowHeight(26);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionBackground(new Color(219, 234, 254));
        tbl.setSelectionForeground(new Color(30, 58, 138));

        JTableHeader header = tbl.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(43, 45, 66));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 205, 215)));
        header.setPreferredSize(new Dimension(0, 30));

        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                return this;
            }
        });
    }

    // ===================== HELPERS =====================
    private JPanel createWhiteCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_SHADOW);
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 14, 14);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));
        return card;
    }

    private JLabel cardTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(55, 65, 81));
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
        return lbl;
    }

    private JLabel label(String text, boolean bold) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 12));
        return lbl;
    }

    private JButton smallButton(String text, java.awt.event.ActionListener al) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(3, 10, 3, 10));
        btn.addActionListener(al);
        return btn;
    }

    // ===================== DATE PICKERS =====================
    private void setDefaultDateRange() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
        filterDateFrom = cal.getTime();
        cal.add(Calendar.MONTH, 1); cal.add(Calendar.DAY_OF_MONTH, -1);
        filterDateTo = cal.getTime();
        lblDateFrom.setText(sdf.format(filterDateFrom));
        lblDateTo.setText(sdf.format(filterDateTo));
    }

    private void pickDate(boolean isFrom) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        JCalendarDialog dialog = new JCalendarDialog(owner, isFrom ? filterDateFrom : filterDateTo);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (isFrom) { filterDateFrom = dialog.getSelectedDate(); lblDateFrom.setText(sdf.format(filterDateFrom)); }
            else { filterDateTo = dialog.getSelectedDate(); lblDateTo.setText(sdf.format(filterDateTo)); }
            loadData();
        }
    }

    private void printDashboard() {
        Object[] options = {"PDF", "Excel", "Batal"};
        int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Dashboard", new String[]{"PDF", "Excel", "Batal"});
        if (choice == 0) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToPDF(tblAntrian, "Dashboard_Antrian");
        else if (choice == 1) com.mycompany.garagemanagementsystem.util.ExportUtils.exportTableToExcel(tblAntrian, "Dashboard_Antrian");
    }

    // ===================== LOAD DATA =====================
    public void loadData() {
        try {
            // Stat cards
            if (filterDateFrom != null && filterDateTo != null) {
                lblTransVal.setText(String.valueOf(dashDAO.getTransaksiByDateRange(filterDateFrom, filterDateTo)));
                lblOmzetVal.setText(nf.format(dashDAO.getOmzetByDateRange(filterDateFrom, filterDateTo)));
            } else {
                lblTransVal.setText(String.valueOf(dashDAO.getTransaksiHariIni()));
                lblOmzetVal.setText(nf.format(dashDAO.getOmzetHariIni()));
            }
            lblAntrianVal.setText(String.valueOf(dashDAO.getTotalAntrean()));
            lblPengerjaanVal.setText(String.valueOf(dashDAO.getDalamPengerjaan()));
            lblSelesaiVal.setText(String.valueOf(dashDAO.getServisSelesai()));
            lblStokKritisVal.setText(String.valueOf(dashDAO.getStokKritis()));

            // Finance
            lblOmzetMinggu.setText("📅 Minggu: " + nf.format(dashDAO.getOmzetMingguan()));
            lblOmzetBulan.setText("📅 Bulan: " + nf.format(dashDAO.getOmzetBulanIni()));
            lblOmzetTahun.setText("📅 Tahun: " + nf.format(dashDAO.getOmzetTahunan()));
            lblMekanikTerajin.setText("🏆 Terajin: " + dashDAO.getMekanikTerajin());

            // Antrian table
            tblAntrianModel.setRowCount(0);
            for (Object[] row : dashDAO.getTabelAntrean()) {
                tblAntrianModel.addRow(row);
            }

            // Sparepart terlaris table
            tblSparepartModel.setRowCount(0);
            for (Object[] row : dashDAO.getSparepartTerlaris()) {
                tblSparepartModel.addRow(row);
            }

            // Kinerja mekanik
            pnlKinerjaContent.removeAll();
            List<Object[]> kinerja = dashDAO.getKinerjaMekanik();
            int maxServis = 1;
            for (Object[] k : kinerja) maxServis = Math.max(maxServis, (int) k[1]);
            Color[] barColors = {
                new Color(59, 130, 246), new Color(16, 185, 129), new Color(245, 158, 11),
                new Color(139, 92, 246), new Color(249, 115, 22), new Color(239, 68, 68)
            };
            int ci = 0;
            for (Object[] k : kinerja) {
                JLabel lbl = new JLabel("  " + k[0] + " — " + k[1] + " servis");
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                pnlKinerjaContent.add(lbl);
                pnlKinerjaContent.add(Box.createVerticalStrut(2));

                JProgressBar pb = new JProgressBar(0, maxServis);
                pb.setValue((int) k[1]);
                pb.setStringPainted(true);
                pb.setString(k[1] + "");
                pb.setFont(new Font("Segoe UI", Font.BOLD, 10));
                pb.setForeground(barColors[ci % barColors.length]);
                pb.setBackground(new Color(240, 240, 245));
                pb.setBorderPainted(false);
                pb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
                pb.setAlignmentX(Component.LEFT_ALIGNMENT);
                pnlKinerjaContent.add(pb);
                pnlKinerjaContent.add(Box.createVerticalStrut(6));
                ci++;
            }
            pnlKinerjaContent.revalidate();
            pnlKinerjaContent.repaint();

            // Charts
            updateCharts();
        } catch (SQLException ex) {
            System.err.println("Dashboard error: " + ex.getMessage());
        }
    }

    private void updateCharts() throws SQLException {
        // Pie: Tipe Kendaraan
        DefaultPieDataset dsTipe = new DefaultPieDataset();
        for (Object[] r : dashDAO.getPerbandinganTipeKendaraan()) {
            dsTipe.setValue(r[0].toString(), (int) r[1]);
        }
        JFreeChart pieChart1 = ChartFactory.createPieChart(null, dsTipe, true, true, false);
        stylePieChart(pieChart1);
        chartPanelTipe.setChart(pieChart1);

        // Pie: Kategori Servis
        DefaultPieDataset dsKat = new DefaultPieDataset();
        for (Object[] r : dashDAO.getKategoriServis()) {
            dsKat.setValue(r[0].toString(), (int) r[1]);
        }
        JFreeChart pieChart2 = ChartFactory.createPieChart(null, dsKat, true, true, false);
        stylePieChart(pieChart2);
        chartPanelKategori.setChart(pieChart2);

        // Bar: Omzet Bulanan
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        String[] bulanNama = {"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        for (Object[] r : dashDAO.getOmzetPerBulanTahunIni()) {
            int bulan = (int) r[0];
            ds.addValue((double) r[1], "Omzet", bulanNama[bulan]);
        }
        JFreeChart barChart = ChartFactory.createBarChart(null, null, null, ds,
                PlotOrientation.VERTICAL, false, true, false);
        styleBarChart(barChart);
        chartPanelOmzetBulanan.setChart(barChart);
    }

    private void stylePieChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200));
        plot.setLabelShadowPaint(null);
        plot.setLabelOutlinePaint(null);
        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 10));
            chart.getLegend().setBackgroundPaint(Color.WHITE);
            chart.getLegend().setBorder(0, 0, 0, 0);
        }
    }

    private void styleBarChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(230, 230, 235));
        plot.setDomainGridlinesVisible(false);
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(59, 130, 246));
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.08);
    }
}
