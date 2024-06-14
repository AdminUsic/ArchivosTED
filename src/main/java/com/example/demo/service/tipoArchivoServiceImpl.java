package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoTipoArchivo;
import com.example.demo.entity.TipoArchivo;
@Service
public class tipoArchivoServiceImpl implements TipoArchivoService{

    @Autowired
    private DaoTipoArchivo daoTipoArchivo;

    @Override
    public List<TipoArchivo> findAll() {
        return (List<TipoArchivo>) daoTipoArchivo.findAll();
    }

    @Override
    public void save(TipoArchivo tipoArchivo) {
       daoTipoArchivo.save(tipoArchivo);
    }

    @Override
    public TipoArchivo findOne(Long id) {
        return (TipoArchivo) daoTipoArchivo.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        daoTipoArchivo.deleteById(id);
    }

    @Override
    public TipoArchivo tipoArchivoByTipo(String nombre) {
        return daoTipoArchivo.tipoArchivoByTipo(nombre);
    }
    
}
