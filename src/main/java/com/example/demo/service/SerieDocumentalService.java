package com.example.demo.service;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.SerieDocumental;

public interface SerieDocumentalService {
	public List<SerieDocumental> findAll();

	public void save(SerieDocumental serieDocumental);

	public SerieDocumental findOne(Long id);

	public void delete(Long id);

	public SerieDocumental serieDocumentalNombre(String nombre);

	public SerieDocumental serieDocumentalModNombre(String nombreActual, String nombre);

	public List<SerieDocumental> listaSerieDocumentalPadre();

    public List<SerieDocumental> listaSubSerieDocumental(Long idSeriePadre);
}
