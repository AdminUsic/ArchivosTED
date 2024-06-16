package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoDescripcionSerieDocumentalData;
import com.example.demo.entity.DescripcionSerieDocumental;

@Service
public class DescripcionSerieDocumentalServiceImpl implements DescripcionSerieDocumentalService{
    
    @Autowired
    private DaoDescripcionSerieDocumentalData dao;

    @Override
    public List<DescripcionSerieDocumental> findAll() {
        return dao.findAll();
    }

    @Override
    public void save(DescripcionSerieDocumental descripcionSerieDocumental) {
        dao.save(descripcionSerieDocumental);
    }

    @Override
    public DescripcionSerieDocumental findOne(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        DescripcionSerieDocumental descripcionSerieDocumental = dao.findById(id).orElse(null);
        descripcionSerieDocumental.setEstado("X");
        dao.save(descripcionSerieDocumental);
    }

    @Override
    public List<DescripcionSerieDocumental> listaDescripcionSerieDocumental() {
        return dao.listaDescripcionSerieDocumental();
    }
}
