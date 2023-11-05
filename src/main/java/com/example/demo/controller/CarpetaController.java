package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.demo.entity.Carpeta;
import com.example.demo.entity.Unidad;
import com.example.demo.service.ArchivoService;
import com.example.demo.service.CarpetaService;
import com.example.demo.service.PersonaService;
import com.example.demo.service.SerieDocumentalService;
import com.example.demo.service.UnidadService;
import com.example.demo.service.VolumenService;
import com.example.demo.service.TipoArchivoService;

@Controller
public class CarpetaController {

    @Autowired
    private CarpetaService carpetaService;

    @Autowired
    private ArchivoService archivoService;

    @Autowired
    private UnidadService unidadService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private TipoArchivoService tipoArchivoService;

    @Autowired
    private SerieDocumentalService documentalService;

    @Autowired
    private VolumenService volumenService;

    @GetMapping(value = "/CARPETAS")
    public String ventanaCarpeta(HttpServletRequest request, Model model) {
        System.out.println("PATALLA CARPETA");

        return "/carpetas/registrar";
    }

    @PostMapping(value = "/VistaIcoCar")
    public String VistaIcoCar(HttpServletRequest request, Model model) {
        System.out.println("PATALLA CARPETA Vista Ico");

        return "/carpetas/vistaICO";
    }

    @PostMapping(value = "/VistaListCar")
    public String VistaLisCar(HttpServletRequest request, Model model) {
        System.out.println("PATALLA CARPETA Vista List");

        return "/carpetas/vistaLIST";
    }

    @PostMapping(value = "/RegistrosCarpetaIco")
    public String RegistrosCarpetaIco(HttpServletRequest request, Model model) {
        System.out.println("PATALLA CARPETA Vista List");
        List<Carpeta> listaCarpetas = new ArrayList<>();
        for (int i = 0; i < carpetaService.findAll().size(); i++) {
            Carpeta carpeta = new Carpeta();
            carpeta = carpetaService.findAll().get(i);
            carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
            listaCarpetas.add(carpeta);
        }
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
        model.addAttribute("ListCarpetas", listaCarpetas);
        return "/carpetas/iconoRegistros";
    }

    @PostMapping(value = "/NuevaCarpeta")
    public String NuevaCarpeta(HttpServletRequest request, Model model) {
        model.addAttribute("carpeta", new Carpeta());
        model.addAttribute("archivos", archivoService.findAll());
        model.addAttribute("unidades", unidadService.findAll());
        model.addAttribute("volumenes", volumenService.findAll());
        model.addAttribute("seriesDocs", documentalService.findAll());
        return "/carpetas/formulario";
    }

    @PostMapping(value = "/NuevaCarpetaModal")
    public String NuevaCarpetaModal(HttpServletRequest request, Model model) {
        model.addAttribute("carpeta", new Carpeta());
        model.addAttribute("archivos", archivoService.findAll());
        model.addAttribute("unidades", unidadService.findAll());
        model.addAttribute("volumenes", volumenService.findAll());
        model.addAttribute("seriesDocs", documentalService.findAll());
        return "/carpetas/formularioModal";
    }

    int cantidadTotalHojasCarpeta(Carpeta carpeta) {
        int sumarCant = 0;
        for (int j = 0; j < carpeta.getArchivos().size(); j++) {
            sumarCant = sumarCant + carpeta.getArchivos().get(j).getCantidadHojas();
        }
        return sumarCant;
    }

    @PostMapping(value = "/RegistrosCarpeta")
    public String tablaRegistros(HttpServletRequest request, Model model) {

        List<Carpeta> listaCarpetas = new ArrayList<>();
        for (int i = 0; i < carpetaService.findAll().size(); i++) {
            Carpeta carpeta = new Carpeta();
            carpeta = carpetaService.findAll().get(i);
            carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
            listaCarpetas.add(carpeta);
        }
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
        model.addAttribute("ListCarpetas", listaCarpetas);

        return "/carpetas/tablaRegistros";
    }

    @PostMapping(value = "/RegistrarCarpeta")
    public ResponseEntity<String> RegistrarCarpeta(HttpServletRequest request, Model model, @Validated Carpeta carpeta,
            @RequestParam(value = "gestion") String gestion) {
        System.out.println("Registrar Carpeta");
        carpeta.setEstado("A");
        carpeta.setHoraRegistro(new Date());
        carpeta.setFechaRegistro(new Date());
        carpeta.setGestion(Integer.parseInt(gestion));
        carpetaService.save(carpeta);
        return ResponseEntity.ok("Se guardó el nuevo registro Correctamente");
    }

