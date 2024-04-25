package com.example.demo.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
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
import com.example.demo.entity.Rol;
import com.example.demo.entity.Usuario;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.UsuarioServiceImpl;
import com.example.demo.service.UtilidadServiceProject;

@Controller
public class loginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UtilidadServiceProject utilidadService;

    @GetMapping(value = "/")
    public String init() throws Exception {

        // for (Usuario usuario : usuarioService.findAll()) {
        //     usuario.setContraseña(utilidadService.encrypt(usuario.getContraseña()));
        //     usuarioService.save(usuario);
        // }

        return "redirect:/ArchivosTED";
    }

    @GetMapping(value = "/ArchivosTED")
    public String home(HttpServletRequest request, Model model) {

        if (request.getSession().getAttribute("userLog") != null) {

            Usuario usu = (Usuario) request.getSession().getAttribute("userLog");
            Usuario userLog = usuarioService.findOne(usu.getId_usuario());
            String contenidoDescencryptado;
            try {
                contenidoDescencryptado = utilidadService.decrypt(userLog.getContraseña());
                userLog.setContraseña(contenidoDescencryptado);
            } catch (Exception e) {
                // TODO: handle exception
            }

            model.addAttribute("userLog", userLog);
            for (Rol rol : usuarioService.findOne(userLog.getId_usuario()).getPersona().getRoles()) {
                if (rol.getNombre().equals("ARCHIVOS")) {
                    // List<Usuario> usuarios = new ArrayList<>();
                    List<Usuario> usuarios = usuarioService.findAll();
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usuarios.get(i) == usuarioService.findOne(userLog.getId_usuario())) {
                            usuarios.remove(i);
                        }
                    }
                    model.addAttribute("userChats", usuarios);
                } else {
                    List<Usuario> usuarios2 = new ArrayList<>();
                    List<Usuario> usuarios = usuarioService.findAll();
                    for (Usuario usuario3 : usuarios) {
                        for (Rol rol2 : usuario3.getPersona().getRoles()) {
                            if (rol2.getNombre().equals("ARCHIVOS")) {
                                usuarios2.add(usuario3);
                            }
                        }
                    }
                    model.addAttribute("userChats", usuarios2);
                }
            }
            // if (usuarioService.findOne(userLog.getId_usuario()).getPersona().getRol().getNombre().equals("ARCHIVOS")) {
            //     // List<Usuario> usuarios = new ArrayList<>();
            //     List<Usuario> usuarios = usuarioService.findAll();
            //     for (int i = 0; i < usuarios.size(); i++) {
            //         if (usuarios.get(i) == usuarioService.findOne(userLog.getId_usuario())) {
            //             usuarios.remove(i);
            //         }
            //     }
            //     model.addAttribute("userChats", usuarios);
            // } else {
            //     List<Usuario> usuarios2 = new ArrayList<>();
            //     List<Usuario> usuarios = usuarioService.findAll();
            //     for (Usuario usuario3 : usuarios) {
            //         if (usuario3.getPersona().getRol().getNombre().equals("ARCHIVOS")) {
            //             usuarios2.add(usuario3);
            //         }
            //     }
            //     model.addAttribute("userChats", usuarios2);
            // }
            System.out.println("METODO DE INICIAR ");
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

            String contenidoDescencryptado;
            try {
                contenidoDescencryptado = utilidadService.decrypt(usuario.getContraseña());
                usuario.setContraseña(contenidoDescencryptado);
            } catch (Exception e) {
                // TODO: handle exception
            }

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
