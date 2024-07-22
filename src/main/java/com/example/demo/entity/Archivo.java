package com.example.demo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "archivo")
public class Archivo implements Serializable {
    private static final long serialVersionUID = 2629195288020321924L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_archivo;

    private Long NroDocumental;

    private String nombre;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipoArchivo")
    private TipoArchivo tipoArchivo;

    private byte[] contenido;

    private byte[] icono;

    private int cantidadHojas;

    private String rutaFisica;

    private String nota;

    private int gestion;

    private String estado;

    @Column
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date fechaRegistro;

    @Column
    @Temporal(TemporalType.TIME)
    private Date horaRegistro;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_carpeta")
    private Carpeta carpeta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_unidad")
    private Unidad unidad;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cubierta")
    private Cubierta cubierta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_serie")
    private SerieDocumental serieDocumental;

    // Otros atributos de archivo
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @PrePersist
    @PreUpdate
    public void beforeSaveOrUpdate() {
        nombre = nombre.toUpperCase();
    }

}
