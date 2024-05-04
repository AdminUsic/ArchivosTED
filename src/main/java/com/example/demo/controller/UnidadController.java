package com.example.demo.controller;

import java.util.List;

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

import com.example.demo.entity.Unidad;
import com.example.demo.entity.Usuario;
import com.example.demo.service.UnidadService;
import com.example.demo.service.UsuarioService;

@Controller
public class UnidadController {

    @Autowired
    private UnidadService unidadService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/UNIDAD")
    public String ventanaDocumentos(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("userLog") != null) {
            return "/unidades/registrar";
        } else {
            return "expiracion";
        }
    }

    @PostMapping(value = "/NuevaUnidad")
    public String NuevaUnidad(HttpServletRequest request, Model model) {
        model.addAttribute("unidad", new Unidad());
        return "/unidades/formulario";
    }

    @PostMapping(value = "/RegistrosUnidades")
    public String RegistrosUnidades(HttpServletRequest request, Model model) {
        List<Unidad> listaUnidades = unidadService.findAll();
        for (int i = 0; i < listaUnidades.size(); i++) {

            String[] palabras = listaUnidades.get(i).getNombre().split(" ");

            StringBuilder primerasLetras = new StringBuilder();

            for (String palabra : palabras) {
                if (!palabra.isEmpty()) {
                    if (palabra.equalsIgnoreCase("y")) {
                        continue;
                    } else if (palabra.equalsIgnoreCase("de")) {
                        continue;
                    } else if (palabra.equalsIgnoreCase("del")) {
                        continue;
                    }
                    char primeraLetra = palabra.charAt(0);
                    primerasLetras.append(primeraLetra);
                }
            }

            listaUnidades.get(i).setSigla(primerasLetras.toString());
        }
        model.addAttribute("unidades", listaUnidades);
              Usuario user = (Usuario) request.getSession().getAttribute("userLog");
        Usuario userLog = usuarioService.findOne(user.getId_usuario());
        model.addAttribute("permisos", userLog.getPermisos());
        return "/unidades/tablaRegistros";
    }

    /*
     * @PostMapping("/GuardarRegistroUnidad")
     * 
     * @ResponseBody
     * public void GuardarRegistroUnidad(HttpServletRequest request, Model model,
     * Unidad unidad, @RequestParam(value = "id_unidadPadre", required = false) Long
     * id_UnidadPadre) {
     * System.out.println("METODO REGISTRAR SECCION");
     * if (id_UnidadPadre != null) {
     * Unidad unidadPadre = unidadService.findOne(id_UnidadPadre);
     * unidad.setUnidadPadre(unidadPadre);
     * }
     * unidad.setEstado("A");
     * unidadService.save(unidad);
     * }
     */

    @PostMapping("/GuardarRegistroUnidad")
    @ResponseBody
    public ResponseEntity<String> GuardarRegistroUnidad(HttpServletRequest request, Model model,
            Unidad unidad, @RequestParam(value = "id_unidadPadre", required = false) Long id_UnidadPadre) {
        System.out.println("METODO REGISTRAR SECCION");

        if (unidadService.UnidadNombre(unidad.getNombre()) == null) {
            if (id_UnidadPadre != null) {
                Unidad unidadPadre = unidadService.findOne(id_UnidadPadre);
                unidad.setUnidadPadre(unidadPadre);
            }

            unidad.setEstado("A");
            unidadService.save(unidad);
            return ResponseEntity.ok("Se realizó el registro correctamente");
        } else {
            return ResponseEntity.ok("Ya existe un registro con este nombre");
        }

    }

    @GetMapping(value = "/ModUnidad/{id_unidad}")
    public String EditarUnidad(HttpServletRequest request, Model model,
            @PathVariable("id_unidad") Long id_unidad) {
        System.out.println("EDITAR UNIDAD");
        model.addAttribute("unidad", unidadService.findOne(id_unidad));
        model.addAttribute("edit", "true");
        return "/unidades/formulario";
    }

    // @PostMapping(value = "/ModUnidadG")
    // @ResponseBody
    // public ResponseEntity<String> ModUnidadG(@Validated Unidad unidad, Model model,
    //         @RequestParam(value = "id_unidadPadre", required = false) Long id_UnidadPadre) {
    //     int cont = 0;
    //     List<Unidad> unidades = unidadService.findAll();
    //     for (int i = 0; i < unidades.size(); i++) {
    //         if (unidades.get(i).getNombre() == unidad.getNombre()) {
    //             unidades.remove(i);
    //             System.out.println("se removio");
    //             break;
    //         }
    //     }

    //     for (Unidad unidad2 : unidades) {
    //         if (unidad2.getNombre().equals(unidad.getNombre())) {
    //             cont++;
    //             System.out.println("exite");
    //             break;
    //         }
    //     }
    //     if (cont == 0) {
    //         if (id_UnidadPadre != null) {
    //             Unidad unidadPadre = unidadService.findOne(id_UnidadPadre);
    //             unidad.setUnidadPadre(unidadPadre);
    //         }
    //         unidad.setEstado("A");
    //         unidadService.save(unidad);
    //         return ResponseEntity.ok("Se modificó el registro correctamente");
    //     } else {
    //         return ResponseEntity.ok("Ya existe un registro con este nombre");
    //     }

    // }
    @PostMapping(value = "/ModUnidadG")
    @ResponseBody
    public ResponseEntity<String> ModUnidadG(@Validated Unidad unidad, Model model,
            @RequestParam(value = "id_unidadPadre", required = false) Long id_UnidadPadre) {
        Unidad unidad2 = unidadService.findOne(unidad.getId_unidad());
        if (unidadService.UnidadModNombre(unidad2.getNombre(), unidad.getNombre()) == null) {
            if (id_UnidadPadre != null) {
                Unidad unidadPadre = unidadService.findOne(id_UnidadPadre);
                unidad.setUnidadPadre(unidadPadre);
            }
            unidad.setEstado("A");
            unidadService.save(unidad);
            return ResponseEntity.ok("Se guardaron los cambios correctamente");
        } else {
            return ResponseEntity.ok("Ya existe un registro con este nombre");
        }

    }

    @PostMapping(value = "/EliminarRegistroUnidad/{id_unidad}")
    @ResponseBody
    public void EliminarRegistroUnidad(HttpServletRequest request, Model model,
            @PathVariable("id_unidad") Long id_unidad) {
        System.out.println("Eliminar UNIDAD");
        // Unidad unidad = unidadService.findOne(id_unidad);
        /*
         * unidad.setEstado("X");
         * unidadService.save(unidad);
         */
        if (!unidadService.findOne(id_unidad).getSubUnidades().isEmpty()) {
            for (Unidad unidad : unidadService.findOne(id_unidad).getSubUnidades()) {
                unidadService.delete(unidad.getId_unidad());
            }
        }
        unidadService.delete(id_unidad);
    }

    // ---------SUB UNIDAD---------------------
    @PostMapping(value = "/NuevaSubUnidad/{id_unidad}")
    public String NuevaSubUnidad(HttpServletRequest request, Model model,
            @PathVariable("id_unidad") Long id_unidad) {
        model.addAttribute("SubUnidad", new Unidad());
        model.addAttribute("NombUnidadPadre", unidadService.findOne(id_unidad).getNombre());
        model.addAttribute("id_UnidadPadre", id_unidad);

        return "/unidades/formularioSubUnidad";
    }

    @PostMapping(value = "/RegistrosSubUnidades/{id_unidad}")
    public String RegistrosSubUnidades(HttpServletRequest request, Model model,
            @PathVariable("id_unidad") Long id_unidad) {
        List<Unidad> listaUnidades = unidadService.findOne(id_unidad).getSubUnidades();

        for (int i = 0; i < listaUnidades.size(); i++) {

            String[] palabras = listaUnidades.get(i).getNombre().split(" ");

            StringBuilder primerasLetras = new StringBuilder();

            for (String palabra : palabras) {
                if (!palabra.isEmpty()) {
                    if (palabra.equalsIgnoreCase("y")) {
                        continue;
                    } else if (palabra.equalsIgnoreCase("de")) {
                        continue;
                    } else if (palabra.equalsIgnoreCase("del")) {
                        continue;
                    }
                    char primeraLetra = palabra.charAt(0);
                    primerasLetras.append(primeraLetra);
                }
            }

            listaUnidades.get(i).setSigla(primerasLetras.toString());
        }
        model.addAttribute("SubUnidades", listaUnidades);
        return "/unidades/tablaRegistrosSubUnidad";
    }

    @PostMapping(value = "/ModSubUnidad/{id_unidad}")
    public String EditarSubUnidad(HttpServletRequest request, Model model,
            @PathVariable("id_unidad") Long id_unidad) {
        System.out.println("EDITAR SUB UNIDAD");
        Unidad unidad = unidadService.findOne(id_unidad);
        model.addAttribute("SubUnidad", unidad);
        model.addAttribute("id_UnidadPadre", unidad.getUnidadPadre().getId_unidad());
        model.addAttribute("edit", "true");
        return "/unidades/formularioSubUnidad";
    }

    @PostMapping(value = "/ConfirmacionEliminacionRegistroU/{id_unidad}")
    public ResponseEntity<String> buscarRegistro(@PathVariable("id_unidad") Long id_unidad){
        String mensaje = "";
        Unidad unidad = unidadService.findOne(id_unidad);

        if (unidad.getSubUnidades().isEmpty()) {
            mensaje = "¿Estas Seguro que desea eliminar el registro de la Unidad/Sección '"+unidad.getNombre()+"'?";
        }

        if (unidad.getSubUnidades().size() == 1) {
                mensaje = "¿Estas Seguro que desea eliminar el registro de la Unidad/Sección '"+unidad.getNombre()+"', la unidad/sección contiene 'una' Sub Unidad/Sección?";
            }
            if (unidad.getSubUnidades().size() > 1) {
                mensaje = "¿Estas Seguro que desea eliminar el registro de la Unidad/Sección '"+unidad.getNombre()+"'', la unidad/sección contiene '"+unidad.getSubUnidades().size()+"' Sub Unidades/Secciones?";
            }
        if (unidad.getUnidadPadre() != null) {
            mensaje = "¿Estas Seguro que desea eliminar el registro de la Sub Unidad/Sección '"+unidad.getNombre()+"'?";
        }
        return ResponseEntity.ok(mensaje);
    }

}
