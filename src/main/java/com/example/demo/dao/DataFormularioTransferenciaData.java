package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.FormularioTransferencia;

public interface DataFormularioTransferenciaData extends JpaRepository<FormularioTransferencia, Long>{
    
  @Query(value = "select f from FormularioTransferencia as f where f.persona.id_persona = ?1 and f.estado != 'X'")
  List<FormularioTransferencia> listaFormularioTransferenciaByIdUsuario(Long id_persona);

  @Query(value = "select f from FormularioTransferencia as f where f.carpeta.id_carpeta = ?1 and f.estado != 'X'")
  FormularioTransferencia formularioTransferenciaCarpeta(Long id_carpeta);
}
