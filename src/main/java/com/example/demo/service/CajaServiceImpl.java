package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoCajaData;
import com.example.demo.entity.Caja;

@Service
public class CajaServiceImpl implements CajaService{

    @Autowired
    private DaoCajaData cajaData;

    @Override
    public List<Caja> findAll() {
        return cajaData.findAll();
    }

    @Override
    public void save(Caja caja) {
       cajaData.save(caja);
    }

    @Override
    public Caja findOne(Long id) {
        return cajaData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        cajaData.deleteById(id);
    }
    
}
