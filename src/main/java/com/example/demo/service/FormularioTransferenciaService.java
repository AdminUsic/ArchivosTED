package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.FormularioTransferencia;

public interface FormularioTransferenciaService {
    public List<FormularioTransferencia> findAll();

	public void save(FormularioTransferencia formularioTransferencia);

	public FormularioTransferencia findOne(Long id);

	public void delete(Long id);

	List<FormularioTransferencia> listaFormularioTransferenciaByIdUsuario(Long id_persona);

}
