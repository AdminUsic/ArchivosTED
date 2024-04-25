package com.example.demo.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "unidad")
public class Unidad implements Serializable{
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_unidad;
    private String nombre;
    private String estado;

    @Transient
    private String sigla;

    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference // Marca esta parte de la relación como "gestionada"
    private List<Persona> personas;

    @OneToMany(mappedBy = "unidad")
    @JsonManagedReference // Marca esta parte de la relación como "gestionada"
    private List<Carpeta> carpetas;

    @OneToMany(mappedBy = "unidadPadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Marca esta parte de la relación como "gestionada"
    private List<Unidad> subUnidades;

    @OneToMany(mappedBy = "unidad")
    @JsonManagedReference // Marca esta parte de la relación como "gestionada"
    private List<Archivo> archivos;

    @OneToMany(mappedBy = "unidad")
    @JsonManagedReference // Marca esta parte de la relación como "gestionada"
    private List<FormularioTransferencia> formularioTransferencias;

    @ManyToOne
    @JoinColumn(name = "unidad_padre_id")
    @JsonBackReference // Marca esta parte de la relación como "referenciada"
    private Unidad unidadPadre;

    // @OneToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "id_persona")
    // private Persona responsable;

    public void beforeSaveOrUpdate() {
        nombre = nombre.toUpperCase();
        estado = estado.toUpperCase();
    }
}
