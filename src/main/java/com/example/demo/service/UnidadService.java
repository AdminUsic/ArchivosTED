package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Unidad;
import com.example.demo.entity.Usuario;

public interface UnidadService {
	public List<Unidad> findAll();

	public void save(Unidad unidad);

	public Unidad findOne(Long id);

	public void delete(Long id);

	public Unidad UnidadNombre(String nombre);

	public Unidad UnidadModNombre(String nombreActual, String nombre);

	
}
