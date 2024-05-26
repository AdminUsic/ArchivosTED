package com.example.demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Archivo;
import com.example.demo.entity.Caja;
import com.example.demo.entity.Control;
import com.example.demo.entity.Cubierta;
import com.example.demo.entity.FormularioTransferencia;
import com.example.demo.entity.Persona;
import com.example.demo.entity.SerieDocumental;
import com.example.demo.entity.Unidad;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.Volumen;
import com.example.demo.service.CajaService;
import com.example.demo.service.ControlService;
import com.example.demo.service.CubiertaService;
import com.example.demo.service.HeaderTableEvent;
import com.example.demo.service.PersonaService;
import com.example.demo.service.TipoControService;
import com.example.demo.service.FormularioTransferenciaService;
import com.example.demo.service.UnidadService;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.UtilidadServiceProject;
import com.example.demo.service.VolumenService;
import com.example.demo.service.personaServiceImpl;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.Cell;
import com.lowagie.text.HeaderFooter;
import java.awt.image.BufferedImage;

@Controller
public class FormularioTransferController {

   @Autowired
   private FormularioTransferenciaService formularioTransferenciaService;

   @Autowired
   private UnidadService unidadService;

   @Autowired
   private CubiertaService cubiertaService;

   @Autowired
   private VolumenService volumenService;

   @Autowired
   private CajaService cajaService;

   @Autowired
   private UsuarioService usuarioService;

   @Autowired
   private PersonaService personaService;

   @Autowired
   private UtilidadServiceProject utilidadService;

   @Autowired
   private TipoControService tipoControService;

   @Autowired
   private ControlService controService;

   @GetMapping("/FormTRANSFERENCIA")
   public String FormTRANSFERENCIA(HttpServletRequest request, Model model) {
      if (request.getSession().getAttribute("userLog") != null) {
         return "/FormularioTransferencias/registrar";
      } else {
         return "expiracion";
      }
   }

   @PostMapping(value = "/NuevoRegistroFormTRANSFERENCIA")
   public String NuevoRegistroFormTRANSFERENCIA(HttpServletRequest request, Model model) {
      Usuario u = (Usuario) request.getSession().getAttribute("userLog");
      Persona p = usuarioService.findOne(u.getId_usuario()).getPersona();
      model.addAttribute("FormularioTransferencia", new FormularioTransferencia());
      model.addAttribute("unidades", p.getUnidad().getSubUnidades());
      return "/FormularioTransferencias/formulario";
   }

   @PostMapping(value = "/RegistrosFormTRANSFERENCIA")
   public String RegistrosFormTRANSFERENCIA(HttpServletRequest request, Model model) {
      Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
      Usuario usuario2 = usuarioService.findOne(usuario.getId_usuario());
      model.addAttribute("rolesUserLog", usuario2.getPersona().getRoles());
      model.addAttribute("FormulariosTransferencias", formularioTransferenciaService.listaFormularioTransferenciaByIdUsuario(usuario2.getPersona().getId_persona()));
      Unidad unidad = unidadService.UnidadNombre("ARCHIVO Y BIBLIOTECA");
      model.addAttribute("usuariosArchivos", usuarioService.listaUsuarioPorUnidad(unidad.getId_unidad()));
      model.addAttribute("idUserLog", usuario2.getId_usuario());
      return "/FormularioTransferencias/tablaRegistros";
   }

