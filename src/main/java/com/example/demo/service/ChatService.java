package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Mensaje;

public interface ChatService {
    public List<Mensaje> findAll();

	public void save(Mensaje mensaje);

	public Mensaje findOne(Long id);

	public void delete(Long id);

	public List<Mensaje> mensajeUsuario(Long id_usuario_remitente, Long id_usuario_destino);
}
