package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Persona;
import com.example.demo.entity.Usuario;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.UsuarioServiceImpl;

@Controller
public class loginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping(value = "/")
    public String init(){
        return "redirect:/ArchivosTED";
    }

    @GetMapping(value = "/ArchivosTED")
    public String home(HttpServletRequest request, Model model) {

        if (request.getSession().getAttribute("userLog") != null) {

            Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");

            model.addAttribute("userLog", usuarioService.findOne(usuario.getId_usuario()));

            if (usuarioService.findOne(usuario.getId_usuario()).getPersona().getRol().getNombre().equals("ARCHIVOS")) {
                //List<Usuario> usuarios = new ArrayList<>();
                List<Usuario> usuarios = usuarioService.findAll();
                for (int i = 0; i < usuarios.size(); i++) {
                    if (usuarios.get(i)== usuarioService.findOne(usuario.getId_usuario())) {
                        usuarios.remove(i);
                    }
                }
                model.addAttribute("userChats", usuarios);
            } else {
                List<Usuario> usuarios2 = new ArrayList<>();
                List<Usuario> usuarios = usuarioService.findAll();
                for (Usuario usuario3 : usuarios) {
                    if (usuario3.getPersona().getRol().getNombre().equals("ARCHIVOS")) {
                        usuarios2.add(usuario3);
                    }
                }
                model.addAttribute("userChats", usuarios2);
            }

            return "index";
        } else {
            return "login";
        }
    }

    @PostMapping(value = "/Iniciar")
    public ResponseEntity<String> Iniciar(HttpServletRequest request, Model model, RedirectAttributes flash,
            @RequestParam(value = "user", required = false) String ci,
            @RequestParam(value = "password", required = false) String password) {
        Usuario usuario = usuarioService.credenciales(ci);

        if (usuario != null) {
            HttpSession session = request.getSession(true); // Crear una nueva sesión si no existe
            session.setMaxInactiveInterval(1800);


            if (usuario.getContraseña().equals(password)) {
                usuario.setSesion("ON");
                usuarioService.save(usuario);
                session.setAttribute("userLog", usuario);
                return ResponseEntity.ok("inicia");

            } else {
                return ResponseEntity.ok("Contrase\u00F1a Incorrecta");
            }
        } else {
            return ResponseEntity.ok("Usuario Incorrecto o no registrado");
        }
    }

}
