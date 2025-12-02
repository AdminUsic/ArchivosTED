package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.example.demo.entity.Archivo;
import com.example.demo.entity.Caja;
import com.example.demo.entity.Carpeta;
import com.example.demo.entity.Control;
import com.example.demo.entity.Cubierta;
import com.example.demo.entity.FormularioTransferencia;
import com.example.demo.entity.SerieDocumental;
import com.example.demo.entity.TipoArchivo;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.Volumen;
import com.example.demo.service.ArchivoService;
import com.example.demo.service.CajaService;
import com.example.demo.service.CarpetaService;
import com.example.demo.service.ControlService;
import com.example.demo.service.CubiertaService;
import com.example.demo.service.FormularioTransferenciaService;
import com.example.demo.service.PersonaService;
import com.example.demo.service.SerieDocumentalService;
import com.example.demo.service.TipoArchivoService;
import com.example.demo.service.TipoControService;
import com.example.demo.service.UnidadService;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.UtilidadServiceProject;
import com.example.demo.service.VolumenService;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ArchivoController {
    @Autowired
    private ArchivoService archivoService;

    @Autowired
    private TipoArchivoService tipoArchivoService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private CarpetaService carpetaService;

    @Autowired
    private UnidadService unidadService;

    @Autowired
    private VolumenService volumenService;

    @Autowired
    private SerieDocumentalService documentalService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TipoControService tipoControService;

    @Autowired
    private ControlService controService;

    @Autowired
    private UtilidadServiceProject utilidadService;

    @Autowired
    private FormularioTransferenciaService formularioTransferenciaService;

    @Autowired
    private CajaService cajaService;

    @Autowired
    private CubiertaService cubiertaService;

    @GetMapping("/DOCUMENTOS")
    public String ventanaDocumentos(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("userLog") != null) {
            return "/archivos/registrar";
        } else {
            return "expiracion";
        }
    }

    @PostMapping(value = "/NuevoRegistroArchivo")
    public String NuevoRegistroArchivo(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("userLog") != null) {
            model.addAttribute("archivo", new Archivo());
            model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
            model.addAttribute("carpetas", carpetaService.findAll());
            model.addAttribute("personas", personaService.findAll());
            model.addAttribute("seccionesDocumental", unidadService.findAll());
            model.addAttribute("cubiertas", cubiertaService.findAll());
            return "/archivos/formulario";
        } else {
            return "expiracion";
        }
    }

    @PostMapping(value = "/RegistrosArchivos")
    public String tablaRegistros(HttpServletRequest request, Model model) {
        model.addAttribute("listaArchivos", archivoService.findAll());
        return "/archivos/tablaRegistros";
    }

    @PostMapping(value = "/RegistrosArchivosCarpeta/{id_archivo}")
    public String RegistrosArchivosCarpeta(HttpServletRequest request, Model model,
            @PathVariable("id_archivo") Long id_archivo) {
        model.addAttribute("listaArchivos", archivoService.archivosCarpeta(id_archivo));
        return "/archivos/tablaRegistros";
    }

    @PostMapping(value = "/SubSecciones/{id_carpeta}")
    public ResponseEntity<String[][]> SubSecciones(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {
        Carpeta carpeta = carpetaService.findOne(id_carpeta);
        String subSecciones[][] = new String[carpeta.getUnidad().getSubUnidades().size()][2];

        for (int i = 0; i < subSecciones.length; i++) {
            int j = 0;
            subSecciones[i][j] = carpeta.getUnidad().getSubUnidades().get(i).getId_unidad().toString();
            j++;
            subSecciones[i][j] = carpeta.getUnidad().getSubUnidades().get(i).getNombre();
        }

        return ResponseEntity.ok(subSecciones);
    }

    @PostMapping("/GuardarRegistroArchivo")
    public ResponseEntity<String> GuardarRegistroArchivo(HttpServletRequest request, Model model,
            @RequestParam(value = "PDF", required = false) MultipartFile archivoPdf, @Validated Archivo archivo,
            @RequestParam(value = "id_carpeta", required = false) Long id_carpeta) {
        if (request.getSession().getAttribute("userLog") != null) {
            Usuario us = (Usuario) request.getSession().getAttribute("userLog");
            Usuario userLog = usuarioService.findOne(us.getId_usuario());
            byte[] contenido = null;

            try {
                if (archivoPdf != null && !archivoPdf.isEmpty()) {

                    contenido = archivoPdf.getBytes();
                    byte[] encryptedBytes = utilidadService.encrypt(contenido);
                    archivo.setContenido(encryptedBytes);
                    PDDocument document = PDDocument.load(contenido);
                    // archivo.setCantidadHojas(document.getNumberOfPages());
                    String nombFile = archivoPdf.getOriginalFilename();
                    String extension = getFileExtension(nombFile);

                    TipoArchivo tipoArchivo = tipoArchivoService.tipoArchivoByTipo(extension);
                    if (tipoArchivo == null) {
                        tipoArchivo = new TipoArchivo();
                        tipoArchivo.setNombre_tipo(extension);
                        tipoArchivo.setEstado("A");
                        tipoArchivoService.save(tipoArchivo);
                    }
                    archivo.setTipoArchivo(tipoArchivo);

                    try {
                        archivo.setIcono(utilidadService.extraerIconPdf(archivoPdf));
                    } catch (Exception e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }
                    document.close();
                }
            } catch (Exception e) {
                System.out.println("Error en la encriptacion: " + e);
                e.printStackTrace();
            }
            Carpeta carpeta = carpetaService.findOne(id_carpeta);
            archivo.setCarpeta(carpeta);
            // if (id_carpeta != null) {
            // Carpeta carpeta = carpetaService.findOne(id_carpeta);
            // archivo.setCarpeta(carpeta);
            // }
            archivo.setFechaRegistro(new Date());
            archivo.setHoraRegistro(new Date());
            archivo.setEstado("A");
            archivo.setPersona(userLog.getPersona());
            archivo.setUnidad(userLog.getPersona().getUnidad());
            archivoService.save(archivo);

            carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
            FormularioTransferencia formularioTransferencia = formularioTransferenciaService
                    .formularioTransferenciaCarpeta(id_carpeta);
            formularioTransferencia.setCantDocumentos(carpeta.getTotalArchivo());
            formularioTransferenciaService.save(formularioTransferencia);

            Caja caja = new Caja();
            caja.setArchivo(archivo);
            caja.setGestion(archivo.getGestion());
            caja.setTituloDoc(archivo.getNombre());
            caja.setEstado("A");
            caja.setNotas(archivo.getNota());
            caja.setFojas(archivo.getCantidadHojas());
            caja.setVolumen(carpeta.getVolumen());
            caja.setCubierta(archivo.getCubierta());
            caja.setFormularioTransferencia(formularioTransferencia);
            cajaService.save(caja);

            Control control = new Control();
            control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
            control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
                    + " de una nueva documental " + archivo.getNombre());
            control.setUsuario(userLog);
            control.setFecha(new Date());
            control.setHora(new Date());
            controService.save(control);
            return ResponseEntity.ok("Se ha Registrado el Archivo correctamente");
        } else {
            return ResponseEntity.ok("Se ha cerrado la sesion por inactividad.");
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }

    @GetMapping(value = "/ModArchivo/{id_archivo}")
    public String EditarArchivo(HttpServletRequest request, Model model,
            @PathVariable("id_archivo") Long id_archivo) {
        if (request.getSession().getAttribute("userLog") != null) {
            model.addAttribute("archivo", archivoService.findOne(id_archivo));
            model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
            model.addAttribute("carpetas", carpetaService.findAll());
            model.addAttribute("personas", personaService.findAll());
            model.addAttribute("seccionesDocumental", unidadService.findAll());
            model.addAttribute("edit", "true");
            model.addAttribute("cubiertas", cubiertaService.findAll());
            return "/archivos/formulario";
        } else {
            return "expiracion";
        }
    }

    @GetMapping(value = "/ModArchivoModal/{id_archivo}")
    public String EditarArchivoModal(HttpServletRequest request, Model model,
            @PathVariable("id_archivo") Long id_archivo) {
        if (request.getSession().getAttribute("userLog") != null) {
            model.addAttribute("archivo", archivoService.findOne(id_archivo));
            model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
            model.addAttribute("carpetas", carpetaService.findAll());
            model.addAttribute("personas", personaService.findAll());
            model.addAttribute("seccionesDocumental", unidadService.findAll());
            model.addAttribute("edit", "true");
            return "/archivos/formularioModal";
        } else {
            return "expiracion";
        }
    }

    @GetMapping(value = "/ContenidoArchivo/{id_archivo}")
    public String ContenidoArchivo(HttpServletRequest request, Model model,
            @PathVariable("id_archivo") Long id_archivo) {
        if (request.getSession().getAttribute("userLog") != null) {
            model.addAttribute("archivo", archivoService.findOne(id_archivo));
            model.addAttribute("edit", "true");
            return "/archivos/archivo";
        } else {
            return "expiracion";
        }
    }

    @PostMapping(value = "/ModArchivoG")
    public ResponseEntity<String> ModArchivoG(HttpServletRequest request, Model model,
            @RequestParam(value = "PDF", required = false) MultipartFile archivoPdf, Archivo archivo,
            @RequestParam(value = "id_carpeta", required = false) Long id_carpeta,
            @RequestParam("id_archivo") Long id_archivo) {
        Usuario us = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(us.getId_usuario());
        Archivo archivoAntes = archivoService.findOne(id_archivo);
        try {

            if (archivoPdf.isEmpty()) {
                archivo.setContenido(archivoAntes.getContenido());
                // archivo.setCantidadHojas(archivoAntes.getCantidadHojas());
                archivo.setIcono(archivoAntes.getIcono());
            } else if (archivoPdf != null && !archivoPdf.isEmpty()) {
                byte[] contenido = archivoPdf.getBytes();

                try {
                    contenido = archivoPdf.getBytes();
                    byte[] encryptedBytes = utilidadService.encrypt(contenido);
                    archivo.setContenido(encryptedBytes);
                    PDDocument document = PDDocument.load(contenido);
                    // archivo.setCantidadHojas(document.getNumberOfPages());
                    String nombFile = archivoPdf.getOriginalFilename();
                    String extension = getFileExtension(nombFile);

                    TipoArchivo tipoArchivo = tipoArchivoService.tipoArchivoByTipo(extension);
                    if (tipoArchivo == null) {
                        tipoArchivo = new TipoArchivo();
                        tipoArchivo.setNombre_tipo(extension);
                        tipoArchivo.setEstado("A");
                        tipoArchivoService.save(tipoArchivo);
                        archivo.setTipoArchivo(tipoArchivo);
                    } else {
                        archivo.setTipoArchivo(archivoAntes.getTipoArchivo());
                    }

                    try {
                        archivo.setIcono(utilidadService.extraerIconPdf(archivoPdf));
                    } catch (Exception e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }
                    document.close();
                } catch (Exception e) {
                    System.out.println("ERROR EN LA ENCRIPTACION DEL ARCHIVO MODIFICADO: " + e);
                }
                PDDocument document = PDDocument.load(contenido);
                archivo.setCantidadHojas(document.getNumberOfPages());
                document.close();
            }
            // System.out.println("Fecha de emision: " + fechaEmision);
            Carpeta carpeta = carpetaService.findOne(id_carpeta);
            carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
            if (id_carpeta != null) {
                // Carpeta carpeta = carpetaService.findOne(id_carpeta);
                archivo.setCarpeta(carpeta);
            }
            archivo.setPersona(archivoAntes.getPersona());
            archivo.setUnidad(archivoAntes.getUnidad());
            archivo.setFechaRegistro(archivoAntes.getFechaRegistro());
            archivo.setHoraRegistro(archivoAntes.getHoraRegistro());
            archivo.setEstado(archivoAntes.getEstado());
            archivoService.save(archivo);

            FormularioTransferencia formularioTransferencia = formularioTransferenciaService
                    .formularioTransferenciaCarpeta(id_carpeta);
            formularioTransferencia.setCantDocumentos(carpeta.getTotalArchivo());
            formularioTransferenciaService.save(formularioTransferencia);

            Caja caja = cajaService.archivoCaja(id_archivo);
            caja.setGestion(archivo.getGestion());
            caja.setTituloDoc(archivo.getNombre());
            caja.setNotas(archivo.getNota());
            caja.setFojas(archivo.getCantidadHojas());
            caja.setVolumen(carpeta.getVolumen());
            caja.setCubierta(archivo.getCubierta());
            caja.setArchivo(archivo);
            cajaService.save(caja);

            System.out.println("ARCHIVO MODIFICADO CORRECTAMENTE");
            Control control = new Control();
            control.setTipoControl(tipoControService.findAllByTipoControl("Modificación"));
            control.setDescripcion("Realizó una nueva " + control.getTipoControl().getNombre()
                    + " de la nueva documental " + archivo.getNombre());
            control.setUsuario(userLog);
            control.setFecha(new Date());
            control.setHora(new Date());
            controService.save(control);
            return ResponseEntity.ok("Se ha Modificado el Archivo correctamente");
        } catch (IOException e) {
            System.out.println("ERROR MODIFICAR ARCHIVO: " + e);
            return ResponseEntity.ok("ERROR MODIFICAR EL ARCHIVO");
        }
    }

    @PostMapping(value = "/EliminarRegistroArchivo/{id_archivo}")
    @ResponseBody
    public ResponseEntity<String> EliminarRegistroArchivo(HttpServletRequest request, Model model,
            @PathVariable("id_archivo") Long id_archivo) {
        if (request.getSession().getAttribute("userLog") != null) {
            Usuario us = (Usuario) request.getSession().getAttribute("userLog");
            Usuario userLog = usuarioService.findOne(us.getId_usuario());
            Archivo archivo = archivoService.findOne(id_archivo);

            Caja caja = cajaService.archivoCaja(id_archivo);
            caja.setEstado("X");
            cajaService.save(caja);

            archivoService.delete(id_archivo);
            Control control = new Control();
            control.setTipoControl(tipoControService.findAllByTipoControl("Eliminó"));
            control.setDescripcion(control.getTipoControl().getNombre()
                    + " una unidad documental " + archivo.getNombre());
            control.setUsuario(userLog);
            control.setFecha(new Date());
            control.setHora(new Date());
            controService.save(control);
            return ResponseEntity.ok("elimina");
        } else {
            return ResponseEntity.ok("Se ha cerrado la sesion por inactividad.");
        }
    }

    @GetMapping("/verPdf/{id}")
    public ResponseEntity<ByteArrayResource> verPdf(@PathVariable Long id) {
        Optional<Archivo> archivoOptional = archivoService.findOneOptional(id);

        if (archivoOptional.isPresent()) {
            Archivo archivo = archivoOptional.get();
            // String secretKey = "Lanza12310099812";
            byte[] contenidoDescencryptado;
            try {
                contenidoDescencryptado = utilidadService.decrypt(archivo.getContenido());
                archivo.setContenido(contenidoDescencryptado);
            } catch (Exception e) {
                // TODO: handle exception
            }
            ByteArrayResource resource = new ByteArrayResource(archivo.getContenido());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + archivo.getNombre() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(archivo.getContenido().length)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/verIcoPdf/{id}")
    public ResponseEntity<byte[]> verIcoPdf(@PathVariable Long id) {
        Optional<Archivo> archivoOptional = archivoService.findOneOptional(id);

        if (archivoOptional.isPresent()) {
            Archivo archivo = archivoOptional.get();

            // Configurar los encabezados para evitar el caché
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.IMAGE_PNG)
                    .contentLength(archivo.getIcono().length)
                    .body(archivo.getIcono());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/CantArchivosCarpeta/{id_carpeta}")
    public String CantArchivosCarpeta(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {
        int cantidad = archivoService.archivosCarpeta(id_carpeta).size();
        model.addAttribute("cantidadArchivo", cantidad);
        return "/carpetas/tablaRegistros";
    }

    // ******************** SECCION BUSQUEDAS **********************************
    @GetMapping("/BusquedaArchivo")
    public String BusquedaArchivo(HttpServletRequest request, Model model) {

        model.addAttribute("volumenes", volumenService.findAll());
        model.addAttribute("seriesDocs", documentalService.findAll());
        model.addAttribute("carpetas", carpetaService.findAll());

        Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(usuario.getId_usuario());

        if (!userLog.getPersona().getCargo().getNombre().equals("VOCAL")) {
            return "/busquedas/busquedas2";
        } else {
            model.addAttribute("unidades", unidadService.findAll());
            return "/busquedas/busquedas";
        }
    }

    void siglas(List<Carpeta> listaCarpetas) {
        for (int i = 0; i < listaCarpetas.size(); i++) {

            String[] palabras = listaCarpetas.get(i).getUnidad().getNombre().split(" ");

            StringBuilder primerasLetras = new StringBuilder();

            for (String palabra : palabras) {
                if (!palabra.isEmpty()) {
                    if (palabra.equalsIgnoreCase("y")) {
                        continue;
                    } else if (palabra.equalsIgnoreCase("de")) {
                        continue;
                    }
                    char primeraLetra = palabra.charAt(0);
                    primerasLetras.append(primeraLetra);
                }
            }
            listaCarpetas.get(i).getUnidad().setSigla(primerasLetras.toString());
        }
    }

    @PostMapping(value = "/AbrirCarpetaEncontrada/{id_carpeta}")
    public String AbrirCarpetaEncontrada(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {

        Carpeta carpeta = new Carpeta();
        carpeta = carpetaService.findOne(id_carpeta);
        carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
        model.addAttribute("carpeta", carpeta);

        return "/busquedas/archivoCarpeta";
    }

    int cantidadTotalHojasCarpeta(Carpeta carpeta) {
        int sumarCant = 0;
        for (int j = 0; j < carpeta.getArchivos().size(); j++) {
            sumarCant = sumarCant + carpeta.getArchivos().get(j).getCantidadHojas();
        }
        return sumarCant;
    }

    // =================== SUBIR ARCHIVOS EXCEL

    @GetMapping(value = "/abrirFormularioSubirExcel/{id_carpeta}")
    public String abrirFormularioSubirExcel(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {
        model.addAttribute("carpeta", id_carpeta);
        Carpeta carpeta = carpetaService.findOne(id_carpeta);
        model.addAttribute("seriesDocumental", carpeta.getSerieDocumental().getSubSeries());
        model.addAttribute("cubiertas", cubiertaService.findAll());
        return "/carpetas/formularioArchivoListExcel";
    }

    @PostMapping("/subirExcelListaArchivo")
    public ResponseEntity<Map<String, Object>> postMethodName(HttpServletRequest request,
            @RequestParam("fileExcel") MultipartFile fileExcel,
            @RequestParam("id_carpeta") Long idCarpeta,
            @RequestParam("serieDocumental") Long serieDocumental,
            @RequestParam("cubierta") Long idCubierta) {

        Map<String, Object> response = new HashMap<>();
        List<String> errores = new ArrayList<>();
        List<String> exitosos = new ArrayList<>();
        int totalProcesados = 0;

        System.out.println("CARGANDO...");

        if (request.getSession().getAttribute("userLog") == null) {
            response.put("success", false);
            response.put("mensaje", "La sesión ha sido cerrada por inactividad.");
            return ResponseEntity.ok(response);
        }

        Usuario us = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(us.getId_usuario());

        try {
            // Validar entidades principales
            Carpeta carpeta = carpetaService.findOne(idCarpeta);
            if (carpeta == null) {
                response.put("success", false);
                response.put("mensaje", "Carpeta no encontrada.");
                return ResponseEntity.ok(response);
            }

            SerieDocumental serieDoc = documentalService.findOne(serieDocumental);
            if (serieDoc == null) {
                response.put("success", false);
                response.put("mensaje", "Serie Documental no encontrada.");
                return ResponseEntity.ok(response);
            }

            Cubierta cubierta = cubiertaService.findOne(idCubierta);
            if (cubierta == null) {
                response.put("success", false);
                response.put("mensaje", "Cubierta no encontrada.");
                return ResponseEntity.ok(response);
            }

            if (fileExcel == null || fileExcel.isEmpty()) {
                response.put("success", false);
                response.put("mensaje", "El archivo está vacío o no se ha proporcionado.");
                return ResponseEntity.ok(response);
            }

            try (InputStream inputStream = fileExcel.getInputStream();
                    Workbook workbook = new XSSFWorkbook(inputStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                int filaInicio = 1;
                int totalFilas = sheet.getLastRowNum();

                // Procesar cada fila
                for (int index = filaInicio; index <= totalFilas; index++) {
                    int numeroFila = index + 1; // Para mostrar el número real de fila en Excel

                    try {
                        Row row = sheet.getRow(index);

                        if (row == null) {
                            System.out.println("Fila " + numeroFila + " es null, omitiendo...");
                            continue;
                        }

                        // Obtener celdas
                        Cell cellNro = row.getCell(0); // Columna 1 (índice 0)
                        Cell cellNroCaja = row.getCell(1); // Columna 2 (índice 1)
                        Cell cellTitulo = row.getCell(2); // Columna 3 (índice 2)
                        Cell cellGestion = row.getCell(3); // Columna 4 (índice 3)
                        Cell cellVolumen = row.getCell(4); // Columna 5 (índice 4)
                        Cell cellNroFolio = row.getCell(6); // Columna 6 (índice 5)
                        Cell cellNotas = row.getCell(7); // Columna 6 (índice 5)

                        // Obtener valores como Strings
                        String nro = getCellValueAsString(cellNro);

                        // Verificar si hay datos para procesar
                        if (nro == null || nro.trim().isEmpty()) {
                            System.out.println("No hay más datos para procesar en fila " + numeroFila);
                            break;
                        }

                        String nroCaja = getCellValueAsString(cellNroCaja);
                        String titulo = getCellValueAsString(cellTitulo);
                        String gestion = getCellValueAsString(cellGestion);
                        String volumen = getCellValueAsString(cellVolumen);
                        String nroFolio = getCellValueAsString(cellNroFolio);
                        String notas = getCellValueAsString(cellNotas);

                        // Validaciones de campos obligatorios
                        List<String> erroresFila = new ArrayList<>();

                        if (titulo == null || titulo.trim().isEmpty()) {
                            erroresFila.add("Título vacío");
                        }

                        if (gestion == null || gestion.trim().isEmpty()) {
                            erroresFila.add("Gestión vacía");
                        }

                        if (!erroresFila.isEmpty()) {
                            errores.add("Fila " + numeroFila + ": " + String.join(", ", erroresFila));
                            continue;
                        }

                        // Validar y parsear gestión
                        int gestionInt;
                        try {
                            gestionInt = Integer.parseInt(gestion);
                        } catch (NumberFormatException e) {
                            errores.add(
                                    "Fila " + numeroFila + ": Gestión inválida ('" + gestion + "' no es un número)");
                            continue;
                        }

                        // Evaluar expresión de folios
                        int cantidadHojas;
                        try {
                            cantidadHojas = evaluateExpression(nroFolio);
                        } catch (Exception e) {
                            cantidadHojas = 0;
                            errores.add("Fila " + numeroFila + ": Advertencia - Error al evaluar folios ('" + nroFolio
                                    + "'). Se asignó 0.");
                        }

                        // Crear y guardar Archivo
                        Archivo archivo = new Archivo();
                        archivo.setCarpeta(carpeta);
                        archivo.setNombre(titulo);
                        archivo.setGestion(gestionInt);
                        archivo.setCantidadHojas(cantidadHojas);
                        archivo.setNota(notas);
                        archivo.setCubierta(cubierta);
                        archivo.setSerieDocumental(serieDoc);
                        archivo.setFechaRegistro(new Date());
                        archivo.setHoraRegistro(new Date());
                        archivo.setEstado("A");
                        archivo.setPersona(userLog.getPersona());
                        archivo.setUnidad(userLog.getPersona().getUnidad());

                        archivoService.save(archivo);

                        // Actualizar carpeta
                        carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
                        FormularioTransferencia formularioTransferencia = formularioTransferenciaService
                                .formularioTransferenciaCarpeta(carpeta.getId_carpeta());
                        formularioTransferencia.setCantDocumentos(carpeta.getTotalArchivo());
                        formularioTransferenciaService.save(formularioTransferencia);

                        // Crear y guardar Caja
                        Caja caja = new Caja();
                        caja.setArchivo(archivo);
                        caja.setGestion(archivo.getGestion());
                        caja.setTituloDoc(archivo.getNombre());
                        caja.setEstado("A");
                        caja.setNotas(archivo.getNota());
                        caja.setFojas(archivo.getCantidadHojas());
                        caja.setVolumen(carpeta.getVolumen());
                        caja.setCubierta(archivo.getCubierta());
                        caja.setFormularioTransferencia(formularioTransferencia);
                        cajaService.save(caja);

                        // Crear control
                        Control control = new Control();
                        control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
                        control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
                                + " de una nueva documental " + archivo.getNombre());
                        control.setUsuario(userLog);
                        control.setFecha(new Date());
                        control.setHora(new Date());
                        controService.save(control);

                        exitosos.add("Fila " + numeroFila + ": " + titulo);
                        totalProcesados++;

                    } catch (Exception e) {
                        errores.add("Fila " + numeroFila + ": Error inesperado - " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                // Preparar respuesta
                response.put("success", true);
                response.put("totalProcesados", totalProcesados);
                response.put("totalErrores", errores.size());
                response.put("registrosExitosos", exitosos);
                response.put("registrosFallidos", errores);

                if (errores.isEmpty()) {
                    response.put("mensaje", "Se han registrado " + totalProcesados + " archivos correctamente");
                } else {
                    response.put("mensaje",
                            "Proceso completado: " + totalProcesados + " exitosos, " + errores.size() + " con errores");
                }

                return ResponseEntity.ok(response);

            } catch (IOException e) {
                e.printStackTrace();
                response.put("success", false);
                response.put("mensaje", "Error al procesar el archivo Excel: " + e.getMessage());
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("mensaje", "Error en el proceso: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return null;
        }
    }

    private int evaluateExpression(String expression) throws Exception {
    // Si es null o vacío, retornar 0
    if (expression == null || expression.trim().isEmpty()) {
        return 0;
    }
    
    // Limpiar espacios
    expression = expression.trim().replaceAll("\\s+", "");
    
    // Validar caracteres permitidos
    if (!expression.matches("[0-9+\\-*/().]+")) {
        throw new Exception("Expresión contiene caracteres no válidos. Solo se permiten: 0-9, +, -, *, /, (, )");
    }
    
    // Validar paréntesis balanceados
    if (!areParenthesesBalanced(expression)) {
        throw new Exception("Los paréntesis no están balanceados");
    }
    
    try {
        // Evaluar la expresión
        double result = evaluate(expression);
        
        // Convertir a entero (redondear)
        return (int) Math.round(result);
        
    } catch (NumberFormatException e) {
        throw new Exception("Error al parsear números en la expresión");
    } catch (ArithmeticException e) {
        throw new Exception("Error aritmético: " + e.getMessage());
    } catch (Exception e) {
        throw new Exception("Error al evaluar la expresión: " + e.getMessage());
    }
}

/**
 * Verifica si los paréntesis están balanceados
 */
private boolean areParenthesesBalanced(String expression) {
    int count = 0;
    for (char c : expression.toCharArray()) {
        if (c == '(') count++;
        if (c == ')') count--;
        if (count < 0) return false; // Paréntesis de cierre antes de apertura
    }
    return count == 0;
}

/**
 * Método principal de evaluación que maneja el orden de operaciones
 */
private double evaluate(String expression) throws Exception {
    return parseAddSubtract(expression);
}

/**
 * Parsea suma y resta (menor precedencia)
 */
private double parseAddSubtract(String expression) throws Exception {
    List<Double> terms = new ArrayList<>();
    List<Character> operators = new ArrayList<>();
    
    StringBuilder currentTerm = new StringBuilder();
    
    for (int i = 0; i < expression.length(); i++) {
        char c = expression.charAt(i);
        
        if (c == '+' || (c == '-' && i > 0 && expression.charAt(i-1) != '(' && expression.charAt(i-1) != '+' && expression.charAt(i-1) != '-' && expression.charAt(i-1) != '*' && expression.charAt(i-1) != '/')) {
            // Guardar el término actual
            if (currentTerm.length() > 0) {
                terms.add(parseMultiplyDivide(currentTerm.toString()));
                currentTerm = new StringBuilder();
            }
            operators.add(c);
        } else {
            currentTerm.append(c);
        }
    }
    
    // Agregar el último término
    if (currentTerm.length() > 0) {
        terms.add(parseMultiplyDivide(currentTerm.toString()));
    }
    
    // Calcular el resultado
    if (terms.isEmpty()) {
        throw new Exception("Expresión vacía");
    }
    
    double result = terms.get(0);
    for (int i = 0; i < operators.size(); i++) {
        char op = operators.get(i);
        double nextTerm = terms.get(i + 1);
        
        if (op == '+') {
            result += nextTerm;
        } else if (op == '-') {
            result -= nextTerm;
        }
    }
    
    return result;
}

/**
 * Parsea multiplicación y división (mayor precedencia)
 */
private double parseMultiplyDivide(String expression) throws Exception {
    List<Double> factors = new ArrayList<>();
    List<Character> operators = new ArrayList<>();
    
    StringBuilder currentFactor = new StringBuilder();
    
    for (int i = 0; i < expression.length(); i++) {
        char c = expression.charAt(i);
        
        if (c == '*' || c == '/') {
            // Guardar el factor actual
            if (currentFactor.length() > 0) {
                factors.add(parseParenthesesAndNumbers(currentFactor.toString()));
                currentFactor = new StringBuilder();
            }
            operators.add(c);
        } else {
            currentFactor.append(c);
        }
    }
    
    // Agregar el último factor
    if (currentFactor.length() > 0) {
        factors.add(parseParenthesesAndNumbers(currentFactor.toString()));
    }
    
    // Calcular el resultado
    if (factors.isEmpty()) {
        throw new Exception("Expresión vacía en multiplicación/división");
    }
    
    double result = factors.get(0);
    for (int i = 0; i < operators.size(); i++) {
        char op = operators.get(i);
        double nextFactor = factors.get(i + 1);
        
        if (op == '*') {
            result *= nextFactor;
        } else if (op == '/') {
            if (nextFactor == 0) {
                throw new ArithmeticException("División por cero");
            }
            result /= nextFactor;
        }
    }
    
    return result;
}

/**
 * Parsea paréntesis y números
 */
private double parseParenthesesAndNumbers(String expression) throws Exception {
    expression = expression.trim();
    
    // Si está entre paréntesis, evaluar el contenido
    if (expression.startsWith("(") && expression.endsWith(")")) {
        return evaluate(expression.substring(1, expression.length() - 1));
    }
    
    // Manejar números negativos al inicio
    if (expression.startsWith("-")) {
        return -parseParenthesesAndNumbers(expression.substring(1));
    }
    
    // Si es un número simple
    try {
        return Double.parseDouble(expression);
    } catch (NumberFormatException e) {
        // Si no es un número simple, evaluar como expresión compleja
        return evaluate(expression);
    }
}

}
