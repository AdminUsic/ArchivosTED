package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoPermisoData;
import com.example.demo.entity.Permiso;

@Service
public class PermisoServiceImpl implements PermisoService{
    @Autowired
    private DaoPermisoData permisoData;

    @Override
    public List<Permiso> findAll() {
        return permisoData.findAll();
    }

    @Override
    public Permiso findOne(Long id) {
        return permisoData.findById(id).orElse(null);
    }
}
