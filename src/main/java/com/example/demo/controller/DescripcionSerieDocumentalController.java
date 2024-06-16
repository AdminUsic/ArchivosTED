package com.example.demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import com.example.demo.entity.DescripcionSerieDocumental;
import com.example.demo.entity.DescripcionSerieDocumentalDetalle;
import com.example.demo.entity.Persona;
import com.example.demo.entity.SerieDocumental;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.Unidad;
import com.example.demo.service.CubiertaService;
import com.example.demo.service.DescripcionSerieDocumentalDetalleService;
import com.example.demo.service.DescripcionSerieDocumentalService;
import com.example.demo.service.SerieDocumentalService;
import com.example.demo.service.UnidadService;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.UtilidadServiceProject;

import net.sf.jasperreports.engine.JRException;

@Controller
public class DescripcionSerieDocumentalController {

    @Autowired
    private SerieDocumentalService documentalService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UnidadService unidadService;

    @Autowired
    private DescripcionSerieDocumentalDetalleService descripcionSerieDocumentalDetalleService;

    @Autowired
    private DescripcionSerieDocumentalService descripcionSerieDocumentalService;

    @Autowired
    private CubiertaService cubiertaService;

    @Autowired
    private UtilidadServiceProject utilidadService;

    @GetMapping(value = "/DescripcionSerieDocumental")
    public String ventanaCarpeta(HttpServletRequest request, Model model) {
        System.out.println("PATALLA CARPETA");
        if (request.getSession().getAttribute("userLog") != null) {
            return "/descripcionSerieDocumental/ventana";
        } else {
            return "expiracion";
        }
    }

    @PostMapping(value = "/NuevoRegistroDescripcionSerieDoc")
    public String NuevoRegistroDescripcionSerieDoc(HttpServletRequest request, Model model) {
        model.addAttribute("descripcionSerieDocumental", new DescripcionSerieDocumental());
        model.addAttribute("seriesDocumentales", documentalService.listaSerieDocumentalPadre());
        model.addAttribute("unidades", unidadService.listaUnidadesPadres());
        return "/descripcionSerieDocumental/formulario";
    }

    @PostMapping(value = "/cargarSubSeriesDocumentales/{idPadre}")
    public ResponseEntity<String[][]> cargarSubSeriesDocumentales(@PathVariable("idPadre") Long idPadre) {
        List<SerieDocumental> serieDocumentales = documentalService.listaSubSerieDocumental(idPadre);
        String subSerie[][] = new String[serieDocumentales.size()][2];
        for (int i = 0; i < serieDocumentales.size(); i++) {
            subSerie[i][0] = serieDocumentales.get(i).getNombre();
            subSerie[i][1] = serieDocumentales.get(i).getId_serie().toString();
        }
        return ResponseEntity.ok(subSerie);
    }

    @PostMapping(value = "/cargarSubUnidades/{idPadre}")
    public ResponseEntity<String[][]> cargarSubUnidades(@PathVariable("idPadre") Long idPadre) {
        List<Unidad> unidades = unidadService.listaSubUnidadesPadres(idPadre);
        String subUnidades[][] = new String[unidades.size()][2];
        for (int i = 0; i < unidades.size(); i++) {
            subUnidades[i][0] = unidades.get(i).getNombre();
            subUnidades[i][1] = unidades.get(i).getId_unidad().toString();
        }
        return ResponseEntity.ok(subUnidades);
    }

    @PostMapping(value = "/RegistrosTipoDescripcionSeriesDoc")
    public String tablaRegistros(HttpServletRequest request, Model model) {
        model.addAttribute("descripcionSeriesDocumentales",
                descripcionSerieDocumentalService.listaDescripcionSerieDocumental());
        Usuario user = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(user.getId_usuario());
        model.addAttribute("permisos", userLog.getPermisos());
        return "/descripcionSerieDocumental/tablaRegistros";
    }

