package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Mensaje;

public interface DaoMensajeData extends JpaRepository<Mensaje, Long> {

    @Query(value = "SELECT * FROM mensaje WHERE (id_usuario_remitente = ?1 AND id_usuario_destino = ?2) "+
    "OR (id_usuario_destino = ?1 AND id_usuario_remitente = ?2) AND estado = 'EN_REVISION' ORDER BY id_mensaje ASC;", nativeQuery = true)
    public List<Mensaje> mensajeUsuario(Long id_usuario_remitente, Long id_usuario_destino);
    
}

