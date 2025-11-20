package com.vinisnzy.connectus_api.infra.client;

import com.vinisnzy.connectus_api.domain.automation.dto.request.WhatsAppQRCodeRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.response.WhatsAppQRCodeResponse;
import com.vinisnzy.connectus_api.domain.messaging.dto.request.SendMessageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Alterar a URL quando for para produção
@FeignClient(url = "https://n8n.flowmind.fun/webhook-test", name = "N8nClient")
public interface N8nClient {

    @PostMapping("/send-message")
    void sendMessage(@RequestBody SendMessageRequest request);

    @GetMapping("/qr-code")
    WhatsAppQRCodeResponse getQrCode(@RequestBody WhatsAppQRCodeRequest request);
}