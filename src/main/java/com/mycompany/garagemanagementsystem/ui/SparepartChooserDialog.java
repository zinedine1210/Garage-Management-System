package com.mycompany.garagemanagementsystem.ui;

import com.mycompany.garagemanagementsystem.model.Sparepart;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import com.mycompany.garagemanagementsystem.util.UIHelper;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class SparepartChooserDialog extends JDialog {

    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtQty;

    private Sparepart selectedSparepart;
    private int selectedQty = 0;
    private final List<Sparepart> sparepartList;

    public SparepartChooserDialog(Window owner, List<Sparepart> sparepartList) {
        super(owner, "Pilih Sparepart", Dialog.ModalityType.APPLICATION_MODAL);
        this.sparepartList = sparepartList;
        buildUI();
        loadData();
        setSize(700, 450);
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        setLayout(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(new JLabel("  Cari: "), BorderLayout.WEST);
        txtSearch = new JTextField();
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        topPanel.add(txtSearch, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Kode", "Nama", "Stok", "Harga Jual", "Satuan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.add(new JLabel("Qty:"));
        txtQty = new JTextField("1", 5);
        bottomPanel.add(txtQty);
        JButton btnPilih = new JButton("Pilih");
        btnPilih.addActionListener(e -> pilih());
        bottomPanel.add(btnPilih);
        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> dispose());
        bottomPanel.add(btnBatal);
        add(bottomPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnPilih);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Sparepart s : sparepartList) {
            tableModel.addRow(new Object[]{
                s.getSparepartId(), s.getKodeSparepart(), s.getNamaSparepart(),
                s.getStok(), s.getHargaJual(), s.getSatuan()
            });
        }
    }

    private void filterTable() {
        String text = txtSearch.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void pilih() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.warn(this, "Pilih sparepart dari tabel.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int spId = (int) tableModel.getValueAt(modelRow, 0);

        int qty;
        try {
            qty = Integer.parseInt(txtQty.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            UIHelper.warn(this, "Qty harus angka positif.");
            return;
        }

        for (Sparepart s : sparepartList) {
            if (s.getSparepartId() == spId) {
                selectedSparepart = s;
                selectedQty = qty;
                break;
            }
        }
        dispose();
    }

    public Sparepart getSelectedSparepart() {
        return selectedSparepart;
    }

    public int getSelectedQty() {
        return selectedQty;
    }
}
