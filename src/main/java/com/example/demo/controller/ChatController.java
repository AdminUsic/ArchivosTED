package com.example.demo.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.FormularioTransferencia;
import com.example.demo.entity.Mensaje;
import com.example.demo.entity.Persona;
import com.example.demo.entity.Rol;
import com.example.demo.entity.Usuario;
import com.example.demo.service.ChatService;
import com.example.demo.service.FormularioTransferenciaService;
import com.example.demo.service.UsuarioService;
import com.example.demo.service.UtilidadServiceProject;

import net.minidev.json.JSONObject;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FormularioTransferenciaService formularioTransferenciaService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UtilidadServiceProject utilidadService;

    @MessageMapping("/salonA")
    @SendTo("/topic/mensajesGrupo")
    // @Transactional
    public JSONObject sendMessage(JSONObject mensajeR) {
        System.out.println("Mensaje recibido: " + mensajeR.getAsString("contenidoTexto"));
        System.out.println("ID usuario: " + mensajeR.getAsString("remitente"));
        Mensaje mensaje = new Mensaje();

        if (mensajeR.getAsString("contenidoTexto") != null) {
            mensaje.setContenidoTexto(mensajeR.getAsString("contenidoTexto"));
        }
        if (mensajeR.getAsString("contenido") != null) {
            FormularioTransferencia formularioTransferencia = formularioTransferenciaService
                    .findOne(Long.parseLong(mensajeR.getAsString("contenido")));
            mensaje.setFormularioTransferencia(formularioTransferencia);

        }

        System.out.println("TODO BIEN");
        Long id_usuario = Long.parseLong(mensajeR.getAsString("remitente"));
        System.out.println("TODO BIEN EL ID ES: " + id_usuario);

        mensaje.setRemitente(usuarioService.findOne(id_usuario));

        LocalDateTime fechaHoraActual = LocalDateTime.now();

        Date fechaActual = new Date();

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");

        String fechaFormateada = formatoFecha.format(fechaActual);

        Date horaActual = java.sql.Time.valueOf(fechaHoraActual.toLocalTime());

        // mensajeR.appendField("fechaRegistro", fechaFormateada);
        mensajeR.appendField("fechaRegistro", utilidadService.fechaTexto(fechaActual));
        mensajeR.appendField("horaRegistro", horaActual);
        System.out.println("Hora actual como Date: " + horaActual);
        // mensaje.setFechaRegistro(String.valueOf(fechaFormateada));
        // mensaje.setHoraRegistro(String.valueOf(horaActual));
        mensaje.setFechaRegistro(fechaActual);
        mensaje.setHoraRegistro(fechaActual);
        chatService.save(mensaje);
        System.out.println("Envia mensaje");
        return mensajeR;
    }

    @MessageMapping("/chat/{userIdEmisor}/{userIdReceptor}")
    @SendTo("/user/{userIdReceptor}/private")
    public JSONObject enviarMensajePrivado(@DestinationVariable("userIdEmisor") String userIdEmisor,
            @DestinationVariable("userIdReceptor") String userIdReceptor,
            JSONObject mensajeR) {
        Long id_emisor = Long.parseLong(userIdEmisor);
        Long id_receptor = Long.parseLong(userIdReceptor);
        // Long id_Sala = Long.parseLong(idSala);
        System.out.println("METODO ENVIAR MENSAJE");
        System.out.println("EL USUARIO RECEPTOR ES: " + userIdReceptor);
        System.out.println("EL USUARIO EMISOR ES: " + userIdEmisor);
        Mensaje mensaje = new Mensaje();
        mensaje.setRemitente(usuarioService.findOne(id_emisor));
        mensaje.setDestino(usuarioService.findOne(id_receptor));
        System.out.println("EL NOMBRE USUARIO RECEPTOR ES: " + usuarioService.findOne(id_receptor).getNombre_user());
        LocalDateTime fechaHoraActual = LocalDateTime.now();

        Date fechaActual = new Date();

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");

        String fechaFormateada = formatoFecha.format(fechaActual);

        Date horaActual = java.sql.Time.valueOf(fechaHoraActual.toLocalTime());

        if (mensajeR.getAsString("contenidoTexto") != null) {
            mensaje.setContenidoTexto(mensajeR.getAsString("contenidoTexto"));
        }
        if (mensajeR.getAsString("contenido") != null) {
            FormularioTransferencia formularioTransferencia = formularioTransferenciaService
                    .findOne(Long.parseLong(mensajeR.getAsString("contenido")));
            mensaje.setFormularioTransferencia(formularioTransferencia);
        }
        mensaje.setTipoMensaje(mensajeR.getAsString("tipoMensaje"));
        // Imprimir la hora actual
        // mensajeR.appendField("fechaRegistro", fechaFormateada);
        // mensajeR.appendField("horaRegistro", horaActual);
        System.out.println("Hora actual como Date: " + horaActual);
        // mensaje.setFechaRegistro(String.valueOf(fechaFormateada));
        // mensaje.setHoraRegistro(String.valueOf(horaActual));
        mensaje.setFechaRegistro(new Date());
        mensaje.setHoraRegistro(new Date());
        chatService.save(mensaje);

        // mensajeR.appendField("id_mensaje", );
        // simpMessagingTemplate.convertAndSendToUser(userIdEmisor, "/private",
        // mensajeR);userIdEmisor

        simpMessagingTemplate.convertAndSendToUser(userIdEmisor, "/private", mensajeR);

        return mensajeR;
    }

    @PostMapping(value = "/MensajesAntiguos/{userIdEmisor}/{userIdReceptor}")
    public ResponseEntity<List<JSONObject>> MensajesAntiguos(@PathVariable("userIdEmisor") Long userIdEmisor,
            @PathVariable("userIdReceptor") Long userIdReceptor) {
        List<JSONObject> mensajesJSON = new ArrayList<>();

        List<Mensaje> mensajes = chatService.mensajeUsuario(userIdEmisor, userIdReceptor);

        for (Mensaje mensaje : mensajes) {
            JSONObject mensajeJSON = new JSONObject();
            mensajeJSON.put("remitente", mensaje.getRemitente().getId_usuario());
            mensajeJSON.put("nameRemitente", mensaje.getRemitente().getNombre_user());

            if (mensaje.getContenidoTexto() != null) {
                mensajeJSON.put("contenidoTexto", mensaje.getContenidoTexto());
            } 
            if (mensaje.getFormularioTransferencia() != null) {
                mensajeJSON.put("contenido", mensaje.getFormularioTransferencia().getId_formularioTransferencia());
            }
            
            mensajeJSON.put("receptor", mensaje.getDestino().getId_usuario());
            mensajeJSON.put("fechaRegistro", mensaje.getFechaRegistro());
            mensajeJSON.put("horaRegistro", mensaje.getHoraRegistro());
            mensajeJSON.put("tipoMensaje", mensaje.getTipoMensaje());

            mensajesJSON.add(mensajeJSON);
        }

        return ResponseEntity.ok(mensajesJSON);
    }

}
