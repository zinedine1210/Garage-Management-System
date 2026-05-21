package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.dao.DirectSaleDAO;
import com.mycompany.garagemanagementsystem.dao.SparepartDAO;
import com.mycompany.garagemanagementsystem.model.DirectSale;
import com.mycompany.garagemanagementsystem.model.DirectSaleDetail;
import com.mycompany.garagemanagementsystem.model.Sparepart;
import com.mycompany.garagemanagementsystem.util.ExportUtils;
import com.mycompany.garagemanagementsystem.util.StyledTable;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class DirectSalePanel extends javax.swing.JPanel {

    private final DirectSaleDAO saleDAO = new DirectSaleDAO();
    private final SparepartDAO sparepartDAO = new SparepartDAO();
    private StyledTable styledTable;
    private JTextField txtSearch;

    public DirectSalePanel() {
        buildUI();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(243, 245, 249));

        JPanel topWrap = new JPanel(new BorderLayout(0, 6));
        topWrap.setOpaque(false);
        topWrap.add(UIHelper.createPageHeader("Penjualan Langsung",
                "Catat penjualan sparepart langsung tanpa servis (oli, busi, ban, dll)"), BorderLayout.NORTH);

        // Filter bar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setBackground(Color.WHITE);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(10, 16, 10, 16)));
        JLabel lblSearch = new JLabel("\uD83D\uDD0D Cari:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterBar.add(lblSearch, BorderLayout.WEST);
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215)),
                new EmptyBorder(6, 10, 6, 10)));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { styledTable.filterData(txtSearch.getText()); }
        });
        filterBar.add(txtSearch, BorderLayout.CENTER);
        topWrap.add(filterBar, BorderLayout.CENTER);
        add(topWrap, BorderLayout.NORTH);

        styledTable = new StyledTable();
        add(styledTable, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        buttonPanel.setBackground(new Color(243, 245, 249));

        JButton btnTambah = createStyledButton("+ Penjualan Baru", new Color(40, 167, 69));
        btnTambah.addActionListener(e -> showSaleDialog());
        buttonPanel.add(btnTambah);

        JButton btnDetail = createStyledButton("Lihat Detail", new Color(23, 162, 184));
        btnDetail.addActionListener(e -> showDetailDialog());
        buttonPanel.add(btnDetail);

        JButton btnHapus = createStyledButton("Hapus", new Color(220, 53, 69));
        btnHapus.addActionListener(e -> deleteSale());
        buttonPanel.add(btnHapus);

        JButton btnRefresh = createStyledButton("Refresh", new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> loadData());
        buttonPanel.add(btnRefresh);

        JButton btnExport = createStyledButton("Export", new Color(23, 162, 184));
        btnExport.addActionListener(e -> {
            String[] options = {"PDF", "Excel", "Batal"};
            int choice = UIHelper.showOptions(this, "Pilih format export:", "Export Penjualan Langsung", options);
            if (choice == 0) ExportUtils.exportTableToPDF(styledTable.getTable(), "Data_Penjualan_Langsung");
            else if (choice == 1) ExportUtils.exportTableToExcel(styledTable.getTable(), "Data_Penjualan_Langsung");
        });
        buttonPanel.add(btnExport);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // ==================== SALE DIALOG ====================
    @SuppressWarnings("unchecked")
    private void showSaleDialog() {
        String dlgTitle = "Penjualan Langsung Baru";
        String dlgSub = "Catat penjualan sparepart langsung kepada pelanggan";
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), dlgTitle, true);
        dialog.setLayout(new BorderLayout());

        dialog.add(UIHelper.createDialogHeader(dlgTitle, dlgSub), BorderLayout.NORTH);

        // ---- Main content ----
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBorder(new EmptyBorder(16, 20, 10, 20));
        content.setBackground(Color.WHITE);

        // --- Top: buyer info ---
        JPanel buyerPanel = new JPanel(new GridBagLayout());
        buyerPanel.setBackground(Color.WHITE);
        buyerPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                " Info Pembeli (Opsional) "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtNama = new JTextField(20);
        JTextField txtTelepon = new JTextField(20);

        addFormField(buyerPanel, gbc, 0, "Nama Pembeli:", txtNama);
        addFormField(buyerPanel, gbc, 1, "No. Telepon:", txtTelepon);

        content.add(buyerPanel, BorderLayout.NORTH);

        // --- Center: item list ---
        JPanel itemPanel = new JPanel(new BorderLayout(0, 6));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                " Daftar Item "));

        // Add item row
        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        addRow.setBackground(Color.WHITE);

        // Load spareparts with stock > 0
        JComboBox<String> cbSparepart = new JComboBox<>();
        cbSparepart.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbSparepart.setPreferredSize(new Dimension(300, 28));
        List<Sparepart> allSpareparts = new ArrayList<>();
        try {
            allSpareparts = sparepartDAO.findAll();
        } catch (SQLException ignored) {}

        cbSparepart.addItem("-- Pilih Sparepart --");
        for (Sparepart sp : allSpareparts) {
            if (sp.getStok() > 0) {
                cbSparepart.addItem(sp.getSparepartId() + " | " + sp.getKodeSparepart()
                        + " | " + sp.getNamaSparepart()
                        + " | Stok: " + sp.getStok()
                        + " | Rp " + String.format("%,.0f", sp.getHargaJual()));
            }
        }
        addRow.add(new JLabel("Sparepart:"));
        addRow.add(cbSparepart);

        JTextField txtQty = new JTextField("1", 4);
        txtQty.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addRow.add(new JLabel("Qty:"));
        addRow.add(txtQty);

        JButton btnAddItem = createStyledButton("+ Tambah", new Color(59, 130, 246));
        addRow.add(btnAddItem);

        itemPanel.add(addRow, BorderLayout.NORTH);

        // Cart table
        String[] cartCols = {"SP ID", "Kode", "Nama Sparepart", "Qty", "Harga", "Subtotal"};
        DefaultTableModel cartModel = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable cartTable = new JTable(cartModel);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartTable.setRowHeight(26);
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        // Hide SP ID column
        cartTable.getColumnModel().getColumn(0).setMinWidth(0);
        cartTable.getColumnModel().getColumn(0).setMaxWidth(0);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setPreferredSize(new Dimension(0, 160));
        itemPanel.add(cartScroll, BorderLayout.CENTER);

        // Remove item button
        JPanel cartBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        cartBtnPanel.setBackground(Color.WHITE);
        JButton btnRemoveItem = createStyledButton("Hapus Item", new Color(220, 53, 69));
        cartBtnPanel.add(btnRemoveItem);
        itemPanel.add(cartBtnPanel, BorderLayout.SOUTH);

        content.add(itemPanel, BorderLayout.CENTER);

        // --- Bottom: summary ---
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(new Color(248, 250, 252));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                new EmptyBorder(10, 12, 10, 12)));
        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.insets = new Insets(3, 6, 3, 6);
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.anchor = GridBagConstraints.WEST;

        JLabel lblGrandTotal = new JLabel("Rp 0");
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGrandTotal.setForeground(new Color(30, 32, 48));

        JTextField txtBayar = new JTextField("0", 15);
        JLabel lblKembali = new JLabel("Rp 0");
        lblKembali.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKembali.setForeground(new Color(40, 167, 69));

        JComboBox<String> cbMetode = new JComboBox<>(new String[]{"Cash", "Transfer", "QRIS"});
        cbMetode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTextField txtCatatan = new JTextField(15);

        sgbc.gridx = 0; sgbc.gridy = 0; sgbc.weightx = 0;
        summaryPanel.add(createSummaryLabel("Grand Total:"), sgbc);
        sgbc.gridx = 1; sgbc.weightx = 1.0;
        summaryPanel.add(lblGrandTotal, sgbc);

        sgbc.gridx = 0; sgbc.gridy = 1; sgbc.weightx = 0;
        summaryPanel.add(createSummaryLabel("Bayar:"), sgbc);
        sgbc.gridx = 1; sgbc.weightx = 1.0;
        summaryPanel.add(txtBayar, sgbc);

        sgbc.gridx = 0; sgbc.gridy = 2; sgbc.weightx = 0;
        summaryPanel.add(createSummaryLabel("Kembalian:"), sgbc);
        sgbc.gridx = 1; sgbc.weightx = 1.0;
        summaryPanel.add(lblKembali, sgbc);

        sgbc.gridx = 0; sgbc.gridy = 3; sgbc.weightx = 0;
        summaryPanel.add(createSummaryLabel("Metode Bayar:"), sgbc);
        sgbc.gridx = 1; sgbc.weightx = 1.0;
        summaryPanel.add(cbMetode, sgbc);

        sgbc.gridx = 0; sgbc.gridy = 4; sgbc.weightx = 0;
        summaryPanel.add(createSummaryLabel("Catatan:"), sgbc);
        sgbc.gridx = 1; sgbc.weightx = 1.0;
        summaryPanel.add(txtCatatan, sgbc);

        content.add(summaryPanel, BorderLayout.SOUTH);

        dialog.add(content, BorderLayout.CENTER);

        // --- Recalc grand total ---
        Runnable recalc = () -> {
            double total = 0;
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                total += (double) cartModel.getValueAt(i, 5);
            }
            lblGrandTotal.setText("Rp " + String.format("%,.0f", total));
            try {
                double bayar = Double.parseDouble(txtBayar.getText().trim().replace(",", ""));
                double kembali = bayar - total;
                lblKembali.setText("Rp " + String.format("%,.0f", kembali));
                lblKembali.setForeground(kembali >= 0 ? new Color(40, 167, 69) : new Color(220, 53, 69));
            } catch (NumberFormatException ex) {
                lblKembali.setText("Rp 0");
            }
        };

        txtBayar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { recalc.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { recalc.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalc.run(); }
        });

        // --- Add item action ---
        final List<Sparepart> spFinal = allSpareparts;
        btnAddItem.addActionListener(e -> {
            if (cbSparepart.getSelectedIndex() <= 0) {
                UIHelper.warn(dialog, "Pilih sparepart terlebih dahulu.");
                return;
            }
            int qty;
            try {
                qty = Integer.parseInt(txtQty.getText().trim());
                if (qty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                UIHelper.warn(dialog, "Qty harus berupa angka positif.");
                return;
            }

            String selected = cbSparepart.getSelectedItem().toString();
            int spId = Integer.parseInt(selected.split(" \\| ")[0].trim());
            Sparepart sp = spFinal.stream().filter(s -> s.getSparepartId() == spId).findFirst().orElse(null);
            if (sp == null) return;

            // Check if already in cart, accumulate qty
            int existingQty = 0;
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                if ((int) cartModel.getValueAt(i, 0) == spId) {
                    existingQty += (int) cartModel.getValueAt(i, 3);
                }
            }
            if (existingQty + qty > sp.getStok()) {
                UIHelper.warn(dialog, "Stok tidak cukup! Stok tersedia: " + sp.getStok()
                        + (existingQty > 0 ? " (sudah di keranjang: " + existingQty + ")" : ""));
                return;
            }

            double harga = sp.getHargaJual();
            double subtotal = harga * qty;
            cartModel.addRow(new Object[]{spId, sp.getKodeSparepart(), sp.getNamaSparepart(), qty, harga, subtotal});
            txtQty.setText("1");
            cbSparepart.setSelectedIndex(0);
            recalc.run();
        });

        // --- Remove item action ---
        btnRemoveItem.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row < 0) {
                UIHelper.warn(dialog, "Pilih item yang ingin dihapus.");
                return;
            }
            cartModel.removeRow(row);
            recalc.run();
        });

        // --- Action buttons ---
        JPanel btnPanel = UIHelper.createDialogButtonPanel();

        JButton btnCancel = createStyledButton("Batal", new Color(108, 117, 125));
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);

        JButton btnSave = createStyledButton("Simpan Penjualan", new Color(40, 167, 69));
        btnSave.addActionListener(e -> {
            if (cartModel.getRowCount() == 0) {
                UIHelper.warn(dialog, "Tambahkan minimal 1 item sparepart.");
                return;
            }
            double grandTotal = 0;
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                grandTotal += (double) cartModel.getValueAt(i, 5);
            }
            double bayar;
            try {
                bayar = Double.parseDouble(txtBayar.getText().trim().replace(",", ""));
            } catch (NumberFormatException ex) {
                UIHelper.warn(dialog, "Masukkan jumlah bayar yang valid.");
                return;
            }
            if (bayar < grandTotal) {
                UIHelper.warn(dialog, "Pembayaran kurang! Total: Rp " + String.format("%,.0f", grandTotal));
                return;
            }

            DirectSale sale = new DirectSale();
            sale.setTanggal(new Date());
            sale.setNamaPembeli(txtNama.getText().trim().isEmpty() ? "Umum" : txtNama.getText().trim());
            sale.setTeleponPembeli(txtTelepon.getText().trim());
            sale.setGrandTotal(grandTotal);
            sale.setBayar(bayar);
            sale.setKembali(bayar - grandTotal);
            sale.setMetodeBayar(cbMetode.getSelectedItem().toString());
            sale.setUserKasir("");
            sale.setCatatan(txtCatatan.getText().trim());

            List<DirectSaleDetail> details = new ArrayList<>();
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                DirectSaleDetail d = new DirectSaleDetail();
                d.setSparepartId((int) cartModel.getValueAt(i, 0));
                d.setQty((int) cartModel.getValueAt(i, 3));
                d.setHarga((double) cartModel.getValueAt(i, 4));
                d.setSubtotal((double) cartModel.getValueAt(i, 5));
                details.add(d);
            }
            sale.setDetails(details);

            try {
                saleDAO.insertWithDetails(sale);
                UIHelper.success(dialog, "Penjualan berhasil disimpan! Stok diperbarui.");
                dialog.dispose();
                loadData();
            } catch (SQLException ex) {
                UIHelper.error(dialog, "Error: " + ex.getMessage());
            }
        });
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(680, 600));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ==================== DETAIL DIALOG ====================
    private void showDetailDialog() {
        Object[] row = styledTable.getSelectedRowData();
        if (row == null) {
            UIHelper.warn(this, "Pilih transaksi penjualan untuk melihat detail.");
            return;
        }
        int penjualanId = (int) row[0];

        try {
            List<DirectSaleDetail> details = saleDAO.findDetailsById(penjualanId);
            String dlgTitle = "Detail Penjualan #" + penjualanId;
            String dlgSub = "Pembeli: " + row[2];
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), dlgTitle, true);
            dialog.setLayout(new BorderLayout());

            dialog.add(UIHelper.createDialogHeader(dlgTitle, dlgSub), BorderLayout.NORTH);

            String[] cols = {"Kode", "Nama Sparepart", "Qty", "Harga", "Subtotal"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            };
            for (DirectSaleDetail d : details) {
                model.addRow(new Object[]{
                    d.getKodeSparepart(),
                    d.getSparepartNama(),
                    d.getQty(),
                    String.format("%,.0f", d.getHarga()),
                    String.format("%,.0f", d.getSubtotal())
                });
            }
            JTable detailTable = new JTable(model);
            detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            detailTable.setRowHeight(28);
            detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            JScrollPane scroll = new JScrollPane(detailTable);
            scroll.setBorder(new EmptyBorder(10, 16, 10, 16));
            dialog.add(scroll, BorderLayout.CENTER);

            JPanel btnPanel = UIHelper.createDialogButtonPanel();
            JButton btnClose = createStyledButton("Tutup", new Color(108, 117, 125));
            btnClose.addActionListener(e -> dialog.dispose());
            btnPanel.add(btnClose);
            dialog.add(btnPanel, BorderLayout.SOUTH);

            dialog.pack();
            dialog.setMinimumSize(new Dimension(550, 350));
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            UIHelper.error(this, "Error: " + ex.getMessage());
        }
    }

    // ==================== DELETE ====================
    private void deleteSale() {
        Object[] row = styledTable.getSelectedRowData();
        if (row == null) {
            UIHelper.warn(this, "Pilih data yang akan dihapus.");
            return;
        }
        if (UIHelper.confirm(this, "Hapus penjualan #" + row[0] + "? Stok sparepart akan dikembalikan.", "Konfirmasi Hapus")) {
            try {
                saleDAO.delete((int) row[0]);
                UIHelper.success(this, "Penjualan berhasil dihapus. Stok dikembalikan.");
                loadData();
            } catch (SQLException ex) {
                UIHelper.error(this, "Error: " + ex.getMessage());
            }
        }
    }

    // ==================== LOAD DATA ====================
    private void loadData() {
        try {
            List<DirectSale> list = saleDAO.findAll();
            List<Object[]> data = new ArrayList<>();
            for (DirectSale s : list) {
                data.add(new Object[]{
                    s.getPenjualanId(),
                    s.getTanggal() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(s.getTanggal()) : "",
                    s.getNamaPembeli() != null ? s.getNamaPembeli() : "Umum",
                    s.getTeleponPembeli() != null ? s.getTeleponPembeli() : "-",
                    s.getGrandTotal(),
                    s.getBayar(),
                    s.getKembali(),
                    s.getMetodeBayar(),
                    s.getUserKasir(),
                    s.getCatatan() != null ? s.getCatatan() : ""
                });
            }
            styledTable.setData(new String[]{
                "ID", "Tanggal", "Pembeli", "Telepon",
                "Grand Total", "Bayar", "Kembalian", "Metode", "Kasir", "Catatan"
            }, data);
        } catch (SQLException ex) {
            UIHelper.error(this, "Error load data: " + ex.getMessage());
        }
    }

    // ==================== HELPERS ====================
    private JButton createStyledButton(String text, Color bg) {
        return UIHelper.createStyledButton(text, bg);
    }

    private void addFormField(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(field, gbc);
    }

    private JLabel createSummaryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(55, 65, 81));
        return lbl;
    }
}
