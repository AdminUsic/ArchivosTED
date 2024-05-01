package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;

import com.example.demo.entity.Archivo;
import com.example.demo.entity.Carpeta;
import com.example.demo.entity.Control;
import com.example.demo.entity.Usuario;
import com.example.demo.service.TipoArchivoService;
import com.example.demo.service.TipoControService;
import com.example.demo.service.UnidadService;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.UtilidadServiceProject;
import com.example.demo.service.VolumenService;
import com.example.demo.service.ArchivoService;
import com.example.demo.service.CarpetaService;
import com.example.demo.service.ControlService;
import com.example.demo.service.PersonaService;
import com.example.demo.service.SerieDocumentalService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Controller
public class ArchivoController {

   @Autowired
   private ArchivoService archivoService;

   @Autowired
   private TipoArchivoService tipoArchivoService;

   @Autowired
   private PersonaService personaService;

   @Autowired
   private CarpetaService carpetaService;

   @Autowired
   private UnidadService unidadService;

   @Autowired
   private VolumenService volumenService;

   @Autowired
   private SerieDocumentalService documentalService;

   @Autowired
   private UsuarioService usuarioService;

   @Autowired
   private TipoControService tipoControService;

   @Autowired
   private ControlService controService;

   @Autowired
   private UtilidadServiceProject utilidadService;

   @GetMapping("/DOCUMENTOS")
   public String ventanaDocumentos(HttpServletRequest request, Model model) {

      return "/archivos/registrar";
   }

   @PostMapping(value = "/NuevoRegistroArchivo")
   public String NuevoRegistroArchivo(HttpServletRequest request, Model model) {
      model.addAttribute("archivo", new Archivo());
      model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
      model.addAttribute("carpetas", carpetaService.findAll());
      model.addAttribute("personas", personaService.findAll());
      model.addAttribute("seccionesDocumental", unidadService.findAll());
      return "/archivos/formulario";
   }

   @PostMapping(value = "/RegistrosArchivos")
   public String tablaRegistros(HttpServletRequest request, Model model) {
      model.addAttribute("listaArchivos", archivoService.findAll());
      return "/archivos/tablaRegistros";
   }

   @PostMapping(value = "/RegistrosArchivosCarpeta/{id_archivo}")
   public String RegistrosArchivosCarpeta(HttpServletRequest request, Model model,
         @PathVariable("id_archivo") Long id_archivo) {
      model.addAttribute("listaArchivos", archivoService.archivosCarpeta(id_archivo));
      return "/archivos/tablaRegistros";
   }

