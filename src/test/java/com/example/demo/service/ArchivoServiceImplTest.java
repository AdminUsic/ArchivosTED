package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.dao.DaoArchivosData;
import com.example.demo.entity.Archivo;

public class ArchivoServiceImplTest {

    @Mock
    private DaoArchivosData archivosData;

    @InjectMocks
    private ArchivoServiceImpl archivoServiceImpl;

    Archivo archivo;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        archivo = new Archivo();
        archivo.setNombre("informe");
        archivo.setEstado("A");
        archivo.setEstado(".pdf");
        archivo.setDescripcion("INFORMES ASS");
    }

    @Test
    void testFindAll() {
        when(archivosData.findAll()).thenReturn(Arrays.asList(archivo));
        assertNotNull(archivosData.findAll());
    }
}
