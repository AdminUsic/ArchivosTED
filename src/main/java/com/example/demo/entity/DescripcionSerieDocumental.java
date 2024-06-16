package com.example.demo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "descripcion_serie_documental")
public class DescripcionSerieDocumental implements Serializable {
    private static final long serialVersionUID = 2629195288020321924L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_descripcion_serie_documental;

    private String codigoSerie;
    private String unidadDocumental;
    private String fechaExtrema;
    private String metroLineal;
    private String estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_unidad")
    private Unidad unidad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_serie")
    private SerieDocumental serieDocumental;


}
