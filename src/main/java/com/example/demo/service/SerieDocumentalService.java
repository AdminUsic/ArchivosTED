package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.SerieDocumental;

public interface SerieDocumentalService {
    public List<SerieDocumental> findAll();

	public void save(SerieDocumental serieDocumental);

	public SerieDocumental findOne(Long id);

	public void delete(Long id);
}
