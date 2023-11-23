package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoCubiertaData;
import com.example.demo.entity.Cubierta;

@Service
public class CubiertaServiceImpl implements CubiertaService{

    @Autowired
    private DaoCubiertaData cubiertaData;

    @Override
    public List<Cubierta> findAll() {
        return cubiertaData.findAll();
    }

    @Override
    public void save(Cubierta cubierta) {
        cubiertaData.save(cubierta);
    }

    @Override
    public Cubierta findOne(Long id) {
        return cubiertaData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Cubierta buscarPorNombre(String nombre) {
        return cubiertaData.buscarPorNombre(nombre);
    }
    
}
