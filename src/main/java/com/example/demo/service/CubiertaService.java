package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Cubierta;

public interface CubiertaService {
    public List<Cubierta> findAll();

	public void save(Cubierta cubierta);

	public Cubierta findOne(Long id);

	public void delete(Long id);
}
