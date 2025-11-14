package com.vinisnzy.connectus_api.domain.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(UUID userId, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                destination,
                payload
        );
    }

    public void sendToTopic(String topic, Object payload) {
        messagingTemplate.convertAndSend(topic, payload);
    }
}
