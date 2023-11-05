package com.example.demo.dao;

import java.util.List;

//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Carpeta;


public interface DaoCarpetaData extends JpaRepository<Carpeta, Long>{
    @Query(value = "SELECT c FROM Carpeta c WHERE c.unidad.id_unidad = ?1 AND c.gestion = ?2")
    public List<Carpeta> buscarPorUnidadGestion(Long id_unidad, int gestion);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.gestion = ?2")
    public List<Carpeta> buscarPorSeriedGestion(Long id_serie, int gestion);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.volumen.id_volumen = ?1 AND c.gestion = ?2")
    public List<Carpeta> buscarPorVolumenGestion(Long id_volumen, int gestion);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.unidad.id_unidad = ?1 AND c.volumen.id_volumen = ?2")
    public List<Carpeta> buscarPorUnidadVolumen(Long id_unidad, Long id_volumen);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.unidad.id_unidad =  ?2")
    public List<Carpeta> buscarPorUnidadSerie(Long id_serie, Long id_unidad);

        @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.volumen.id_volumen = ?2")
    public List<Carpeta> buscarPorSerieVolumen(Long id_serie, Long id_volumen);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.unidad.id_unidad = ?2 AND c.volumen.id_volumen = ?3")
    public List<Carpeta> buscarPorUnidadSerieVolumen(Long id_serie, Long id_unidad, Long id_volumen);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.unidad.id_unidad = ?2 AND c.gestion = ?3")
    public List<Carpeta> buscarPorUnidadSerieGestion(Long id_serie, Long id_unidad, int gestion);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.unidad.id_unidad = ?1 AND c.volumen.id_volumen = ?2 AND c.gestion = ?3")
    public List<Carpeta> buscarPorUnidadVolumenGestion(Long id_unidad, Long id_volumen, int gestion);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.unidad.id_unidad = ?2 AND c.volumen.id_volumen = ?3 AND c.gestion = ?4")
    public List<Carpeta> buscarPorUnidadSerieVolumenGestion(Long id_serie, Long id_unidad, Long id_volumen, int gestion);

    @Query(value = "select * from carpeta c where c.gestion = ?1", nativeQuery = true)
    public List<Carpeta> buscarPorGestion(int gestion);
}
