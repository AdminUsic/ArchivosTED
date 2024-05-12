package com.example.demo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mensaje")
public class Mensaje implements Serializable {
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_mensaje;
    private String contenidoTexto;

    // private byte[] contenidoFile;

    private String tipoMensaje;

    private String estado;

    // private String fechaRegistro;

    // private String horaRegistro;

    @Column
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "dd-MM-yy")
    private Date fechaRegistro;

    @Column
    @Temporal(TemporalType.TIME)
    @JsonFormat(pattern = "HH:mm:ss")
    private Date horaRegistro;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_formularioTransferencia")
    private FormularioTransferencia formularioTransferencia;

    @ManyToOne
    @JoinColumn(name = "id_usuario_remitente")
    private Usuario remitente;

    @ManyToOne
    @JoinColumn(name = "id_usuario_destino")
    private Usuario destino;

}