    @GetMapping(value = "/ModCarpeta/{id_carpeta}")
    public String EditarCarpeta(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {
        System.out.println("EDITAR CARPETA");
        model.addAttribute("carpeta", carpetaService.findOne(id_carpeta));
        model.addAttribute("archivos", archivoService.findAll());
        model.addAttribute("unidades", unidadService.findAll());
        model.addAttribute("volumenes", volumenService.findAll());
        model.addAttribute("seriesDocs", documentalService.findAll());
        model.addAttribute("edit", "true");
        return "/carpetas/formulario";
    }

    @GetMapping(value = "/ModCarpetaModal/{id_carpeta}")
    public String EditarCarpetaModal(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {
        System.out.println("EDITAR CARPETA");
        model.addAttribute("carpeta", carpetaService.findOne(id_carpeta));
        model.addAttribute("archivos", archivoService.findAll());
        model.addAttribute("unidades", unidadService.findAll());
        model.addAttribute("volumenes", volumenService.findAll());
        model.addAttribute("seriesDocs", documentalService.findAll());
        model.addAttribute("edit", "true");
        return "/carpetas/formularioModal";
    }

    @PostMapping(value = "/ModCarpetaG")
    public ResponseEntity<String> ModCarpeta(@Validated Carpeta carpeta, Model model,
            @RequestParam(value = "gestion") String gestion) {
        carpeta.setGestion(Integer.parseInt(gestion));
        carpeta.setEstado("A");
        carpetaService.save(carpeta);
        return ResponseEntity.ok("Se ha Modificado el registro Correctamente");
    }

    @PostMapping(value = "/EliminarCarpeta/{id_carpeta}")
    @ResponseBody
    public void EliminarCarpeta(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {
        System.out.println("Eliminar CARPETA");

        Carpeta carpeta = carpetaService.findOne(id_carpeta);

        for (int i = 0; i < carpeta.getArchivos().size(); i++) {
            archivoService.delete(carpeta.getArchivos().get(i).getId_archivo());
        }
        carpetaService.delete(id_carpeta);
    }

    @PostMapping(value = "/ContenidoCarpeta/{id_carpeta}")
    public String ContenidoCarpeta(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {

        Carpeta carpeta = new Carpeta();
        carpeta = carpetaService.findOne(id_carpeta);
        carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
        model.addAttribute("carpeta", carpeta);

        return "/carpetas/archivoCarpeta";
    }
    
    @PostMapping(value = "/ContenidoCarpetaList/{id_carpeta}")
    public String ContenidoCarpetaList(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {

        Carpeta carpeta = new Carpeta();
        carpeta = carpetaService.findOne(id_carpeta);
        carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
        model.addAttribute("carpeta", carpeta);

        return "/carpetas/archivoCarpetaList";
    }

    @PostMapping(value = "/RegistrosArchivosCList/{id_carpeta}")
    public String RegistrosArchivosCList(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {

        Carpeta carpeta = new Carpeta();
        carpeta = carpetaService.findOne(id_carpeta);
        model.addAttribute("listaArchivos", carpeta.getArchivos());

        return "/carpetas/tablaRegistroArchivoList";
    }

    @PostMapping(value = "/AgregarArchivo/{id_carpeta}")
    public String AgregarArchivo(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {
        model.addAttribute("carp", id_carpeta);
        model.addAttribute("archivo", new Archivo());
        model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
        model.addAttribute("personas", personaService.findAll());
        model.addAttribute("seriesDocumental", carpetaService.findOne(id_carpeta).getSerieDocumental().getSubSeries());
        model.addAttribute("seccionesDocumental", carpetaService.findOne(id_carpeta).getUnidad().getSubUnidades());
        return "/carpetas/formularioArchivoC";
        
    }

    @PostMapping(value = "/AgregarArchivoCList/{id_carpeta}")
    public String AgregarArchivoCList(HttpServletRequest request, Model model,
            @PathVariable("id_carpeta") Long id_carpeta) {
        model.addAttribute("carp", id_carpeta);
        model.addAttribute("archivo", new Archivo());
        model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
        model.addAttribute("personas", personaService.findAll());
        model.addAttribute("seccionesDocumental", carpetaService.findOne(id_carpeta).getUnidad().getSubUnidades());
        return "/carpetas/formularioArchivoCList";
        
    }

    @PostMapping(value = "/ModificarArchivoC/{id_archivo}")
    public String ModificarArchivoC(HttpServletRequest request, Model model,
            @PathVariable("id_archivo") Long id_archivo) {
        Archivo archivo = archivoService.findOne(id_archivo);
        model.addAttribute("carp", archivo.getCarpeta().getId_carpeta());
        model.addAttribute("archivo", archivo);
        model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
        model.addAttribute("personas", personaService.findAll());
        model.addAttribute("seccionesDocumental",
                carpetaService.findOne(archivo.getCarpeta().getId_carpeta()).getUnidad().getSubUnidades());
        model.addAttribute("edit", "true");
        return "/carpetas/formularioArchivoC";
    }

    @PostMapping(value = "/ModificarArchivoCList/{id_archivo}")
    public String ModificarArchivoCList(HttpServletRequest request, Model model,
            @PathVariable("id_archivo") Long id_archivo) {
        Archivo archivo = archivoService.findOne(id_archivo);
        model.addAttribute("carp", archivo.getCarpeta().getId_carpeta());
        model.addAttribute("archivo", archivo);
        model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
        model.addAttribute("personas", personaService.findAll());
        model.addAttribute("seccionesDocumental",
                carpetaService.findOne(archivo.getCarpeta().getId_carpeta()).getUnidad().getSubUnidades());
        model.addAttribute("edit", "true");
        return "/carpetas/formularioArchivoCList";
    }

}
