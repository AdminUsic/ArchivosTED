package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoRolData;
import com.example.demo.entity.Rol;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    DaoRolData daoRolData;

    @Override
    public List<Rol> findAll() {
        return (List<Rol>) daoRolData.findAll();
    }

    @Override
    public void save(Rol rol) {
        daoRolData.save(rol);
    }

    @Override
    public Rol findOne(Long id) {
        return daoRolData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        daoRolData.deleteById(id);
    }

    @Override
    public Rol UltimoRegistro() {
        return daoRolData.UltimoRegistro();
    }

    @Override
    public Rol rolByNombre(String nombre) {
        return daoRolData.rolByNombre(nombre);
    }

    @Override
    public Rol rolByNombreMod(String nombreActual, String nombre) {
        return daoRolData.rolByNombreMod(nombreActual, nombre);
    }

}
