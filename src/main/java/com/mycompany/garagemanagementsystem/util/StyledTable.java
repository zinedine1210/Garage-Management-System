package com.mycompany.garagemanagementsystem.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

/**
 * Komponen tabel cantik dengan pagination, alternating row colors, dan styled header.
 */
public class StyledTable extends JPanel {

    private final JTable table;
    private final DefaultTableModel fullModel;
    private final JPanel paginationPanel;
    private final JLabel lblInfo;
    private final JComboBox<Integer> cbPageSize;
    private final JButton btnFirst, btnPrev, btnNext, btnLast;
    private final JLabel lblPage;
    private final JTextField txtSearch;

    private int currentPage = 1;
    private int pageSize = 20;
    private List<Object[]> allData = new ArrayList<>();
    private List<Object[]> filteredData = new ArrayList<>();
    private String[] columnNames = {};

    // Colors
    private static final Color HEADER_BG = new Color(43, 45, 66);
    private static final Color HEADER_FG = Color.WHITE;
    private static final Color ROW_EVEN = Color.WHITE;
    private static final Color ROW_ODD = new Color(245, 247, 250);
    private static final Color ROW_SELECTED = new Color(200, 220, 255);
    private static final Color GRID_COLOR = new Color(220, 225, 235);
    private static final Color PAGINATION_BG = new Color(248, 249, 252);

    public StyledTable() {
        setLayout(new BorderLayout());

        // ----- Table -----
        fullModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(fullModel);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(GRID_COLOR));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        // ----- Pagination -----
        paginationPanel = new JPanel(new BorderLayout(10, 0));
        paginationPanel.setBackground(PAGINATION_BG);
        paginationPanel.setBorder(new EmptyBorder(6, 10, 6, 10));

        // Left: info
        lblInfo = new JLabel("0 data");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        paginationPanel.add(lblInfo, BorderLayout.WEST);

        // Center: page controls
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        navPanel.setOpaque(false);

        btnFirst = createNavButton("\u00AB");
        btnPrev = createNavButton("\u2039");
        lblPage = new JLabel("1 / 1");
        lblPage.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPage.setBorder(new EmptyBorder(0, 8, 0, 8));
        btnNext = createNavButton("\u203A");
        btnLast = createNavButton("\u00BB");

        btnFirst.addActionListener(e -> goToPage(1));
        btnPrev.addActionListener(e -> goToPage(currentPage - 1));
        btnNext.addActionListener(e -> goToPage(currentPage + 1));
        btnLast.addActionListener(e -> goToPage(getTotalPages()));

        navPanel.add(btnFirst);
        navPanel.add(btnPrev);
        navPanel.add(lblPage);
        navPanel.add(btnNext);
        navPanel.add(btnLast);

        paginationPanel.add(navPanel, BorderLayout.CENTER);

        // Right: page size
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        sizePanel.setOpaque(false);
        sizePanel.add(new JLabel("Per halaman:"));
        cbPageSize = new JComboBox<>(new Integer[]{10, 20, 50, 100});
        cbPageSize.setSelectedItem(20);
        cbPageSize.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbPageSize.addActionListener(e -> {
            pageSize = (int) cbPageSize.getSelectedItem();
            currentPage = 1;
            refreshPage();
        });
        sizePanel.add(cbPageSize);
        paginationPanel.add(sizePanel, BorderLayout.EAST);

        add(paginationPanel, BorderLayout.SOUTH);

        // Search field (hidden, controlled externally)
        txtSearch = new JTextField();
    }

    private void styleTable() {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setGridColor(GRID_COLOR);
        table.setSelectionBackground(ROW_SELECTED);
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                lbl.setBackground(HEADER_BG);
                lbl.setForeground(HEADER_FG);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(55, 58, 80)),
                        new EmptyBorder(6, 8, 6, 8)));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        header.setReorderingAllowed(false);

        // Row rendering with alternating colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    lbl.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                }
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 238, 245)),
                        new EmptyBorder(4, 8, 4, 8)));
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                return lbl;
            }
        });
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(36, 28));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 205, 215)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(230, 235, 245));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    // ========== PUBLIC API ==========

    public void setData(String[] columns, List<Object[]> data) {
        this.columnNames = columns;
        this.allData = new ArrayList<>(data);
        this.filteredData = new ArrayList<>(data);
        this.currentPage = 1;
        refreshPage();
    }

    public void filterData(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredData = new ArrayList<>(allData);
        } else {
            String lower = searchText.toLowerCase();
            filteredData = new ArrayList<>();
            for (Object[] row : allData) {
                for (Object cell : row) {
                    if (cell != null && cell.toString().toLowerCase().contains(lower)) {
                        filteredData.add(row);
                        break;
                    }
                }
            }
        }
        currentPage = 1;
        refreshPage();
    }

    public JTable getTable() {
        return table;
    }

    public int getSelectedRowIndex() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return -1;
        return (currentPage - 1) * pageSize + viewRow;
    }

    public Object[] getSelectedRowData() {
        int idx = getSelectedRowIndex();
        if (idx < 0 || idx >= filteredData.size()) return null;
        return filteredData.get(idx);
    }

    public int getFilteredRowCount() {
        return filteredData.size();
    }

    public List<Object[]> getFilteredData() {
        return filteredData;
    }

    public List<Object[]> getAllData() {
        return allData;
    }

    /**
     * Set filtered data externally (for custom multi-criteria filtering).
     */
    public void setFilteredData(List<Object[]> data) {
        this.filteredData = new ArrayList<>(data);
        this.currentPage = 1;
        refreshPage();
    }

    public DefaultTableModel getFullModel() {
        return fullModel;
    }

    public void addSelectionListener(javax.swing.event.ListSelectionListener listener) {
        table.getSelectionModel().addListSelectionListener(listener);
    }

    // ========== PAGINATION LOGIC ==========

    private int getTotalPages() {
        int total = filteredData.size();
        return Math.max(1, (int) Math.ceil((double) total / pageSize));
    }

    private void goToPage(int page) {
        int total = getTotalPages();
        if (page < 1) page = 1;
        if (page > total) page = total;
        currentPage = page;
        refreshPage();
    }

    private void refreshPage() {
        fullModel.setColumnIdentifiers(columnNames);
        fullModel.setRowCount(0);

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, filteredData.size());

        for (int i = start; i < end; i++) {
            fullModel.addRow(filteredData.get(i));
        }

        // Update controls
        int totalPages = getTotalPages();
        lblPage.setText(currentPage + " / " + totalPages);
        btnFirst.setEnabled(currentPage > 1);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
        btnLast.setEnabled(currentPage < totalPages);

        int totalFiltered = filteredData.size();
        int showing = end - start;
        if (totalFiltered == allData.size()) {
            lblInfo.setText(String.format("Menampilkan %d-%d dari %d data", start + 1, end, totalFiltered));
        } else {
            lblInfo.setText(String.format("Menampilkan %d-%d dari %d data (difilter dari %d)", start + 1, end, totalFiltered, allData.size()));
        }
        if (totalFiltered == 0) {
            lblInfo.setText("Tidak ada data");
        }
    }
}
