package com.example.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Archivo;
import com.example.demo.entity.TipoArchivo;
import com.example.demo.entity.Usuario;
import com.example.demo.service.TipoArchivoService;
import com.example.demo.service.UsuarioService;

@Controller
public class TipoArchivoController {
    @Autowired
    private TipoArchivoService tipoArchivoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/TIPOARCHIVO")
    public String ventanaDocumentos(HttpServletRequest request, Model model) {
//        model.addAttribute("archivo", new Archivo());
        //model.addAttribute("listaArchivos", archivosData.findAll());
        if (request.getSession().getAttribute("userLog") != null) {
            return "/tipoArchivos/registrar";
        } else {
            return "expiracion";
        }
        
    }

    @PostMapping(value="/NuevoRegistroTipoArchivo")
    public String NuevoRegistroArchivo(HttpServletRequest request, Model model) {
        model.addAttribute("tipoArchivo", new TipoArchivo());        
        return "/tipoArchivos/formulario";
    }

    @PostMapping(value="/RegistrosTipoArchivos")
    public String tablaRegistros(HttpServletRequest request, Model model) {
        model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
              Usuario user = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(user.getId_usuario());
        model.addAttribute("permisos", userLog.getPermisos());
        return "/tipoArchivos/tablaRegistros";
    }

    @PostMapping("/GuardarRegistroTipoArchivo")
    @ResponseBody
    public void GuardarRegistroArchivo(HttpServletRequest request, Model model,
           TipoArchivo tipoArchivo) {
                System.out.println("METODO REGISTRAR TIPO ARCHIVO");       
            tipoArchivo.setEstado("A");
            tipoArchivoService.save(tipoArchivo);
 
    }

    @GetMapping(value="/ModTipoArchivo/{id_tipoArchivo}")
    public String EditarArchivo(HttpServletRequest request, Model model,
    @PathVariable("id_tipoArchivo") Long id_tipoArchivo){
        System.out.println("EDITAR ARCHIVOS");

        model.addAttribute("tipoArchivo", tipoArchivoService.findOne(id_tipoArchivo));
        model.addAttribute("edit", "true");

        return "/tipoArchivos/formulario";
    }

     @PostMapping(value = "/ModTipoArchivoG")
      @ResponseBody
    public void ModArchivoG(@Validated Archivo archivo, Model model) {
        TipoArchivo tipoArchivo = archivo.getTipoArchivo();
        tipoArchivo.setEstado("A");
        tipoArchivoService.save(tipoArchivo);
    }

  @PostMapping(value="/EliminarRegistroTipoArchivo/{id_tipoArchivo}")
  @ResponseBody
    public void EliminarRegistroArchivo(HttpServletRequest request, Model model,
    @PathVariable("id_tipoArchivo") Long id_tipoArchivo){
        System.out.println("Eliminar ARCHIVOS");
        TipoArchivo tipoArchivo = tipoArchivoService.findOne(id_tipoArchivo);
        tipoArchivo.setEstado("X");
        tipoArchivoService.save(tipoArchivo);
    }

}