    @PostMapping(value = "/NuevoRegistroDescripcionSerieDocDetalle/{id_serie}")
    public String NuevoRegistroSubSerieDoc(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {
        model.addAttribute("descripcionSerieDocDetalle", new DescripcionSerieDocumentalDetalle());
        model.addAttribute("cubiertas", cubiertaService.findAll());
        model.addAttribute("id_descripcioSerieDocumental", id_serie);
        return "/descripcionSerieDocumental/formularioDescripcionSerieDetalle";
    }

    @PostMapping(value = "/RegistrosDescripcionSeriesDocDetalles/{id_serie}")
    public String RegistrosSubSeriesDoc(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {
        model.addAttribute("descripcionSeriesDocDetalles", descripcionSerieDocumentalDetalleService
                .listaDescripcionSerieDocumentalDetalleByIdDescripcionSerieDocumental(id_serie));
        return "/descripcionSerieDocumental/tablaRegistrosDescripcionSerie";
    }

    @GetMapping(value = "/VentanaDescripcionSerieDocDetalles/{id_serie}")
    public String VentanaDescripcionSerieDocDetalles(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {
        // System.out.println("EDITAR SERIE DOCUMENTAL");
        model.addAttribute("descripcionSerieDocPadre", descripcionSerieDocumentalService.findOne(id_serie));
        return "/descripcionSerieDocumental/ventanaDescripcionSeries";
    }

    @PostMapping("/GuardarRegistroDescripcionSeriesDoc")
    @ResponseBody
    public ResponseEntity<String> GuardarRegistroDescripcionSeriesDoc(HttpServletRequest request, Model model,
            DescripcionSerieDocumental descripcionSerieDocumental,
            @RequestParam(value = "subSeccion", required = false) Long selectSubSeccionDModal,
            @RequestParam(value = "subSerieDocumental", required = false) Long subSerieDocumental,
            @RequestParam(value = "fechaExtrema", required = false) String fechaExtrema) {
        // System.out.println("METODO REGISTRAR DESCRIPCION SERIE DOCUMENTAL");

        descripcionSerieDocumental.setEstado("A");
        descripcionSerieDocumental.setUnidad(unidadService.findOne(selectSubSeccionDModal));
        descripcionSerieDocumental.setSerieDocumental(documentalService.findOne(subSerieDocumental));
        descripcionSerieDocumental.setFechaExtrema(fechaExtrema);
        descripcionSerieDocumentalService.save(descripcionSerieDocumental);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @GetMapping(value = "/ModDescripcionSerieDoc/{id_serie}")
    public String EditarArchivo(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {
        // System.out.println("EDITAR DESCRIPCION SERIE DOCUMENTAL");

        model.addAttribute("descripcionSerieDocumental", descripcionSerieDocumentalService.findOne(id_serie));
        model.addAttribute("seriesDocumentales", documentalService.listaSerieDocumentalPadre());
        model.addAttribute("unidades", unidadService.listaUnidadesPadres());
        model.addAttribute("edit", "true");

        return "/descripcionSerieDocumental/formulario";
    }

    @PostMapping(value = "/ModDescripcionSerieDocDetalles/{id_serie}")
    public String ModDescripcionSerieDocDetalles(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {

        model.addAttribute("descripcionSerieDocDetalle", descripcionSerieDocumentalDetalleService.findOne(id_serie));
        model.addAttribute("cubiertas", cubiertaService.findAll());
        model.addAttribute("id_descripcioSerieDocumental", id_serie);
        model.addAttribute("edit", "true");

        return "/descripcionSerieDocumental/formularioDescripcionSerieDetalle";
    }

    @PostMapping("/ModDescripcionSerieDocG")
    @ResponseBody
    public ResponseEntity<String> ModDescripcionSerieDocG(HttpServletRequest request, Model model,
            DescripcionSerieDocumental descripcionSerieDocumental,
            @RequestParam(value = "subSeccion", required = false) Long selectSubSeccionDModal,
            @RequestParam(value = "subSerieDocumental", required = false) Long subSerieDocumental,
            @RequestParam(value = "fechaExtrema", required = false) String fechaExtrema) {
        // System.out.println("METODO REGISTRAR DESCRIPCION SERIE DOCUMENTAL");

        DescripcionSerieDocumental dSerieDocumental = descripcionSerieDocumentalService
                .findOne(descripcionSerieDocumental.getId_descripcion_serie_documental());
        dSerieDocumental.setUnidad(unidadService.findOne(selectSubSeccionDModal));
        dSerieDocumental.setSerieDocumental(documentalService.findOne(subSerieDocumental));
        dSerieDocumental.setFechaExtrema(fechaExtrema);
        dSerieDocumental.setCodigoSerie(descripcionSerieDocumental.getCodigoSerie());
        dSerieDocumental.setMetroLineal(descripcionSerieDocumental.getMetroLineal());
        dSerieDocumental.setUnidadDocumental(descripcionSerieDocumental.getUnidadDocumental());
        descripcionSerieDocumentalService.save(dSerieDocumental);
        return ResponseEntity.ok("Se guardaron los cambios correctamente");
    }

    @PostMapping(value = "/EliminarRegistroDescripcionSerieDoc/{id_serie}")
    @ResponseBody
    public void EliminarRegistroDescripcionSerieDoc(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {
        // System.out.println("Eliminar SERIE DOCUMENTAL");
        descripcionSerieDocumentalService.delete(id_serie);
    }

    @PostMapping("/GuardarRegistroDescripcionSeriesDocDetalle")
    @ResponseBody
    public ResponseEntity<String> GuardarRegistroDescripcionSeriesDocDetalle(HttpServletRequest request, Model model,
            DescripcionSerieDocumentalDetalle descripcionSerieDocumentalDetalle,
            @RequestParam(value = "id_seriePadre", required = false) Long id_seriePadre) {
        // System.out.println("METODO REGISTRAR DESCRIPCION SERIE DOCUMENTAL");
        descripcionSerieDocumentalDetalle
                .setDescripcionSerieDocumental(descripcionSerieDocumentalService.findOne(id_seriePadre));
        descripcionSerieDocumentalDetalle.setEstado("A");
        descripcionSerieDocumentalDetalleService.save(descripcionSerieDocumentalDetalle);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/GuardarCambiosDescripcionSeriesDocDetalle")
    @ResponseBody
    public ResponseEntity<String> GuardarCambiosDescripcionSeriesDocDetalle(HttpServletRequest request, Model model,
            DescripcionSerieDocumentalDetalle descripcionSerieDocumentalDetalle2) {
        // System.out.println("METODO REGISTRAR DESCRIPCION SERIE DOCUMENTAL");
        DescripcionSerieDocumentalDetalle descripcionSerieDocumentalDetalle = descripcionSerieDocumentalDetalleService
                .findOne(descripcionSerieDocumentalDetalle2.getId_descripcion_serie_documental_detalle());
        descripcionSerieDocumentalDetalle.setCubierta(descripcionSerieDocumentalDetalle2.getCubierta());
        descripcionSerieDocumentalDetalle.setContenido(descripcionSerieDocumentalDetalle2.getContenido());
        descripcionSerieDocumentalDetalle.setFechaExtrema(descripcionSerieDocumentalDetalle2.getFechaExtrema());
        descripcionSerieDocumentalDetalle.setUbicacionFisica(descripcionSerieDocumentalDetalle2.getUbicacionFisica());
        descripcionSerieDocumentalDetalle.setObservacion(descripcionSerieDocumentalDetalle2.getObservacion());
        descripcionSerieDocumentalDetalle.setVerificaion(descripcionSerieDocumentalDetalle2.getVerificaion());
        descripcionSerieDocumentalDetalleService.save(descripcionSerieDocumentalDetalle);
        return ResponseEntity.ok("Se guardaron los cambios correctamente");
    }

    @PostMapping(value = "/EliminarRegistroDescripcionSerieDocDetalle/{id_serie}")
    @ResponseBody
    public void EliminarRegistroDescripcionSerieDocDetalle(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {
        // System.out.println("Eliminar SERIE DOCUMENTAL");
        descripcionSerieDocumentalDetalleService.delete(id_serie);
    }

    @GetMapping("/GenerarReporteDescripcionSerie/{id_descripcionSerie}")
    public ResponseEntity<ByteArrayResource> GenerarReporteDescripcionSerie(Model model, HttpServletRequest request,
            @PathVariable("id_descripcionSerie") Long id_descripcionSerie) throws SQLException {
        // byte[] bytes = generarPdf(id_formularioTransferencia);
        String nombreArchivo = "DescripcionSerieDocReport.jrxml";

        Path projectPath = Paths.get("").toAbsolutePath();
        Path imagePath = Paths.get(projectPath.toString(), "src", "main", "resources", "static", "logo",
                "logoCabezera.png");
        String imagen = imagePath.toString();
        // System.out.println(imagen);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("idDescripcionSerie", id_descripcionSerie);
        parametros.put("rutaImg", imagen);
        //parametros.put("lugarFechaTexto", "Cobija, " + utilidadService.fechaActualTexto());

        ByteArrayOutputStream stream;
        try {
            stream = utilidadService.compilarAndExportarReporte(nombreArchivo, parametros);
            byte[] bytes = stream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + "Descripcion Serie Documental.pdf")
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

    @GetMapping("/GenerarReporteExcelDescripcionSerie/{id_descripcionSerie}")
    public ResponseEntity<ByteArrayResource> GenerarReporteExcelDescripcionSerie(Model model,
            HttpServletRequest request,
            @PathVariable("id_descripcionSerie") Long id_descripcionSerie) throws SQLException {

        String nombreArchivo = "DescripcionSerieDocReport.jrxml";

        Path projectPath = Paths.get("").toAbsolutePath();
        Path imagePath = Paths.get(projectPath.toString(), "src", "main", "resources", "static", "logo",
                "logoCabezera.png");
        String imagen = imagePath.toString();

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("idDescripcionSerie", id_descripcionSerie);
        parametros.put("rutaImg", imagen);
        //parametros.put("lugarFechaTexto", "Cobija, " + utilidadService.fechaActualTexto());

        ByteArrayOutputStream stream;
        try {
            stream = utilidadService.compilarAndExportarReporteExcel(nombreArchivo, parametros);
            byte[] bytes = stream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Descripcion Serie Documental.xlsx")
                    .contentType(
                            MediaType.parseMediaType(
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(bytes.length)
                    .body(resource);
        } catch (IOException | JRException e) {
            // Manejo de excepciones comunes
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Devolver un estado de error
        }
    }
}
