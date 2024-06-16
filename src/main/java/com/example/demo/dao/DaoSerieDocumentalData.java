package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.SerieDocumental;

public interface DaoSerieDocumentalData extends JpaRepository<SerieDocumental, Long>{
    @Query(value = "select s from SerieDocumental s where s.nombre = ?1 and s.estado != 'X'")
    public SerieDocumental serieDocumentalNombre(String nombre);

    @Query(value = "select s from SerieDocumental s where s.nombre != ?1 and s.nombre = ?2 and s.estado != 'X'")
    public SerieDocumental serieDocumentalModNombre(String nombreActual, String nombre);

    @Query(value = "SELECT s FROM SerieDocumental s WHERE s.seriePadre IS NULL AND s.estado != 'X'")
    public List<SerieDocumental> listaSerieDocumentalPadre();

    @Query(value = "select s from SerieDocumental s where s.seriePadre.id_serie = ?1 and s.estado != 'X'")
    public List<SerieDocumental> listaSubSerieDocumental(Long idSeriePadre);
}
