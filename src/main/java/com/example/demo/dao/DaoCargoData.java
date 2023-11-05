package com.example.demo.dao;

//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Cargo;

public interface DaoCargoData extends JpaRepository<Cargo, Long>{
    
}