   @PostMapping(value = "/SubSecciones/{id_carpeta}")
   public ResponseEntity<String[][]> SubSecciones(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {
      Carpeta carpeta = carpetaService.findOne(id_carpeta);
      String subSecciones[][] = new String[carpeta.getUnidad().getSubUnidades().size()][2];

      for (int i = 0; i < subSecciones.length; i++) {
         int j = 0;
         subSecciones[i][j] = carpeta.getUnidad().getSubUnidades().get(i).getId_unidad().toString();
         j++;
         subSecciones[i][j] = carpeta.getUnidad().getSubUnidades().get(i).getNombre();
      }

      return ResponseEntity.ok(subSecciones);
   }

//    @PostMapping("/GuardarRegistroArchivo")
//    public ResponseEntity<String> GuardarRegistroArchivo(HttpServletRequest request, Model model,
//          @RequestParam("PDF") MultipartFile archivoPdf, @Validated Archivo archivo,
//          @RequestParam(value = "id_carpeta", required = false) Long id_carpeta) {
//       System.out.println("METODO REGISTRAR ARCHIVO");
//       Usuario us = (Usuario) request.getSession().getAttribute("userLog");
//       Usuario userLog = usuarioService.findOne(us.getId_usuario());
//       try {
//          byte[] contenido = archivoPdf.getBytes();
//          System.out.println("Tamaño del arreglo de bytes: " + contenido.length);

//          try {
//             byte[] encryptedBytes = utilidadService.encrypt(contenido);
//             // Convertir los bytes encriptados a un String codificado en base64
//             archivo.setContenido(encryptedBytes);
//             System.out.println("Encriptacion Completa");
//          } catch (Exception e) {
//             System.out.println("Error en la encriptacion: " + e);
//          }
//          if (id_carpeta != null) {
//             Carpeta carpeta = carpetaService.findOne(id_carpeta);
//             archivo.setCarpeta(carpeta);
//          }

//          PDDocument document = PDDocument.load(contenido);
//          archivo.setCantidadHojas(document.getNumberOfPages());
//          // archivo.setFechaEmision(fechaEmision);
//          archivo.setFechaRegistro(new Date());
//          archivo.setHoraRegistro(new Date());
//          archivo.setEstado("A");
//          String nombFile = archivoPdf.getOriginalFilename();
//          // String[] extension = nombFile.split(".");

//          // archivo.setExtension(extension[extension.length - 1]);
//          String[] extension = nombFile.split("\\.");
// archivo.setExtension(extension[extension.length - 1]);


//          document.close();
//          archivoService.save(archivo);
//          Control control = new Control();
//          control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
//          control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
//                + " de una nueva documental "+archivo.getNombre());
//          control.setUsuario(userLog);
//          control.setFecha(new Date());
//          control.setHora(new Date());
//          controService.save(control);
//          return ResponseEntity.ok("Se ha Registrado el Archivo correctamente");
//       } catch (IOException e) {
//          System.out.println("ERROR REGISTRAR ARCHIVO: " + e);
//          return ResponseEntity.ok("ERROR AL REGISTRAR ARCHIVO");
//       }
//    }
// @PostMapping("/GuardarRegistroArchivo")
// public ResponseEntity<String> GuardarRegistroArchivo(HttpServletRequest request, Model model,
//      @RequestParam("PDF") MultipartFile archivoPdf, @Validated Archivo archivo,
//      @RequestParam(value = "id_carpeta", required = false) Long id_carpeta) {
//   System.out.println("METODO REGISTRAR ARCHIVO");
//   Usuario us = (Usuario) request.getSession().getAttribute("userLog");
//   Usuario userLog = usuarioService.findOne(us.getId_usuario());
//   try {
//      byte[] contenido = archivoPdf.getBytes();
//      System.out.println("Tamaño del arreglo de bytes: " + contenido.length);

//      try {
//         byte[] encryptedBytes = utilidadService.encrypt(contenido);
//         // Convertir los bytes encriptados a un String codificado en base64
//         archivo.setContenido(encryptedBytes);
//         System.out.println("Encriptacion Completa");
//      } catch (Exception e) {
//         System.out.println("Error en la encriptacion: " + e);
//      }
//      if (id_carpeta != null) {
//         Carpeta carpeta = carpetaService.findOne(id_carpeta);
//         archivo.setCarpeta(carpeta);
//      }

//      PDDocument document = PDDocument.load(contenido);
//      archivo.setCantidadHojas(document.getNumberOfPages());
//      // archivo.setFechaEmision(fechaEmision);
//      archivo.setFechaRegistro(new Date());
//      archivo.setHoraRegistro(new Date());
//      archivo.setEstado("A");
//      String nombFile = archivoPdf.getOriginalFilename();
//      archivo.setExtension(getFileExtension(nombFile));

//      // Guardar la primera página del PDF como imagen WebP
//      BufferedImage firstPageImage = renderFirstPageAsImage(document);
//      ByteArrayOutputStream baos = new ByteArrayOutputStream();
//      ImageIO.write(firstPageImage, "webp", baos);
//      byte[] icono = baos.toByteArray();
//      archivo.setIcono(icono);

//      document.close();
//      archivoService.save(archivo);
//      Control control = new Control();
//      control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
//      control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
//            + " de una nueva documental "+archivo.getNombre());
//      control.setUsuario(userLog);
//      control.setFecha(new Date());
//      control.setHora(new Date());
//      controService.save(control);
//      return ResponseEntity.ok("Se ha Registrado el Archivo correctamente");
//   } catch (IOException e) {
//      System.out.println("ERROR REGISTRAR ARCHIVO: " + e);
//      return ResponseEntity.ok("ERROR AL REGISTRAR ARCHIVO");
//   }
// }
@PostMapping("/GuardarRegistroArchivo")
public ResponseEntity<String> GuardarRegistroArchivo(HttpServletRequest request, Model model,
                                                     @RequestParam("PDF") MultipartFile archivoPdf, @Validated Archivo archivo,
                                                     @RequestParam(value = "id_carpeta", required = false) Long id_carpeta) {
    System.out.println("METODO REGISTRAR ARCHIVO");
    Usuario us = (Usuario) request.getSession().getAttribute("userLog");
    Usuario userLog = usuarioService.findOne(us.getId_usuario());
    try {
        byte[] contenido = archivoPdf.getBytes();
        System.out.println("Tamaño del arreglo de bytes: " + contenido.length);

        try {
            byte[] encryptedBytes = utilidadService.encrypt(contenido);
            // Convertir los bytes encriptados a un String codificado en base64
            archivo.setContenido(encryptedBytes);
            System.out.println("Encriptacion Completa");
        } catch (Exception e) {
            System.out.println("Error en la encriptacion: " + e);
        }
        if (id_carpeta != null) {
            Carpeta carpeta = carpetaService.findOne(id_carpeta);
            archivo.setCarpeta(carpeta);
        }

        PDDocument document = PDDocument.load(contenido);
        archivo.setCantidadHojas(document.getNumberOfPages());
        // archivo.setFechaEmision(fechaEmision);
        archivo.setFechaRegistro(new Date());
        archivo.setHoraRegistro(new Date());
        archivo.setEstado("A");
        String nombFile = archivoPdf.getOriginalFilename();
        archivo.setExtension(getFileExtension(nombFile));

        // Guardar la primera página del PDF como imagen WebP
        BufferedImage firstPageImage = renderFirstPageAsImage(document);

        // Recortar la imagen para guardar solo la mitad superior
        BufferedImage upperHalfImage = cropUpperHalfImage(firstPageImage);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(upperHalfImage, "webp", baos);
        byte[] icono = baos.toByteArray();
        archivo.setIcono(icono);

        document.close();
        archivoService.save(archivo);
        Control control = new Control();
        control.setTipoControl(tipoControService.findAllByTipoControl("Registro"));
        control.setDescripcion("Realizó un nuevo " + control.getTipoControl().getNombre()
                + " de una nueva documental " + archivo.getNombre());
        control.setUsuario(userLog);
        control.setFecha(new Date());
        control.setHora(new Date());
        controService.save(control);
        return ResponseEntity.ok("Se ha Registrado el Archivo correctamente");
    } catch (IOException e) {
        System.out.println("ERROR REGISTRAR ARCHIVO: " + e);
        return ResponseEntity.ok("ERROR AL REGISTRAR ARCHIVO");
    }
}

private BufferedImage cropUpperHalfImage(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();

    // Recortar la imagen para guardar solo la mitad superior
    BufferedImage upperHalfImage = image.getSubimage(0, 0, width, height / 2);

    return upperHalfImage;
}

private String getFileExtension(String filename) {
    int lastDotIndex = filename.lastIndexOf(".");
    if (lastDotIndex != -1) {
        return filename.substring(lastDotIndex + 1);
    }
    return "";
}

// private BufferedImage renderFirstPageAsImage(PDDocument document) throws IOException {
//     PDFRenderer renderer = new PDFRenderer(document);
//     return renderer.renderImageWithDPI(0, 300); // Ajusta la resolución según tus necesidades
// }

   @GetMapping(value = "/ModArchivo/{id_archivo}")
   public String EditarArchivo(HttpServletRequest request, Model model,
         @PathVariable("id_archivo") Long id_archivo) {
      System.out.println("EDITAR ARCHIVOS");
      model.addAttribute("archivo", archivoService.findOne(id_archivo));
      model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
      model.addAttribute("carpetas", carpetaService.findAll());
      model.addAttribute("personas", personaService.findAll());
      model.addAttribute("seccionesDocumental", unidadService.findAll());
      model.addAttribute("edit", "true");
      return "/archivos/formulario";
   }

   @GetMapping(value = "/ModArchivoModal/{id_archivo}")
   public String EditarArchivoModal(HttpServletRequest request, Model model,
         @PathVariable("id_archivo") Long id_archivo) {
      System.out.println("EDITAR ARCHIVOS");
      model.addAttribute("archivo", archivoService.findOne(id_archivo));
      model.addAttribute("TiposArchivos", tipoArchivoService.findAll());
      model.addAttribute("carpetas", carpetaService.findAll());
      model.addAttribute("personas", personaService.findAll());
      model.addAttribute("seccionesDocumental", unidadService.findAll());
      model.addAttribute("edit", "true");
      return "/archivos/formularioModal";
   }

   @GetMapping(value = "/ContenidoArchivo/{id_archivo}")
   public String ContenidoArchivo(HttpServletRequest request, Model model,
         @PathVariable("id_archivo") Long id_archivo) {
      System.out.println("EDITAR ARCHIVOS");
      model.addAttribute("archivo", archivoService.findOne(id_archivo));
      model.addAttribute("edit", "true");
      return "/archivos/archivo";
   }

   @PostMapping(value = "/ModArchivoG")
   public ResponseEntity<String> ModArchivoG(HttpServletRequest request, Model model,
         @RequestParam(value = "PDF", required = false) MultipartFile archivoPdf, Archivo archivo,
         @RequestParam(value = "id_carpeta", required = false) Long id_carpeta,
         @RequestParam("id_archivo") Long id_archivo) {
      System.out.println("METODO MODIFICAR ARCHIVO");
      Usuario us = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(us.getId_usuario());
      Archivo archivoAntes = archivoService.findOne(id_archivo);
      try {
         byte[] contenido = archivoPdf.getBytes();
         System.out.println("Tamaño del arreglo de bytes: " + contenido.length);
         if (archivoPdf.isEmpty()) {
            archivo.setContenido(archivoAntes.getContenido());
            archivo.setCantidadHojas(archivoAntes.getCantidadHojas());
         } else if (!archivoPdf.isEmpty()) {
            try {
               // archivo.setContenido(encrypt(contenido, "Lanza12310099812"));
               archivo.setContenido(utilidadService.encrypt(contenido));
            } catch (Exception e) {
               System.out.println("ERROR EN LA ENCRIPTACION DEL ARCHIVO MODIFICADO: " + e);
            }
            PDDocument document = PDDocument.load(contenido);

            archivo.setCantidadHojas(document.getNumberOfPages());
            document.close();
         }
         // System.out.println("Fecha de emision: " + fechaEmision);
         if (id_carpeta != null) {
            Carpeta carpeta = carpetaService.findOne(id_carpeta);
            archivo.setCarpeta(carpeta);
         }

         archivo.setFechaRegistro(archivoAntes.getFechaRegistro());
         archivo.setHoraRegistro(archivo.getHoraRegistro());
         archivo.setEstado("A");

         archivoService.save(archivo);
         System.out.println("ARCHIVO MODIFICADO CORRECTAMENTE");
         Control control = new Control();
         control.setTipoControl(tipoControService.findAllByTipoControl("Modificación"));
         control.setDescripcion("Realizó una nueva " + control.getTipoControl().getNombre()
               + " de la nueva documental "+archivo.getNombre());
         control.setUsuario(userLog);
         control.setFecha(new Date());
         control.setHora(new Date());
         controService.save(control);
         return ResponseEntity.ok("Se ha Modificado el Archivo correctamente");
      } catch (IOException e) {
         System.out.println("ERROR MODIFICAR ARCHIVO: " + e);
         return ResponseEntity.ok("ERROR MODIFICAR EL ARCHIVO");
      }
   }

   @PostMapping(value = "/EliminarRegistroArchivo/{id_archivo}")
   @ResponseBody
   public void EliminarRegistroArchivo(HttpServletRequest request, Model model,
         @PathVariable("id_archivo") Long id_archivo) {
      System.out.println("Eliminar ARCHIVOS");
      Usuario us = (Usuario) request.getSession().getAttribute("userLog");
      Usuario userLog = usuarioService.findOne(us.getId_usuario());
      Archivo archivo = archivoService.findOne(id_archivo);
      /*
       * Archivo archivo = archivoService.findOne(id_archivo);
       * archivo.setEstado("X");
       * archivoService.save(archivo);
       */
      archivoService.delete(id_archivo);
      Control control = new Control();
      control.setTipoControl(tipoControService.findAllByTipoControl("Eliminó"));
      control.setDescripcion(control.getTipoControl().getNombre()
               + " una unidad documental "+archivo.getNombre());
      control.setUsuario(userLog);
      control.setFecha(new Date());
      control.setHora(new Date());
      controService.save(control);
   }

   @GetMapping("/verPdf/{id}")
   public ResponseEntity<ByteArrayResource> verPdf(@PathVariable Long id) {
      Optional<Archivo> archivoOptional = archivoService.findOneOptional(id);

      if (archivoOptional.isPresent()) {
         Archivo archivo = archivoOptional.get();
         // String secretKey = "Lanza12310099812";
         byte[] contenidoDescencryptado;
         try {
            contenidoDescencryptado = utilidadService.decrypt(archivo.getContenido());
            archivo.setContenido(contenidoDescencryptado);
         } catch (Exception e) {
            // TODO: handle exception
         }
         ByteArrayResource resource = new ByteArrayResource(archivo.getContenido());

         return ResponseEntity.ok()
               .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + archivo.getNombre() + ".pdf")
               .contentType(MediaType.APPLICATION_PDF)
               .contentLength(archivo.getContenido().length)
               .body(resource);
      } else {
         return ResponseEntity.notFound().build();
      }
   }

