package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoArchivosData;
import com.example.demo.dao.DaoCarpetaData;
import com.example.demo.entity.Archivo;
import com.example.demo.entity.Carpeta;

@Service
public class CarpetaServiceImlp implements CarpetaService{

    @Autowired
    private DaoCarpetaData carpetaData;

    @Autowired
    private DaoArchivosData archivosData;

    @Override
    public List<Carpeta> findAll() {
    return (List<Carpeta>) carpetaData.findAll();
    }

    @Override
    public void save(Carpeta carpeta) {
        carpetaData.save(carpeta);
    }

    @Override
    public Carpeta findOne(Long id) {
        return carpetaData.findById(id).orElse(null);
    }

    @Override
    public Optional<Carpeta> findOneOptional(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'findOneOptional'");
    }

    @Override
    public void delete(Long id) {
        // for (Archivo archivo : archivosData.archivosCarpeta(id)) {
        //     archivosData.deleteById(archivo.getId_archivo());
        // }
        //carpetaData.deleteById(id);
        Carpeta carpeta = carpetaData.findById(id).orElse(null);
        carpeta.setEstado("X");
        carpetaData.save(carpeta);
    }

    @Override
    public List<Carpeta> buscarPorGestion(int gestion) {
        return (List<Carpeta>) carpetaData.buscarPorGestion(gestion);
    }

    @Override
    public List<Carpeta> buscarPorUnidadGestion(Long id_unidad, int gestion) {
        return (List<Carpeta>) carpetaData.buscarPorUnidadGestion(id_unidad, gestion);
    }

    @Override
    public List<Carpeta> buscarPorSeriedGestion(Long id_serie, int gestion) {
        return (List<Carpeta>) carpetaData.buscarPorSeriedGestion(id_serie, gestion);
    }

    @Override
    public List<Carpeta> buscarPorVolumenGestion(Long id_volumen, int gestion) {
        return (List<Carpeta>) carpetaData.buscarPorVolumenGestion(id_volumen, gestion);
    }

    @Override
    public List<Carpeta> buscarPorUnidadVolumen(Long id_unidad, Long id_volumen) {
        return (List<Carpeta>) carpetaData.buscarPorUnidadVolumen(id_unidad, id_volumen);
    }

    @Override
    public List<Carpeta> buscarPorUnidadSerie(Long id_serie, Long id_unidad) {
        return (List<Carpeta>) carpetaData.buscarPorUnidadSerie(id_serie, id_unidad);
    }

    @Override
    public List<Carpeta> buscarPorUnidadSerieVolumen(Long id_serie, Long id_unidad, Long id_volumen) {
        return (List<Carpeta>) carpetaData.buscarPorUnidadSerieVolumen(id_serie, id_unidad, id_volumen);
    }

    @Override
    public List<Carpeta> buscarPorUnidadSerieVolumenGestion(Long id_serie, Long id_unidad, Long id_volumen,
            int gestion) {
        return (List<Carpeta>) carpetaData.buscarPorUnidadSerieVolumenGestion(id_serie, id_unidad, id_volumen, gestion);
    }

    @Override
    public List<Carpeta> buscarPorSerieVolumen(Long id_serie, Long id_volumen) {
        return (List<Carpeta>) carpetaData.buscarPorSerieVolumen(id_serie, id_volumen);
    }

    @Override
    public List<Carpeta> buscarPorUnidadSerieGestion(Long id_serie, Long id_unidad, int gestion) {
        return (List<Carpeta>) carpetaData.buscarPorUnidadSerieGestion(id_serie, id_unidad, gestion);
    }

    @Override
    public List<Carpeta> buscarPorUnidadVolumenGestion(Long id_unidad, Long id_volumen, int gestion) {
        return (List<Carpeta>) carpetaData.buscarPorUnidadVolumenGestion(id_unidad, id_volumen, gestion);
    }

    @Override
    public List<Carpeta> listarPorIdPersona(Long idPersona) {
        return carpetaData.listarPorIdPersona(idPersona);
    }

    @Override
    public List<Carpeta> listarTodasCarpetas() {
        return carpetaData.listarTodasCarpetas();
    }
    
}
