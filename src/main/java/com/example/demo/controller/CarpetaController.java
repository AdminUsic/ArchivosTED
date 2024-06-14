package com.example.demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Archivo;
import com.example.demo.entity.Caja;
import com.example.demo.entity.Carpeta;
import com.example.demo.entity.Control;
import com.example.demo.entity.FormularioTransferencia;
import com.example.demo.entity.Unidad;
import com.example.demo.entity.Usuario;
import com.example.demo.service.ArchivoService;
import com.example.demo.service.CajaService;
import com.example.demo.service.CarpetaService;
import com.example.demo.service.ControlService;
import com.example.demo.service.CubiertaService;
import com.example.demo.service.FormularioTransferenciaService;
import com.example.demo.service.PersonaService;
import com.example.demo.service.SerieDocumentalService;
import com.example.demo.service.UnidadService;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.UtilidadServiceProject;
import com.example.demo.service.VolumenService;

import net.sf.jasperreports.engine.JRException;

import com.example.demo.service.TipoArchivoService;
import com.example.demo.service.TipoControService;

@Controller
public class CarpetaController {

   @Autowired
   private CarpetaService carpetaService;

   @Autowired
   private ArchivoService archivoService;

   @Autowired
   private UnidadService unidadService;

   @Autowired
   private PersonaService personaService;

   @Autowired
   private TipoArchivoService tipoArchivoService;

   @Autowired
   private SerieDocumentalService documentalService;

   @Autowired
   private VolumenService volumenService;

   @Autowired
   private TipoControService tipoControService;

   @Autowired
   private ControlService controService;

   @Autowired
   private UsuarioService usuarioService;

   @Autowired
   private CubiertaService cubiertaService;

   @Autowired
   private FormularioTransferenciaService formularioTransferenciaService;

   @Autowired
   private UtilidadServiceProject utilidadService;

   @GetMapping(value = "/CARPETAS")
   public String ventanaCarpeta(HttpServletRequest request, Model model) {
      System.out.println("PATALLA CARPETA");
      if (request.getSession().getAttribute("userLog") != null) {
         return "/carpetas/registrar";
      } else {
         return "expiracion";
      }
   }

   @PostMapping(value = "/VistaIcoCar")
   public String VistaIcoCar(HttpServletRequest request, Model model) {
      System.out.println("PATALLA CARPETA Vista Ico");
      Usuario user = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(user.getId_usuario());
      model.addAttribute("permisos", userLog.getPermisos());
      return "/carpetas/vistaICO";
   }

   @PostMapping(value = "/VistaListCar")
   public String VistaLisCar(HttpServletRequest request, Model model) {
      System.out.println("PATALLA CARPETA Vista List");

      return "/carpetas/vistaLIST";
   }

