package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoUnidadData;
import com.example.demo.entity.Unidad;
import com.example.demo.entity.Usuario;

@Service
public class UnidadServiceImpl implements UnidadService{

    @Autowired
    private DaoUnidadData daoUnidadData;

    @Override
    public List<Unidad> findAll() {
        return (List<Unidad>) daoUnidadData.findAll();
    }

    @Override
    public void save(Unidad unidad) {
        daoUnidadData.save(unidad);
    }

    @Override
    public Unidad findOne(Long id) {
        return daoUnidadData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        daoUnidadData.deleteById(id);
    }

    @Override
    public Unidad UnidadNombre(String nombre) {
        return daoUnidadData.UnidadNombre(nombre);
    }

    @Override
    public Unidad UnidadModNombre(String nombreActual, String nombre) {
        return daoUnidadData.UnidadModNombre(nombreActual, nombre);
    }
    
}