   // @GetMapping("/verIcoPdf/{id}")
   // public ResponseEntity<byte[]> verIcoPdf(@PathVariable Long id) {
   //    Optional<Archivo> archivoOptional = archivoService.findOneOptional(id);

   //    if (archivoOptional.isPresent()) {
   //       Archivo archivo = archivoOptional.get();

   //       try {
   //          // String secretKey = "Lanza12310099812";
   //          // Cargar el documento PDF
   //          byte[] contenidoDescencryptado;
   //          try {
   //             contenidoDescencryptado = utilidadService.decrypt(archivo.getContenido());
   //             archivo.setContenido(contenidoDescencryptado);
   //          } catch (Exception e) {
   //             // Manejo de excepción (por ejemplo, log)
   //          }
   //          PDDocument document = PDDocument.load(archivo.getContenido());

   //          // Obtener el renderizador PDF
   //          PDFRenderer renderer = new PDFRenderer(document);

   //          // Convertir la primera página a imagen con una resolución de 300 DPI
   //          BufferedImage image = renderer.renderImageWithDPI(0, 300); // Ajusta la resolución según tus necesidades

   //          // Redimensionar la imagen a 300 píxeles de ancho (ajusta según tus necesidades)
   //          int newWidth = 300;
   //          int newHeight = (int) (image.getHeight() * ((double) newWidth / image.getWidth()));

