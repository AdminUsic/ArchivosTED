package com.example.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Rol;
import com.example.demo.service.RolService;

@Controller
public class RolController {
    @Autowired
    private RolService rolService;

    @GetMapping("/ROL")
    public String ventanaDocumentos(HttpServletRequest request, Model model) {
        
        return "/roles/registrar";
    }

    @PostMapping(value = "/NuevoRegistroRol")
    public String NuevoRegistroRol(HttpServletRequest request, Model model) {
        model.addAttribute("rol", new Rol());
         System.out.println("NUEVO ROL");
        return "/roles/formulario";
    }

    @PostMapping(value = "/RegistrosRoles")
    public String RegistrosRoles(HttpServletRequest request, Model model) {
        model.addAttribute("roles", rolService.findAll());
        return "/roles/tablaRegistros";
    }

    /*@PostMapping("/GuardarRegistroRol")
    @ResponseBody
    public void GuardarRegistroRol(HttpServletRequest request, Model model,
            Rol rol) {
        System.out.println("METODO REGISTRAR ROL");
        rol.setEstado("A");
        rolService.save(rol);
    }*/

    @PostMapping("/GuardarRegistroRol")
    public ResponseEntity<Rol> GuardarRegistroRol(Model model,
            @Validated Rol rol) {
        System.out.println("METODO REGISTRAR ROL");
        rol.setEstado("A");
        rolService.save(rol);
        Rol rol2 = rolService.UltimoRegistro();
        System.out.println("en nombre del rol es: "+rol2.getNombre());
        return ResponseEntity.ok(rol2);
    }

    @GetMapping(value = "/ModRol/{id_rol}")
    public String EditarRol(HttpServletRequest request, Model model,
            @PathVariable("id_rol") Long id_rol) {
        System.out.println("EDITAR ROL");
        model.addAttribute("rol", rolService.findOne(id_rol));
        model.addAttribute("edit", "true");
        return "/roles/formulario";
    }
/* 
    @PostMapping(value = "/ModRolG")
    @ResponseBody
    public void ModRolG(@Validated Rol rol, Model model) {
        rol.setEstado("A");
        rolService.save(rol);
    }*/
    @PostMapping(value = "/ModRolG")
    public ResponseEntity<Rol> ModRolG(@Validated Rol rol, Model model) {
        rol.setEstado("A");
        rolService.save(rol);
        return ResponseEntity.ok(rol);
    }

    @PostMapping(value = "/EliminarRegistroRol/{id_rol}")
    @ResponseBody
    public void EliminarRegistroRol(HttpServletRequest request, Model model,
            @PathVariable("id_rol") Long id_rol) {
        System.out.println("Eliminar ROL");
        rolService.delete(id_rol);
    }
}
