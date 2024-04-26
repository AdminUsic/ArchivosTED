package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Menu;

public interface MenuService {
  
  public List<Menu> findAll();

	public Menu findOne(Long id);
}
