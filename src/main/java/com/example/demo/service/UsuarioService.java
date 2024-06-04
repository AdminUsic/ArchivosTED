package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

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

    public List<Usuario> listaUsuarioPorNombreRol(String nombreRol);

    public Usuario getUsuarioActivo(Long idPersona);

    public List<Usuario> listaUsuarioPorNombreUnidad(String nombre);

    public List<Usuario> listaUsuarioChatRestoPersonal(Long id);

    public List<Usuario> listaUsuarioChatPersonalArchivo(Long id);

}
