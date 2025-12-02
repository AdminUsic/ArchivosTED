package com.example.demo.entity;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carpeta")
public class Carpeta implements Serializable {
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_carpeta;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_volumen")
    private Volumen volumen;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_unidad")
    private Unidad unidad;

    private int gestion;

    @Column
    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;

    @Column
    @Temporal(TemporalType.TIME)
    private Date horaRegistro;

    private String estado;

    @OneToMany(mappedBy = "carpeta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Archivo> archivos;

    @JsonIgnore
    public List<Archivo> getArchivosOrdenados() {
        return archivos.stream() // Convierte la lista de asignaciones en un stream.
                .filter(archivo -> !"X".equals(archivo.getEstado())) // Filtra las asignaciones cuyo
                                                                             // estado no es ELIMINADO.
                .sorted(Comparator.comparing(Archivo::getFechaRegistro)) // Ordena las
                // asignaciones por el campo 'fecha'.
                .collect(Collectors.toList()); // Recoge el resultado en una lista.
    }

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_serie")
    private SerieDocumental serieDocumental;

    @OneToMany(mappedBy = "carpeta", fetch = FetchType.LAZY)
    private List<FormularioTransferencia> formularioTransferencias;

    @Transient
    private int TotalArchivo;

}
