package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Unidad;

public interface DaoUnidadData extends JpaRepository<Unidad, Long>{
    @Query(value = "select * from unidad u where u.nombre = ?1", nativeQuery = true)
    public Unidad UnidadNombre(String nombre);
}
