package com.ymanch.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ActiveSessionTracker {
//	This class will track which users are actively connected to your WebSocket.
	// Map of userId to their active session IDs
	private static final ConcurrentHashMap<Long, CopyOnWriteArraySet<String>> activeSessions = new ConcurrentHashMap<>();

	// Add a new session for a user
	public static void addSession(Long userId, String sessionId) {
		activeSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(sessionId);
	}

	// Remove a session for a user
	public static void removeSession(Long userId, String sessionId) {
		if (activeSessions.containsKey(userId)) {
			CopyOnWriteArraySet<String> sessions = activeSessions.get(userId);
			sessions.remove(sessionId);
			if (sessions.isEmpty()) {
				activeSessions.remove(userId);
			}
		}
	}

	// Get all active sessions for a user
	public static CopyOnWriteArraySet<String> getSessions(Long userId) {
		return activeSessions.getOrDefault(userId, new CopyOnWriteArraySet<>());
	}

	// Get all active users
	public static CopyOnWriteArraySet<Long> getActiveUsers() {
		return new CopyOnWriteArraySet<>(activeSessions.keySet());
	}
}
