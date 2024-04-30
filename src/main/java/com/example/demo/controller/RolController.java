package com.example.demo.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Control;
import com.example.demo.entity.Rol;
import com.example.demo.entity.Unidad;
import com.example.demo.entity.Usuario;
import com.example.demo.service.ControlService;
import com.example.demo.service.MenuService;
import com.example.demo.service.RolService;
import com.example.demo.service.TipoControService;
import com.example.demo.service.UsuarioService;

@Controller
public class RolController {
    @Autowired
    private RolService rolService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private TipoControService tipoControService;

    @Autowired
    private ControlService controService;

    // @GetMapping("/ROL")
    // public String ventanaDocumentos(HttpServletRequest request, Model model) {
    // Usuario u = (Usuario) request.getSession().getAttribute("userLog");
    // Usuario userLog = usuarioService.findOne(u.getId_usuario());
    // return "/roles/registrar";
    // }

    @GetMapping("/ROL")
    public String ventanaDocumentos(HttpServletRequest request, Model model) {
        Usuario u = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(u.getId_usuario());
        return "/roles/ventana";
    }

    @PostMapping(value = "/NuevoRegistroRol")
    public String NuevoRegistroRol(HttpServletRequest request, Model model) {
        model.addAttribute("rol", new Rol());
        model.addAttribute("menus", menuService.findAll());
        System.out.println("NUEVO ROL");
        return "/roles/formulario";
    }

    @PostMapping(value = "/RegistrosRoles")
    public String RegistrosRoles(HttpServletRequest request, Model model) {
        model.addAttribute("roles", rolService.findAll());
        return "/roles/tablaRegistros";
    }

    /*
     * @PostMapping("/GuardarRegistroRol")
     * 
     * @ResponseBody
     * public void GuardarRegistroRol(HttpServletRequest request, Model model,
     * Rol rol) {
     * System.out.println("METODO REGISTRAR ROL");
     * rol.setEstado("A");
     * rolService.save(rol);
     * }
     */

    // @PostMapping("/GuardarRegistroRol")
    // public ResponseEntity<Rol> GuardarRegistroRol(Model model,@Validated Rol rol)
    // {
    // System.out.println("METODO REGISTRAR ROL");
    // rol.setEstado("A");
    // rolService.save(rol);
    // Rol rol2 = rolService.UltimoRegistro();
    // System.out.println("en nombre del rol es: "+rol2.getNombre());
    // return ResponseEntity.ok(rol2);
    // }
    @PostMapping("/GuardarRegistroRol")
    @ResponseBody
    public ResponseEntity<String> GuardarRegistroRol(HttpServletRequest request, Model model, @Validated Rol rol) {
        System.out.println("METODO REGISTRAR SECCION");
        Usuario u = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(u.getId_usuario());
        if (rolService.rolByNombre(rol.getNombre()) == null) {
            rol.setEstado("A");
            rolService.save(rol);
            Control control = new Control();
            control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
            control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
                    + " del rol con el nombre de " + rol.getNombre());
            control.setUsuario(userLog);
            control.setFecha(new Date());
            control.setHora(new Date());
            controService.save(control);
            return ResponseEntity.ok("Se realizó el registro correctamente");
        } else {
            return ResponseEntity.ok("Ya existe un registro con este nombre");
        }
    }

    @GetMapping(value = "/ModRol/{id_rol}")
    public String EditarRol(HttpServletRequest request, Model model,
            @PathVariable("id_rol") Long id_rol) {

        System.out.println("EDITAR ROL");
        model.addAttribute("menus", menuService.findAll());
        model.addAttribute("rol", rolService.findOne(id_rol));
        model.addAttribute("edit", "true");
        return "/roles/formulario";
    }

    /*
     * @PostMapping(value = "/ModRolG")
     * 
     * @ResponseBody
     * public void ModRolG(@Validated Rol rol, Model model) {
     * rol.setEstado("A");
     * rolService.save(rol);
     * }
     */
    @PostMapping(value = "/ModRolG")
    @ResponseBody
    public ResponseEntity<String> ModRolG(@Validated Rol rol, Model model, HttpServletRequest request) {
        Usuario u = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(u.getId_usuario());

        Rol rol2 = rolService.findOne(rol.getId_rol());
        // rol2.setNombre(rol.getNombre());
        // rol2.setMenus(rol2.getMenus());
        // rolService.save(rol2);
        if (rolService.rolByNombreMod(rol2.getNombre(), rol.getNombre()) == null) {
            rol.setEstado("A");
            rolService.save(rol);

            Control control = new Control();
            control.setTipoControl(tipoControService.findAllByTipoControl("Modificación"));
            control.setDescripcion("Realizó una nueva " + control.getTipoControl().getNombre()
                    + " del rol con el nombre de " + rol.getNombre());
            control.setUsuario(userLog);
            control.setFecha(new Date());
            control.setHora(new Date());
            controService.save(control);
            return ResponseEntity.ok("Se guardaron los cambios correctamente");
        } else {
            return ResponseEntity.ok("Ya existe un registro con este nombre");
        }

    }

    @PostMapping(value = "/EliminarRegistroRol/{id_rol}")
    @ResponseBody
    public void EliminarRegistroRol(HttpServletRequest request, Model model,
            @PathVariable("id_rol") Long id_rol) {
        System.out.println("Eliminar ROL");
        rolService.delete(id_rol);
    }
}
