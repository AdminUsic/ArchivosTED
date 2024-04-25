package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Usuario;

public interface UsuarioService {
    public List<Usuario> findAll();
    
    public void save(Usuario usuario);

    public Usuario findOne(Long id);

    public void delete(Long id);

    public Optional<Usuario> findOneOptional(Long id);

    Usuario credenciales(String ci);

    public void eliminar(Long id);

    public List<Usuario> listaUsuarioPorUnidad(Long idUnidad);

}
