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

import com.example.demo.entity.Usuario;
import com.example.demo.entity.Volumen;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.VolumenService;

@Controller
public class VolumenController {

    @Autowired
    private VolumenService volumenService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/VOLUMEN")
    public String ventanaDocumentos(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("userLog") != null) {
            return "/volumenes/registrar";
        } else {
            return "expiracion";
        }
    }

    @PostMapping(value = "/NuevoRegistroVolumen")
    public String NuevoRegistroVolumen(HttpServletRequest request, Model model) {
        model.addAttribute("volumen", new Volumen());
        return "/volumenes/formulario";
    }

    @PostMapping(value = "/RegistrosVolumenes")
    public String RegistrosVolumenes(HttpServletRequest request, Model model) {
        model.addAttribute("volumenes", volumenService.listaDeVolumenes());
              Usuario user = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(user.getId_usuario());
        model.addAttribute("permisos", userLog.getPermisos());
        return "/volumenes/tablaRegistros";
    }

    @PostMapping("/GuardarRegistroVolumen")
    @ResponseBody
    public void GuardarRegistroVolumen(HttpServletRequest request, Model model,
            Volumen volumen) {
        System.out.println("METODO REGISTRAR TIPO ARCHIVO");
        volumen.setEstado("A");
        volumenService.save(volumen);

    }

    @GetMapping(value = "/ModVolumen/{id_volumen}")
    public String EditarVolumen(HttpServletRequest request, Model model,
            @PathVariable("id_volumen") Long id_volumen) {
        System.out.println("EDITAR VOLUMEN");

        model.addAttribute("volumen", volumenService.findOne(id_volumen));
        model.addAttribute("edit", "true");

        return "/volumenes/formulario";
    }

    @PostMapping(value = "/ModVolumenG")
    @ResponseBody
    public void ModVolumenG(@Validated Volumen volumen, Model model) {
        volumen.setEstado("A");
        volumenService.save(volumen);
    }

    @PostMapping(value = "/EliminarRegistroVolumen/{id_volumen}")
    @ResponseBody
    public void EliminarRegistroVolumen(HttpServletRequest request, Model model,
            @PathVariable("id_volumen") Long id_volumen) {
        System.out.println("Eliminar VOLUMEN");
        volumenService.delete(id_volumen);
    }

}
