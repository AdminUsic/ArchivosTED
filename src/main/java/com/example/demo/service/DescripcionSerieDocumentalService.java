package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.DescripcionSerieDocumental;

public interface DescripcionSerieDocumentalService {
    public List<DescripcionSerieDocumental> findAll();

	public void save(DescripcionSerieDocumental descripcionSerieDocumental);

	public DescripcionSerieDocumental findOne(Long id);

	public void delete(Long id);

    public List<DescripcionSerieDocumental> listaDescripcionSerieDocumental();
}