   //          BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
   //          Graphics2D g = resizedImage.createGraphics();
   //          g.drawImage(image, 0, 0, newWidth, newHeight, null);
   //          g.dispose();

   //          // Crear un flujo de bytes para la imagen redimensionada
   //          ByteArrayOutputStream baos = new ByteArrayOutputStream();

   //          // Guardar la imagen redimensionada en formato WebP
   //          ImageIO.write(resizedImage, "webp", baos);

   //          baos.flush();
   //          byte[] imageBytes = baos.toByteArray();
   //          baos.close();
   //          document.close();

   //          // Configurar los encabezados para evitar el caché
   //          HttpHeaders headers = new HttpHeaders();
   //          headers.setCacheControl("no-cache, no-store, must-revalidate");
   //          headers.setPragma("no-cache");
   //          headers.setExpires(0);

   //          // Utilizar MediaTypeFactory para crear un tipo de medio personalizado para WebP
   //          MediaType mediaType = MediaTypeFactory.getMediaType("image/webp")
   //                .orElse(MediaType.APPLICATION_OCTET_STREAM);

   //          return ResponseEntity.ok()
   //                .headers(headers)
   //                .contentType(mediaType) // Utiliza el tipo de medio personalizado para WebP
   //                .contentLength(imageBytes.length)
   //                .body(imageBytes);
   //       } catch (IOException e) {
   //          e.printStackTrace();
   //          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
   //       }
   //    } else {
   //       return ResponseEntity.notFound().build();
   //    }
   // }
   // @GetMapping("/verIcoPdf/{id}")
   //  public ResponseEntity<byte[]> verIcoPdf(@PathVariable Long id) {
   //      Optional<Archivo> archivoOptional = archivoService.findOneOptional(id);

