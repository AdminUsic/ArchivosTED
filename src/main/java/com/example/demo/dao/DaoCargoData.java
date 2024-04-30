package com.example.demo.dao;

//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Cargo;

public interface DaoCargoData extends JpaRepository<Cargo, Long> {
    @Query("SELECT c FROM Cargo c WHERE c.nombre = :nombre")
    public Cargo cargoByNombre(@Param("nombre") String nombre);

    @Query("SELECT c FROM Cargo c WHERE c.nombre != :nombreActual and c.nombre = :nombreNuevo")
    public Cargo cargoByNombreMod(@Param("nombreActual") String nombreActual, @Param("nombreNuevo") String nombreNuevo);

}
