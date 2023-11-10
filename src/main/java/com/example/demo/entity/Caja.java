package com.example.demo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "caja")
public class Caja implements Serializable {
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_caja;
    
    private int NroCaja;
    private String tituloDoc;
    private int gestion;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_volumen")
    private Volumen volumen;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cubierta")
    private Cubierta cubierta;

    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_formularioTransferencia")
    private FormularioTransferencia formularioTransferencia;

    private String notas;

    private String estado;
}
