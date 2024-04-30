package com.example.demo.controller;

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

import com.example.demo.entity.SerieDocumental;
import com.example.demo.entity.Unidad;
import com.example.demo.service.SerieDocumentalService;

@Controller
public class SerieDocumentarController {

    @Autowired
    private SerieDocumentalService documentalService;

    @GetMapping("/SERIEDOC")
    public String ventanaDocumentos(HttpServletRequest request, Model model) {
        // model.addAttribute("archivo", new Archivo());
        // model.addAttribute("listaArchivos", archivosData.findAll());
        return "/seriesDocs/registrar";
    }

    @PostMapping(value = "/NuevoRegistroSerieDoc")
    public String NuevoRegistroSerieDoc(HttpServletRequest request, Model model) {
        model.addAttribute("serieDoc", new SerieDocumental());
        return "/seriesDocs/formulario";
    }

    @PostMapping(value = "/RegistrosTipoSeriesDoc")
    public String tablaRegistros(HttpServletRequest request, Model model) {
        model.addAttribute("seriesDocs", documentalService.findAll());
        return "/seriesDocs/tablaRegistros";
    }

    @PostMapping(value = "/NuevoRegistroSubSerieDoc/{id_serie}")
    public String NuevoRegistroSubSerieDoc(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {

        model.addAttribute("serieDoc", new SerieDocumental());
        model.addAttribute("id_seriePadre", id_serie);
        return "/seriesDocs/formularioSubSerie";
    }

    @PostMapping(value = "/RegistrosSubSeriesDoc/{id_serie}")
    public String RegistrosSubSeriesDoc(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {
        List<SerieDocumental> Subseries = documentalService.findOne(id_serie).getSubSeries();
        model.addAttribute("Subseries", Subseries);
        return "/seriesDocs/tablaRegistrosSubSerie";
    }

    @GetMapping(value = "/VentanaSubSerieDoc/{id_serie}")
    public String VentanaSubSerieDoc(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {
        System.out.println("EDITAR SERIE DOCUMENTAL");
        model.addAttribute("serieDocPadre", documentalService.findOne(id_serie));
        return "/seriesDocs/ventanaSubSeries";
    }

    @PostMapping("/GuardarRegistroSeriesDoc")
    @ResponseBody
    public ResponseEntity<String> GuardarRegistroSeriesDoc(HttpServletRequest request, Model model,
            SerieDocumental serieDocumental,
            @RequestParam(value = "id_seriePadre", required = false) Long id_seriePadre) {
        System.out.println("METODO REGISTRAR SERIE DOCUMENTAL");

        if (documentalService.serieDocumentalNombre(serieDocumental.getNombre()) == null) {
            if (id_seriePadre != null) {
                SerieDocumental seriePadre = documentalService.findOne(id_seriePadre);
                serieDocumental.setSeriePadre(seriePadre);
            }
            serieDocumental.setEstado("A");
            documentalService.save(serieDocumental);

            return ResponseEntity.ok("Se realizó el registro correctamente");
        } else {
            return ResponseEntity.ok("Ya existe un registro con este nombre");
        }
    }

    @GetMapping(value = "/ModSerieDoc/{id_serie}")
    public String EditarArchivo(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {
        System.out.println("EDITAR SERIE DOCUMENTAL");

        model.addAttribute("serieDoc", documentalService.findOne(id_serie));
        model.addAttribute("edit", "true");

        return "/seriesDocs/formulario";
    }

    @PostMapping(value = "/ModSubSerieDoc/{id_serie}")
    public String ModSubSerieDoc(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {
        System.out.println("EDITAR SERIE DOCUMENTAL");
        SerieDocumental serieDocumental = documentalService.findOne(id_serie);
        model.addAttribute("serieDoc", serieDocumental);
        model.addAttribute("id_seriePadre", serieDocumental.getSeriePadre().getId_serie());
        model.addAttribute("edit", "true");

        return "/seriesDocs/formularioSubSerie";
    }

    @PostMapping(value = "/ModSerieDocG")
    @ResponseBody
    public ResponseEntity<String> ModSerieDocG(@Validated SerieDocumental serieDocumental, Model model,
            @RequestParam(value = "id_seriePadre", required = false) Long id_seriePadre) {
        SerieDocumental serieDocumental2 = documentalService.findOne(serieDocumental.getId_serie());
        if (documentalService.serieDocumentalModNombre(serieDocumental2.getNombre(),
                serieDocumental.getNombre()) == null) {
            if (id_seriePadre != null) {
                SerieDocumental seriePadre = documentalService.findOne(id_seriePadre);
                serieDocumental.setSeriePadre(seriePadre);
            }
            documentalService.save(serieDocumental);
            return ResponseEntity.ok("Se guardaron los cambios correctamente");
        } else {
            return ResponseEntity.ok("Ya existe un registro con este nombre");
        }
    }

    @PostMapping(value = "/EliminarRegistroSerieDoc/{id_serie}")
    @ResponseBody
    public void EliminarRegistroSerieDoc(HttpServletRequest request, Model model,
            @PathVariable("id_serie") Long id_serie) {
        System.out.println("Eliminar SERIE DOCUMENTAL");
        /*
         * SerieDocumental serieDocumental = documentalService.findOne(id_serie);
         * serieDocumental.setEstado("X");
         * documentalService.save(serieDocumental);
         */
        documentalService.delete(id_serie);
    }

}
