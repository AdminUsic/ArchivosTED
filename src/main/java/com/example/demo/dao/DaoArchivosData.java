package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Archivo;

public interface DaoArchivosData extends JpaRepository<Archivo, Long> {
    @Query(value = "select * from Archivo as ar where ar.id_carpeta=?1", nativeQuery = true)
    public List<Archivo> archivosCarpeta(Long id_carpeta);

    @Query(value = """
            select * from archivo a
            where a.nombre = ?1
            and a.gestion = ?2
            and a.estado = 'A'
            and a.id_persona = ?3
            and a.id_unidad = ?4
            and a.id_cubierta = ?5
            and a.id_carpeta = ?6
            and a.id_serie = ?7
                """, nativeQuery = true)
    public Archivo buscarArchivoCarpeta(String nombre, int gestion, Long idPersona, Long idUnidad, Long idCubierta, Long idCarpeta, Long idSerieDoc);
}
