package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.validation.annotation.Validated;

import com.example.demo.entity.Archivo;
import com.example.demo.entity.Persona;

import com.example.demo.entity.Usuario;
import com.example.demo.service.PersonaService;
import com.example.demo.service.UnidadService;
import com.example.demo.service.UsuarioService;
import com.itextpdf.text.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private UnidadService unidadService;

    @GetMapping(value = "/USUARIO")
    // @PreAuthorize("hasAuthority('ARCHIVOS Y BIBLIOTECA')")
    public String ventanaUsuario(HttpServletRequest request, Model model) {
        System.out.println("PATALLA USUARIO");
        return "/usuarios/registrar";
    }

    @PostMapping(value = "/NuevoUsuario")
    public String NuevoUsuario(HttpServletRequest request, Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("personas", personaService.findAll());
        return "/usuarios/formulario";
    }

    @PostMapping(value = "/RegistrosUsuario")
    public String tablaRegistros(HttpServletRequest request, Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "/usuarios/tablaRegistros";
    }

    @PostMapping(value = "/RegistrarUsuario")
    @ResponseBody
    public void RegistrarUsuario(HttpServletRequest request, Model model, @Validated Usuario usuario,
            @RequestParam("foto") MultipartFile foto) throws IOException {
        System.out.println("Registrar Usuario");

        Persona persona = usuario.getPersona();
        persona.setEstado("A");
        System.out.println("el nombre el usuario es: " + persona.getNombre());
        personaService.save(persona);

        if (!foto.isEmpty()) {

            usuario.setFotoPerfil(foto.getBytes());

        } else {
            String nombreArchivo = "UsuarioImg.png";
            String filePath = Paths.get("").toAbsolutePath().toString() + "/src/main/resources/static/logo/";
            File archivo = new File(filePath + nombreArchivo);
            try {
                byte[] contenidoArchivo = Files.readAllBytes(archivo.toPath());
                usuario.setFotoPerfil(contenidoArchivo);
            } catch (Exception e) {
                System.out.println("Error en el foto predeterminada: " + e);
            }
        }
        usuario.setEstado("A");
        usuario.setSesion("OFF");
        usuario.setHoraRegistro(new Date());
        usuario.setFechaRegistro(new Date());
        usuarioService.save(usuario);
    }

    @GetMapping(value = "/ModUsuario/{id_usuario}")
    public String EditarUsuario(HttpServletRequest request, Model model,
            @PathVariable("id_usuario") Long id_usuario) {
        System.out.println("EDITAR USUARIOS");
        model.addAttribute("usuario", usuarioService.findOne(id_usuario));
        model.addAttribute("personas", personaService.findAll());
        model.addAttribute("edit", "true");
        return "/usuarios/formulario";
    }

    @GetMapping(value = "/ModUsuarioPerfil/{id_usuario}")
    public String ModUsuarioPerfilG(HttpServletRequest request, Model model,
            @PathVariable("id_usuario") Long id_usuario) {
        System.out.println("EDITAR PERFIL");
        model.addAttribute("perfil", usuarioService.findOne(id_usuario));
        model.addAttribute("edit", "true");
        return "/usuarios/formularioModal";
    }

    @PostMapping(value = "/ModUsuarioG")
    @ResponseBody
    public void ModUsuario(@Validated Usuario usuario, Model model,
            @RequestParam("foto") MultipartFile foto) throws IOException {

        Usuario usuario2 = usuarioService.findOne(usuario.getId_usuario());

        if (!foto.isEmpty()) {
            usuario.setFotoPerfil(foto.getBytes());
        } else {
            usuario.setFotoPerfil(usuario2.getFotoPerfil());
        }
        usuario.setFechaRegistro(usuario2.getFechaRegistro());
        usuario.setHoraRegistro(usuario2.getHoraRegistro());
        usuario.setEstado(usuario2.getEstado());
        usuarioService.save(usuario);
    }

    @PostMapping(value = "/ModUsuarioPerfilG")
    @ResponseBody
    public void ModUsuarioPerfilG(@Validated Usuario usuario, Model model,
            @RequestParam("nombreP") String nombreP, @RequestParam("apellido") String apellido,
            @RequestParam("ci") String ci,
            @RequestParam("foto") MultipartFile foto) throws IOException {

        Usuario usuario2 = usuarioService.findOne(usuario.getId_usuario());

        if (!foto.isEmpty()) {
            usuario.setFotoPerfil(foto.getBytes());
        } else {
            usuario.setFotoPerfil(usuario2.getFotoPerfil());
        }
        Persona persona = usuario2.getPersona();

        persona.setNombre(nombreP);
        persona.setApellido(apellido);
        persona.setCi(ci);
        usuario.setPersona(persona);
        usuario.setFechaRegistro(usuario2.getFechaRegistro());
        usuario.setHoraRegistro(usuario2.getHoraRegistro());
        usuario.setEstado(usuario2.getEstado());
        usuarioService.save(usuario);

        model.addAttribute("userLog", usuario);
    }

    @Transactional
    @PostMapping(value = "/EliminarUsuario/{id_usuario}")
    @ResponseBody
    public void EliminarUsuario(HttpServletRequest request, Model model,
            @PathVariable("id_usuario") Long id_usuario) {
        System.out.println("Eliminar USUARIOS");
        // Usuario usuario = usuarioService.findOne(id_usuario);

        // personaService.delete(usuario.getPersona().getId_persona());
        //usuarioService.delete(id_usuario);
        usuarioService.eliminar(id_usuario);
        System.out.println("USUARIO ELIMINADO CON EL ID DE "+id_usuario);
    }

    @GetMapping("/verFoto/{id}")
    public ResponseEntity<byte[]> verIcoPdf(@PathVariable Long id) {
        Optional<Usuario> usuarioOptional = usuarioService.findOneOptional(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            // Configurar los encabezados para evitar el caché
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.IMAGE_PNG)
                    .contentLength(usuario.getFotoPerfil().length)
                    .body(usuario.getFotoPerfil());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/CerrarSesion/{id_usuario}")
    @ResponseBody
    public void CerrarSesion(HttpServletRequest request, Model model, RedirectAttributes flash,
            @PathVariable("id_usuario") Long id_usuario) {

        HttpSession session = request.getSession();
        if (session != null) {

            session.invalidate();

            Usuario usuario = usuarioService.findOne(id_usuario);
            usuario.setSesion("OFF");
            flash.addAttribute("validado", "Se cerro sesion con exito!");
        }
        // return "redirect:/login";
    }

    @PostMapping(value = "/UsuariosReceptores")
    public ResponseEntity<List<String[]>> UsuariosReceptores(HttpServletRequest request) {
        List<String[]> userChats = new ArrayList<>();

        Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
        Usuario logueado = usuarioService.findOne(usuario.getId_usuario());

        if (usuarioService.findOne(usuario.getId_usuario()).getPersona().getRol().getNombre().equals("ARCHIVOS")) {
            List<Usuario> usuarios = usuarioService.findAll();
            for (Usuario u : usuarios) {
                if (u.getId_usuario() != usuario.getId_usuario()) {
                    String[] userData = { u.getNombre_user(), String.valueOf(u.getId_usuario()) };
                    userChats.add(userData);
                }
            }
        } else {
            List<Usuario> usuarios2 = new ArrayList<>();
            List<Usuario> usuarios = usuarioService.findAll();
            for (Usuario usuario3 : usuarios) {
                if (usuario3.getPersona().getRol().getNombre().equals("ARCHIVOS")) {
                    String[] userData = { usuario3.getNombre_user(), String.valueOf(usuario3.getId_usuario()) };
                    userChats.add(userData);
                }
            }
        }

        return new ResponseEntity<>(userChats, HttpStatus.OK);
    }

}