package com.example.demo.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "volumen")
public class Volumen implements Serializable{
    private static final long serialVersionUID = 2629195288020321924L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_volumen;

    private String nombre;

    private String estado;

    @OneToMany(mappedBy = "volumen")
    private List<Carpeta> carpetas;

}
