package com.example.demo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Phrase;

/**
 * Clase que maneja los eventos de pagina necesarios para agregar un encabezado
 * y conteo de paginas a un documento.
 * El encabezado, definido en onEndPage, consiste en una tabla con 3 celdas que
 * contienen:
 * Frase del encabezado | pagina de | total de paginas, con una linea horizontal
 * separando el
 * encabezado del texto
 *
 * Referencia: http://itextpdf.com/examples/iia.php?id=104
 *
 * @author David
 */

public class Encabezado extends PdfPageEventHelper {
    private String encabezado;
    PdfTemplate total;
    int totalPaginas;
    Path projectPath = Paths.get("").toAbsolutePath();
    String imagen = projectPath + "/src/main/resources/static/logo/logoCabezera.png";
    String fuenteCalibriRegular = projectPath
            + "/src/main/resources/static/fuenteLetra/Calibri Regular.ttf";
    String fuenteCalibriBold = projectPath + "/src/main/resources/static/fuenteLetra/Calibri Bold.ttf";
    Font fontSimple = FontFactory.getFont(fuenteCalibriRegular, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11);
    Font fontNegrilla = FontFactory.getFont(fuenteCalibriBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11);
    Font fontSimple9 = FontFactory.getFont(fuenteCalibriRegular, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9);
    Font fontNegrilla9 = FontFactory.getFont(fuenteCalibriBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9);

    /**
     * Crea el objecto PdfTemplate el cual contiene el numero total de
     * paginas en el documento
     */
    public void onOpenDocument(PdfWriter writer, Document document) {

        total = writer.getDirectContent().createTemplate(30, 16);
        totalPaginas = 0; // Inicializar el contador de páginas
    }

    /**
     * Esta es el metodo a llamar cuando ocurra el evento onEndPage, es en este
     * evento donde crearemos el encabezado de la pagina con los elementos
     * indicados.
     */
    public void onEndPage(PdfWriter writer, Document document) {

        try {
            // Se determina el ancho y altura de la tabla
            PdfPTable headerTable = new PdfPTable(4);
            headerTable.setWidthPercentage(95);
            headerTable.setTotalWidth(527);
            //headerTable.setLockedWidth(true);
            headerTable.getDefaultCell().setFixedHeight(20);
            // Definir los anchos de las columnas
            float[] columnWidths = { 2.5f, 4f, 1f, 1f };
            headerTable.setWidths(columnWidths);

            Image image = Image.getInstance(imagen);
            PdfPCell celda1 = new PdfPCell(image, true);
            celda1.setRowspan(4);
            celda1.setPaddingTop(8);
            celda1.setPaddingBottom(8);
            celda1.setPaddingLeft(25);
            celda1.setPaddingRight(25);
            headerTable.addCell(celda1);

            // Celda 2: Texto del encabezado
            PdfPCell cell2 = new PdfPCell(
                    new Phrase("FORMULARIO DE TRANSFERENCIA DE DOCUMENTOS AL ARCHIVO CENTRAL", fontNegrilla));
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setRowspan(4);
            headerTable.addCell(cell2);

            // Celda 3: "FORMATO"
            PdfPCell cell3 = new PdfPCell(new Phrase("FORMATO", fontNegrilla9));
            cell3.setColspan(2); // Ocupará dos de las cuatro columnas
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(cell3);

            // Celda 4: "FOR-ACH-GDC-01"
            PdfPCell cell4 = new PdfPCell(new Phrase("FOR-ACH-GDC-01", fontNegrilla9));
            cell4.setColspan(2); // Ocupará dos de las cuatro columnas
            cell4.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(cell4);

            // Celda 5: "Fecha de Aprobación: 12/05/2023"
            PdfPCell cell5 = new PdfPCell(new Phrase("Fecha de Aprobación: 12/05/2023", fontSimple9));
            cell5.setColspan(2); // Ocupará dos de las cuatro columnas
            cell5.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(cell5);

            // Celda 6: "Versión: 03"
            PdfPCell cell6 = new PdfPCell(new Phrase("Versión: 03", fontSimple9));
            cell6.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
            cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(cell6);

            PdfPCell cell7 = new PdfPCell();
            cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell7.setHorizontalAlignment(Element.ALIGN_CENTER);

            // Crear un párrafo con el número de página actual y el total de páginas
            Phrase phrase = new Phrase();
            phrase.setFont(fontSimple9);
            phrase.add(new Phrase(String.format("Pag. %d de %d", writer.getPageNumber(), totalPaginas), fontSimple9));
            cell7.addElement(phrase);

            // Agregar la celda a la tabla de encabezado
            headerTable.addCell(cell7);

            headerTable.writeSelectedRows(0, -1, 47, 760, writer.getDirectContent());

        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        } catch (IOException e) {
            // Manejo de excepciones de lectura de imagen
            e.printStackTrace();
        }
    }

    /**
     * Realiza el conteo de paginas al momento de cerrar el documento
     */
    public void onCloseDocument(PdfWriter writer, Document document) {
        // Actualizar el número total de páginas al final del documento
        totalPaginas = writer.getPageNumber() - 1;
    }

    // Getter and Setters

    public String getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(String encabezado) {
        this.encabezado = encabezado;
    }
}