   @PostMapping(value = "/RegistrosCarpetaIco")
   public String RegistrosCarpetaIco(HttpServletRequest request, Model model) {

      Usuario user = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(user.getId_usuario());
      List<Carpeta> listaCarpetas = new ArrayList<>();

      if (userLog.getPersona().getCargo().getNombre().equals("ARCHIVO Y BIBLIOTECA")) {
         List<Carpeta> listCarp = carpetaService.listarTodasCarpetas();
         for (Carpeta carpeta : listCarp) {
            carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
            listaCarpetas.add(carpeta);
         }
      } else {
         List<Carpeta> listCarp = carpetaService.listarPorIdPersona(userLog.getPersona().getId_persona());
         for (Carpeta carpeta : listCarp) {
            carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
            listaCarpetas.add(carpeta);
         }
      }

      for (int i = 0; i < listaCarpetas.size(); i++) {

         String[] palabras = listaCarpetas.get(i).getUnidad().getNombre().split(" ");

         StringBuilder primerasLetras = new StringBuilder();

         for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
               if (palabra.equalsIgnoreCase("y")) {
                  continue;
               } else if (palabra.equalsIgnoreCase("de")) {
                  continue;
               }
               char primeraLetra = palabra.charAt(0);
               primerasLetras.append(primeraLetra);
            }
         }
         listaCarpetas.get(i).getUnidad().setSigla(primerasLetras.toString());
      }
      model.addAttribute("ListCarpetas", listaCarpetas);

      model.addAttribute("permisos", userLog.getPermisos());
      model.addAttribute("userLog", userLog);
      return "/carpetas/iconoRegistros";
   }

   @PostMapping(value = "/NuevaCarpeta")
   public String NuevaCarpeta(HttpServletRequest request, Model model) {
      model.addAttribute("carpeta", new Carpeta());
      model.addAttribute("archivos", archivoService.findAll());
      model.addAttribute("unidades", unidadService.findAll());
      model.addAttribute("volumenes", volumenService.listaDeVolumenes());
      model.addAttribute("seriesDocs", documentalService.findAll());
      return "/carpetas/formulario";
   }

   @PostMapping(value = "/NuevaCarpetaModal")
   public String NuevaCarpetaModal(HttpServletRequest request, Model model) {
      model.addAttribute("carpeta", new Carpeta());
      model.addAttribute("archivos", archivoService.findAll());
      model.addAttribute("unidades", unidadService.findAll());
      model.addAttribute("volumenes", volumenService.listaDeVolumenes());
      model.addAttribute("seriesDocs", documentalService.findAll());
      
      return "/carpetas/formularioModal";
   }

   int cantidadTotalHojasCarpeta(Carpeta carpeta) {
      int sumarCant = 0;
      for (int j = 0; j < carpeta.getArchivos().size(); j++) {
         sumarCant = sumarCant + carpeta.getArchivos().get(j).getCantidadHojas();
      }
      return sumarCant;
   }

   @PostMapping(value = "/RegistrosCarpeta")
   public String tablaRegistros(HttpServletRequest request, Model model) {

      Usuario user = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(user.getId_usuario());
      List<Carpeta> listaCarpetas = new ArrayList<>();

      if (userLog.getPersona().getCargo().getNombre().equals("ARCHIVO Y BIBLIOTECA")) {
         List<Carpeta> listCarp = carpetaService.listarTodasCarpetas();
         for (Carpeta carpeta : listCarp) {
            carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
            listaCarpetas.add(carpeta);
         }
      } else {
         List<Carpeta> listCarp = carpetaService.listarPorIdPersona(userLog.getPersona().getId_persona());
         for (Carpeta carpeta : listCarp) {
            carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
            listaCarpetas.add(carpeta);
         }
      }

      for (int i = 0; i < listaCarpetas.size(); i++) {

         String[] palabras = listaCarpetas.get(i).getUnidad().getNombre().split(" ");

         StringBuilder primerasLetras = new StringBuilder();

         for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
               if (palabra.equalsIgnoreCase("y")) {
                  continue;
               } else if (palabra.equalsIgnoreCase("de")) {
                  continue;
               }
               char primeraLetra = palabra.charAt(0);
               primerasLetras.append(primeraLetra);
            }
         }
         listaCarpetas.get(i).getUnidad().setSigla(primerasLetras.toString());
      }
      model.addAttribute("ListCarpetas", listaCarpetas);

      model.addAttribute("permisos", userLog.getPermisos());
      model.addAttribute("userLog", userLog);
      return "/carpetas/tablaRegistros";
   }

   @PostMapping(value = "/RegistrarCarpeta")
   public ResponseEntity<String> RegistrarCarpeta(HttpServletRequest request, Model model, @Validated Carpeta carpeta,
         @RequestParam(value = "gestion") String gestion) {
      System.out.println("Registrar Carpeta");
      Usuario us = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(us.getId_usuario());

      carpeta.setPersona(userLog.getPersona());
      carpeta.setUnidad(userLog.getPersona().getUnidad());
      carpeta.setEstado("A");
      carpeta.setHoraRegistro(new Date());
      carpeta.setFechaRegistro(new Date());
      carpeta.setGestion(Integer.parseInt(gestion));
      carpetaService.save(carpeta);

      FormularioTransferencia formularioTransferencia = new FormularioTransferencia();
      formularioTransferencia.setEstado("A");
      formularioTransferencia.setCarpeta(carpeta);
      formularioTransferencia.setUnidad(userLog.getPersona().getUnidad());
      formularioTransferencia.setCargo(userLog.getPersona().getCargo());
      formularioTransferencia.setFechaRegistro(new Date());
      formularioTransferencia.setGestion(carpeta.getGestion());
      formularioTransferencia.setHoraRegistro(new Date());
      formularioTransferencia.setPersona(userLog.getPersona());
      formularioTransferencia.setCantDocumentos(0);
      formularioTransferenciaService.save(formularioTransferencia);

      Control control = new Control();
      control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
      control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
            + " de un Formulario de Transferencia");
      control.setUsuario(userLog);
      control.setFecha(new Date());
      control.setHora(new Date());
      controService.save(control);
      return ResponseEntity.ok("Se guardó el nuevo registro Correctamente");
   }

   @GetMapping(value = "/ModCarpeta/{id_carpeta}")
   public String EditarCarpeta(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {
      System.out.println("EDITAR CARPETA");
      model.addAttribute("carpeta", carpetaService.findOne(id_carpeta));
      model.addAttribute("archivos", archivoService.findAll());
      model.addAttribute("unidades", unidadService.findAll());
      model.addAttribute("volumenes", volumenService.findAll());
      model.addAttribute("seriesDocs", documentalService.findAll());
      model.addAttribute("edit", "true");
      return "/carpetas/formulario";
   }

   @GetMapping(value = "/ModCarpetaModal/{id_carpeta}")
   public String EditarCarpetaModal(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {
      System.out.println("EDITAR CARPETA");
      model.addAttribute("carpeta", carpetaService.findOne(id_carpeta));
      model.addAttribute("archivos", archivoService.findAll());
      model.addAttribute("unidades", unidadService.findAll());
      model.addAttribute("volumenes", volumenService.findAll());
      model.addAttribute("seriesDocs", documentalService.findAll());
      model.addAttribute("edit", "true");
      return "/carpetas/formularioModal";
   }

   @PostMapping(value = "/ModCarpetaG")
   public ResponseEntity<String> ModCarpeta(HttpServletRequest request, @Validated Carpeta carpeta, Model model,
         @RequestParam(value = "gestion") String gestion) {
      Usuario us = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(us.getId_usuario());
      carpeta.setGestion(Integer.parseInt(gestion));
      carpeta.setEstado("A");
      carpetaService.save(carpeta);
      Control control = new Control();
      control.setTipoControl(tipoControService.findAllByTipoControl("Modificación"));
      control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
            + " de un Formulario de Transferencia");
      control.setUsuario(userLog);
      control.setFecha(new Date());
      control.setHora(new Date());
      controService.save(control);
      return ResponseEntity.ok("Se ha Modificado el registro Correctamente");
   }

   @PostMapping(value = "/EliminarCarpeta/{id_carpeta}")
   @ResponseBody
   public void EliminarCarpeta(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {
      System.out.println("Eliminar CARPETA");
      Usuario us = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(us.getId_usuario());
      Carpeta carpeta = carpetaService.findOne(id_carpeta);

      for (int i = 0; i < carpeta.getArchivos().size(); i++) {
         archivoService.delete(carpeta.getArchivos().get(i).getId_archivo());
      }
      carpetaService.delete(id_carpeta);
      Control control = new Control();
      control.setTipoControl(tipoControService.findAllByTipoControl("Eliminó"));
      control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
            + " de un Formulario de Transferencia");
      control.setUsuario(userLog);
      control.setFecha(new Date());
      control.setHora(new Date());
      controService.save(control);
   }

   @PostMapping(value = "/ContenidoCarpeta/{id_carpeta}")
   public String ContenidoCarpeta(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {

      Carpeta carpeta = new Carpeta();
      carpeta = carpetaService.findOne(id_carpeta);
      carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
      model.addAttribute("carpeta", carpeta);
      Usuario user = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(user.getId_usuario());
      model.addAttribute("permisos", userLog.getPermisos());
      model.addAttribute("userLog", userLog);
      return "/carpetas/archivoCarpeta";
   }

   @PostMapping(value = "/ContenidoCarpetaList/{id_carpeta}")
   public String ContenidoCarpetaList(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {

      Carpeta carpeta = carpetaService.findOne(id_carpeta);
      carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
      model.addAttribute("carpeta", carpeta);
      Usuario user = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(user.getId_usuario());
      model.addAttribute("permisos", userLog.getPermisos());
      model.addAttribute("userLog", userLog);
      return "/carpetas/archivoCarpetaList";
   }

   @PostMapping(value = "/RegistrosArchivosCList/{id_carpeta}")
   public String RegistrosArchivosCList(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {

      Carpeta carpeta = carpetaService.findOne(id_carpeta);
      carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
      model.addAttribute("carpeta", carpeta);
      model.addAttribute("listaArchivos", carpeta.getArchivos());
      Usuario user = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(user.getId_usuario());
      model.addAttribute("permisos", userLog.getPermisos());
      return "/carpetas/tablaRegistroArchivoList";
   }

   @PostMapping(value = "/AgregarArchivo/{id_carpeta}")
   public String AgregarArchivo(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {
      Usuario us = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(us.getId_usuario());
      model.addAttribute("carp", id_carpeta);
      model.addAttribute("archivo", new Archivo());
      model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
      model.addAttribute("personas", personaService.findAll());
      model.addAttribute("seriesDocumental", carpetaService.findOne(id_carpeta).getSerieDocumental().getSubSeries());
      model.addAttribute("seccionesDocumental", carpetaService.findOne(id_carpeta).getUnidad().getSubUnidades());
      model.addAttribute("cubiertas", cubiertaService.findAll());
      model.addAttribute("userLog", userLog);
      return "/carpetas/formularioArchivoC";

   }

   @PostMapping(value = "/AgregarArchivoCList/{id_carpeta}")
   public String AgregarArchivoCList(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {
      Usuario us = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(us.getId_usuario());
      model.addAttribute("carp", id_carpeta);
      model.addAttribute("archivo", new Archivo());
      model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
      model.addAttribute("personas", personaService.findAll());
      model.addAttribute("seriesDocumental", carpetaService.findOne(id_carpeta).getSerieDocumental().getSubSeries());
      model.addAttribute("seccionesDocumental", carpetaService.findOne(id_carpeta).getUnidad().getSubUnidades());
      model.addAttribute("cubiertas", cubiertaService.findAll());
      model.addAttribute("userLog", userLog);
      return "/carpetas/formularioArchivoCList";

   }

   @PostMapping(value = "/ModificarArchivoC/{id_archivo}")
   public String ModificarArchivoC(HttpServletRequest request, Model model,
         @PathVariable("id_archivo") Long id_archivo) {
      Usuario us = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(us.getId_usuario());
      Archivo archivo = archivoService.findOne(id_archivo);
      model.addAttribute("carp", archivo.getCarpeta().getId_carpeta());
      model.addAttribute("archivo", archivo);
      model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
      model.addAttribute("personas", personaService.findAll());
      model.addAttribute("seccionesDocumental",
            carpetaService.findOne(archivo.getCarpeta().getId_carpeta()).getUnidad().getSubUnidades());
      model.addAttribute("userLog", userLog);
      model.addAttribute("edit", "true");
      return "/carpetas/formularioArchivoC";
   }

   @PostMapping(value = "/ModificarArchivoCList/{id_archivo}")
   public String ModificarArchivoCList(HttpServletRequest request, Model model,
         @PathVariable("id_archivo") Long id_archivo) {
      Usuario us = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(us.getId_usuario());
      Archivo archivo = archivoService.findOne(id_archivo);
      model.addAttribute("carp", archivo.getCarpeta().getId_carpeta());
      model.addAttribute("archivo", archivo);
      model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
      model.addAttribute("personas", personaService.findAll());
      model.addAttribute("seccionesDocumental",
            carpetaService.findOne(archivo.getCarpeta().getId_carpeta()).getUnidad().getSubUnidades());
      model.addAttribute("userLog", userLog);
      model.addAttribute("edit", "true");
      return "/carpetas/formularioArchivoCList";
   }

   @PostMapping("/buscarCarpeta")
   public String BuscandoCarpeta(HttpServletRequest request, Model model,
         @RequestParam(value = "unidadCarpeta", required = false) Long unidadCarpeta,
         @RequestParam(value = "volumenCarpeta", required = false) Long volumenCarpeta,
         @RequestParam(value = "seriesDocsCarpeta", required = false) Long seriesDocsCarpeta,
         @RequestParam(value = "gestion", required = false) int gestionCarpeta) {

      List<Carpeta> listaCarpetas = new ArrayList<>();

      if (gestionCarpeta != 0 && unidadCarpeta != null && volumenCarpeta != null && seriesDocsCarpeta != null) {
         listaCarpetas = carpetaService.buscarPorUnidadSerieVolumenGestion(seriesDocsCarpeta,
               unidadCarpeta, volumenCarpeta, gestionCarpeta);
         contarPaginas(listaCarpetas);
         model.addAttribute("ListCarpetas", listaCarpetas);
      } else {
         if (gestionCarpeta != 0 && unidadCarpeta != null && volumenCarpeta != null) {
            listaCarpetas = carpetaService.buscarPorUnidadVolumenGestion(unidadCarpeta, volumenCarpeta,
                  gestionCarpeta);
            contarPaginas(listaCarpetas);
            model.addAttribute("ListCarpetas", listaCarpetas);
         } else {
            if (seriesDocsCarpeta != null && unidadCarpeta != null && gestionCarpeta != 0) {
               listaCarpetas = carpetaService.buscarPorUnidadSerieGestion(seriesDocsCarpeta,
                     unidadCarpeta, gestionCarpeta);
               contarPaginas(listaCarpetas);
               model.addAttribute("ListCarpetas", listaCarpetas);
            } else {
               if (seriesDocsCarpeta != null && unidadCarpeta != null && volumenCarpeta != null) {
                  listaCarpetas = carpetaService.buscarPorUnidadSerieVolumen(seriesDocsCarpeta, unidadCarpeta,
                        volumenCarpeta);
                  contarPaginas(listaCarpetas);
                  model.addAttribute("ListCarpetas", listaCarpetas);
               } else {
                  if (gestionCarpeta != 0 && unidadCarpeta != null) {
                     listaCarpetas = carpetaService.buscarPorUnidadGestion(unidadCarpeta, gestionCarpeta);
                     contarPaginas(listaCarpetas);
                     model.addAttribute("ListCarpetas", listaCarpetas);
                  }
                  if (gestionCarpeta != 0 && seriesDocsCarpeta != null) {
                     listaCarpetas = carpetaService.buscarPorSeriedGestion(seriesDocsCarpeta, gestionCarpeta);
                     contarPaginas(listaCarpetas);
                     model.addAttribute("ListCarpetas", listaCarpetas);
                  } else {
                     if (gestionCarpeta != 0 && volumenCarpeta != null) {
                        listaCarpetas = carpetaService.buscarPorVolumenGestion(volumenCarpeta, gestionCarpeta);
                        contarPaginas(listaCarpetas);
                        model.addAttribute("ListCarpetas", listaCarpetas);
                     } else {
                        if (unidadCarpeta != null && volumenCarpeta != null) {
                           listaCarpetas = carpetaService.buscarPorUnidadVolumen(unidadCarpeta,
                                 volumenCarpeta);
                           contarPaginas(listaCarpetas);
                           model.addAttribute("ListCarpetas", listaCarpetas);
                        } else {
                           if (unidadCarpeta != null && seriesDocsCarpeta != null) {
                              listaCarpetas = carpetaService.buscarPorUnidadSerie(seriesDocsCarpeta,
                                    unidadCarpeta);
                              contarPaginas(listaCarpetas);
                              model.addAttribute("ListCarpetas", listaCarpetas);
                           } else {
                              if (gestionCarpeta != 0) {
                                 listaCarpetas = carpetaService.buscarPorGestion(gestionCarpeta);
                                 contarPaginas(listaCarpetas);
                                 model.addAttribute("ListCarpetas", listaCarpetas);
                              } else {
                                 if (volumenCarpeta != null) {
                                    listaCarpetas = volumenService.findOne(volumenCarpeta).getCarpetas();
                                    contarPaginas(listaCarpetas);
                                    model.addAttribute("ListCarpetas", listaCarpetas);
                                 } else {
                                    if (seriesDocsCarpeta != null) {
                                       listaCarpetas = documentalService.findOne(seriesDocsCarpeta)
                                             .getCarpetas();
                                       contarPaginas(listaCarpetas);
                                       model.addAttribute("ListCarpetas", listaCarpetas);
                                    } else {
                                       if (unidadCarpeta != null) {
                                          listaCarpetas = unidadService.findOne(unidadCarpeta)
                                                .getCarpetas();
                                          contarPaginas(listaCarpetas);
                                          model.addAttribute("ListCarpetas", listaCarpetas);
                                       } else {
                                          model.addAttribute("ListCarpetas", null);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return "/busquedas/registrosCarpeta";
   }

   @PostMapping("/buscarCarpeta2")
   public String BuscandoCarpeta2(HttpServletRequest request, Model model,
         @RequestParam(value = "volumenCarpeta", required = false) Long volumenCarpeta,
         @RequestParam(value = "seriesDocsCarpeta", required = false) Long seriesDocsCarpeta,
         @RequestParam(value = "gestion", required = false) int gestionCarpeta) {

      Usuario usuario = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(usuario.getId_usuario());
      Long unidadCarpeta = userLog.getPersona().getUnidad().getId_unidad();

      List<Carpeta> listaCarpetas = new ArrayList<>();

      if (gestionCarpeta != 0 && unidadCarpeta != null && volumenCarpeta != null && seriesDocsCarpeta != null) {
         listaCarpetas = carpetaService.buscarPorUnidadSerieVolumenGestion(seriesDocsCarpeta,
               unidadCarpeta, volumenCarpeta, gestionCarpeta);
         contarPaginas(listaCarpetas);
         model.addAttribute("ListCarpetas", listaCarpetas);
      } else {
         if (gestionCarpeta != 0 && unidadCarpeta != null && volumenCarpeta != null) {
            listaCarpetas = carpetaService.buscarPorUnidadVolumenGestion(unidadCarpeta, volumenCarpeta,
                  gestionCarpeta);
            contarPaginas(listaCarpetas);
            model.addAttribute("ListCarpetas", listaCarpetas);
         } else {
            if (seriesDocsCarpeta != null && unidadCarpeta != null && gestionCarpeta != 0) {
               listaCarpetas = carpetaService.buscarPorUnidadSerieGestion(seriesDocsCarpeta,
                     unidadCarpeta, gestionCarpeta);
               contarPaginas(listaCarpetas);
               model.addAttribute("ListCarpetas", listaCarpetas);
            } else {
               if (seriesDocsCarpeta != null && unidadCarpeta != null && volumenCarpeta != null) {
                  listaCarpetas = carpetaService.buscarPorUnidadSerieVolumen(seriesDocsCarpeta, unidadCarpeta,
                        volumenCarpeta);
                  contarPaginas(listaCarpetas);
                  model.addAttribute("ListCarpetas", listaCarpetas);
               } else {
                  if (gestionCarpeta != 0 && unidadCarpeta != null) {
                     listaCarpetas = carpetaService.buscarPorUnidadGestion(unidadCarpeta, gestionCarpeta);
                     contarPaginas(listaCarpetas);
                     model.addAttribute("ListCarpetas", listaCarpetas);
                  }
                  if (gestionCarpeta != 0 && seriesDocsCarpeta != null) {
                     listaCarpetas = carpetaService.buscarPorSeriedGestion(seriesDocsCarpeta, gestionCarpeta);
                     contarPaginas(listaCarpetas);
                     model.addAttribute("ListCarpetas", listaCarpetas);
                  } else {
                     if (gestionCarpeta != 0 && volumenCarpeta != null) {
                        listaCarpetas = carpetaService.buscarPorVolumenGestion(volumenCarpeta, gestionCarpeta);
                        contarPaginas(listaCarpetas);
                        model.addAttribute("ListCarpetas", listaCarpetas);
                     } else {
                        if (unidadCarpeta != null && volumenCarpeta != null) {
                           listaCarpetas = carpetaService.buscarPorUnidadVolumen(unidadCarpeta,
                                 volumenCarpeta);
                           contarPaginas(listaCarpetas);
                           model.addAttribute("ListCarpetas", listaCarpetas);
                        } else {
                           if (unidadCarpeta != null && seriesDocsCarpeta != null) {
                              listaCarpetas = carpetaService.buscarPorUnidadSerie(seriesDocsCarpeta,
                                    unidadCarpeta);
                              contarPaginas(listaCarpetas);
                              model.addAttribute("ListCarpetas", listaCarpetas);
                           } else {
                              if (gestionCarpeta != 0) {
                                 listaCarpetas = carpetaService.buscarPorGestion(gestionCarpeta);
                                 contarPaginas(listaCarpetas);
                                 model.addAttribute("ListCarpetas", listaCarpetas);
                              } else {
                                 if (volumenCarpeta != null) {
                                    listaCarpetas = volumenService.findOne(volumenCarpeta).getCarpetas();
                                    contarPaginas(listaCarpetas);
                                    model.addAttribute("ListCarpetas", listaCarpetas);
                                 } else {
                                    if (seriesDocsCarpeta != null) {
                                       listaCarpetas = documentalService.findOne(seriesDocsCarpeta)
                                             .getCarpetas();
                                       contarPaginas(listaCarpetas);
                                       model.addAttribute("ListCarpetas", listaCarpetas);
                                    } else {
                                       if (unidadCarpeta != null) {
                                          listaCarpetas = unidadService.findOne(unidadCarpeta)
                                                .getCarpetas();
                                          contarPaginas(listaCarpetas);
                                          model.addAttribute("ListCarpetas", listaCarpetas);
                                       } else {
                                          model.addAttribute("ListCarpetas", null);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return "/busquedas/registrosCarpeta";
   }

   void contarPaginas(List<Carpeta> listaCarpetas) {
      // List<Carpeta> listaCarpetas = new ArrayList<>();
      for (int i = 0; i < listaCarpetas.size(); i++) {
         Carpeta carpeta = new Carpeta();
         carpeta = listaCarpetas.get(i);
         carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
         // listaCarpetas.add(carpeta);
      }
      // model.addAttribute("ListCarpetas", listaCarpetas);
   }

   @GetMapping(value = "/GenerarFormularioTransferencia/{id_carpeta}")
   public ResponseEntity<ByteArrayResource> GenerarFormularioTransferencia(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) throws SQLException {

      FormularioTransferencia formularioTransferencia = formularioTransferenciaService
            .formularioTransferenciaCarpeta(id_carpeta);

      String nombreArchivo = "FormularioTransferenciaReport.jrxml";

      Path projectPath = Paths.get("").toAbsolutePath();
      Path imagePath = Paths.get(projectPath.toString(), "src", "main", "resources", "static", "logo",
            "logoCabezera.png");
      String imagen = imagePath.toString();
      // System.out.println(imagen);
      Map<String, Object> parametros = new HashMap<>();
      parametros.put("idFormulario", formularioTransferencia.getId_formularioTransferencia());
      parametros.put("rutaImg", imagen);
      parametros.put("lugarFechaTexto", "Cobija, " + utilidadService.fechaActualTexto());

      ByteArrayOutputStream stream;
      try {
         stream = utilidadService.compilarAndExportarReporte(nombreArchivo, parametros);
         byte[] bytes = stream.toByteArray();
         ByteArrayResource resource = new ByteArrayResource(bytes);

         return ResponseEntity.ok()
               .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + "Formulario de Transferencia.pdf")
               .contentType(MediaType.APPLICATION_PDF)
               .contentLength(bytes.length)
               .body(resource);
      } catch (IOException | JRException e) {
         // Manejo de excepciones comunes
         System.out.println("ERROR: " + e.getMessage());
         e.printStackTrace();
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Devolver un estado de error
      }
   }

   @GetMapping(value = "/optenerIdentFormulario/{id_carpeta}")
   public ResponseEntity<String> optenerIdentFormulario(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {
      System.out.println("AAAAAAAA");
      FormularioTransferencia formularioTransferencia = formularioTransferenciaService
            .formularioTransferenciaCarpeta(id_carpeta);

      return ResponseEntity.ok(String.valueOf(formularioTransferencia.getId_formularioTransferencia()));
   }

}
