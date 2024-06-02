package com.example.demo.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.pdfbox.pdmodel.PDDocument;
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

import com.example.demo.entity.Archivo;
import com.example.demo.entity.Carpeta;
import com.example.demo.entity.Control;
import com.example.demo.entity.Usuario;
import com.example.demo.service.ArchivoService;
import com.example.demo.service.CarpetaService;
import com.example.demo.service.ControlService;
import com.example.demo.service.PersonaService;
import com.example.demo.service.SerieDocumentalService;
import com.example.demo.service.TipoArchivoService;
import com.example.demo.service.TipoControService;
import com.example.demo.service.UnidadService;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.UtilidadServiceProject;
import com.example.demo.service.VolumenService;

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
            @RequestParam("PDF") MultipartFile archivoPdf, @Validated Archivo archivo,
            @RequestParam(value = "id_carpeta", required = false) Long id_carpeta) {
        if (request.getSession().getAttribute("userLog") != null) {
            Usuario us = (Usuario) request.getSession().getAttribute("userLog");
            Usuario userLog = usuarioService.findOne(us.getId_usuario());
            try {
                byte[] contenido = archivoPdf.getBytes();
                System.out.println("Tamaño del archivo: " + contenido.length);

                try {
                    byte[] encryptedBytes = utilidadService.encrypt(contenido);
                    archivo.setContenido(encryptedBytes);
                } catch (Exception e) {
                    System.out.println("Error en la encriptacion: " + e);
                }
                if (id_carpeta != null) {
                    Carpeta carpeta = carpetaService.findOne(id_carpeta);
                    archivo.setCarpeta(carpeta);
                }

                PDDocument document = PDDocument.load(contenido);
                archivo.setCantidadHojas(document.getNumberOfPages());
                archivo.setFechaRegistro(new Date());
                archivo.setHoraRegistro(new Date());
                archivo.setEstado("A");
                String nombFile = archivoPdf.getOriginalFilename();
                archivo.setExtension(getFileExtension(nombFile));

                try {
                    archivo.setIcono(utilidadService.extraerIconPdf(archivoPdf));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                document.close();
                archivoService.save(archivo);
                Control control = new Control();
                control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
                control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
                        + " de una nueva documental " + archivo.getNombre());
                control.setUsuario(userLog);
                control.setFecha(new Date());
                control.setHora(new Date());
                controService.save(control);
                return ResponseEntity.ok("Se ha Registrado el Archivo correctamente");
            } catch (IOException e) {
                System.out.println("ERROR REGISTRAR ARCHIVO: " + e.getMessage());
                return ResponseEntity.ok("ERROR AL REGISTRAR ARCHIVO");
            }
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
            byte[] contenido = archivoPdf.getBytes();

            if (archivoPdf.isEmpty()) {
                archivo.setContenido(archivoAntes.getContenido());
                archivo.setCantidadHojas(archivoAntes.getCantidadHojas());
                archivo.setIcono(archivoAntes.getIcono());
            } else if (!archivoPdf.isEmpty()) {
                try {
                    // archivo.setContenido(encrypt(contenido, "Lanza12310099812"));
                    archivo.setContenido(utilidadService.encrypt(contenido));
                    archivo.setIcono(utilidadService.extraerIconPdf(archivoPdf));
                } catch (Exception e) {
                    System.out.println("ERROR EN LA ENCRIPTACION DEL ARCHIVO MODIFICADO: " + e);
                }
                PDDocument document = PDDocument.load(contenido);

                archivo.setCantidadHojas(document.getNumberOfPages());
                document.close();
            }
            // System.out.println("Fecha de emision: " + fechaEmision);
            if (id_carpeta != null) {
                Carpeta carpeta = carpetaService.findOne(id_carpeta);
                archivo.setCarpeta(carpeta);
            }

            archivo.setFechaRegistro(archivoAntes.getFechaRegistro());
            archivo.setHoraRegistro(archivo.getHoraRegistro());
            archivo.setEstado("A");

            archivoService.save(archivo);
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
        model.addAttribute("unidades", unidadService.findAll());
        model.addAttribute("volumenes", volumenService.findAll());
        model.addAttribute("seriesDocs", documentalService.findAll());
        model.addAttribute("carpetas", carpetaService.findAll());
        System.out.println("VENTANA DE BUSQUEDAS");
        return "/busquedas/busquedas";
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

}
