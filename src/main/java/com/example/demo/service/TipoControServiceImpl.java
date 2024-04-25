package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoTipoControlData;
import com.example.demo.entity.TipoControl;

@Service
public class TipoControServiceImpl implements TipoControService{
  @Autowired
  private DaoTipoControlData daoTipoControl;

  @Override
  public List<TipoControl> findAll() {
    return daoTipoControl.findAll();
  }

  @Override
  public void save(TipoControl tipoControl) {
    daoTipoControl.save(tipoControl);
  }

  @Override
  public TipoControl findOne(Long id) {
    return daoTipoControl.findById(id).orElse(null);
  }

  @Override
  public void delete(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'delete'");
  }

  @Override
  public TipoControl findAllByTipoControl(String nombre) {
    return daoTipoControl.findAllByTipoControl(nombre);
  }
}
