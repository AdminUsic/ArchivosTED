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

import com.example.demo.entity.Cargo;
import com.example.demo.entity.Usuario;
import com.example.demo.service.CargoService;
import com.example.demo.service.UsuarioService;

@Controller
public class CargoController {
    @Autowired
    private CargoService cargoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/GARGOS")
    public String ventanaDocumentos(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("userLog") != null) {
            return "/cargos/registrar";
        } else {
            return "expiracion";
        }
    }

    @PostMapping(value = "/NuevoCargo")
    public String NuevoRegistroSeccion(HttpServletRequest request, Model model) {
        model.addAttribute("cargo", new Cargo());
        return "/cargos/formulario";
    }

    @PostMapping(value = "/RegistrosCargos")
    public String tablaRegistros(HttpServletRequest request, Model model) {
        model.addAttribute("cargos", cargoService.findAll());
        Usuario user = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(user.getId_usuario());
        model.addAttribute("permisos", userLog.getPermisos());
        return "/cargos/tablaRegistros";
    }

    @PostMapping("/GuardarRegistroCargo")
    @ResponseBody
    public ResponseEntity<String> GuardarRegistroSeccion(HttpServletRequest request, Model model,
            Cargo cargo) {
        // System.out.println("METODO REGISTRAR CARGO");
        if (cargoService.cargoByNombre(cargo.getNombre()) == null) {
            cargo.setEstado("A");
            cargoService.save(cargo);
            return ResponseEntity.ok("Se guardaron los cambios correctamente");
        } else {
            return ResponseEntity.ok("Ya existe un registro con este nombre");
        }
    }

    @GetMapping(value = "/ModCargo/{id_cargo}")
    public String EditarSeccion(HttpServletRequest request, Model model,
            @PathVariable("id_cargo") Long id_cargo) {
        System.out.println("EDITAR SECCION");
        model.addAttribute("cargo", cargoService.findOne(id_cargo));
        model.addAttribute("edit", "true");
        return "/cargos/formulario";
    }

    @PostMapping(value = "/ModCargoG")
    @ResponseBody
    public ResponseEntity<String> ModSeccionG(@Validated Cargo cargo, Model model) {
        Cargo cargo2 = cargoService.findOne(cargo.getId_cargo());

        if (cargoService.cargoByNombreMod(cargo2.getNombre(), cargo.getNombre()) == null) {
            cargo.setEstado("A");
            cargoService.save(cargo);
            return ResponseEntity.ok("Se realizó el registro correctamente");
        } else {
            return ResponseEntity.ok("Ya existe un registro con este nombre");
        }
    }

    @PostMapping(value = "/EliminarRegistroCargo/{id_cargo}")
    @ResponseBody
    public void EliminarRegistroSeccion(HttpServletRequest request, Model model,
            @PathVariable("id_cargo") Long id_cargo) {
        System.out.println("Eliminar CARGOS");
        /*
         * Cargo cargo = cargoService.findOne(id_cargo);
         * cargo.setEstado("X");
         * cargoService.save(cargo);
         */
        cargoService.delete(id_cargo);
    }

}
