package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoDescripcionSerieDocumentalDetalleData;
import com.example.demo.entity.DescripcionSerieDocumentalDetalle;

@Service
public class DescripcionSerieDocumentalDetalleServiceImpl implements DescripcionSerieDocumentalDetalleService {

    @Autowired
    private DaoDescripcionSerieDocumentalDetalleData dao;

    @Override
    public List<DescripcionSerieDocumentalDetalle> findAll() {
        return dao.findAll();
    }

    @Override
    public void save(DescripcionSerieDocumentalDetalle descripcionSerieDocumentalDetalle) {
        dao.save(descripcionSerieDocumentalDetalle);
    }

    @Override
    public DescripcionSerieDocumentalDetalle findOne(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        DescripcionSerieDocumentalDetalle descripcionSerieDocumentalDetalle = dao.findById(id).orElse(null);
        descripcionSerieDocumentalDetalle.setEstado("X");
        dao.save(descripcionSerieDocumentalDetalle);
    }

    @Override
    public List<DescripcionSerieDocumentalDetalle> listaDescripcionSerieDocumentalDetalle() {
        return dao.listaDescripcionSerieDocumentalDetalle();
    }

    @Override
    public List<DescripcionSerieDocumentalDetalle> listaDescripcionSerieDocumentalDetalleByIdDescripcionSerieDocumental(Long id_descripcion_serie_documental){
        return dao.listaDescripcionSerieDocumentalDetalleByIdDescripcionSerieDocumental(id_descripcion_serie_documental);
    }

}
