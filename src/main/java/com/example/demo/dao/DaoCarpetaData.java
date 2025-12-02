package com.example.demo.dao;

import java.util.List;

//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Carpeta;


public interface DaoCarpetaData extends JpaRepository<Carpeta, Long>{
    @Query(value = "SELECT c FROM Carpeta c WHERE c.unidad.id_unidad = ?1 AND c.gestion = ?2 ORDER BY c.fechaRegistro DESC")
    public List<Carpeta> buscarPorUnidadGestion(Long id_unidad, int gestion);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.gestion = ?2 ORDER BY c.fechaRegistro DESC")
    public List<Carpeta> buscarPorSeriedGestion(Long id_serie, int gestion);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.volumen.id_volumen = ?1 AND c.gestion = ?2 ORDER BY c.fechaRegistro DESC")
    public List<Carpeta> buscarPorVolumenGestion(Long id_volumen, int gestion);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.unidad.id_unidad = ?1 AND c.volumen.id_volumen = ?2 ORDER BY c.fechaRegistro DESC")
    public List<Carpeta> buscarPorUnidadVolumen(Long id_unidad, Long id_volumen);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.unidad.id_unidad =  ?2 ORDER BY c.fechaRegistro DESC")
    public List<Carpeta> buscarPorUnidadSerie(Long id_serie, Long id_unidad);

        @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.volumen.id_volumen = ?2 ORDER BY c.fechaRegistro DESC")
    public List<Carpeta> buscarPorSerieVolumen(Long id_serie, Long id_volumen);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.unidad.id_unidad = ?2 AND c.volumen.id_volumen = ?3 ORDER BY c.fechaRegistro DESC")
    public List<Carpeta> buscarPorUnidadSerieVolumen(Long id_serie, Long id_unidad, Long id_volumen);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.unidad.id_unidad = ?2 AND c.gestion = ?3 ORDER BY c.fechaRegistro DESC")
    public List<Carpeta> buscarPorUnidadSerieGestion(Long id_serie, Long id_unidad, int gestion);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.unidad.id_unidad = ?1 AND c.volumen.id_volumen = ?2 AND c.gestion = ?3 ORDER BY c.fechaRegistro DESC")
    public List<Carpeta> buscarPorUnidadVolumenGestion(Long id_unidad, Long id_volumen, int gestion);

    @Query(value = "SELECT c FROM Carpeta c WHERE c.serieDocumental.id_serie = ?1 AND c.unidad.id_unidad = ?2 AND c.volumen.id_volumen = ?3 AND c.gestion = ?4 ORDER BY c.fechaRegistro DESC")
    public List<Carpeta> buscarPorUnidadSerieVolumenGestion(Long id_serie, Long id_unidad, Long id_volumen, int gestion);

    @Query(value = "select * from carpeta c where c.gestion = ?1 ORDER BY c.fecha_registro DESC", nativeQuery = true)
    public List<Carpeta> buscarPorGestion(int gestion);

    @Query(value = "select * from carpeta c where c.id_persona = ?1 and c.estado != 'X' ORDER BY c.fecha_registro DESC", nativeQuery = true)
    public List<Carpeta> listarPorIdPersona(Long idPersona);

    @Query(value = "select * from carpeta c where c.estado != 'X' ORDER BY c.fecha_registro DESC", nativeQuery = true)
    public List<Carpeta> listarTodasCarpetas();
}
