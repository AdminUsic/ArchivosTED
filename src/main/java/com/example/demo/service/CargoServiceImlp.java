package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoCargoData;
import com.example.demo.entity.Cargo;
@Service
public class CargoServiceImlp implements CargoService{

    @Autowired
    private DaoCargoData cargoData;

    @Override
    public List<Cargo> findAll() {
        return (List<Cargo>) cargoData.findAll(); 
    }

    @Override
    public void save(Cargo cargo) {
        cargoData.save(cargo);
    }

    @Override
    public Cargo findOne(Long id) {
        return cargoData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        cargoData.deleteById(id);
    }

    @Override
    public Cargo cargoByNombre(String nombre) {
        return cargoData.cargoByNombre(nombre);
    }

    @Override
    public Cargo cargoByNombreMod(String nombreActual, String nombre) {
        return cargoData.cargoByNombreMod(nombreActual, nombre);
    }

}
