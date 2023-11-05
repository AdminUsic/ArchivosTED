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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "serieDocumental")
public class SerieDocumental implements Serializable {
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_serie;

    private String nombre;

    private String estado;

    @OneToMany(mappedBy = "serieDocumental")
    private List<Carpeta> carpetas;

    @OneToMany(mappedBy = "seriePadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SerieDocumental> subSeries;

    @OneToMany(mappedBy = "serieDocumental", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Archivo> archivos;

    @ManyToOne
    @JoinColumn(name = "serie_padre_id")
    private SerieDocumental seriePadre;

}
