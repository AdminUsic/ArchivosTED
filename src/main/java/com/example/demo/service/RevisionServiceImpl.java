package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoRevisionData;
import com.example.demo.entity.Revision;

@Service
public class RevisionServiceImpl implements RevisionService{

    @Autowired
    private DaoRevisionData dao;

    @Override
    public List<Revision> findAll() {
        return dao.findAll();
    }

    @Override
    public void save(Revision entidad) {
        dao.save(entidad);
    }

    @Override
    public Revision findOne(Long id) {
        return dao.findById(id).orElse(null);
    }
    
}
