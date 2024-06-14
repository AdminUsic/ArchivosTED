package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.TipoArchivo;

public interface TipoArchivoService {
    public List<TipoArchivo> findAll();

	public void save(TipoArchivo tipoArchivo);

	public TipoArchivo findOne(Long id);

	public void delete(Long id);

	public TipoArchivo tipoArchivoByTipo(String nombre);
}
