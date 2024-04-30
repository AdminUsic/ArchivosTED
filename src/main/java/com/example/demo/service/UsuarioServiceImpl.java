package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoUsuarioData;

import com.example.demo.entity.Usuario;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private DaoUsuarioData daoUsuarioData;

    @Override
    public List<Usuario> findAll() {
        return (List<Usuario>) daoUsuarioData.findAll();
    }

    @Override
    public void save(Usuario usuario) {
        daoUsuarioData.save(usuario);
    }

    @Override
    public Usuario findOne(Long id) {
        return daoUsuarioData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        daoUsuarioData.deleteById(id);
    }

    @Override
    public Optional<Usuario> findOneOptional(Long id) {
        return daoUsuarioData.findById(id);
    }

    @Override
    public Usuario credenciales(String ci) {
        
        try {
            return daoUsuarioData.credenciales(ci);    
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void eliminar(Long id) {
        daoUsuarioData.eliminar(id);
    }

    @Override
    public List<Usuario> listaUsuarioPorUnidad(Long idUnidad) {
        return daoUsuarioData.listaUsuarioPorUnidad(idUnidad);
    }

    @Override
    public List<Usuario> listaUsuarioPorNombreRol(String nombreRol) {
        return daoUsuarioData.listaUsuarioPorNombreRol(nombreRol);
    }

}
