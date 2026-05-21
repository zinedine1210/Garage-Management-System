package com.mycompany.garagemanagementsystem.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import javax.swing.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExportUtils {

    private static final String NAMA_BENGKEL = "BENGKEL GARAGE MANAGEMENT";
    private static final String ALAMAT_BENGKEL = "Jl. Raya Otomotif No. 123, Jakarta";
    private static final String TELP_BENGKEL = "Telp: 021-555-1234";

    public static void exportTableToExcel(JTable table, String title) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setSelectedFile(new java.io.File(title + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx"));

        int result = fc.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return;

        try (Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(fc.getSelectedFile())) {
            Sheet sheet = wb.createSheet(title);

            // Letterhead rows
            CellStyle titleStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            Row r0 = sheet.createRow(0);
            r0.createCell(0).setCellValue(NAMA_BENGKEL);
            r0.getCell(0).setCellStyle(titleStyle);
            Row r1 = sheet.createRow(1);
            r1.createCell(0).setCellValue(ALAMAT_BENGKEL + " | " + TELP_BENGKEL);
            Row r2 = sheet.createRow(2);
            r2.createCell(0).setCellValue(getDocDescription(title));
            Row r3 = sheet.createRow(3);
            r3.createCell(0).setCellValue("Tanggal cetak: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));

            // Header style - black background white font
            CellStyle headerStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int startRow = 5;
            Row headerRow = sheet.createRow(startRow);
            for (int i = 0; i < table.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(table.getColumnName(i));
                cell.setCellStyle(headerStyle);
            }

            int rowNum = startRow + 1;
            for (int i = 0; i < table.getRowCount(); i++) {
                Row row = sheet.createRow(rowNum++);
                for (int j = 0; j < table.getColumnCount(); j++) {
                    Object value = table.getValueAt(i, j);
                    row.createCell(j).setCellValue(value != null ? value.toString() : "");
                }
            }

            for (int i = 0; i < table.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(fos);
            JOptionPane.showMessageDialog(null, "Export ke Excel berhasil: " + fc.getSelectedFile().getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error export Excel: " + e.getMessage());
        }
    }

    public static void exportTableToPDF(JTable table, String title) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setSelectedFile(new java.io.File(title + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf"));

        int result = fc.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return;

        try (FileOutputStream fos = new FileOutputStream(fc.getSelectedFile())) {
            Document doc = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
            PdfWriter.getInstance(doc, fos);
            doc.open();

            // Letterhead
            com.itextpdf.text.Font fontBengkel = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontAlamat = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font fontDocTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);

            Paragraph pBengkel = new Paragraph(NAMA_BENGKEL, fontBengkel);
            pBengkel.setAlignment(Element.ALIGN_CENTER);
            doc.add(pBengkel);

            Paragraph pAlamat = new Paragraph(ALAMAT_BENGKEL + " | " + TELP_BENGKEL, fontAlamat);
            pAlamat.setAlignment(Element.ALIGN_CENTER);
            doc.add(pAlamat);

            doc.add(new Paragraph(" "));

            // Separator line
            PdfPTable separator = new PdfPTable(1);
            separator.setWidthPercentage(100);
            PdfPCell lineCell = new PdfPCell();
            lineCell.setBorderWidthBottom(1.5f);
            lineCell.setBorderWidthTop(0);
            lineCell.setBorderWidthLeft(0);
            lineCell.setBorderWidthRight(0);
            lineCell.setFixedHeight(3);
            separator.addCell(lineCell);
            doc.add(separator);
            doc.add(new Paragraph(" "));

            // Document description
            Paragraph pDocTitle = new Paragraph(getDocDescription(title), fontDocTitle);
            pDocTitle.setAlignment(Element.ALIGN_CENTER);
            doc.add(pDocTitle);

            Paragraph pTanggal = new Paragraph("Tanggal cetak: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()), fontAlamat);
            pTanggal.setAlignment(Element.ALIGN_RIGHT);
            doc.add(pTanggal);
            doc.add(new Paragraph(" "));

            // Table
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
            pdfTable.setWidthPercentage(100);

            // Header - BLACK background, WHITE font
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            for (int i = 0; i < table.getColumnCount(); i++) {
                PdfPCell headerCell = new PdfPCell(new Phrase(table.getColumnName(i), headerFont));
                headerCell.setBackgroundColor(BaseColor.BLACK);
                headerCell.setPadding(6);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(headerCell);
            }

            // Data rows - alternating colors
            com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.NORMAL);
            BaseColor altColor = new BaseColor(245, 245, 245);
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    Object value = table.getValueAt(i, j);
                    PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : "", cellFont));
                    cell.setPadding(5);
                    if (i % 2 == 1) cell.setBackgroundColor(altColor);
                    pdfTable.addCell(cell);
                }
            }

            doc.add(pdfTable);
            doc.close();
            JOptionPane.showMessageDialog(null, "Export ke PDF berhasil: " + fc.getSelectedFile().getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error export PDF: " + e.getMessage());
        }
    }

    private static String getDocDescription(String title) {
        if (title.startsWith("Nota_Transaksi")) return "NOTA SERVIS KENDARAAN";
        if (title.startsWith("Laporan_Transaksi")) return "LAPORAN TRANSAKSI SERVIS";
        if (title.startsWith("Dashboard")) return "LAPORAN DASHBOARD BENGKEL";
        if (title.startsWith("Data_Client")) return "LAPORAN DATA PELANGGAN";
        if (title.startsWith("Data_Kendaraan")) return "LAPORAN DATA KENDARAAN";
        if (title.startsWith("Data_Mekanik")) return "LAPORAN DATA MEKANIK";
        if (title.startsWith("Data_Supplier")) return "LAPORAN DATA SUPPLIER";
        if (title.startsWith("Data_Sparepart")) return "LAPORAN DATA SPAREPART";
        return "DOKUMEN " + title.toUpperCase().replace("_", " ");
    }
}
