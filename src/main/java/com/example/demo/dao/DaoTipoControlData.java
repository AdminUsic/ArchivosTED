package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.TipoControl;

public interface DaoTipoControlData extends JpaRepository<TipoControl, Long>{
  @Query(value = "select t from TipoControl t where t.nombre = ?1")
    public TipoControl findAllByTipoControl(String nombre);
}