   //      if (archivoOptional.isPresent()) {
   //          Archivo archivo = archivoOptional.get();

   //          try {
   //              byte[] contenidoDescencriptado;
   //              try {
   //                  contenidoDescencriptado = utilidadService.decrypt(archivo.getContenido());
   //                  archivo.setContenido(contenidoDescencriptado);
   //              } catch (Exception e) {
   //                  // Manejo de excepción (por ejemplo, log)
   //              }

   //              PDDocument document = PDDocument.load(archivo.getContenido());
   //              BufferedImage image = renderFirstPageAsImage(document);

   //              ByteArrayOutputStream baos = new ByteArrayOutputStream();
   //              ImageIO.write(image, "webp", baos);
   //              byte[] imageBytes = baos.toByteArray();
   //              baos.close();
   //              document.close();

   //              HttpHeaders headers = new HttpHeaders();
   //              headers.setCacheControl("no-cache, no-store, must-revalidate");
   //              headers.setPragma("no-cache");
   //              headers.setExpires(0);

   //              MediaType mediaType = MediaTypeFactory.getMediaType("image/webp")
   //                      .orElse(MediaType.APPLICATION_OCTET_STREAM);

   //              return ResponseEntity.ok()
   //                      .headers(headers)
   //                      .contentType(mediaType)
   //                      .contentLength(imageBytes.length)
   //                      .body(imageBytes);
   //          } catch (IOException e) {
   //              e.printStackTrace();
   //              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
   //          }
   //      } else {
   //          return ResponseEntity.notFound().build();
   //      }
   //  }
   @GetMapping("/verIcoPdf/{id}")
    public ResponseEntity<byte[]> verIcoPdf(@PathVariable Long id) {
        Optional<Archivo> archivoOptional = archivoService.findOneOptional(id);

        if (archivoOptional.isPresent()) {
         Archivo archivo = archivoOptional.get();

         // Configurar los encabezados para evitar el caché
         HttpHeaders headers = new HttpHeaders();
         headers.setCacheControl("no-cache, no-store, must-revalidate");
         headers.setPragma("no-cache");
         headers.setExpires(0);

         return ResponseEntity.ok()
                 .headers(headers)
                 .contentType(MediaType.IMAGE_PNG)
                 .contentLength(archivo.getIcono().length)
                 .body(archivo.getIcono());
     } else {
         return ResponseEntity.notFound().build();
     }
    }

