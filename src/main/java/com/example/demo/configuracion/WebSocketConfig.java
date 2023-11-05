package com.example.demo.configuracion;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer {

        @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/user"); // Define el prefijo para los canales de mensajes
        config.setApplicationDestinationPrefixes("/app"); // Define el prefijo para los mensajes enviados desde el cliente al servidor
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Define el punto de conexión para los clientes WebSocket
        registry.addEndpoint("/salonA").withSockJS();
        
    }
}
