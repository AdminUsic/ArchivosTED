package com.example.demo.dao;

//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.TipoArchivo;

public interface DaoTipoArchivo extends JpaRepository<TipoArchivo, Long>{
    @Query(value = "select t from TipoArchivo t where t.nombre_tipo = ?1 and t.estado !='X'")
    public TipoArchivo tipoArchivoByTipo(String nombre);
}
