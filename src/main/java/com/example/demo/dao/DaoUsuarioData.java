package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Usuario;

public interface DaoUsuarioData extends JpaRepository<Usuario, Long>{
    
    @Query(value = "select u from Usuario u where u.persona.ci = ?1")
    public Usuario credenciales(String ci);

    @Modifying
    @Query(value = "delete from usuario where id_usuario = ?1", nativeQuery = true)
    public void eliminar(Long id);
}
