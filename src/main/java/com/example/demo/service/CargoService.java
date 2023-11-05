package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Cargo;

public interface CargoService {
    public List<Cargo> findAll();

	public void save(Cargo cargo);

	public Cargo findOne(Long id);

	public void delete(Long id);
}
