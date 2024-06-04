package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoMenuData;
import com.example.demo.entity.Menu;

@Service
public class MenuServiceImpl implements MenuService{
  @Autowired
  private DaoMenuData daoMenuData;

  @Override
  public List<Menu> findAll() {
    return daoMenuData.findAll();
  }

  @Override
  public Menu findOne(Long id) {
    return daoMenuData.findById(id).orElse(null);
  }

  @Override
  public Menu menuDisponible() {
    return daoMenuData.menuDisponible();
  }

}
