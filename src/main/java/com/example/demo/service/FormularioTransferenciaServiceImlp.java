package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DataFormularioTransferenciaData;
import com.example.demo.entity.FormularioTransferencia;

@Service
public class FormularioTransferenciaServiceImlp implements FormularioTransferenciaService{

    @Autowired
    private DataFormularioTransferenciaData dataFormularioTransferenciaData;

    @Override
    public List<FormularioTransferencia> findAll() {
        return dataFormularioTransferenciaData.findAll();
    }

    @Override
    public void save(FormularioTransferencia formularioTransferencia) {
        dataFormularioTransferenciaData.save(formularioTransferencia);
    }

    @Override
    public FormularioTransferencia findOne(Long id) {
        return dataFormularioTransferenciaData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        //dataFormularioTransferenciaData.deleteById(id);
        FormularioTransferencia formularioTransferencia = dataFormularioTransferenciaData.findById(id).orElse(null);
        formularioTransferencia.setEstado("X");
        dataFormularioTransferenciaData.save(formularioTransferencia);
    }

    @Override
    public List<FormularioTransferencia> listaFormularioTransferenciaByIdUsuario(Long id_persona) {
        return dataFormularioTransferenciaData.listaFormularioTransferenciaByIdUsuario(id_persona);
    }

    @Override
    public FormularioTransferencia formularioTransferenciaCarpeta(Long id_carpeta) {
        return dataFormularioTransferenciaData.formularioTransferenciaCarpeta(id_carpeta);
    }
    
}
