package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.SerieDocumental;

public interface DaoSerieDocumentalData extends JpaRepository<SerieDocumental, Long>{
    @Query(value = "select s from SerieDocumental s where s.nombre = ?1 and s.estado != 'X'")
    public SerieDocumental serieDocumentalNombre(String nombre);

    @Query(value = "select s from SerieDocumental s where s.nombre != ?1 and s.nombre = ?2 and s.estado != 'X'")
    public SerieDocumental serieDocumentalModNombre(String nombreActual, String nombre);
}
