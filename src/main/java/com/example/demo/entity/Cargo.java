package com.example.demo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

//import org.springframework.data.annotation.Id;
/*import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;*/

import lombok.Getter;
import lombok.Setter;

//@Document(collection = "cargo")

@Getter
@Setter
@Entity
@Table(name = "cargo")
public class Cargo implements Serializable {
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_cargo;
    private String nombre;
    private String estado;

    @PrePersist
    @PreUpdate
    public void beforeSaveOrUpdate() {
        nombre = nombre.toUpperCase();
        estado = estado.toUpperCase();
    }
}
