package com.example.demo.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "miapp")
public class DatosConfig {
    @Value("${miapp.claveEncriptFile}")
    private String claveEncriptFile;

    @Value("${miapp.claveEncriptPassword}")
    private String claveEncriptPassword;

    // Otros métodos y configuraciones

    public String getClaveEncriptFile() {
        return claveEncriptFile;
    }

    public void setClaveEncriptFile(String claveEncriptFile) {
        this.claveEncriptFile = claveEncriptFile;
    }

    public String getClaveEncriptPassword() {
        return claveEncriptPassword;
    }

    public void setClaveEncriptPassworde(String claveEncriptPassword) {
        this.claveEncriptPassword = claveEncriptPassword;
    }
}
