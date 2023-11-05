package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoArchivosData;

import com.example.demo.entity.Archivo;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Service
public class ArchivoServiceImpl implements ArchivoService{

    @Autowired
    DaoArchivosData archivosData;

    @Override
    public List<Archivo> findAll() {
        return (List<Archivo>) archivosData.findAll();
    }

    @Override
    public void save(Archivo archivo) {
        archivosData.save(archivo);
    }

    @Override
    public Archivo findOne(Long id) {
       return archivosData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        archivosData.deleteById(id);
    }

    @Override
    public Optional<Archivo> findOneOptional(Long id) {
         return archivosData.findById(id);
    }

    @Override
    public List<Archivo> archivosCarpeta(Long id_carpeta) {
        return archivosData.archivosCarpeta(id_carpeta);
    }
    
}
