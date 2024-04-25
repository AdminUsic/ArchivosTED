package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Control;

public interface DaoControlData extends JpaRepository<Control, Long>{

  @Query(value = "select c from Control c where c.usuario.id_usuario = ?1")
  public List<Control> findAllByControlUsuario(Long idUsuario);

}
