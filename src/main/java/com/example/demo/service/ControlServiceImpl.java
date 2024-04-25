package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoControlData;
import com.example.demo.entity.Control;

@Service
public class ControlServiceImpl implements ControlService{
  
  @Autowired
  private DaoControlData daoControl;

  @Override
  public List<Control> findAll() {
    return daoControl.findAll();
  }

  @Override
  public void save(Control control) {
    daoControl.save(control);
  }

  @Override
  public Control findOne(Long id) {
    return daoControl.findById(id).orElse(null);
  }

  @Override
  public void delete(Long id) {
    daoControl.deleteById(id);
  }

  @Override
  public List<Control> findAllByControlUsuario(Long idUsuario) {
    return daoControl.findAllByControlUsuario(idUsuario);
  }
}
