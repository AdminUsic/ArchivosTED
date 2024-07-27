package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Usuario;

public interface DaoUsuarioData extends JpaRepository<Usuario, Long> {

    @Query(value = "select u from Usuario u where u.persona.ci = ?1 and u.estado != 'X'")
    public Usuario credenciales(String ci);

    @Modifying
    @Query(value = "delete from usuario where id_usuario = ?1", nativeQuery = true)
    public void eliminar(Long id);

    @Query(value = "select u from Usuario u where u.persona.unidad.id_unidad = ?1 AND u.estado != 'X'")
    public List<Usuario> listaUsuarioPorUnidad(Long idUnidad);

    @Query(value = "select u from Usuario u where u.persona.unidad.nombre = ?1 AND u.estado != 'X'")
    public List<Usuario> listaUsuarioPorNombreUnidad(String nombre);

    @Query(value = """
            select * from usuario u
            inner join persona p on p.id_persona = u.id_persona
            inner join unidad uni on uni.id_unidad = p.id_unidad
            where uni.nombre = 'ARCHIVO Y BIBLIOTECA' and u.id_usuario != ?1 and u.estado != 'X'
                """, nativeQuery = true)
    public List<Usuario> listaUsuarioChatRestoPersonal(Long id);

    @Query(value = """
                    select * from usuario u
            inner join persona p on p.id_persona = u.id_persona
            inner join unidad uni on uni.id_unidad = p.id_unidad
            where uni.nombre != 'ARCHIVO Y BIBLIOTECA' and u.id_usuario != ?1 and u.estado != 'X'
                        """, nativeQuery = true)
    public List<Usuario> listaUsuarioChatPersonalArchivo(Long id);

    @Query(value = """
            select * from usuario u
            inner join persona p ON p.id_persona = u.id_persona
            inner join rol_persona rp ON rp.id_persona = u.id_persona
            inner join rol r on r.id_rol = rp.id_rol
            where r.nombre = ?1 AND u.estado != 'X'
            """, nativeQuery = true)
    public List<Usuario> listaUsuarioPorNombreRol(@Param("nombreRol") String nombreRol);

    @Query(value = "select u from Usuario u where u.persona.id_persona = ?1 and u.estado != 'X'")
    public Usuario getUsuarioActivo(Long idPersona);
}
