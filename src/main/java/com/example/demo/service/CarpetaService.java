package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Carpeta;

public interface CarpetaService {
    public List<Carpeta> findAll();

	public void save(Carpeta carpeta);

	public Carpeta findOne(Long id);

	public Optional<Carpeta> findOneOptional(Long id);

	public void delete(Long id);

    public List<Carpeta> buscarPorUnidadGestion(Long id_unidad, int gestion);

    public List<Carpeta> buscarPorSeriedGestion(Long id_serie, int gestion);

    public List<Carpeta> buscarPorVolumenGestion(Long id_volumen, int gestion);

    public List<Carpeta> buscarPorUnidadVolumen(Long id_unidad, Long id_volumen);

    public List<Carpeta> buscarPorUnidadSerie(Long id_serie, Long id_unidad);

    public List<Carpeta> buscarPorSerieVolumen(Long id_serie, Long id_volumen);

    public List<Carpeta> buscarPorUnidadSerieVolumen(Long id_serie, Long id_unidad, Long id_volumen);

    public List<Carpeta> buscarPorUnidadSerieGestion(Long id_serie, Long id_unidad, int gestion);

    public List<Carpeta> buscarPorUnidadVolumenGestion(Long id_unidad, Long id_volumen, int gestion);

    public List<Carpeta> buscarPorUnidadSerieVolumenGestion(Long id_serie, Long id_unidad, Long id_volumen, int gestion);

    public List<Carpeta> buscarPorGestion(int gestion);
}
