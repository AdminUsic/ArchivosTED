package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Caja;

public interface CajaService {
    public List<Caja> findAll();

	public void save(Caja caja);

	public Caja findOne(Long id);

	public void delete(Long id);
}
