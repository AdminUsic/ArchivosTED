package com.example.demo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class UtilidadServiceProjectImpl implements UtilidadServiceProject {

  String secretKey = "Lanza12310099812"; // La clave debe tener 16, 24 o 32 caracteres para AES-128, AES-192 o
  // AES-256 respectivamente

  @Autowired
  private DataSource dataSource; // Inyectar el DataSource de Spring

  @Override
  public String decrypt(String encryptedText) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
    byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
    byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
    return new String(decryptedBytes, StandardCharsets.UTF_8);
  }

  @Override
  public String encrypt(String data) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
    byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  @Override
  public String fechaActualTexto() {
    Date fechaActual = new Date();

    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    String fechaActualStr = formatoFecha.format(fechaActual);

    String[] separarte = fechaActualStr.split("/");

    String fechaActualText = "";

    switch (Integer.parseInt(separarte[1])) {
      case 1:
        fechaActualText = fechaActualText + "Enero de ";
        break;
      case 2:
        fechaActualText = fechaActualText + "Febrero de ";
        break;
      case 3:
        fechaActualText = fechaActualText + "Marzo de ";
        break;
      case 4:
        fechaActualText = fechaActualText + "Abril de ";
        break;
      case 5:
        fechaActualText = fechaActualText + "Mayo de ";
        break;
      case 6:
        fechaActualText = fechaActualText + "Junio de ";
        break;
      case 7:
        fechaActualText = fechaActualText + "Julio de ";
        break;
      case 8:
        fechaActualText = fechaActualText + "Agosto de ";
        break;
      case 9:
        fechaActualText = fechaActualText + "Septiembre de ";
        break;
      case 10:
        fechaActualText = fechaActualText + "Octubre de ";
        break;
      case 11:
        fechaActualText = fechaActualText + "Noviembre de ";
        break;
      case 12:
        fechaActualText = fechaActualText + "Diciembre de ";
        break;

      default:
        break;
    }
    System.out.println("Fecha actual: " + fechaActualStr);
    return fechaActualText = separarte[0] + " de " + fechaActualText + separarte[2];
  }

  @Override
  public String fechaTexto(Date fecha) {

    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    String fechaActualStr = formatoFecha.format(fecha);

    String[] separarte = fechaActualStr.split("/");

    String fechaActualText = "";

    switch (Integer.parseInt(separarte[1])) {
      case 1:
        fechaActualText = fechaActualText + "Enero de ";
        break;
      case 2:
        fechaActualText = fechaActualText + "Febrero de ";
        break;
      case 3:
        fechaActualText = fechaActualText + "Marzo de ";
        break;
      case 4:
        fechaActualText = fechaActualText + "Abril de ";
        break;
      case 5:
        fechaActualText = fechaActualText + "Mayo de ";
        break;
      case 6:
        fechaActualText = fechaActualText + "Junio de ";
        break;
      case 7:
        fechaActualText = fechaActualText + "Julio de ";
        break;
      case 8:
        fechaActualText = fechaActualText + "Agosto de ";
        break;
      case 9:
        fechaActualText = fechaActualText + "Septiembre de ";
        break;
      case 10:
        fechaActualText = fechaActualText + "Octubre de ";
        break;
      case 11:
        fechaActualText = fechaActualText + "Noviembre de ";
        break;
      case 12:
        fechaActualText = fechaActualText + "Diciembre de ";
        break;

      default:
        break;
    }
    System.out.println("Fecha actual: " + fechaActualStr);
    return fechaActualText = separarte[0] + " de " + fechaActualText + separarte[2];
  }

  @Override
  public byte[] decrypt(byte[] encryptedData) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
    return cipher.doFinal(encryptedData);
  }

  @Override
  public byte[] encrypt(byte[] data) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
    return cipher.doFinal(data);
  }

  @Override
  public byte[] extraerIconPdf(MultipartFile file) throws Exception {
    byte[] contenido = file.getBytes();
    PDDocument document = PDDocument.load(contenido);

    // Guardar la primera página del PDF como imagen WebP
    BufferedImage firstPageImage = renderFirstPageAsImage(document);

    // Recortar la imagen para guardar solo la mitad superior
    BufferedImage upperHalfImage = cropUpperHalfImage(firstPageImage);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(upperHalfImage, "webp", baos);
    byte[] icono = baos.toByteArray();
    return icono;
  }

  private BufferedImage cropUpperHalfImage(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();

    // Recortar la imagen para guardar solo la mitad superior
    BufferedImage upperHalfImage = image.getSubimage(0, 0, width, height / 2);

    return upperHalfImage;
  }

  private BufferedImage renderFirstPageAsImage(PDDocument document) throws IOException {
    PDFRenderer renderer = new PDFRenderer(document);
    return renderer.renderImageWithDPI(0, 300); // Ajusta la resolución según tus necesidades
  }

  @Override
  public byte[] generarReporte(String rutaJasper, Map<String, Object> parametros) {
    Connection conexionBD = null;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    try {
      // Obtener una conexión a la base de datos
      conexionBD = dataSource.getConnection();

      // Llenar el informe con datos y obtener los bytes
      JasperPrint jasperPrint = JasperFillManager.fillReport(rutaJasper, parametros, conexionBD);
      net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);

      return byteArrayOutputStream.toByteArray();
    } catch (SQLException | JRException e) {
      System.out.println("ERROR: " + e.getMessage());
      e.printStackTrace();
      // Manejar errores aquí
      return null;
    } finally {
      // Cerrar la conexión a la base de datos en el bloque finally para garantizar su
      // cierre
      if (conexionBD != null) {
        try {
          conexionBD.close();
        } catch (SQLException e) {
          System.out.println("ERROR: " + e.getMessage());
          e.printStackTrace();
          // Manejar errores aquí
        }
      }
    }
  }

  @Override
  public ByteArrayOutputStream compilarAndExportarReporte(String nombreArchivo, Map<String, Object> params)
      throws IOException, JRException, SQLException {
    Connection con = null;

    // return stream;
    ByteArrayOutputStream stream = new ByteArrayOutputStream();

    Path rootPath = Paths.get("").toAbsolutePath();
    Path directorio = Paths.get(rootPath.toString(), "reportes", nombreArchivo);
    String ruta = directorio.toString();

    try (InputStream reportStream = new FileInputStream(ruta)) {
      con = dataSource.getConnection();

      JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, con);
      JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
    } catch (IOException | JRException | SQLException e) {
      System.out.println("ERROR: " + e.getMessage());
      e.printStackTrace();
    }
    con.close();
    return stream;

  }

}
