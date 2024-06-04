package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Menu;

public interface DaoMenuData extends JpaRepository<Menu, Long>{
    @Query(value = "select * from menu m where m.id_text is not null", nativeQuery = true)
    public Menu menuDisponible();
}
