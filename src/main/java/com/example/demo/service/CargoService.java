package com.example.demo.service;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Caja;
import com.example.demo.entity.Cargo;

public interface CargoService {
	public List<Cargo> findAll();

	public void save(Cargo cargo);

	public Cargo findOne(Long id);

	public void delete(Long id);

	public Cargo cargoByNombre(String nombre);

	public Cargo cargoByNombreMod(String nombreActual, String nombre);
}
