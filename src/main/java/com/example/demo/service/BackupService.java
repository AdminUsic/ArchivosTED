package com.example.demo.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BackupService {
    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public void backupDatabase() throws IOException, InterruptedException {
        // Especifica el comando para pg_dump, ajustando la ruta del ejecutable según tu configuración
        String pgDumpCommand = "pg_dump";
        
        // Especifica el nombre de la base de datos que deseas hacer backup
        String databaseName = "tu_base_de_datos";
        
        // Especifica la ruta donde guardar el archivo de backup
        String backupFilePath = "/ruta/del/backup.sql";

        // Construye el comando completo
        String command = String.format("%s -h %s -p %d -U %s -d %s -f %s",
                pgDumpCommand,
                obtenerHost(),
                obtenerPuerto(),
                username,
                databaseName,
                backupFilePath);

        // Ejecuta el comando en un proceso
        Process process = Runtime.getRuntime().exec(command);

        // Espera a que el proceso termine
        if (process.waitFor(10, TimeUnit.MINUTES)) {
            // El backup se completó correctamente
            System.out.println("Backup completado con éxito.");
        } else {
            // El backup excedió el tiempo de espera
            System.err.println("Error: Tiempo de espera excedido.");
        }
    }

    private String obtenerHost() {
        // Analiza la URL de la base de datos para obtener el host
        return databaseUrl.split("//")[1].split(":")[0];
    }

    private int obtenerPuerto() {
        // Analiza la URL de la base de datos para obtener el puerto
        return Integer.parseInt(databaseUrl.split(":")[2].split("/")[0]);
    }
}