    private BufferedImage renderFirstPageAsImage(PDDocument document) throws IOException {
        PDFRenderer renderer = new PDFRenderer(document);
        return renderer.renderImageWithDPI(0, 300); // Ajusta la resolución según tus necesidades
    }

   @GetMapping("/CantArchivosCarpeta/{id_carpeta}")
   public String CantArchivosCarpeta(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {
      int cantidad = archivoService.archivosCarpeta(id_carpeta).size();
      model.addAttribute("cantidadArchivo", cantidad);
      return "/carpetas/tablaRegistros";
   }

   @GetMapping("/verReporte")
   public ResponseEntity<ByteArrayResource> verPdf(Model model, HttpServletRequest request)
         throws DocumentException, MalformedURLException, IOException {
      byte[] bytes = generatePdf();

      ByteArrayResource resource = new ByteArrayResource(bytes);

      return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + "Reporte de lista de Archivos.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(bytes.length)
            .body(resource);
   }

   public byte[] generatePdf() throws DocumentException, MalformedURLException, IOException {

      // Crear un objeto Document con tamaño carta (Letter)
      Document document = new Document(new Rectangle(612, 792));

      // Crear un flujo de bytes para almacenar el PDF generado
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      // Crear un escritor de PDF utilizando el flujo de bytes
      PdfWriter writer = PdfWriter.getInstance(document, outputStream);

      // Abrir el documento
      document.open();

      // Obtener el contenido de la página actual
      PdfContentByte content = writer.getDirectContent();

      String nombreArchivo = "Logo-TED-Pando-Actual-1-1536x1094.png"; // Nombre del archivo o ruta relativa
      String filePath = Paths.get("").toAbsolutePath().toString() + "/src/main/resources/static/logo/";

      File archivo = new File(filePath + nombreArchivo);
      String rutaPlantilla = archivo.getAbsolutePath();
      System.out.println("rutaAbsoluta: " + rutaPlantilla);
      // Agregar la imagen de fondo
      Image background = Image.getInstance(rutaPlantilla);
      background.scaleAbsolute(document.getPageSize());
      background.setAbsolutePosition(0, 0); // Establecer posición absoluta en (0, 0)
      content.addImage(background);

      // Agregar un párrafo con el encabezado
      Paragraph header = new Paragraph("Encabezado del PDF");
      header.setAlignment(Element.ALIGN_CENTER);
      document.add(header);

      // Agregar un párrafo vacío para el salto de línea
      Paragraph emptyParagraph = new Paragraph();
      emptyParagraph.add(" ");
      document.add(emptyParagraph);

      // Crear una tabla con 4 columnas
      PdfPTable tablaArchivos = new PdfPTable(4);

      // Agregar encabezados de columna
      List<Archivo> archivos = archivoService.findAll();

      Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

      PdfPCell cell1 = new PdfPCell(new Paragraph("Nombre del Archivo", headerFont));
      PdfPCell cell2 = new PdfPCell(new Paragraph("Descripcion", headerFont));
      PdfPCell cell3 = new PdfPCell(new Paragraph("Tipo de Archivo", headerFont));
      PdfPCell cell4 = new PdfPCell(new Paragraph("Ruta Fisica", headerFont));

      cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

      tablaArchivos.addCell(cell1);
      tablaArchivos.addCell(cell2);
      tablaArchivos.addCell(cell3);
      tablaArchivos.addCell(cell4);

      // Agregar filas a la tabla
      Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
      for (Archivo archivo2 : archivos) {
         tablaArchivos.addCell(archivo2.getNombre());
         tablaArchivos.addCell(archivo2.getDescripcion());
         tablaArchivos.addCell(archivo2.getTipoArchivo().getNombre_tipo());
         tablaArchivos.addCell(archivo2.getRutaFisica());
      }

      // Agregar la tabla al documento
      document.add(tablaArchivos);

      // Cerrar el documento
      document.close();

      // Obtener los bytes del flujo de bytes
      byte[] pdfBytes = outputStream.toByteArray();

      return pdfBytes;
   }

   // ******************** SECCION BUSQUEDAS **********************************
   @GetMapping("/BusquedaArchivo")
   public String BusquedaArchivo(HttpServletRequest request, Model model) {
      model.addAttribute("unidades", unidadService.findAll());
      model.addAttribute("volumenes", volumenService.findAll());
      model.addAttribute("seriesDocs", documentalService.findAll());
      model.addAttribute("carpetas", carpetaService.findAll());
      System.out.println("VENTANA DE BUSQUEDAS");
      return "/busquedas/busquedas";
   }

   /*
    * @PostMapping("/buscarCarpeta")
    * public String buscarCarpeta(HttpServletRequest request, Model model,
    * 
    * @RequestParam(value = "unidadCarpeta", required = false)Long unidadCarpeta,
    * 
    * @RequestParam(value = "volumenCarpeta", required = false)Long volumenCarpeta,
    * 
    * @RequestParam(value = "seriesDocsCarpeta", required = false)Long
    * seriesDocsCarpeta,
    * 
    * @RequestParam(value = "gestion", required = false)int gestionCarpeta) {
    * System.out.println("BUSQUEDAS DE CARPETA");
    * 
    * if (unidadCarpeta!=null) {
    * // model.addAttribute("ListCarpetas",
    * carpetaService.buscarPorUnidad(unidadCarpeta));
    * model.addAttribute("ListCarpetas",
    * unidadService.findOne(unidadCarpeta).getCarpetas());
    * } else if (volumenCarpeta!=null) {
    * model.addAttribute("ListCarpetas",
    * volumenService.findOne(volumenCarpeta).getCarpetas());
    * } else if (seriesDocsCarpeta!=null) {
    * model.addAttribute("ListCarpetas",
    * documentalService.findOne(seriesDocsCarpeta).getCarpetas());
    * } else if (gestionCarpeta!=0 && volumenCarpeta!=null) {
    * model.addAttribute("ListCarpetas",
    * carpetaService.buscarPorUnidadGestion(unidadCarpeta, gestionCarpeta));
    * //break;
    * } else if (gestionCarpeta!=0) {
    * model.addAttribute("ListCarpetas",
    * carpetaService.buscarPorGestion(gestionCarpeta));
    * }
    * 
    * return "/busquedas/registrosCarpeta";
    * }
    */

   void siglas(List<Carpeta> listaCarpetas) {
      // List<Carpeta> listaCarpetas = new ArrayList<>();
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
      // model.addAttribute("ListCarpetas", listaCarpetas);
   }

   @PostMapping(value = "/AbrirCarpetaEncontrada/{id_carpeta}")
   public String AbrirCarpetaEncontrada(HttpServletRequest request, Model model,
         @PathVariable("id_carpeta") Long id_carpeta) {

      Carpeta carpeta = new Carpeta();
      carpeta = carpetaService.findOne(id_carpeta);
      carpeta.setTotalArchivo(cantidadTotalHojasCarpeta(carpeta));
      model.addAttribute("carpeta", carpeta);

      return "/busquedas/archivoCarpeta";
   }

   int cantidadTotalHojasCarpeta(Carpeta carpeta) {
      int sumarCant = 0;
      for (int j = 0; j < carpeta.getArchivos().size(); j++) {
         sumarCant = sumarCant + carpeta.getArchivos().get(j).getCantidadHojas();
      }
      return sumarCant;
   }

}
