package com.example.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.demo.entity.Usuario;
import com.example.demo.service.ControlService;
import com.example.demo.service.UsuarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControlUsuarioController {

  @Autowired
  private ControlService controlService;

  @Autowired
  private UsuarioService usuarioService;

  @GetMapping("/ControlUsuario")
  public String ventanaControl(HttpServletRequest request, Model model) {
    System.out.println("PATALLA USUARIO");
    Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
    Usuario userLog = usuarioService.findOne(usuario.getId_usuario());
    if (request.getSession().getAttribute("userLog") != null) {
      return "/control/ventana";
    } else {
      return "expiracion";
    }
  }

  @PostMapping("/registrosUsuariosControl")
  public String registrosUsuariosControl(HttpServletRequest request, Model model) {

    Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
    Usuario userLog = usuarioService.findOne(usuario.getId_usuario());

    model.addAttribute("titulo","Registros de Usuarios de la Unidad  " +userLog.getPersona().getUnidad().getNombre());
    model.addAttribute("usuarios", usuarioService.listaUsuarioPorUnidad(userLog.getPersona().getUnidad().getId_unidad()));

    return "/control/tablaRegistro";
  }

  @PostMapping("/abrirVentanaControl/{idUsuario}")
  public String abrirVentanaControl(Model model, @PathVariable("idUsuario")Long idUsuario) {

    model.addAttribute("usuario", usuarioService.findOne(idUsuario));

    return "/control/ventanaControl";
  }

  @PostMapping("/listarActividadUsuario/{idUsuario}")
  public String listarActividadUsuario(Model model, @PathVariable("idUsuario")Long idUsuario) {

    model.addAttribute("controles", controlService.findAllByControlUsuario(idUsuario));

    return "/control/tablaRegistroControl";
  }

}
