package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DaoChatData;
import com.example.demo.dao.DaoMensajeData;
import com.example.demo.entity.Mensaje;

@Service
public class ChatServiceImpl implements ChatService{

    @Autowired
    private DaoChatData chatData;

    @Autowired
    private DaoMensajeData daoMensajeData;

    @Override
    public List<Mensaje> findAll() {
        return chatData.findAll();
    }

    @Override
    public void save(Mensaje mensaje) {
        chatData.save(mensaje);
    }

    @Override
    public Mensaje findOne(Long id) {
        return chatData.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        chatData.deleteById(id);
    }

    @Override
    public List<Mensaje> mensajeUsuario(Long id_usuario_remitente, Long id_usuario_destino) {
        return daoMensajeData.mensajeUsuario(id_usuario_remitente, id_usuario_destino);
    }
    
}
