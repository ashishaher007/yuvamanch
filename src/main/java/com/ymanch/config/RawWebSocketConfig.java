package com.ymanch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
@Configuration
@EnableWebSocket
public class RawWebSocketConfig implements WebSocketConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(RawWebSocketConfig.class);

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                logger.info("Raw WebSocket connection established with session ID: " + session.getId());
                session.sendMessage(new org.springframework.web.socket.TextMessage("Welcome to the raw WebSocket!"));
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
                logger.info("Raw WebSocket connection closed with session ID: " + session.getId());
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, org.springframework.web.socket.TextMessage message) throws Exception {
                logger.info("Raw WebSocket message received: " + message.getPayload());
                session.sendMessage(new org.springframework.web.socket.TextMessage("Echo: " + message.getPayload()));
            }
        }, "/ws").setAllowedOriginPatterns("*");
    }
}
