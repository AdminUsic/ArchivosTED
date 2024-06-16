package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Unidad;

public interface DaoUnidadData extends JpaRepository<Unidad, Long>{
    @Query(value = "select u from Unidad u where u.nombre = ?1 and u.estado !='X'")
    public Unidad UnidadNombre(String nombre);

    @Query(value = "select u from Unidad u where u.nombre != ?1 and u.nombre = ?2 and u.estado !='X'")
    public Unidad UnidadModNombre(String nombreActual, String nombre);

    @Query("SELECT u FROM Unidad u WHERE u.unidadPadre IS NULL AND u.estado != 'X'")
    List<Unidad> listaUnidadesPadres();

    @Query(value = "select u from Unidad u where u.unidadPadre.id_unidad = ?1 and u.estado !='X'")
    public List<Unidad> listaSubUnidadesPadres(Long idUnidadPadre);
}
