package com.example.demo.entity;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity 
@Table(name = "tipo_control")
public class TipoControl implements Serializable{
  private static final long serialVersionUID = 2629195288020321924L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_tipo_control;
  private String nombre;

  @OneToMany(mappedBy = "tipoControl", fetch = FetchType.LAZY)
  private List<Control> control;
}
