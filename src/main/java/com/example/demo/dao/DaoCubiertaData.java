package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Cubierta;

public interface DaoCubiertaData extends JpaRepository<Cubierta, Long>{
    @Query(value = "select c from Cubierta c where c.nombre = ?1")
    public Cubierta buscarPorNombre(String nombre);
}
