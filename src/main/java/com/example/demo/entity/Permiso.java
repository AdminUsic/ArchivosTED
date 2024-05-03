package com.example.demo.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Permiso implements Serializable {
  private static final long serialVersionUID = 2629195288020321924L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_permiso;
  private String nombre;

      @ManyToMany(mappedBy = "permisos", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();
}
