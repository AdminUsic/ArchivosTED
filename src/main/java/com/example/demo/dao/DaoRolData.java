package com.example.demo.dao;

import com.example.demo.entity.Rol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DaoRolData extends JpaRepository<Rol, Long> {
    @Query(value = "SELECT * FROM Rol r " +
            "ORDER BY r.id_rol DESC " + // Agrega un espacio después de "DESC"
            "LIMIT 1;", nativeQuery = true)
    public Rol UltimoRegistro();

    @Query("SELECT r FROM Rol r WHERE r.nombre = ?1")
    public Rol rolByNombre(String nombre);
}
