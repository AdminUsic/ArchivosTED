package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Unidad;

public interface DaoUnidadData extends JpaRepository<Unidad, Long>{
    @Query(value = "select u from Unidad u where u.nombre = ?1")
    public Unidad UnidadNombre(String nombre);

    @Query(value = "select u from Unidad u where u.nombre != ?1 and u.nombre = ?2")
    public Unidad UnidadModNombre(String nombreActual, String nombre);
}
