package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.validation.annotation.Validated;

import com.example.demo.entity.Archivo;
import com.example.demo.entity.Control;
import com.example.demo.entity.Mensaje;
import com.example.demo.entity.Persona;
import com.example.demo.entity.Rol;
import com.example.demo.entity.Usuario;
import com.example.demo.service.ChatService;
import com.example.demo.service.ControlService;
import com.example.demo.service.PermisoService;
import com.example.demo.service.PersonaService;
import com.example.demo.service.TipoControService;
import com.example.demo.service.UnidadService;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.UtilidadServiceProject;
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

    @Autowired
    private UtilidadServiceProject utilidadService;

    @Autowired
    private TipoControService tipoControService;

    @Autowired
    private ControlService controService;

    @Autowired
    private PermisoService permisoService;

    @Autowired
    private ChatService chatService;

    @GetMapping(value = "/USUARIO")
    public String ventanaUsuario(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("userLog") != null) {
            return "/usuarios/registrar";
        } else {
            return "expiracion";
        }
    }

    @PostMapping(value = "/NuevoUsuario")
    public String NuevoUsuario(HttpServletRequest request, Model model) {

        if (request.getSession().getAttribute("userLog") != null) {
            model.addAttribute("usuario", new Usuario());
            model.addAttribute("personas", personaService.findAll());
            model.addAttribute("permisos", permisoService.findAll());
            return "/usuarios/formulario";
        } else {
            return "expiracion";
        }
    }

    @PostMapping(value = "/RegistrosUsuario")
    public String tablaRegistros(HttpServletRequest request, Model model) {
        Usuario user = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(user.getId_usuario());
        List<Usuario> usuarios = usuarioService.findAll();
        for (Usuario usuario : usuarios) {
            String contenidoDescencryptado;
            try {
                contenidoDescencryptado = utilidadService.decrypt(usuario.getContraseña());
                usuario.setContraseña(contenidoDescencryptado);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        // model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("permisos", userLog.getPermisos());
        model.addAttribute("usuarios", usuarios);
        return "/usuarios/tablaRegistros";
    }

    @PostMapping(value = "/RegistrarUsuario")
    @ResponseBody
    public ResponseEntity<String> RegistrarUsuario(HttpServletRequest request, Model model, @Validated Usuario usuario,
            @RequestParam("foto") MultipartFile foto) throws IOException {
        if (request.getSession().getAttribute("userLog") != null) {
            Usuario user = (Usuario) request.getSession().getAttribute("userLog");
            Usuario userLog = usuarioService.findOne(user.getId_usuario());
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

            try {
                String encryptedBytes = utilidadService.encrypt(usuario.getContraseña());
                // Convertir los bytes encriptados a un String codificado en base64
                usuario.setContraseña(encryptedBytes);
                // archivo.setContenido(encryptedBytes);
                System.out.println("Encriptacion Completa");
            } catch (Exception e) {
                System.out.println("Error en la encriptacion: " + e);
            }
            usuarioService.save(usuario);
            Control control = new Control();
            control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
            control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
                    + " de Usuario con el nombre de " + usuario.getNombre_user());
            control.setUsuario(userLog);
            control.setFecha(new Date());
            control.setHora(new Date());
            controService.save(control);
            return ResponseEntity.ok("Se realizó el registro correctamente");
        } else {
            return ResponseEntity.ok("Se ha cerrado la sesion por inactividad.");
        }
    }

    @GetMapping(value = "/ModUsuario/{id_usuario}")
    public String EditarUsuario(HttpServletRequest request, Model model,
            @PathVariable("id_usuario") Long id_usuario) {
        Usuario usuario = usuarioService.findOne(id_usuario);
        String contenidoDescencryptado;
        try {
            contenidoDescencryptado = utilidadService.decrypt(usuario.getContraseña());
            usuario.setContraseña(contenidoDescencryptado);
        } catch (Exception e) {
            // TODO: handle exception
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("personas", personaService.findAll());
        model.addAttribute("permisos", permisoService.findAll());
        model.addAttribute("edit", "true");
        return "/usuarios/formulario";
    }

    @GetMapping(value = "/ModUsuarioPerfil/{id_usuario}")
    public String ModUsuarioPerfilG(HttpServletRequest request, Model model,
            @PathVariable("id_usuario") Long id_usuario) {
        if (request.getSession().getAttribute("userLog") != null) {
            Usuario usuario = usuarioService.findOne(id_usuario);
            String contenidoDescencryptado;
            try {
                contenidoDescencryptado = utilidadService.decrypt(usuario.getContraseña());
                usuario.setContraseña(contenidoDescencryptado);
            } catch (Exception e) {
                // TODO: handle exception
            }
            model.addAttribute("perfil", usuario);
            model.addAttribute("edit", "true");
            return "/usuarios/formularioModal";
        } else {
            return "expiracion";
        }
    }

    @PostMapping(value = "/ModUsuarioG")
    @ResponseBody
    public ResponseEntity<String> ModUsuario(@Validated Usuario usuario, Model model, HttpServletRequest request,
            @RequestParam("foto") MultipartFile foto) throws IOException {
        if (request.getSession().getAttribute("userLog") != null) {
            Usuario user = (Usuario) request.getSession().getAttribute("userLog");
            Usuario userLog = usuarioService.findOne(usuario.getId_usuario());
            Usuario usuario2 = usuarioService.findOne(usuario.getId_usuario());

            if (!foto.isEmpty()) {
                usuario.setFotoPerfil(foto.getBytes());
            } else {
                usuario.setFotoPerfil(usuario2.getFotoPerfil());
            }
            usuario.setFechaRegistro(usuario2.getFechaRegistro());
            usuario.setHoraRegistro(usuario2.getHoraRegistro());
            usuario.setEstado(usuario2.getEstado());
            try {
                String encrypted = utilidadService.encrypt(usuario.getContraseña());
                usuario.setContraseña(encrypted);
            } catch (Exception e) {
                System.out.println("Error en la encriptacion: " + e);
                e.printStackTrace();
            }
            usuarioService.save(usuario);
            Control control = new Control();
            control.setTipoControl(tipoControService.findAllByTipoControl("Modificación"));
            control.setDescripcion("Realizó una " + control.getTipoControl().getNombre()
                    + " de Usuario con el nombre de " + usuario.getNombre_user());
            control.setUsuario(userLog);
            control.setFecha(new Date());
            control.setHora(new Date());
            controService.save(control);
            return ResponseEntity.ok("Se guardaron los cambios correctamente");
        } else {
            return ResponseEntity.ok("Se ha cerrado la sesion por inactividad.");
        }
    }

    @PostMapping(value = "/ModUsuarioPerfilG")
    @ResponseBody
    public ResponseEntity<String> ModUsuarioPerfilG(@Validated Usuario usuario, Model model, HttpServletRequest request,
            @RequestParam("nombreP") String nombreP, @RequestParam("apellido") String apellido,
            @RequestParam("ci") String ci,
            @RequestParam("foto") MultipartFile foto) throws IOException {
        if (request.getSession().getAttribute("userLog") != null) {
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
            usuario.setPermisos(usuario2.getPermisos());

            try {
                String encryptedBytes = utilidadService.encrypt(usuario.getContraseña());
                // Convertir los bytes encriptados a un String codificado en base64
                usuario.setContraseña(encryptedBytes);
                // archivo.setContenido(encryptedBytes);
                System.out.println("Encriptacion Completa");
            } catch (Exception e) {
                System.out.println("Error en la encriptacion: " + e);
            }

            usuarioService.save(usuario);

            model.addAttribute("userLog", usuario);

            return ResponseEntity.ok("continua");
        } else {
            return ResponseEntity.ok("cerrada");
        }
    }

    @Transactional
    @PostMapping(value = "/EliminarUsuario/{id_usuario}")
    @ResponseBody
    public void EliminarUsuario(HttpServletRequest request, Model model,
            @PathVariable("id_usuario") Long id_usuario) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(usuario.getId_usuario());
        Usuario usuario2 = usuarioService.findOne(id_usuario);
        
        ///usuarioService.eliminar(usuario2.getId_usuario());
        usuario2.setEstado("X");
        usuarioService.save(usuario2);
        Control control = new Control();
        control.setTipoControl(tipoControService.findAllByTipoControl("Eliminó"));
        control.setDescripcion(control.getTipoControl().getNombre()
                + " un registro de Usuario con el nombre de " + usuario.getNombre_user());
        control.setUsuario(userLog);
        control.setFecha(new Date());
        control.setHora(new Date());
        controService.save(control);
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

        if (logueado.getPersona().getUnidad().getNombre().equals("ARCHIVO Y BIBLIOTECA")) {
            for (Usuario user : usuarioService.listaUsuarioChatPersonalArchivo(logueado.getId_usuario())) {
                String[] userData = { user.getNombre_user(), String.valueOf(user.getId_usuario()) };
                userChats.add(userData);
            }
        } else {
            for (Usuario user : usuarioService.listaUsuarioChatRestoPersonal(logueado.getId_usuario())) {
                String[] userData = { user.getNombre_user(), String.valueOf(user.getId_usuario()) };
                userChats.add(userData);
            }
        }

        // for (Rol rol : usuarioService.findOne(usuario.getId_usuario()).getPersona().getRoles()) {
        //     if (rol.getNombre().equals("ARCHIVOS")) {
        //         List<Usuario> usuarios = usuarioService.findAll();
        //         for (Usuario u : usuarios) {
        //             if (u.getId_usuario() != usuario.getId_usuario()) {
        //                 String[] userData = { u.getNombre_user(), String.valueOf(u.getId_usuario()) };
        //                 userChats.add(userData);
        //             }
        //         }
        //         break;
        //     } else {
        //         List<Usuario> usuarios2 = new ArrayList<>();
        //         List<Usuario> usuarios = usuarioService.findAll();
        //         for (Usuario usuario3 : usuarios) {
        //             for (Rol rol2 : usuario3.getPersona().getRoles()) {
        //                 if (rol2.getNombre().equals("ARCHIVOS")) {
        //                     String[] userData = { usuario3.getNombre_user(), String.valueOf(usuario3.getId_usuario()) };
        //                     userChats.add(userData);

        //                 }
        //             }
        //         }
        //     }
        // }

        return new ResponseEntity<>(userChats, HttpStatus.OK);
    }

}