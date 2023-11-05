package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderTableEvent  extends PdfPageEventHelper {
//private PdfPTable headerTable;

   /*  public HeaderTableEvent (PdfPTable headerTable) {
        this.headerTable = headerTable;
    }*/

    public void onEndPage(PdfWriter writer, Document document) {
        PdfPTable headerTable = new PdfPTable(1);
            PdfPCell cell = new PdfPCell(new Phrase("Contenido de la Tabla en el Encabezado"));
            headerTable.addCell(cell);
            headerTable.writeSelectedRows(0, -1, document.leftMargin(), writer.getPageSize().getTop(document.topMargin())+40, writer.getDirectContent());
        
        
    }
}