package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Revision;

public interface DaoRevisionData extends JpaRepository<Revision, Long>{
    
}
