package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.DescripcionSerieDocumental;

public interface DaoDescripcionSerieDocumentalData extends JpaRepository<DescripcionSerieDocumental, Long> {

    @Query("select ds from DescripcionSerieDocumental ds where ds.estado != 'X'")
    public List<DescripcionSerieDocumental> listaDescripcionSerieDocumental();

}
