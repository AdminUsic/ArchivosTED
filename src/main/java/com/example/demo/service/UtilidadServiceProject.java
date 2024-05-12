package com.example.demo.service;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

public interface UtilidadServiceProject {

  String decrypt(String encryptedText) throws Exception;

  String encrypt(String data) throws Exception;

  String fechaActualTexto();

  String fechaTexto(Date fecha);

  byte[] decrypt(byte[] encryptedData) throws Exception;

  byte[] encrypt(byte[] data) throws Exception;

  byte[] extraerIconPdf(MultipartFile file) throws Exception;

}
