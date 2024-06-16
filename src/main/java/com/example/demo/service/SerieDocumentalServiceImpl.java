package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoSerieDocumentalData;
import com.example.demo.entity.SerieDocumental;

@Service
public class SerieDocumentalServiceImpl implements SerieDocumentalService{

    @Autowired
    private DaoSerieDocumentalData daoSerieDocumentalData;

    @Override
    public List<SerieDocumental> findAll() {
        return daoSerieDocumentalData.findAll();
    }

    @Override
    public void save(SerieDocumental serieDocumental) {
        daoSerieDocumentalData.save(serieDocumental);
    }

    @Override
    public SerieDocumental findOne(Long id) {
        return daoSerieDocumentalData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        daoSerieDocumentalData.deleteById(id);
    }

    @Override
    public SerieDocumental serieDocumentalNombre(String nombre) {
        return daoSerieDocumentalData.serieDocumentalNombre(nombre);
    }

    @Override
    public SerieDocumental serieDocumentalModNombre(String nombreActual, String nombre) {
        return daoSerieDocumentalData.serieDocumentalModNombre(nombreActual, nombre);
    }

    @Override
    public List<SerieDocumental> listaSerieDocumentalPadre() {
        return daoSerieDocumentalData.listaSerieDocumentalPadre();
    }

    @Override
    public List<SerieDocumental> listaSubSerieDocumental(Long idSeriePadre) {
        return daoSerieDocumentalData.listaSubSerieDocumental(idSeriePadre);
    }

    
}
