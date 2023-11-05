package com.example.demo.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.DaoPersonaData;
import com.example.demo.entity.Persona;
import com.example.demo.entity.Usuario;
import com.example.demo.service.CargoService;
import com.example.demo.service.PersonaService;
import com.example.demo.service.RolService;
import com.example.demo.service.UnidadService;
import com.example.demo.service.UsuarioService;

@Controller
public class PersonaController {

    @Autowired
    private PersonaService personaService;
    @Autowired
    private CargoService cargoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @Autowired
    private UnidadService unidadService;

    @Autowired
    private DaoPersonaData daoPersonaData;

    @GetMapping(value = "/PERSONA")
    public String ventanaPersona(HttpServletRequest request, Model model) {
        System.out.println("PATALLA PERSONA");
        return "/personas/registrar";
    }

    @PostMapping(value = "/NuevaPersona")
    public String NuevaPersona(HttpServletRequest request, Model model) {
        model.addAttribute("persona", new Persona());
        model.addAttribute("cargos", cargoService.findAll());
        model.addAttribute("roles", rolService.findAll());
        model.addAttribute("unidades", unidadService.findAll());
        return "/personas/formulario";
    }

    @PostMapping(value = "/RegistrosPersona")
    public String tablaRegistros(HttpServletRequest request, Model model) {
        model.addAttribute("personas", personaService.findAll());
        return "/personas/tablaRegistros";
    }

    @PostMapping("/RegistrarPersona")
    public ResponseEntity<String> RegistrarPersona(@Validated Persona persona) {

        if (personaService.personaCi(persona.getCi()) == null) {
            persona.setHoraRegistro(new Date());
            persona.setFechaRegistro(new Date());
            persona.setEstado("A");
            personaService.save(persona);
            return ResponseEntity.ok("Se realizó el registro correctamente");
        } else {
            if (personaService.personaCi(persona.getCi()) != null) {
                Persona persona2 = personaService.personaCi(persona.getCi());
                if (persona2.getCargo() == null && persona2.getUnidad() == null && persona2.getUnidad() == null) {
                    persona2.setCargo(persona.getCargo());
                    persona2.setRol(persona.getRol());
                    persona2.setUnidad(persona.getUnidad());
                    personaService.save(persona2);

                    System.out.println("Se encontro una persona previamente registrada");
                    return ResponseEntity.ok("Se realizó el registro correctamente");
                } else {
                    return ResponseEntity.ok("Ya existe un registro con este C.I.");
                }

            } else {
                return ResponseEntity.ok("Ya existe un registro con este C.I.");
            }
        }
    }

    @GetMapping(value = "/ModPersona/{id_persona}")
    public String EditarPersona(HttpServletRequest request, Model model,
            @PathVariable("id_persona") Long id_persona) {
        System.out.println("EDITAR PERSONAS");
        model.addAttribute("persona", personaService.findOne(id_persona));
        model.addAttribute("cargos", cargoService.findAll());
        model.addAttribute("roles", rolService.findAll());
        model.addAttribute("unidades", unidadService.findAll());
        model.addAttribute("edit", "true");
        return "/personas/formulario";
    }

    @PostMapping(value = "/ModPersonaG")
    @ResponseBody
    public ResponseEntity<String> ModUsuarioG(@Validated Persona persona, Model model) {
        Persona persona2 = personaService.findOne(persona.getId_persona());

        List<Persona> listapers = personaService.findAll();
        System.out.println("el limite de la lista es: " + listapers.size());
        for (int i = 0; i < listapers.size(); i++) {
            if (listapers.get(i).getCi() == persona2.getCi()) {
                listapers.remove(i);
                break;
            }
        }
        System.out.println("el nuevo limite de la lista es: " + listapers.size());
        int cont = 0;
        for (int i = 0; i < listapers.size(); i++) {
            if (listapers.get(i).getCi().equals(persona.getCi())) {
                cont++;
                break;
            }
        }
        System.out.println("la variable cont: " + cont);
        if (cont == 0) {
            persona.setFechaRegistro(persona2.getFechaRegistro());
            persona.setHoraRegistro(persona2.getHoraRegistro());

            persona.setEstado("A");
            personaService.save(persona);
            return ResponseEntity.ok("Se modificó el registro correctamente");
        } else {
            return ResponseEntity.ok("Ya existe un registro con este C.I.");
        }
    }

    /*@PostMapping(value = "/EliminarPersona/{id_persona}")
    @ResponseBody
    public void EliminarPersona(HttpServletRequest request, Model model,
            @PathVariable("id_persona") Long id_persona) {
        System.out.println("Eliminar PERSONA ID: "+id_persona);
        Persona persona = personaService.findOne(id_persona);

        if (persona.getUsuario() == null) {
            personaService.delete(id_persona);
        } else {
            Usuario usuario = persona.getUsuario();
            usuarioService.delete(usuario.getId_usuario());
            personaService.delete(id_persona);

        }
        daoPersonaData.deleteById(id_persona);
        System.out.println("SE ELIMINO LA PERSONA ID: "+id_persona);
    }*/
    @Transactional
    @PostMapping(value = "/EliminarPersona/{id_persona}")
    @ResponseBody
    public void EliminarPersona(HttpServletRequest request, Model model,
            @PathVariable("id_persona") Long id_persona) {
        System.out.println("Eliminar PERSONA ID: "+id_persona);
        Persona persona = personaService.findOne(id_persona);

        if (persona.getUsuario() == null) {
            personaService.eliminar(id_persona);
        } else {
            Usuario usuario = persona.getUsuario();
            //usuarioService.delete(usuario.getId_usuario());
            //personaService.delete(id_persona);
            usuarioService.eliminar(usuario.getId_usuario());
            personaService.eliminar(id_persona);
        }
        System.out.println("SE ELIMINO LA PERSONA ID: "+id_persona);
    }

    @PostMapping("/RegistrarPersonaA")
    public ResponseEntity<String> RegistrarPersonaA(@RequestParam(value = "nombrePerson") String nombre,
            @RequestParam(value = "apellidoPerson") String apellido, @RequestParam(value = "ciPerson") String ci) {

        if (usuarioService.credenciales(ci) == null) {
            Persona persona = new Persona();
            persona.setNombre(nombre);
            persona.setApellido(apellido);
            persona.setCi(ci);
            persona.setHoraRegistro(new Date());
            persona.setFechaRegistro(new Date());
            persona.setEstado("A");
            personaService.save(persona);
            return ResponseEntity.ok("Se realizó el registro correctamente");

        } else {
            return ResponseEntity.ok("Ya existe un registro con este C.I.");
        }

    }

    @PostMapping("/DatoPersona")
    public ResponseEntity<String[]> DatoPersona(@RequestParam(value = "ciPerson") String ci) {

        Persona persona = personaService.personaCi(ci);
        // System.out.println("EL CI DE LA NUEVA PERSONA ES:
        // "+usuario.getPersona().getCi());
        String[] pers = new String[2];
        pers[0] = String.valueOf(persona.getId_persona());
        pers[1] = persona.getNombre() + " " + persona.getApellido();
        return ResponseEntity.ok(pers);
    }

}
