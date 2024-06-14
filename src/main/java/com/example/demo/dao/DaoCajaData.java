package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Caja;

public interface DaoCajaData extends JpaRepository<Caja, Long>{
    @Query(value = "select * from caja as c where c.id_archivo = 17 and c.estado != 'X';", nativeQuery = true)
    public Caja archivoCaja(Long id_archivo);
}
