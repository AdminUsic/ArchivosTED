package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.SerieDocumental;

public interface DaoSerieDocumentalData extends JpaRepository<SerieDocumental, Long>{
    @Query(value = "select u from SerieDocumental u where u.nombre = ?1")
    public SerieDocumental serieDocumentalNombre(String nombre);

    @Query(value = "select u from SerieDocumental u where u.nombre != ?1 and u.nombre = ?2")
    public SerieDocumental serieDocumentalModNombre(String nombreActual, String nombre);
}
