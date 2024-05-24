package com.example.demo.dao;

//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Persona;

public interface DaoPersonaData extends JpaRepository<Persona, Long>{
    @Query(value = "select p from Persona p where p.ci = ?1 and p.estado != 'X'")
    public Persona personaCi(String ci);

    @Modifying
    @Query(value = "delete from persona where id_persona = ?1", nativeQuery = true)
    public void eliminar(Long id);

    @Query(value = "select p from Persona p where p.ci != ?1 and p.ci = ?2 and p.estado != 'X'")
    public Persona personaModCi(String ciActual, String ic);
}
