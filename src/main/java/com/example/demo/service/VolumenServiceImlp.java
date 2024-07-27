package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoVolumenData;
import com.example.demo.entity.Volumen;

@Service
public class VolumenServiceImlp implements VolumenService{

    @Autowired
    private DaoVolumenData daoVolumenData;

    @Override
    public List<Volumen> findAll() {
        return (List<Volumen>) daoVolumenData.findAll();
    }

    @Override
    public void save(Volumen volumen) {
        daoVolumenData.save(volumen);
    }

    @Override
    public Volumen findOne(Long id) {
        return daoVolumenData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        daoVolumenData.deleteById(id);
    }

    @Override
    public List<Volumen> listaDeVolumenes() {
        return daoVolumenData.listaDeVolumenes();
    }

    @Override
    public Volumen volumenPorNombre(String nombre) {
        return daoVolumenData.volumenPorNombre(nombre);
    }
    
}
