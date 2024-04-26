package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Rol;

public interface RolService {
    public List<Rol> findAll();
    
    public void save(Rol rol);

    public Rol findOne(Long id);

    public void delete(Long id);

    public Rol UltimoRegistro();

    public Rol rolByNombre(String nombre);
}
