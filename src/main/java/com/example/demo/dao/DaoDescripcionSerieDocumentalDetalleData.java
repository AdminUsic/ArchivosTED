package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.DescripcionSerieDocumentalDetalle;

public interface DaoDescripcionSerieDocumentalDetalleData extends JpaRepository<DescripcionSerieDocumentalDetalle, Long> {

    @Query("select ds from DescripcionSerieDocumentalDetalle ds where ds.estado != 'X'")
    public List<DescripcionSerieDocumentalDetalle> listaDescripcionSerieDocumentalDetalle();

    @Query("select ds from DescripcionSerieDocumentalDetalle ds where ds.descripcionSerieDocumental.id_descripcion_serie_documental = ?1 and ds.estado != 'X'")
    public List<DescripcionSerieDocumentalDetalle> listaDescripcionSerieDocumentalDetalleByIdDescripcionSerieDocumental(Long id_descripcion_serie_documental);
}
