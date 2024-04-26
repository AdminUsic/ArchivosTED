package com.example.demo.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rol")
public class Rol implements Serializable {

    private static final long serialVersionUID = 2629195288020321924L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_rol;

    private String nombre;

    private String estado;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Persona> personas = new HashSet<>();

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="rol_menu",
    joinColumns=@JoinColumn(name = "id_rol"),
    inverseJoinColumns = @JoinColumn(name = "id_menu"))
    private Set<Menu> menus;
}
