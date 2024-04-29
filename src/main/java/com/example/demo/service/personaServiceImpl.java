package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoPersonaData;
import com.example.demo.entity.Persona;

@Service
public class personaServiceImpl implements PersonaService{
    @Autowired
    private DaoPersonaData daoPersonaData;

    @Override
    public List<Persona> findAll() {
        return (List<Persona>) daoPersonaData.findAll();
    }

    @Override
    public void save(Persona persona) {
        daoPersonaData.save(persona);
    }

    @Override
    public Persona findOne(Long id) {
        
        return (Persona) daoPersonaData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        daoPersonaData.deleteById(id);
    }

    @Override
    public Persona personaCi(String ci) {
        return daoPersonaData.personaCi(ci);
    }

    @Override
    public void eliminar(Long id) {
        daoPersonaData.eliminar(id);
    }

    @Override
    public Persona personaModCi(String ciActual, String ic) {
        return daoPersonaData.personaModCi(ciActual, ic);
    }
    
}
