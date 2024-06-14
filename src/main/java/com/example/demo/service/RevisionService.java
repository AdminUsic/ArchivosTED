package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Revision;

public interface RevisionService {
    public List<Revision> findAll();

	public void save(Revision entidad);

	public Revision findOne(Long id);
}
