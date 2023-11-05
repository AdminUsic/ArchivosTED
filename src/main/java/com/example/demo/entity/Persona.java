package com.example.demo.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "persona")
public class Persona implements Serializable{
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_persona;

    private String nombre;

    private String apellido;

    private String ci;

    private String estado;

    @Column
    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;

    @Column
    @Temporal(TemporalType.TIME)
    private Date horaRegistro;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cargo")
    private Cargo cargo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_unidad")
    @JsonBackReference // Marca esta parte de la relación como "referenciada"
    private Unidad unidad;

    /*@OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Marca esta parte de la relación como "gestionada"
    private List<FormularioTransferencia> formularioTransferencias;*/

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @PrePersist
    @PreUpdate
    public void beforeSaveOrUpdate() {
        nombre = nombre.toUpperCase();
        apellido = apellido.toUpperCase();
        ci = ci.toUpperCase();
        estado = estado.toUpperCase();
    }

    @OneToOne(mappedBy = "persona", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Usuario usuario;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<FormularioTransferencia> formularioTransferencias;

}
