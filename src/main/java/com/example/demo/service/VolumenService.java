package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Volumen;

public interface VolumenService{
    public List<Volumen> findAll();
    
    public void save(Volumen volumen);

    public Volumen findOne(Long id);

    public void delete(Long id);
}
