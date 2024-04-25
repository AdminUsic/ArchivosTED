package com.example.demo.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Persona;
import com.example.demo.entity.Usuario;
import com.example.demo.service.UsuarioService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ConfigController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/Backup-restauracion")
    public String BackupRestauracion(HttpServletRequest request, Model model) {
        // model.addAttribute("claveFile", secretKey);
        // model.addAttribute("clavePass", secretKey2);
        return "/datos/ventana";
    }

    @PostMapping(value = "/keyFile")
    public ResponseEntity keyFile(HttpServletRequest request) {
        // TODO: process POST request
        return ResponseEntity.ok("secretKey");
    }

    @PostMapping(value = "/keyPassw")
    public ResponseEntity keyPassw(HttpServletRequest request) {
        // TODO: process POST request
        return ResponseEntity.ok("secretKey2");
    }


    @GetMapping("/descargar-backup")
    public ResponseEntity<FileSystemResource> descargarBackup(@RequestParam(value = "contraseña") String contraseña)
            throws IOException {
        // Especifica la ruta del archivo de backup
        String backupFilePath = "/ruta/del/backup.sql";

        // Crea un objeto FileSystemResource para representar el archivo
        FileSystemResource file = new FileSystemResource(new File(backupFilePath));

        // Construye la respuesta con el archivo adjunto
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.contentLength())
                .body(file);
    }

}
