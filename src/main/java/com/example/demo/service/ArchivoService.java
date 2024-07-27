package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Archivo;
import java.util.Optional;
public interface ArchivoService {
    public List<Archivo> findAll();

	public void save(Archivo archivo);

	public Archivo findOne(Long id);

	public Optional<Archivo> findOneOptional(Long id);

	public void delete(Long id);

	public List<Archivo> archivosCarpeta(Long id_carpeta);

	public Archivo buscarArchivoCarpeta(String nombre, int gestion, Long idPersona, Long idUnidad, Long idCubierta, Long idCarpeta, Long idSerieDoc);

}