   @PostMapping("/GuardarRegistroFormTRANSFERENCIA")
   @ResponseBody
   public ResponseEntity<String> GuardarRegistroFormTRANSFERENCIA(HttpServletRequest request, Model model,
         FormularioTransferencia formularioTransferencia) {
      Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
      Usuario u = usuarioService.findOne(usuario.getId_usuario());

      formularioTransferencia.setFechaRegistro(new Date());
      formularioTransferencia.setHoraRegistro(new Date());
      formularioTransferencia.setEstado("A");
      formularioTransferencia.setCantCajas(0);
      formularioTransferencia.setUnidad(u.getPersona().getUnidad());
      formularioTransferencia.setPersona(u.getPersona());
      formularioTransferenciaService.save(formularioTransferencia);
      Control control = new Control();
      control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
      control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
            + " de un Formulario de Transferencia");
      control.setUsuario(usuario);
      control.setFecha(new Date());
      control.setHora(new Date());
      controService.save(control);
      return ResponseEntity.ok("Se realizó el registro correctamente");

   }

   @GetMapping(value = "/ModFormTRANSFERENCIA/{id_formularioTransferencia}")
   public String ModFormTRANSFERENCIA(HttpServletRequest request, Model model,
         @PathVariable("id_formularioTransferencia") Long id_formularioTransferencia) {
      System.out.println("EDITAR VOLUMEN");

      model.addAttribute("FormularioTransferencia",
            formularioTransferenciaService.findOne(id_formularioTransferencia));
      model.addAttribute("edit", "true");

      return "/FormularioTransferencias/formulario";
   }

   @PostMapping(value = "/ModFormTRANSFERENCIAG")
   @ResponseBody
   public ResponseEntity<String> ModFormTRANSFERENCIAG(HttpServletRequest request,
         @Validated FormularioTransferencia formularioTransferencia,
         Model model) {
      Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(usuario.getId_usuario());
      FormularioTransferencia formularioTransferencia2 = formularioTransferenciaService
            .findOne(formularioTransferencia.getId_formularioTransferencia());
      formularioTransferencia.setFechaRegistro(formularioTransferencia2.getFechaRegistro());
      formularioTransferencia.setHoraRegistro(formularioTransferencia2.getHoraRegistro());
      formularioTransferencia.setPersona(formularioTransferencia2.getPersona());
      formularioTransferencia.setEstado("A");
      formularioTransferencia.setUnidad(formularioTransferencia2.getUnidad());
      formularioTransferenciaService.save(formularioTransferencia);
      Control control = new Control();
      control.setTipoControl(tipoControService.findAllByTipoControl("Modificación"));
      control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
            + " de un Formulario de Transferencia");
      control.setUsuario(userLog);
      control.setFecha(new Date());
      control.setHora(new Date());
      controService.save(control);
      return ResponseEntity.ok("Se Modificó el registro correctamente");
   }

   @PostMapping(value = "/EliminarRegistroFormTRANSFERENCIA/{id_formularioTransferencia}")
   @ResponseBody
   public void EliminarRegistroFormTRANSFERENCIA(HttpServletRequest request, Model model,
         @PathVariable("id_formularioTransferencia") Long id_formularioTransferencia) {
      
      Usuario us = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(us.getId_usuario());
      FormularioTransferencia formularioTransferencia = formularioTransferenciaService
            .findOne(id_formularioTransferencia);
      for (Caja caja : formularioTransferencia.getCajas()) {
         cajaService.delete(caja.getId_caja());
      }
      formularioTransferenciaService.delete(id_formularioTransferencia);
      Control control = new Control();
      control.setTipoControl(tipoControService.findAllByTipoControl("Eliminó"));
      control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
            + " de un Formulario de Transferencia");
      control.setUsuario(userLog);
      control.setFecha(new Date());
      control.setHora(new Date());
      controService.save(control);
   }

   @PostMapping(value = "/SubVentana")
   public String VentanaSubSerieDoc(HttpServletRequest request, Model model) {
      System.out.println("Sub ventana del modal");

      return "/FormularioTransferencias/subVentana";
   }

   @PostMapping(value = "/SubFormulario")
   public String SubFormulario(HttpServletRequest request, Model model) {
      model.addAttribute("cubiertas", cubiertaService.findAll());
      model.addAttribute("volumenes", volumenService.findAll());
      model.addAttribute("caja", new Caja());
      return "/FormularioTransferencias/SubFormulario";
   }

   @PostMapping(value = "/SubTablaRegistros/{id_formularioTransferencia}")
   public String SubFormulario(HttpServletRequest request, Model model,
         @PathVariable("id_formularioTransferencia") Long id_formularioTransferencia) {
      model.addAttribute("cajas", formularioTransferenciaService.findOne(id_formularioTransferencia).getCajas());
      // model.addAttribute("caja", new Caja());
      return "/FormularioTransferencias/tablaSubRegistros";
   }

   @PostMapping("/GuardarSubRegistro")
   @ResponseBody
   public void GuardarSubRegistro(HttpServletRequest request, Model model, @Validated Caja caja,
         @RequestParam(value = "id_formTransferencia", required = false) Long id_formTransferencia,
         @RequestParam(value = "gestion") String gestion) {
      // System.out.println("METODO REGISTRAR SERIE DOCUMENTAL");
      FormularioTransferencia formularioTransferencia = formularioTransferenciaService.findOne(id_formTransferencia);
      caja.setFormularioTransferencia(formularioTransferencia);
      caja.setGestion(Integer.parseInt(gestion));
      caja.setEstado("A");
      cajaService.save(caja);

      List<Caja> cajas = caja.getFormularioTransferencia().getCajas();

      int contarCajas = 0;
      int aux = 0;

      int[] filaCant = new int[cajas.size()];
      int i = 0;
      for (Caja caja2 : cajas) {
         filaCant[i] = caja2.getNroCaja();
         i++;
      }
      Arrays.sort(filaCant);

      for (Caja caja2 : cajas) {
         int c = caja2.getNroCaja();
         if (c > aux) {
            contarCajas++;
            System.out.println("CANTIDAD DE CAJAS: " + contarCajas);
            aux = c;
         }
      }
      formularioTransferencia.setCantCajas(contarCajas);
      formularioTransferenciaService.save(formularioTransferencia);

   }

   @PostMapping(value = "/ModSubRegistro/{id_caja}")
   public String ModSubRegistro(HttpServletRequest request, Model model,
         @PathVariable("id_caja") Long id_caja) {

      System.out.println("EDITAR Modu Cajas");

      model.addAttribute("cubiertas", cubiertaService.findAll());
      model.addAttribute("volumenes", volumenService.findAll());
      // model.addAttribute("cajas",
      // cajaService.findOne(id_caja).getFormularioTransferencia().getCajas());
      model.addAttribute("caja", cajaService.findOne(id_caja));
      model.addAttribute("edit", "true");

      return "/FormularioTransferencias/SubFormulario";
   }

   @PostMapping(value = "/ModSubRegistroG")
   @ResponseBody
   public void ModSerieDocG(Caja caja, Model model,
         @RequestParam(value = "id_formTransferencia", required = false) Long id_formTransferencia,
         @RequestParam(value = "gestion") String gestion) {
      // Caja caja2 = cajaService.findOne(caja.getId_caja());
      // caja.setFormularioTransferencia(formularioTransferenciaService.findOne(id_formTransferencia));
      caja.setGestion(Integer.parseInt(gestion));
      System.out.println("MOD CAJA SUBN");
      caja.setEstado("A");
      cajaService.save(caja);

      List<Caja> cajas = caja.getFormularioTransferencia().getCajas();

      int contarCajas = 0;
      int aux = 0;

      int[] filaCant = new int[cajas.size()];
      int i = 0;
      for (Caja caja2 : cajas) {
         filaCant[i] = caja2.getNroCaja();
         i++;
      }
      Arrays.sort(filaCant);

      for (Caja caja2 : cajas) {
         int c = caja2.getNroCaja();
         if (c > aux) {
            contarCajas++;
            System.out.println("CANTIDAD DE CAJAS: " + contarCajas);
            aux = c;
         }
      }
      FormularioTransferencia formularioTransferencia = caja.getFormularioTransferencia();
      formularioTransferencia.setCantCajas(contarCajas);
      formularioTransferenciaService.save(formularioTransferencia);
   }

   @PostMapping(value = "/EliminarSubRegistro/{id_caja}")
   @ResponseBody
   public void EliminarRegistroSerieDoc(HttpServletRequest request, Model model,
         @PathVariable("id_caja") Long id_caja) {
      System.out.println("Eliminar CAJA");

      Long idF = cajaService.findOne(id_caja).getFormularioTransferencia().getId_formularioTransferencia();

      cajaService.delete(id_caja);

      List<Caja> cajas = formularioTransferenciaService.findOne(idF).getCajas();

      int contarCajas = 0;
      int aux = 0;

      int[] filaCant = new int[cajas.size()];
      int i = 0;
      for (Caja caja2 : cajas) {
         filaCant[i] = caja2.getNroCaja();
         i++;
      }
      Arrays.sort(filaCant);

      for (Caja caja2 : cajas) {
         int c = caja2.getNroCaja();
         if (c > aux) {
            contarCajas++;
            System.out.println("CANTIDAD DE CAJAS: " + contarCajas);
            aux = c;
         }
      }
      FormularioTransferencia formularioTransferencia = formularioTransferenciaService.findOne(idF);
      formularioTransferencia.setCantCajas(contarCajas);
      formularioTransferenciaService.save(formularioTransferencia);
   }

   @GetMapping("/GenerarReporte/{id_formularioTransferencia}")
   public ResponseEntity<ByteArrayResource> verPdf(Model model, HttpServletRequest request,
         @PathVariable("id_formularioTransferencia") Long id_formularioTransferencia)
         throws DocumentException, MalformedURLException, IOException {
      byte[] bytes = generarPdf(id_formularioTransferencia);

      ByteArrayResource resource = new ByteArrayResource(bytes);

      return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + "Reporte de lista de Archivos.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(bytes.length)
            .body(resource);
   }

   public byte[] generarPdf(Long id) throws IOException, DocumentException {

      FormularioTransferencia formularioTransferencia = formularioTransferenciaService.findOne(id);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      Document document = new Document(PageSize.LETTER, 30f, 20f, 50f, 40f);
      Paragraph emptyParagraph = new Paragraph();

      try {
         PdfWriter writer = PdfWriter.getInstance(document, outputStream);
         document.open();

         // Encabezado
         Path projectPath = Paths.get("").toAbsolutePath();
         String imagen = projectPath + "/src/main/resources/static/logo/logoCabezera.png";
         String fuenteCalibriRegular = projectPath
               + "/src/main/resources/static/fuenteLetra/Calibri Regular.ttf";
         String fuenteCalibriBold = projectPath + "/src/main/resources/static/fuenteLetra/Calibri Bold.ttf";
         Font fontSimple = FontFactory.getFont(fuenteCalibriRegular, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11);
         Font fontNegrilla = FontFactory.getFont(fuenteCalibriBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11);
         Font fontSimple9 = FontFactory.getFont(fuenteCalibriRegular, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9);
         Font fontNegrilla9 = FontFactory.getFont(fuenteCalibriBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9);

         PdfPTable headerTable = new PdfPTable(4);
         headerTable.setWidthPercentage(95);

         float[] columntable = { 2.5f, 4f, 1f, 1f };
         headerTable.setWidths(columntable);

         Image image = Image.getInstance(imagen);
         PdfPCell celda1 = new PdfPCell(image, true);
         celda1.setRowspan(4);
         celda1.setPaddingTop(8);
         celda1.setPaddingBottom(8);
         celda1.setPaddingLeft(25);
         celda1.setPaddingRight(25);
         headerTable.addCell(celda1);

         PdfPCell celda2 = new PdfPCell(
               new Phrase("FORMULARIO DE TRANSFERENCIA DE DOCUMENTOS AL ARCHIVO CENTRAL", fontNegrilla));
         celda2.setVerticalAlignment(Element.ALIGN_MIDDLE);
         celda2.setHorizontalAlignment(Element.ALIGN_CENTER);
         celda2.setRowspan(4);
         headerTable.addCell(celda2);

         // Celda 6 ocupando la mitad de una columna
         PdfPCell celda6 = new PdfPCell(new Phrase("FORMATO", fontNegrilla9));
         celda6.setColspan(2); // Ocupará dos de las cuatro columnas
         celda6.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         celda6.setHorizontalAlignment(Element.ALIGN_CENTER);
         headerTable.addCell(celda6);

         // Celda 6 ocupando la mitad de una columna
         PdfPCell celda4 = new PdfPCell(new Phrase("FOR-ACH-GDC-01", fontNegrilla9));
         celda4.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         celda4.setHorizontalAlignment(Element.ALIGN_CENTER);
         celda4.setColspan(2); // Ocupará dos de las cuatro columnas
         headerTable.addCell(celda4);

         // Nueva celda para ocupar la otra mitad de la columna
         PdfPCell celda7 = new PdfPCell(new Phrase("Fecha de Aprobaci\u00F3n: 12/05/2023", fontSimple9));
         celda7.setColspan(2); // Ocupará dos de las cuatro columnas
         celda7.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         celda7.setHorizontalAlignment(Element.ALIGN_CENTER);
         headerTable.addCell(celda7);

         PdfPCell celda8 = new PdfPCell(new Phrase("Versi\u00F3n: 03", fontSimple9));
         celda8.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         celda8.setHorizontalAlignment(Element.ALIGN_CENTER);
         headerTable.addCell(celda8);
         PdfPCell celda9 = new PdfPCell(new Phrase("P\u00E1g.: 1 de 1", fontSimple9));
         celda9.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         celda9.setHorizontalAlignment(Element.ALIGN_CENTER);
         headerTable.addCell(celda9);

         document.add(headerTable);
         emptyParagraph.add(" ");
         document.add(emptyParagraph);

         // Tabla 2: Dividida en 2 columnas, 5 filas
         PdfPTable tablaSegunda = new PdfPTable(2);
         tablaSegunda.setWidthPercentage(95);

         float[] columnWidths2 = { 3.5f, 6f };
         tablaSegunda.setWidths(columnWidths2);

         PdfContentByte canvas2 = writer.getDirectContent();
         canvas2.saveState();
         canvas2.setLineDash(2, 2);

         PdfPCell cell1 = new PdfPCell(new Phrase("SECCIÓN DOCUMENTAL:", fontNegrilla));
         canvas2.setLineDash(2, 2);

         PdfPCell cell2 = new PdfPCell(new Phrase(formularioTransferencia.getUnidad().getNombre(), fontSimple));
         canvas2.setLineDash(2, 2);

         PdfPCell cell3 = new PdfPCell(new Phrase("SUB SECCIÓN DOCUMENTAL:", fontNegrilla));

         PdfPCell cell4 = new PdfPCell(
               new Phrase(formularioTransferencia.getUnidad().getUnidadPadre().getNombre(), fontSimple));

         PdfPCell cell5 = new PdfPCell(new Phrase("CANTIDAD DE CAJAS:", fontNegrilla));

         PdfPCell cell6 = new PdfPCell(
               new Phrase(String.valueOf(formularioTransferencia.getCantCajas()), fontSimple));

         PdfPCell cell7 = new PdfPCell(new Phrase("CANTIDAD DE DOCUMENTOS:", fontNegrilla));

         PdfPCell cell8 = new PdfPCell(
               new Phrase(String.valueOf(formularioTransferencia.getCantDocumentos()), fontSimple));

         PdfPCell cell9 = new PdfPCell(new Phrase("FECHAS EXTREMAS:", fontNegrilla));

         // PdfPCell cell10 = new PdfPCell(new
         // Phrase(formularioTransferencia.getFechaExtrema(), fontSimple));
         PdfPCell cell10 = new PdfPCell(
               new Phrase(utilidadService.fechaTexto(formularioTransferencia.getFechaExtrema()), fontSimple));

         // Agregar las celdas a la tabla
         tablaSegunda.addCell(cell1);
         tablaSegunda.addCell(cell2);
         tablaSegunda.addCell(cell3);
         tablaSegunda.addCell(cell4);
         tablaSegunda.addCell(cell5);
         tablaSegunda.addCell(cell6);
         tablaSegunda.addCell(cell7);
         tablaSegunda.addCell(cell8);
         tablaSegunda.addCell(cell9);
         tablaSegunda.addCell(cell10);

         document.add(tablaSegunda);
         canvas2.restoreState();
         emptyParagraph.add(" ");
         document.add(emptyParagraph);

         // TABLA 3
         PdfPTable tablaArchivos = new PdfPTable(7);
         tablaArchivos.setWidthPercentage(95);

         float[] columnWidths = { 0.6f, 1.4f, 4f, 1f, 1.5f, 1.5f, 1f };
         tablaArchivos.setWidths(columnWidths);

         // Crear celda con el color específico para la primera fila
         PdfPCell greenCell = new PdfPCell(new Phrase("NRO.", fontNegrilla));
         greenCell.setHorizontalAlignment(Element.ALIGN_CENTER);
         greenCell.setBackgroundColor(new BaseColor(101, 147, 8)); // HEX #659308
         greenCell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         greenCell.setHorizontalAlignment(Element.ALIGN_CENTER);
         greenCell.setPaddingBottom(12);
         greenCell.setPaddingTop(10);
         tablaArchivos.addCell(greenCell);

         PdfPCell greenCell2 = new PdfPCell(new Phrase("N° DE CAJA", fontNegrilla));
         greenCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
         greenCell2.setBackgroundColor(new BaseColor(101, 147, 8)); // HEX #659308
         greenCell2.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         greenCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
         tablaArchivos.addCell(greenCell2);

         PdfPCell greenCell3 = new PdfPCell(new Phrase("TITULO DOCUMENTAL", fontNegrilla));
         greenCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
         greenCell3.setBackgroundColor(new BaseColor(101, 147, 8)); // HEX #659308
         greenCell3.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         greenCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
         tablaArchivos.addCell(greenCell3);

         PdfPCell greenCell4 = new PdfPCell(new Phrase("AÑO", fontNegrilla));
         greenCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
         greenCell4.setBackgroundColor(new BaseColor(101, 147, 8)); // HEX #659308
         greenCell4.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         greenCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
         tablaArchivos.addCell(greenCell4);

         PdfPCell greenCell5 = new PdfPCell(new Phrase("VOLUMEN", fontNegrilla));
         greenCell5.setHorizontalAlignment(Element.ALIGN_CENTER);
         greenCell5.setBackgroundColor(new BaseColor(101, 147, 8)); // HEX #659308
         greenCell5.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         greenCell5.setHorizontalAlignment(Element.ALIGN_CENTER);
         tablaArchivos.addCell(greenCell5);

         PdfPCell greenCell6 = new PdfPCell(new Phrase("CUBIERTA", fontNegrilla));
         greenCell6.setHorizontalAlignment(Element.ALIGN_CENTER);
         greenCell6.setBackgroundColor(new BaseColor(101, 147, 8)); // HEX #659308
         greenCell6.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         greenCell6.setHorizontalAlignment(Element.ALIGN_CENTER);
         tablaArchivos.addCell(greenCell6);

         PdfPCell greenCell7 = new PdfPCell(new Phrase("NOTAS", fontNegrilla));
         greenCell7.setHorizontalAlignment(Element.ALIGN_CENTER);
         greenCell7.setBackgroundColor(new BaseColor(101, 147, 8)); // HEX #659308
         greenCell7.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
         greenCell7.setHorizontalAlignment(Element.ALIGN_CENTER);
         tablaArchivos.addCell(greenCell7);

         List<Caja> cajas = formularioTransferencia.getCajas();

         /*
          * int contarCajas = 0;
          * int aux = 0;
          * 
          * int[] filaCant = new int[cajas.size()];
          * int i = 0;
          * for (Caja caja2 : cajas) {
          * filaCant[i] = caja2.getNroCaja();
          * i++;
          * }
          * Arrays.sort(filaCant);
          * 
          * for (Caja caja2 : cajas) {
          * int c = caja2.getNroCaja();
          * if (c>aux) {
          * contarCajas++;
          * System.out.println("CANTIDAD DE CAJAS: " + contarCajas);
          * aux = c;
          * }
          * }
          */

         int c = 1;
         for (Caja caja : cajas) {
            System.out.println("cajas");
            PdfPCell titleCell = new PdfPCell(new Phrase(String.valueOf(c), fontSimple));
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaArchivos.addCell(titleCell);

            PdfPCell titleCell1 = new PdfPCell(new Phrase(String.valueOf(caja.getNroCaja()), fontSimple));
            titleCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaArchivos.addCell(titleCell1);

            PdfPCell titleCell2 = new PdfPCell(new Phrase(caja.getTituloDoc(), fontSimple));
            titleCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaArchivos.addCell(titleCell2);

            PdfPCell titleCell3 = new PdfPCell(new Phrase(String.valueOf(caja.getGestion()), fontSimple));
            titleCell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaArchivos.addCell(titleCell3);

            PdfPCell titleCell4 = new PdfPCell(new Phrase(caja.getVolumen().getNombre(), fontSimple));
            titleCell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaArchivos.addCell(titleCell4);

            PdfPCell titleCell5 = new PdfPCell(new Phrase(caja.getCubierta().getNombre(), fontSimple));
            titleCell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaArchivos.addCell(titleCell5);

            PdfPCell titleCell6 = new PdfPCell(new Phrase(caja.getNotas(), fontSimple));
            titleCell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaArchivos.addCell(titleCell6);
            c++;
         }

         document.add(tablaArchivos);

         emptyParagraph.add(" ");
         document.add(emptyParagraph);

         emptyParagraph.add(" ");
         Paragraph paragraph = new Paragraph(
               "Pando, " + utilidadService.fechaActualTexto(), fontSimple);
         paragraph.setAlignment(Paragraph.ALIGN_CENTER);
         document.add(paragraph);

         emptyParagraph.add(" ");
         document.add(emptyParagraph);
         emptyParagraph.add(" ");
         document.add(emptyParagraph);
         emptyParagraph.add(" ");
         document.add(emptyParagraph);

         Paragraph paragraph2 = new Paragraph("    ", fontSimple);
         paragraph2.setAlignment(Paragraph.ALIGN_CENTER);
         document.add(paragraph2);
         Paragraph textFinal = new Paragraph("Entregué Conforme                         Recibí Conforme",
               fontSimple);
         textFinal.setAlignment(Paragraph.ALIGN_CENTER);
         document.add(textFinal);

         // Cerrar el documento
         document.close();
      } catch (DocumentException e) {
         e.printStackTrace();
      }

      return outputStream.toByteArray();
   }

   private void configureCell(PdfPCell cell) {
      cell.setBackgroundColor(BaseColor.GREEN);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
      // Otras configuraciones de celda
   }

   private PdfPCell createCell(String content, Font font) {
      PdfPCell cell = new PdfPCell(new Phrase(content, font));
      configureCell(cell);
      return cell;
   }

   @GetMapping("/verIcoPdfFormulario/{id}")
   public ResponseEntity<byte[]> verIcoPdfFormulario(@PathVariable Long id) throws IOException, DocumentException {

      byte[] bytes = generarPdf(id);

      try {

         PDDocument document = PDDocument.load(bytes);

         // Obtener el renderizador PDF
         PDFRenderer renderer = new PDFRenderer(document);

         // Convertir la primera página a imagen
         BufferedImage image = renderer.renderImageWithDPI(0, 300); // Ajusta la resolución según tus necesidades

         // Crear un flujo de bytes para la imagen
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         // Guardar la imagen en formato JPG
         ImageIO.write(image, "jpg", baos);

         baos.flush();
         byte[] imageBytes = baos.toByteArray();
         baos.close();
         document.close();

         // Configurar los encabezados para evitar el caché
         HttpHeaders headers = new HttpHeaders();
         headers.setCacheControl("no-cache, no-store, must-revalidate");
         headers.setPragma("no-cache");
         headers.setExpires(0);

         return ResponseEntity.ok()
               .headers(headers)
               .contentType(MediaType.IMAGE_JPEG)
               .contentLength(imageBytes.length)
               .body(imageBytes);
      } catch (IOException e) {
         e.printStackTrace();
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

      }
   }

   // ----------- CUBIERTA AGREGAR ------------------
   @PostMapping("/RegistrarCubiertaA")
   public ResponseEntity<String> RegistrarCubiertaA(@RequestParam(value = "nombreCubierta") String nombre) {

      if (cubiertaService.buscarPorNombre(nombre) == null) {
         Cubierta cubierta = new Cubierta();
         cubierta.setNombre(nombre);
         cubierta.setEstado(nombre);
         cubiertaService.save(cubierta);
         return ResponseEntity.ok("Se realizó el registro correctamente");

      } else {
         return ResponseEntity.ok("Ya existe un registro con este nombre");
      }

   }

   @PostMapping("/DatoCubierta")
   public ResponseEntity<String[]> DatoCubierta(@RequestParam(value = "nombreCubierta") String nombre) {

      Cubierta cubierta = cubiertaService.buscarPorNombre(nombre);
      String[] cub = new String[2];
      cub[0] = String.valueOf(cubierta.getId_cubierta());
      cub[1] = cubierta.getNombre();
      return ResponseEntity.ok(cub);
   }
}
