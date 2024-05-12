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
import com.example.demo.entity.Control;
import com.example.demo.entity.Persona;
import com.example.demo.entity.Usuario;
import com.example.demo.service.CargoService;
import com.example.demo.service.ControlService;
import com.example.demo.service.PersonaService;
import com.example.demo.service.RolService;
import com.example.demo.service.TipoControService;
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

    @Autowired
    private TipoControService tipoControService;

    @Autowired
    private ControlService controService;

    @GetMapping(value = "/PERSONA")
    public String ventanaPersona(HttpServletRequest request, Model model) {

        if (request.getSession().getAttribute("userLog") != null) {
            return "/personas/registrar";
        } else {
            return "expiracion";
        }
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
        Usuario user = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(user.getId_usuario());
        model.addAttribute("permisos", userLog.getPermisos());
        model.addAttribute("personas", personaService.findAll());
        return "/personas/tablaRegistros";
    }

    @PostMapping("/RegistrarPersona")
    public ResponseEntity<String> RegistrarPersona(HttpServletRequest request, @Validated Persona persona) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(usuario.getId_usuario());
        if (personaService.personaCi(persona.getCi()) == null) {
            persona.setHoraRegistro(new Date());
            persona.setFechaRegistro(new Date());
            persona.setEstado("A");
            personaService.save(persona);
            Control control = new Control();
            control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
            control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
                    + " de Persona con el nombre de " + persona.getNombre() +" "+ persona.getApellido()+ " - "
                    + persona.getCi());
                    control.setUsuario(userLog);
            control.setFecha(new Date());
            control.setHora(new Date());
            controService.save(control);
            return ResponseEntity.ok("Se realizó el registro correctamente");
        } else {
            if (personaService.personaCi(persona.getCi()) != null) {
                Persona persona2 = personaService.personaCi(persona.getCi());
                if (persona2.getCargo() == null && persona2.getUnidad() == null && persona2.getUnidad() == null) {
                    persona2.setCargo(persona.getCargo());
                    persona2.setRoles(persona.getRoles());
                    persona2.setUnidad(persona.getUnidad());
                    personaService.save(persona2);

                    System.out.println("Se encontro una persona previamente registrada");
                    Control control = new Control();
                    control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
                    control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
                            + " de Persona con el nombre de " + persona.getNombre() +" "+ persona.getApellido()+ " - "
                            + persona2.getCi());
                            control.setUsuario(userLog);
                    control.setFecha(new Date());
                    control.setHora(new Date());
                    controService.save(control);
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
        model.addAttribute("persona", personaService.findOne(id_persona));
        model.addAttribute("cargos", cargoService.findAll());
        model.addAttribute("roles", rolService.findAll());
        model.addAttribute("unidades", unidadService.findAll());
        model.addAttribute("edit", "true");
        return "/personas/formulario";
    }

    @PostMapping(value = "/ModPersonaG")
    @ResponseBody
    public ResponseEntity<String> ModUsuarioG(HttpServletRequest request, @Validated Persona persona, Model model) {
        Persona persona2 = personaService.findOne(persona.getId_persona());
        Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(usuario.getId_usuario());
        
        if (personaService.personaModCi(persona2.getCi(), persona.getCi()) == null) {
            persona.setFechaRegistro(persona2.getFechaRegistro());
            persona.setHoraRegistro(persona2.getHoraRegistro());
            persona.setEstado("A");
            personaService.save(persona);
            Control control = new Control();
            control.setTipoControl(tipoControService.findAllByTipoControl("Modificación"));
            control.setDescripcion("Realizó una " + control.getTipoControl().getNombre()
                    + " de un registro de Persona");
                    control.setUsuario(userLog);
                    control.setFecha(new Date());
            control.setHora(new Date());
            controService.save(control);
            return ResponseEntity.ok("Se modificó el registro correctamente");
        } else {
            return ResponseEntity.ok("Ya existe un registro con este C.I.");
        }
    }

    @Transactional
    @PostMapping(value = "/EliminarPersona/{id_persona}")
    @ResponseBody
    public void EliminarPersona(HttpServletRequest request, Model model,
            @PathVariable("id_persona") Long id_persona) {
                
        Usuario us = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(us.getId_usuario());
        Persona persona = personaService.findOne(id_persona);

        if (usuarioService.getUsuarioActivo(id_persona) == null) {
            //personaService.eliminar(id_persona);
            persona.setEstado("X");
            personaService.save(persona);
        } else {
            Usuario usuario = usuarioService.getUsuarioActivo(id_persona);
            usuario.setEstado("X");
            usuarioService.save(usuario);
            persona.setEstado("X");
            personaService.save(persona);
            // usuarioService.eliminar(usuario.getId_usuario());
            // personaService.eliminar(id_persona);
        }
        Control control = new Control();
        control.setTipoControl(tipoControService.findAllByTipoControl("Eliminó"));
        control.setDescripcion("El usuario " + userLog.getPersona().getNombre() +" "+userLog.getPersona().getApellido() + " -  "
                + userLog.getPersona().getCi() + " " + control.getTipoControl().getNombre()
                + " de Persona con el nombre de " + persona.getNombre() +" "+ persona.getApellido());
        control.setFecha(new Date());
        control.setHora(new Date());
        controService.save(control);
    }

    @PostMapping("/RegistrarPersonaA")
    public ResponseEntity<String> RegistrarPersonaA(@RequestParam(value = "nombrePerson") String nombre,
            @RequestParam(value = "apellidoPerson") String apellido, @RequestParam(value = "ciPerson") String ci,
            HttpServletRequest request) {
        Usuario us = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(us.getId_usuario());
        if (usuarioService.credenciales(ci) == null) {
            Persona persona = new Persona();
            persona.setNombre(nombre);
            persona.setApellido(apellido);
            persona.setCi(ci);
            persona.setHoraRegistro(new Date());
            persona.setFechaRegistro(new Date());
            persona.setEstado("A");
            personaService.save(persona);
            Control control = new Control();
            control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
            control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
                    + " de Persona con el nombre de " + persona.getNombre() +" "+ persona.getApellido() + " - "
                    + persona.getCi());
                    control.setUsuario(userLog);
            control.setFecha(new Date());
            control.setHora(new Date());
            controService.save(control);
            return ResponseEntity.ok("Se realizó el registro correctamente");

        } else {
            return ResponseEntity.ok("Ya existe un registro con este C.I.");
        }

    }

    @PostMapping("/DatoPersona")
    public ResponseEntity<String[]> DatoPersona(@RequestParam(value = "ciPerson") String ci) {

        Persona persona = personaService.personaCi(ci);
        String[] pers = new String[2];
        pers[0] = String.valueOf(persona.getId_persona());
        pers[1] = persona.getNombre() + " " + persona.getApellido();
        return ResponseEntity.ok(pers);
    }

}
