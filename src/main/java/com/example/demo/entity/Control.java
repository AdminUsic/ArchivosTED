package com.example.demo.entity;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity 
@Table(name = "control")
public class Control implements Serializable{
  private static final long serialVersionUID = 2629195288020321924L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_control;

  private String descripcion;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_usuario")
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_tipo_control")
  private TipoControl tipoControl;

  @Column
  @Temporal(TemporalType.DATE)
  private Date fecha;

  @Column
  @Temporal(TemporalType.TIME)
  private Date hora;
}
