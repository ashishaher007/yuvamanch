package com.ymanch.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
//	You need to update the session tracker when users connect to or disconnect from the WebSocket.
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		// Assuming you pass `userId` in the WebSocket handshake headers
		Long userId = Long.valueOf(headerAccessor.getFirstNativeHeader("userId"));
		String sessionId = headerAccessor.getSessionId();

		ActiveSessionTracker.addSession(userId, sessionId);
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		Long userId = Long.valueOf(headerAccessor.getFirstNativeHeader("userId"));
		String sessionId = headerAccessor.getSessionId();

		ActiveSessionTracker.removeSession(userId, sessionId);
	}
}
