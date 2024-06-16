package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.DescripcionSerieDocumentalDetalle;

public interface DescripcionSerieDocumentalDetalleService {
    public List<DescripcionSerieDocumentalDetalle> findAll();

	public void save(DescripcionSerieDocumentalDetalle descripcionSerieDocumentalDetalle);

	public DescripcionSerieDocumentalDetalle findOne(Long id);

	public void delete(Long id);

    public List<DescripcionSerieDocumentalDetalle> listaDescripcionSerieDocumentalDetalle();

	public List<DescripcionSerieDocumentalDetalle> listaDescripcionSerieDocumentalDetalleByIdDescripcionSerieDocumental(Long id_descripcion_serie_documental);
}
