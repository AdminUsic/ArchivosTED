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

import com.example.demo.entity.Cargo;
import com.example.demo.service.CargoService;

@Controller
public class CargoController {
    @Autowired
    private CargoService cargoService;

     @GetMapping("/GARGOS")
    public String ventanaDocumentos(HttpServletRequest request, Model model) {

        return "/cargos/registrar";
    }

    @PostMapping(value = "/NuevoCargo")
    public String NuevoRegistroSeccion(HttpServletRequest request, Model model) {
        model.addAttribute("cargo", new Cargo());
        return "/cargos/formulario";
    }

    @PostMapping(value = "/RegistrosCargos")
    public String tablaRegistros(HttpServletRequest request, Model model) {
        model.addAttribute("cargos", cargoService.findAll());
        return "/cargos/tablaRegistros";
    }

    @PostMapping("/GuardarRegistroCargo")
    @ResponseBody
    public void GuardarRegistroSeccion(HttpServletRequest request, Model model,
            Cargo cargo) {
        System.out.println("METODO REGISTRAR CARGO"); 
            cargo.setEstado("A");
            cargoService.save(cargo);
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
    public void ModSeccionG(@Validated Cargo cargo, Model model) {
        cargo.setEstado("A");
        cargoService.save(cargo);
    }

    @PostMapping(value = "/EliminarRegistroCargo/{id_cargo}")
    @ResponseBody
    public void EliminarRegistroSeccion(HttpServletRequest request, Model model,
            @PathVariable("id_cargo") Long id_cargo) {
        System.out.println("Eliminar CARGOS");
        /*Cargo cargo = cargoService.findOne(id_cargo);
        cargo.setEstado("X");
        cargoService.save(cargo);*/
        cargoService.delete(id_cargo);
    }

}
