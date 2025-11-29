package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Persona;

public interface PersonaService {
    public List<Persona> findAll();

	public void save(Persona entidad);

	public Persona findOne(Long id);

	public void delete(Long id);

	public Persona personaCi(String ci);

	public Persona personaModCi(String ciActual, String ic);

	public void eliminar(Long id);

	List<Persona> listarPersonasSinUsuario();
}
