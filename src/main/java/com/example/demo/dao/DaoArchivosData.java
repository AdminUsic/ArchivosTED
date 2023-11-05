package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Archivo;

public interface DaoArchivosData extends JpaRepository<Archivo, Long> {
    @Query(value = "select * from Archivo as ar " +
            "where ar.id_carpeta=?;", nativeQuery = true)
    public List<Archivo> archivosCarpeta(Long id_carpeta);
}
