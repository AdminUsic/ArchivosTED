package com.example.demo.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Menu implements Serializable{
  private static final long serialVersionUID = 2629195288020321924L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_menu;

  private String nombre;
  private String idText;
  private String icono;
  private String ruta;

  @ManyToOne
  @JoinColumn(name = "menu_padre_id")
  private Menu menuPadre;

  @OneToMany(mappedBy = "menuPadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Menu> subMenus;

    @ManyToMany(mappedBy = "menus", fetch = FetchType.LAZY)
    private Set<Rol> roles = new HashSet<>();
}
