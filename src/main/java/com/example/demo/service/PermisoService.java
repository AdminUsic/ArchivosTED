package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Permiso;

public interface PermisoService {
    public List<Permiso> findAll();

	public Permiso findOne(Long id);
}
