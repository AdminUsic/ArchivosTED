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

import com.example.demo.configuracion.DatosConfig;
import com.example.demo.entity.Persona;
import com.example.demo.entity.Usuario;
import com.example.demo.service.UsuarioService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ConfigController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DatosConfig datosConfig;

    @GetMapping("/Backup-restauracion")
    public String BackupRestauracion(HttpServletRequest request, Model model) {
        // model.addAttribute("claveFile", secretKey);
        // model.addAttribute("clavePass", secretKey2);
        return "/datos/ventana";
    }

    @PostMapping(value = "/confirmacionUser")
    public ResponseEntity confirmacionUser(HttpServletRequest request, @RequestParam(value = "contraseña") String contraseña) {
        Usuario userLog = (Usuario) request.getSession().getAttribute("userLog");
        Usuario usuario = usuarioService.findOne(userLog.getId_usuario());
        
        if (usuario.getContraseña().equals(contraseña)) {
            Path projectPath = Paths.get("").toAbsolutePath();
            String archivo = projectPath
                    + "/src/main/java/com/example/demo/configuracion/dwad%A3#SAd$ASccwaS";
            try {
                // Crear instancia de FileWriter con la ruta del archivo
                FileWriter fileWriter = new FileWriter(archivo);

                // Crear instancia de BufferedWriter para escribir en el archivo
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                String secretKey = "Lanza12310099812";
                try {
                    String encryptedBytes = encrypt(secretKey, secretKey);
                    // Convertir los bytes encriptados a un String codificado en base64
                    bufferedWriter.write(encryptedBytes);
                    // archivo.setContenido(encryptedBytes);
                    System.out.println("Encriptacion Completa");
                } catch (Exception e) {
                    System.out.println("Error en la encriptacion: " + e);
                }
                // Escribir el contenido en el archivo
                //bufferedWriter.write("Lanza");

                // Cerrar el BufferedWriter para liberar recursos
                bufferedWriter.close();

                System.out.println("Contenido escrito exitosamente en el archivo.");

            } catch (IOException e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok("ingresa");
        } else {
            return ResponseEntity.ok("no ingresa");
        }
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

   /*  @PostMapping(value = "/cambiarkeyFile")
    public ResponseEntity cambiarkeyUser(HttpServletRequest request,
            @RequestParam(value = "newClave") String newClave) {

        if (request.getSession().getAttribute("userLog") != null) {

            datosConfig.setClaveEncriptFile(newClave);
            return ResponseEntity.ok("Se cambió la contraseña correctamente");
        } else {
            return ResponseEntity.ok("/ArchivosTED");
        }
    }*/
    @PostMapping(value = "/cambiarkeyFile")
    public ResponseEntity cambiarkeyUser(HttpServletRequest request,
            @RequestParam(value = "newClave") String newClave) {

        if (request.getSession().getAttribute("userLog") != null) {

            datosConfig.setClaveEncriptFile(newClave);
            return ResponseEntity.ok("Se cambió la contraseña correctamente");
        } else {
            return ResponseEntity.ok("/ArchivosTED");
        }
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

    // -------------ENCRIPTAR EL CONTRASEÑA -----------------

    private static String encrypt(String data, String secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // -----------------DESENCRIPTAR ------------------
    private static String decrypt(String encryptedText, String secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
