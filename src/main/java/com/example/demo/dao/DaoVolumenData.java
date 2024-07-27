package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Volumen;

public interface DaoVolumenData extends JpaRepository<Volumen, Long> {

    @Query(value = "SELECT v FROM Volumen v WHERE v.estado != 'X' ORDER BY CASE WHEN v.nombre = 'N/A' THEN 0 ELSE 1 END, v.nombre")
    public List<Volumen> listaDeVolumenes();

    @Query(value = "SELECT v FROM Volumen v WHERE v.estado != 'X' and v.nombre = ?1")
    public Volumen volumenPorNombre(String nombre);

}
