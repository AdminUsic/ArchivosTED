package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Control;

public interface ControlService {
  public List<Control> findAll();

	public void save(Control control);

	public Control findOne(Long id);

	public void delete(Long id);

	public List<Control> findAllByControlUsuario(Long idUsuario);
}
