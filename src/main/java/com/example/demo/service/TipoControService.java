package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.TipoControl;

public interface TipoControService {
  public List<TipoControl> findAll();

	public void save(TipoControl tipoControl);

	public TipoControl findOne(Long id);

	public void delete(Long id);

	public TipoControl findAllByTipoControl(String nombre);
}
