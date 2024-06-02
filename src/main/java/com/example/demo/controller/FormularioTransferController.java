package com.example.demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

import com.example.demo.entity.Caja;
import com.example.demo.entity.Control;
import com.example.demo.entity.Cubierta;
import com.example.demo.entity.FormularioTransferencia;
import com.example.demo.entity.Persona;
import com.example.demo.entity.Unidad;
import com.example.demo.entity.Usuario;
import com.example.demo.service.CajaService;
import com.example.demo.service.ControlService;
import com.example.demo.service.CubiertaService;
import com.example.demo.service.PersonaService;
import com.example.demo.service.TipoControService;
import com.example.demo.service.FormularioTransferenciaService;
import com.example.demo.service.UnidadService;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.UtilidadServiceProject;
import com.example.demo.service.VolumenService;
import com.itextpdf.text.DocumentException;

import net.sf.jasperreports.engine.JRException;

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
      model.addAttribute("FormulariosTransferencias", formularioTransferenciaService
            .listaFormularioTransferenciaByIdUsuario(usuario2.getPersona().getId_persona()));
      Unidad unidad = unidadService.UnidadNombre("ARCHIVO Y BIBLIOTECA");
      model.addAttribute("usuariosArchivos", usuarioService.listaUsuarioPorUnidad(unidad.getId_unidad()));
      model.addAttribute("idUserLog", usuario2.getId_usuario());
      return "/FormularioTransferencias/tablaRegistros";
   }

   @PostMapping("/GuardarRegistroFormTRANSFERENCIA")
   @ResponseBody
   public ResponseEntity<String> GuardarRegistroFormTRANSFERENCIA(HttpServletRequest request, Model model,
         FormularioTransferencia formularioTransferencia) throws java.text.ParseException {
      Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
      Usuario u = usuarioService.findOne(usuario.getId_usuario());

      // // Obtén la fecha en formato DD/MM/YYYY
      // String fechaStr = formularioTransferencia.getFechaExtremaFormat();

      // // Define el formato de entrada y salida
      // SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy");
      // SimpleDateFormat sdfOutput = new SimpleDateFormat("yy-MM-dd");

      // try {
      // // Parsea la fecha en formato DD/MM/YYYY
      // Date fechaParseada = sdfInput.parse(fechaStr);

      // // Formatea la fecha al formato yy-MM-dd
      // String fechaFormateadaStr = sdfOutput.format(fechaParseada);

      // // Parsea la fecha formateada al tipo Date
      // Date fechaFormateada = sdfOutput.parse(fechaFormateadaStr);

      // // Asigna la fecha formateada al atributo FechaExtrema
      // formularioTransferencia.setFechaExtrema(fechaFormateada);
      // } catch (ParseException e) {
      // // Manejar la excepción de parseo de fecha aquí
      // e.printStackTrace();
      // }

      formularioTransferencia.setFechaRegistro(new Date());
      formularioTransferencia.setHoraRegistro(new Date());
      formularioTransferencia.setEstado("A");
      formularioTransferencia.setUnidad(u.getPersona().getUnidad());
      formularioTransferencia.setCargo(u.getPersona().getCargo());
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
         @PathVariable("id_formularioTransferencia") Long id_formularioTransferencia) throws java.text.ParseException {
      FormularioTransferencia formularioTransferencia = formularioTransferenciaService
            .findOne(id_formularioTransferencia);
      // Define el formato de entrada y salida
      // SimpleDateFormat sdfInput = new SimpleDateFormat("yy-MM-dd");
      // SimpleDateFormat sdfOutput = new SimpleDateFormat("dd/MM/yyyy");

      // try {
      // // Parsea la fecha en formato yy-MM-dd
      // String fechaStr = sdfInput.format(formularioTransferencia.getFechaExtrema());

      // // Formatea la fecha al formato DD/MM/YYYY
      // Date fechaFormateada = sdfInput.parse(fechaStr);

      // // Convierte la fecha formateada a una cadena en formato DD/MM/YYYY
      // formularioTransferencia.setFechaExtremaFormat(sdfOutput.format(fechaFormateada));
      // } catch (ParseException e) {
      // // Manejar la excepción de parseo de fecha aquí
      // e.printStackTrace();
      // }

      model.addAttribute("FormularioTransferencia", formularioTransferencia);
      model.addAttribute("edit", "true");

      return "/FormularioTransferencias/formulario";
   }

   @PostMapping(value = "/ModFormTRANSFERENCIAG")
   @ResponseBody
   public ResponseEntity<String> ModFormTRANSFERENCIAG(HttpServletRequest request,
         @Validated FormularioTransferencia formularioTransferencia,
         Model model) throws java.text.ParseException {
      Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(usuario.getId_usuario());

      FormularioTransferencia formularioRegistrado = formularioTransferenciaService
            .findOne(formularioTransferencia.getId_formularioTransferencia());

      // // Obtén la fecha en formato DD/MM/YYYY
      // String fechaStr = formularioTransferencia.getFechaExtremaFormat();

      // // Define el formato de entrada y salida
      // SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy");
      // SimpleDateFormat sdfOutput = new SimpleDateFormat("yy-MM-dd");

      // try {
      // // Parsea la fecha en formato DD/MM/YYYY
      // Date fechaParseada = sdfInput.parse(fechaStr);

      // // Formatea la fecha al formato yy-MM-dd
      // String fechaFormateadaStr = sdfOutput.format(fechaParseada);

      // // Parsea la fecha formateada al tipo Date
      // Date fechaFormateada = sdfOutput.parse(fechaFormateadaStr);

      // // Asigna la fecha formateada al atributo FechaExtrema
      // formularioRegistrado.setFechaExtrema(fechaFormateada);
      // } catch (ParseException e) {
      // // Manejar la excepción de parseo de fecha aquí
      // e.printStackTrace();
      // }
      formularioRegistrado.setGestion(formularioTransferencia.getGestion());
      formularioRegistrado.setCantDocumentos(formularioTransferencia.getCantDocumentos());
      formularioTransferenciaService.save(formularioRegistrado);

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
      if (request.getSession().getAttribute("userLog") != null) {
         return "/FormularioTransferencias/subVentana";
      } else {
         return "expiracion";
      }
   }

   @PostMapping(value = "/SubFormulario")
   public String SubFormulario(HttpServletRequest request, Model model) {
      model.addAttribute("cubiertas", cubiertaService.findAll());
      model.addAttribute("volumenes", volumenService.listaDeVolumenes());
      model.addAttribute("caja", new Caja());
      return "/FormularioTransferencias/SubFormulario";
   }

   @PostMapping(value = "/SubTablaRegistros/{id_formularioTransferencia}")
   public String SubFormulario(HttpServletRequest request, Model model,
         @PathVariable("id_formularioTransferencia") Long id_formularioTransferencia) {
      model.addAttribute("cajas", formularioTransferenciaService.findOne(id_formularioTransferencia).getCajas());
      return "/FormularioTransferencias/tablaSubRegistros";
   }

   @PostMapping("/GuardarSubRegistro")
   @ResponseBody
   public void GuardarSubRegistro(HttpServletRequest request, Model model, @Validated Caja caja,
         @RequestParam(value = "id_formTransferencia", required = false) Long id_formTransferencia,
         @RequestParam(value = "gestion") String gestion) {
      FormularioTransferencia formularioTransferencia = formularioTransferenciaService.findOne(id_formTransferencia);
      caja.setFormularioTransferencia(formularioTransferencia);
      caja.setGestion(Integer.parseInt(gestion));
      caja.setEstado("A");

      cajaService.save(caja);

      formularioTransferenciaService.save(formularioTransferencia);

   }

   @PostMapping(value = "/ModSubRegistro/{id_caja}")
   public String ModSubRegistro(HttpServletRequest request, Model model,
         @PathVariable("id_caja") Long id_caja) {

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

      caja.setGestion(Integer.parseInt(gestion));
      caja.setEstado("A");
      cajaService.save(caja);

      FormularioTransferencia formularioTransferencia = caja.getFormularioTransferencia();
      formularioTransferenciaService.save(formularioTransferencia);
   }

   @PostMapping(value = "/EliminarSubRegistro/{id_caja}")
   @ResponseBody
   public void EliminarRegistroSerieDoc(HttpServletRequest request, Model model,
         @PathVariable("id_caja") Long id_caja) {

      Long idF = cajaService.findOne(id_caja).getFormularioTransferencia().getId_formularioTransferencia();

      cajaService.delete(id_caja);

   }

   @GetMapping("/GenerarReporte/{id_formularioTransferencia}")
   public ResponseEntity<ByteArrayResource> verPdf(Model model, HttpServletRequest request,
         @PathVariable("id_formularioTransferencia") Long id_formularioTransferencia) throws SQLException {
      // byte[] bytes = generarPdf(id_formularioTransferencia);
      String nombreArchivo = "FormularioTransferenciaReport.jrxml";

      Path projectPath = Paths.get("").toAbsolutePath();
      Path imagePath = Paths.get(projectPath.toString(), "src", "main", "resources", "static", "logo",
            "logoCabezera.png");
      String imagen = imagePath.toString();
      // System.out.println(imagen);
      Map<String, Object> parametros = new HashMap<>();
      parametros.put("idFormulario", id_formularioTransferencia);
      parametros.put("rutaImg", imagen);
      parametros.put("lugarFechaTexto", "Cobija, " + utilidadService.fechaActualTexto());

      ByteArrayOutputStream stream;
      try {
         stream = utilidadService.compilarAndExportarReporte(nombreArchivo, parametros);
         byte[] bytes = stream.toByteArray();
         ByteArrayResource resource = new ByteArrayResource(bytes);

         return ResponseEntity.ok()
               .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + "Formulario de Transferencia.pdf")
               .contentType(MediaType.APPLICATION_PDF)
               .contentLength(bytes.length)
               .body(resource);
      } catch (IOException | JRException e) {
         // Manejo de excepciones comunes
         System.out.println("ERROR: " + e.getMessage());
         e.printStackTrace();
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Devolver un estado de error
      }
   }

   @GetMapping("/verIcoPdfFormulario/{id}")
   public ResponseEntity<byte[]> verIcoPdfFormulario(@PathVariable Long id)
         throws DocumentException, JRException, SQLException {

      String nombreArchivo = "FormularioTransferenciaReport.jrxml";

      Path projectPath = Paths.get("").toAbsolutePath();
      Path imagePath = Paths.get(projectPath.toString(), "src", "main", "resources", "static", "logo",
            "logoCabezera.png");
      String imagen = imagePath.toString();
      // System.out.println(imagen);
      Map<String, Object> parametros = new HashMap<>();
      parametros.put("idFormulario", id);
      parametros.put("rutaImg", imagen);
      parametros.put("lugarFechaTexto", "Cobija, " + utilidadService.fechaActualTexto());

      ByteArrayOutputStream stream;
      try {
         try {
            stream = utilidadService.compilarAndExportarReporte(nombreArchivo, parametros);
            byte[] bytes = stream.toByteArray();
            PDDocument document = PDDocument.load(bytes);
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 300);
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
         } catch (JRException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
         }
      } catch (IOException e) {
         System.out.println("ERROR: " + e.getMessage());
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
