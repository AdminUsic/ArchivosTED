package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import net.sf.jasperreports.engine.JRException;

public interface UtilidadServiceProject {

  String decrypt(String encryptedText) throws Exception;

  String encrypt(String data) throws Exception;

  String fechaActualTexto();

  String fechaTexto(Date fecha);

  byte[] decrypt(byte[] encryptedData) throws Exception;

  byte[] encrypt(byte[] data) throws Exception;

  byte[] extraerIconPdf(MultipartFile file) throws Exception;

  byte[] generarReporte(String rutaJasper, Map<String, Object> parametros);

  ByteArrayOutputStream compilarAndExportarReporte(String ruta, Map<String, Object> params) throws IOException, JRException, SQLException;
}
