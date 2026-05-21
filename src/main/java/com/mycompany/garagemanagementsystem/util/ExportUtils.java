package com.mycompany.garagemanagementsystem.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.ColumnText;
import javax.swing.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mycompany.garagemanagementsystem.model.ServiceTransaction;
import com.mycompany.garagemanagementsystem.model.TransactionDetail;
import com.mycompany.garagemanagementsystem.util.AppConfig;

public class ExportUtils {

    private static final BaseColor HEADER_BG = new BaseColor(30, 58, 138);
    private static final BaseColor HEADER_BG_DARK = new BaseColor(22, 42, 110);

    private static String getNamaBengkel() { return AppConfig.getCompanyName(); }
    private static String getAlamatBengkel() { return AppConfig.getCompanyAddress(); }
    private static String getTelpBengkel() { return AppConfig.getCompanyPhoneFormatted(); }

    // ==================== SHARED PDF HEADER ====================

    /**
     * Draws a full-width edge-to-edge header: logo LEFT, company info RIGHT.
     * White text on dark blue background. Report title included in header.
     */
    private static void addPdfReportHeader(Document doc, PdfWriter writer, String title) throws DocumentException {
        float pageW = doc.getPageSize().getWidth();
        float pageH = doc.getPageSize().getHeight();
        float headerH = 85;

        // Draw background rectangle edge-to-edge
        PdfContentByte cb = writer.getDirectContentUnder();
        cb.setColorFill(HEADER_BG);
        cb.rectangle(0, pageH - headerH, pageW, headerH);
        cb.fill();

        // Accent line at the bottom of header
        cb.setColorFill(new BaseColor(59, 130, 246));
        cb.rectangle(0, pageH - headerH, pageW, 3);
        cb.fill();

        // Header table: [Logo | Company Info + Title]
        PdfPTable hdr = new PdfPTable(2);
        hdr.setTotalWidth(pageW);
        hdr.setLockedWidth(true);
        hdr.setWidths(new float[]{70, pageW - 70});

        com.itextpdf.text.Font fCompany = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
        com.itextpdf.text.Font fAddr = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.NORMAL, BaseColor.WHITE);
        com.itextpdf.text.Font fDocTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);

        // Logo cell (left)
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setPaddingLeft(15);
        logoCell.setPaddingTop(16);
        logoCell.setPaddingBottom(16);
        try {
            String logoPath = AppConfig.getLogoPath();
            if (logoPath != null && !logoPath.trim().isEmpty()) {
                java.net.URL logoUrl = null;
                java.io.File logoFile = new java.io.File(logoPath);
                if (logoFile.exists()) {
                    logoUrl = logoFile.toURI().toURL();
                } else {
                    String cp = logoPath.startsWith("/") ? logoPath : "/" + logoPath;
                    logoUrl = ExportUtils.class.getResource(cp);
                }
                if (logoUrl != null) {
                    Image logo = Image.getInstance(logoUrl);
                    logo.scaleToFit(42, 42);
                    logoCell.addElement(logo);
                }
            }
        } catch (Exception ignored) {}
        hdr.addCell(logoCell);

        // Company info cell (right)
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        infoCell.setPaddingTop(14);
        infoCell.setPaddingBottom(14);
        infoCell.setPaddingLeft(5);

        Paragraph pName = new Paragraph(getNamaBengkel(), fCompany);
        infoCell.addElement(pName);

        String addrLine = getAlamatBengkel() + ", " + AppConfig.getCompanyCity() + "  |  " + getTelpBengkel();
        Paragraph pAddr = new Paragraph(addrLine, fAddr);
        pAddr.setSpacingBefore(1);
        infoCell.addElement(pAddr);

        Paragraph pTitle = new Paragraph(getDocDescription(title), fDocTitle);
        pTitle.setSpacingBefore(4);
        infoCell.addElement(pTitle);

        hdr.addCell(infoCell);

        // Date line inside header (right-aligned, at bottom of header area)
        com.itextpdf.text.Font fDate = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 7, com.itextpdf.text.Font.NORMAL, BaseColor.WHITE);
        String dateStr = "Tanggal cetak: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
        Phrase datePhrase = new Phrase(dateStr, fDate);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, datePhrase, pageW - 15, pageH - headerH + 8, 0);

        // Write at absolute top
        hdr.writeSelectedRows(0, -1, 0, pageH, writer.getDirectContent());

        // Push doc flow below header + accent line + gap
        // Use a fixed-height table as spacer (spacingBefore is unreliable for first element)
        PdfPTable spacerTbl = new PdfPTable(1);
        spacerTbl.setTotalWidth(pageW);
        spacerTbl.setLockedWidth(true);
        PdfPCell spacerCell = new PdfPCell();
        spacerCell.setBorder(Rectangle.NO_BORDER);
        spacerCell.setFixedHeight(headerH + 10);
        spacerTbl.addCell(spacerCell);
        doc.add(spacerTbl);
    }

    /**
     * Adds a signature/pengesahan section at the bottom of a PDF report.
     */
    private static void addPdfSignatureSection(Document doc) throws DocumentException {
        doc.add(spacer(15));

        String city = AppConfig.getCompanyCity();
        String picName = AppConfig.getCompanyPicName();
        String picPosition = AppConfig.getCompanyPicPosition();
        String companyName = getNamaBengkel();

        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE, dd MMMM yyyy", new java.util.Locale("id", "ID"));
        String dateStr = city + ", " + sdfDay.format(new Date());

        com.itextpdf.text.Font fNormal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.NORMAL);
        com.itextpdf.text.Font fUnderBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.BOLD | com.itextpdf.text.Font.UNDERLINE);

        PdfPTable sigTable = new PdfPTable(1);
        sigTable.setWidthPercentage(35);
        sigTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell dateCell = new PdfPCell(new Phrase(dateStr, fNormal));
        dateCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setPaddingBottom(3);
        sigTable.addCell(dateCell);

        PdfPCell posCell = new PdfPCell(new Phrase(picPosition + " " + companyName, fNormal));
        posCell.setBorder(Rectangle.NO_BORDER);
        posCell.setPaddingBottom(45);
        sigTable.addCell(posCell);

        PdfPCell nameCell = new PdfPCell(new Phrase(picName, fUnderBold));
        nameCell.setBorder(Rectangle.NO_BORDER);
        sigTable.addCell(nameCell);

        doc.add(sigTable);
    }

    /**
     * Adds signature section to an Excel sheet for Laporan exports.
     */
    private static void addExcelSignatureSection(Sheet sheet, Workbook wb, int startRowNum, int colCount) {
        int rowNum = startRowNum + 2;
        String city = AppConfig.getCompanyCity();
        String picName = AppConfig.getCompanyPicName();
        String picPosition = AppConfig.getCompanyPicPosition();
        String companyName = getNamaBengkel();

        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE, dd MMMM yyyy", new java.util.Locale("id", "ID"));
        String dateStr = city + ", " + sdfDay.format(new Date());

        int sigCol = Math.max(colCount - 2, 0);

        Row sigRow1 = sheet.createRow(rowNum);
        sigRow1.createCell(sigCol).setCellValue(dateStr);

        Row sigRow2 = sheet.createRow(rowNum + 1);
        sigRow2.createCell(sigCol).setCellValue(picPosition + " " + companyName);

        CellStyle underlineStyle = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font underlineFont = wb.createFont();
        underlineFont.setBold(true);
        underlineFont.setUnderline(org.apache.poi.ss.usermodel.Font.U_SINGLE);
        underlineStyle.setFont(underlineFont);

        Row sigRow3 = sheet.createRow(rowNum + 5);
        Cell nc = sigRow3.createCell(sigCol);
        nc.setCellValue(picName);
        nc.setCellStyle(underlineStyle);
    }

    /**
     * Writes a styled Excel header: rows 0-2 with dark blue background, white text,
     * company info, address+city, phone, and report title. Spans all columns.
     */
    private static int addExcelReportHeader(Sheet sheet, Workbook wb, String title, int colCount) {
        int spanCols = Math.max(colCount, 6);

        // Style: company name row
        CellStyle nameStyle = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font nameFont = wb.createFont();
        nameFont.setBold(true);
        nameFont.setFontHeightInPoints((short) 14);
        nameFont.setColor(IndexedColors.WHITE.getIndex());
        nameStyle.setFont(nameFont);
        nameStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        nameStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        nameStyle.setAlignment(HorizontalAlignment.CENTER);
        nameStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Style: address row
        CellStyle addrStyle = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font addrFont = wb.createFont();
        addrFont.setColor(IndexedColors.WHITE.getIndex());
        addrFont.setFontHeightInPoints((short) 10);
        addrStyle.setFont(addrFont);
        addrStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        addrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addrStyle.setAlignment(HorizontalAlignment.CENTER);

        // Style: title row
        CellStyle titleStyle = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 12);
        titleFont.setColor(IndexedColors.WHITE.getIndex());
        titleStyle.setFont(titleFont);
        titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        // Row 0: Company name
        Row r0 = sheet.createRow(0);
        r0.setHeightInPoints(24);
        for (int i = 0; i < spanCols; i++) {
            Cell c = r0.createCell(i);
            c.setCellStyle(nameStyle);
        }
        r0.getCell(0).setCellValue(getNamaBengkel());
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, spanCols - 1));

        // Row 1: Address, City | Phone
        Row r1 = sheet.createRow(1);
        r1.setHeightInPoints(18);
        String addrText = getAlamatBengkel() + ", " + AppConfig.getCompanyCity() + "  |  " + getTelpBengkel();
        for (int i = 0; i < spanCols; i++) {
            Cell c = r1.createCell(i);
            c.setCellStyle(addrStyle);
        }
        r1.getCell(0).setCellValue(addrText);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, spanCols - 1));

        // Row 2: Report title
        Row r2 = sheet.createRow(2);
        r2.setHeightInPoints(20);
        for (int i = 0; i < spanCols; i++) {
            Cell c = r2.createCell(i);
            c.setCellStyle(titleStyle);
        }
        r2.getCell(0).setCellValue(getDocDescription(title));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, spanCols - 1));

        // Row 3: Date
        Row r3 = sheet.createRow(3);
        r3.createCell(0).setCellValue("Tanggal cetak: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));

        return 5; // data starts at row 5
    }

    // ==================== EXPORT TABLE TO EXCEL (PLAIN) ====================

    public static void exportTableToExcel(JTable table, String title) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setSelectedFile(new java.io.File(title + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx"));

        int result = fc.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return;

        try (Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(fc.getSelectedFile())) {
            Sheet sheet = wb.createSheet(title);
            int startRow = addExcelReportHeader(sheet, wb, title, table.getColumnCount());

            CellStyle headerStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

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

            for (int i = 0; i < table.getColumnCount(); i++) sheet.autoSizeColumn(i);

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
            Document doc = new Document(PageSize.A4.rotate(), 30, 30, 0, 20);
            PdfWriter writer = PdfWriter.getInstance(doc, fos);
            doc.open();

            addPdfReportHeader(doc, writer, title);

            // Table
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
            pdfTable.setWidthPercentage(100);

            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            for (int i = 0; i < table.getColumnCount(); i++) {
                PdfPCell headerCell = new PdfPCell(new Phrase(table.getColumnName(i), headerFont));
                headerCell.setBackgroundColor(new BaseColor(30, 58, 138));
                headerCell.setPadding(6);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(headerCell);
            }

            com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.NORMAL);
            BaseColor altColor = new BaseColor(240, 243, 250);
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
        if (title.startsWith("Laporan_Pembelian")) return "LAPORAN PEMBELIAN SPAREPART";
        if (title.startsWith("Laporan_Pendapatan")) return "LAPORAN PENDAPATAN";
        if (title.startsWith("Laporan_Omzet")) return "LAPORAN OMZET";
        if (title.startsWith("Laporan_Kinerja")) return "LAPORAN KINERJA MEKANIK";
        if (title.startsWith("Laporan_Sparepart_Terlaris")) return "LAPORAN SPAREPART TERLARIS";
        if (title.startsWith("Dashboard")) return "LAPORAN DASHBOARD BENGKEL";
        if (title.startsWith("Data_Client")) return "LAPORAN DATA PELANGGAN";
        if (title.startsWith("Data_Kendaraan")) return "LAPORAN DATA KENDARAAN";
        if (title.startsWith("Data_Mekanik")) return "LAPORAN DATA MEKANIK";
        if (title.startsWith("Data_Supplier")) return "LAPORAN DATA SUPPLIER";
        if (title.startsWith("Data_Sparepart")) return "LAPORAN DATA SPAREPART";
        if (title.startsWith("Data_Penjualan")) return "LAPORAN PENJUALAN LANGSUNG";
        return "DOKUMEN " + title.toUpperCase().replace("_", " ");
    }

    // ==================== RECEIPT PRINTING ====================

    public static void printServiceReceipt(ServiceTransaction t) {
        if (t == null) return;
        String fileName = "Nota_Transaksi_" + t.getTransId() + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf";

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File(fileName));
        if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;

        try (FileOutputStream fos = new FileOutputStream(fc.getSelectedFile())) {
            // Portrait receipt: 80mm x 200mm (thermal receipt style, generous height)
            Rectangle pageSize = new Rectangle(226, 680); // ~80mm x 240mm in points
            Document doc = new Document(pageSize, 10, 10, 10, 10);
            PdfWriter.getInstance(doc, fos);
            doc.open();

            // Fonts - compact for receipt
            com.itextpdf.text.Font f9b = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 9, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font f7 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 7, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font f7b = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 7, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font f6 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 6, com.itextpdf.text.Font.NORMAL, BaseColor.DARK_GRAY);
            com.itextpdf.text.Font f6b = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 6, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font f5g = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 5, com.itextpdf.text.Font.NORMAL, BaseColor.GRAY);
            com.itextpdf.text.Font f6w = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 6, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            com.itextpdf.text.Font f8b = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 8, com.itextpdf.text.Font.BOLD);

            // ===== HEADER =====
            Paragraph pCompany = new Paragraph(getNamaBengkel(), f9b);
            pCompany.setAlignment(Element.ALIGN_CENTER);
            doc.add(pCompany);
            String addr = getAlamatBengkel() + ", " + AppConfig.getCompanyCity();
            Paragraph pAddr = new Paragraph(addr, f6);
            pAddr.setAlignment(Element.ALIGN_CENTER);
            doc.add(pAddr);
            Paragraph pPhone = new Paragraph(getTelpBengkel(), f6);
            pPhone.setAlignment(Element.ALIGN_CENTER);
            doc.add(pPhone);

            // Dashed separator
            Paragraph dash1 = new Paragraph("================================", f6);
            dash1.setAlignment(Element.ALIGN_CENTER);
            doc.add(dash1);

            // Title + No
            Paragraph pTitle = new Paragraph("NOTA SERVIS", f8b);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            doc.add(pTitle);
            Paragraph pNo = new Paragraph("No. TRX-" + String.format("%05d", t.getTransId()), f6);
            pNo.setAlignment(Element.ALIGN_CENTER);
            doc.add(pNo);

            Paragraph dash2 = new Paragraph("================================", f6);
            dash2.setAlignment(Element.ALIGN_CENTER);
            doc.add(dash2);

            // ===== INFO (label: value) =====
            PdfPTable info = new PdfPTable(2);
            info.setWidthPercentage(100);
            info.setWidths(new float[]{35, 65});

            addReceiptRow(info, "Tanggal", t.getTanggal() != null ? new SimpleDateFormat("dd-MM-yyyy HH:mm").format(t.getTanggal()) : "-", f6, f7b);
            addReceiptRow(info, "Pelanggan", t.getClientNama() != null ? t.getClientNama() : "-", f6, f7b);
            addReceiptRow(info, "No. Polisi", t.getNoPolisi() != null ? t.getNoPolisi() : "-", f6, f7b);
            addReceiptRow(info, "Mekanik", t.getMekanikNama() != null ? t.getMekanikNama() : "-", f6, f7b);
            addReceiptRow(info, "Status", t.getStatusServis() != null ? t.getStatusServis() : "-", f6, f7b);
            addReceiptRow(info, "Bayar Via", t.getMetodeBayar() != null ? t.getMetodeBayar() : "Cash", f6, f7b);
            if (t.getRegistrationId() != null) {
                addReceiptRow(info, "No. Reg", "REG-" + String.format("%05d", t.getRegistrationId()), f6, f7b);
            }
            doc.add(info);

            // Keluhan (full-width row)
            if (t.getKeluhan() != null && !t.getKeluhan().trim().isEmpty()) {
                Paragraph lblK = new Paragraph("Keluhan:", f6);
                doc.add(lblK);
                Paragraph valK = new Paragraph(t.getKeluhan(), f7);
                doc.add(valK);
            }

            Paragraph dash3 = new Paragraph("================================", f6);
            dash3.setAlignment(Element.ALIGN_CENTER);
            doc.add(dash3);

            // ===== SPAREPART TABLE =====
            java.util.List<TransactionDetail> details = t.getDetails();
            if (details != null && !details.isEmpty()) {
                Paragraph spTitle = new Paragraph("SPAREPART", f6b);
                doc.add(spTitle);

                PdfPTable spTable = new PdfPTable(3);
                spTable.setWidthPercentage(100);
                spTable.setWidths(new float[]{8, 72, 20});

                String[] spHeaders = {"#", "Nama", "Qty"};
                for (String h : spHeaders) {
                    PdfPCell hc = new PdfPCell(new Phrase(h, f6w));
                    hc.setBackgroundColor(HEADER_BG);
                    hc.setPadding(2);
                    hc.setHorizontalAlignment(Element.ALIGN_CENTER);
                    spTable.addCell(hc);
                }

                int no = 1;
                for (TransactionDetail d : details) {
                    PdfPCell cNo = new PdfPCell(new Phrase(String.valueOf(no++), f6));
                    cNo.setPadding(1); cNo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cNo.setBorderWidth(0.5f);
                    PdfPCell cNama = new PdfPCell(new Phrase(d.getSparepartNama() != null ? d.getSparepartNama() : "Sparepart #" + d.getSparepartId(), f6));
                    cNama.setPadding(1); cNama.setBorderWidth(0.5f);
                    PdfPCell cQty = new PdfPCell(new Phrase(String.valueOf(d.getQty()), f6));
                    cQty.setPadding(1); cQty.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cQty.setBorderWidth(0.5f);
                    spTable.addCell(cNo); spTable.addCell(cNama); spTable.addCell(cQty);
                }
                doc.add(spTable);
            }

            Paragraph dash4 = new Paragraph("================================", f6);
            dash4.setAlignment(Element.ALIGN_CENTER);
            doc.add(dash4);

            // ===== TOTALS =====
            PdfPTable totTable = new PdfPTable(2);
            totTable.setWidthPercentage(100);
            totTable.setWidths(new float[]{50, 50});

            addReceiptRow(totTable, "Total Jasa", "Rp " + String.format("%,.0f", t.getTotalJasa()), f6, f7b);
            addReceiptRow(totTable, "Total Sparepart", "Rp " + String.format("%,.0f", t.getTotalSparepart()), f6, f7b);

            // Grand Total
            PdfPCell gtL = new PdfPCell(new Phrase("GRAND TOTAL", f8b));
            gtL.setBorder(Rectangle.TOP); gtL.setBorderWidthTop(0.5f);
            gtL.setPaddingTop(2); gtL.setPaddingBottom(2);
            totTable.addCell(gtL);
            PdfPCell gtV = new PdfPCell(new Phrase("Rp " + String.format("%,.0f", t.getGrandTotal()), f8b));
            gtV.setBorder(Rectangle.TOP); gtV.setBorderWidthTop(0.5f);
            gtV.setPaddingTop(2); gtV.setPaddingBottom(2);
            gtV.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totTable.addCell(gtV);

            double kembalian = Math.max(0, t.getKembali());
            addReceiptRow(totTable, "Bayar", "Rp " + String.format("%,.0f", t.getBayar()), f6, f7b);
            addReceiptRow(totTable, "Kembali", "Rp " + String.format("%,.0f", kembalian), f6, f7b);

            doc.add(totTable);
            doc.add(spacer(6));

            // ===== FOOTER =====
            Paragraph dash5 = new Paragraph("================================", f6);
            dash5.setAlignment(Element.ALIGN_CENTER);
            doc.add(dash5);

            Paragraph pFooter = new Paragraph("Terima kasih atas kepercayaan Anda", f5g);
            pFooter.setAlignment(Element.ALIGN_CENTER);
            doc.add(pFooter);
            Paragraph pFooter2 = new Paragraph("Simpan nota ini sebagai bukti servis", f5g);
            pFooter2.setAlignment(Element.ALIGN_CENTER);
            doc.add(pFooter2);

            doc.close();
            JOptionPane.showMessageDialog(null, "Nota berhasil dicetak: " + fc.getSelectedFile().getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error cetak nota: " + e.getMessage());
        }
    }

    private static void addReceiptRow(PdfPTable table, String label, String value,
            com.itextpdf.text.Font fLabel, com.itextpdf.text.Font fValue) {
        PdfPCell cL = new PdfPCell(new Phrase(label, fLabel));
        cL.setBorder(Rectangle.NO_BORDER); cL.setPaddingBottom(1);
        table.addCell(cL);
        PdfPCell cV = new PdfPCell(new Phrase(value, fValue));
        cV.setBorder(Rectangle.NO_BORDER); cV.setPaddingBottom(1);
        cV.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cV);
    }

    private static void addInfoRow(PdfPTable table, String label, String value,
            com.itextpdf.text.Font fLabel, com.itextpdf.text.Font fValue) {
        PdfPCell cLabel = new PdfPCell(new Phrase(label, fLabel));
        cLabel.setBorder(Rectangle.NO_BORDER);
        cLabel.setPaddingBottom(3);
        table.addCell(cLabel);
        PdfPCell cValue = new PdfPCell(new Phrase(value, fValue));
        cValue.setBorder(Rectangle.NO_BORDER);
        cValue.setPaddingBottom(3);
        table.addCell(cValue);
    }

    private static void addTotalRow(PdfPTable table, String label, double amount,
            com.itextpdf.text.Font fLabel, com.itextpdf.text.Font fValue) {
        PdfPCell cLabel = new PdfPCell(new Phrase(label, fLabel));
        cLabel.setBorder(Rectangle.NO_BORDER);
        cLabel.setPaddingBottom(2);
        table.addCell(cLabel);
        PdfPCell cValue = new PdfPCell(new Phrase("Rp " + String.format("%,.0f", amount), fValue));
        cValue.setBorder(Rectangle.NO_BORDER);
        cValue.setPaddingBottom(2);
        cValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cValue);
    }

    private static void addSeparator(Document doc) throws DocumentException {
        PdfPTable sep = new PdfPTable(1);
        sep.setWidthPercentage(100);
        PdfPCell line = new PdfPCell();
        line.setBorderWidthBottom(1f);
        line.setBorderWidthTop(0);
        line.setBorderWidthLeft(0);
        line.setBorderWidthRight(0);
        line.setFixedHeight(2);
        sep.addCell(line);
        doc.add(sep);
    }

    private static Paragraph spacer(float height) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingBefore(height);
        return p;
    }

    private static Paragraph centered(String text, com.itextpdf.text.Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }

    // ==================== EXPORT WITH TOTALS ====================

    public static void exportTableToPDFWithTotals(JTable table, String title, int[] totalColumns) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setSelectedFile(new java.io.File(title + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf"));

        int result = fc.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return;

        try (FileOutputStream fos = new FileOutputStream(fc.getSelectedFile())) {
            Document doc = new Document(PageSize.A4.rotate(), 30, 30, 0, 20);
            PdfWriter writer = PdfWriter.getInstance(doc, fos);
            doc.open();

            addPdfReportHeader(doc, writer, title);

            // ===== TABLE =====
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
            pdfTable.setWidthPercentage(100);

            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            for (int i = 0; i < table.getColumnCount(); i++) {
                PdfPCell headerCl = new PdfPCell(new Phrase(table.getColumnName(i), headerFont));
                headerCl.setBackgroundColor(new BaseColor(30, 58, 138));
                headerCl.setPadding(6);
                headerCl.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(headerCl);
            }

            com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.NORMAL);
            BaseColor altColor = new BaseColor(240, 243, 250);
            double[] totals = new double[table.getColumnCount()];
            java.util.Set<Integer> totalColSet = new java.util.HashSet<>();
            for (int c : totalColumns) totalColSet.add(c);

            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    Object value = table.getValueAt(i, j);
                    PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : "", cellFont));
                    cell.setPadding(5);
                    if (i % 2 == 1) cell.setBackgroundColor(altColor);
                    pdfTable.addCell(cell);

                    if (totalColSet.contains(j) && value != null) {
                        try { totals[j] += Double.parseDouble(value.toString().replace(",", "")); } catch (NumberFormatException ignored) {}
                    }
                }
            }

            // Totals row
            com.itextpdf.text.Font totalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.BOLD);
            for (int j = 0; j < table.getColumnCount(); j++) {
                PdfPCell cell;
                if (j == 0) {
                    cell = new PdfPCell(new Phrase("TOTAL", totalFont));
                } else if (totalColSet.contains(j)) {
                    cell = new PdfPCell(new Phrase(String.format("%,.0f", totals[j]), totalFont));
                } else {
                    cell = new PdfPCell(new Phrase(""));
                }
                cell.setPadding(6);
                cell.setBackgroundColor(new BaseColor(220, 230, 241));
                cell.setBorderWidthTop(1.5f);
                pdfTable.addCell(cell);
            }

            doc.add(pdfTable);

            // Signature section for Laporan
            addPdfSignatureSection(doc);

            doc.close();
            JOptionPane.showMessageDialog(null, "Export ke PDF berhasil: " + fc.getSelectedFile().getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error export PDF: " + e.getMessage());
        }
    }

    public static void exportTableToExcelWithTotals(JTable table, String title, int[] totalColumns) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setSelectedFile(new java.io.File(title + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx"));

        int result = fc.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return;

        try (Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(fc.getSelectedFile())) {
            Sheet sheet = wb.createSheet(title);
            int startRow = addExcelReportHeader(sheet, wb, title, table.getColumnCount());

            // Table column header
            CellStyle colHeaderStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font colHeaderFont = wb.createFont();
            colHeaderFont.setBold(true); colHeaderFont.setColor(IndexedColors.WHITE.getIndex());
            colHeaderStyle.setFont(colHeaderFont);
            colHeaderStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            colHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(startRow);
            for (int i = 0; i < table.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(table.getColumnName(i));
                cell.setCellStyle(colHeaderStyle);
            }

            java.util.Set<Integer> totalColSet = new java.util.HashSet<>();
            for (int c : totalColumns) totalColSet.add(c);
            double[] totals = new double[table.getColumnCount()];

            int rowNum = startRow + 1;
            for (int i = 0; i < table.getRowCount(); i++) {
                Row row = sheet.createRow(rowNum++);
                for (int j = 0; j < table.getColumnCount(); j++) {
                    Object value = table.getValueAt(i, j);
                    row.createCell(j).setCellValue(value != null ? value.toString() : "");
                    if (totalColSet.contains(j) && value != null) {
                        try { totals[j] += Double.parseDouble(value.toString().replace(",", "")); } catch (NumberFormatException ignored) {}
                    }
                }
            }

            // Totals row
            CellStyle totalStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font totalFontExcel = wb.createFont();
            totalFontExcel.setBold(true);
            totalStyle.setFont(totalFontExcel);
            totalStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row totalRow = sheet.createRow(rowNum);
            for (int j = 0; j < table.getColumnCount(); j++) {
                Cell cell = totalRow.createCell(j);
                if (j == 0) {
                    cell.setCellValue("TOTAL");
                } else if (totalColSet.contains(j)) {
                    cell.setCellValue(String.format("%,.0f", totals[j]));
                }
                cell.setCellStyle(totalStyle);
            }

            for (int i = 0; i < table.getColumnCount(); i++) sheet.autoSizeColumn(i);

            // Signature section for Laporan
            addExcelSignatureSection(sheet, wb, rowNum, table.getColumnCount());

            wb.write(fos);
            JOptionPane.showMessageDialog(null, "Export ke Excel berhasil: " + fc.getSelectedFile().getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error export Excel: " + e.getMessage());
        }
    }
}
