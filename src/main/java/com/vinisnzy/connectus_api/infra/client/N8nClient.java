package com.vinisnzy.connectus_api.infra.client;

import com.vinisnzy.connectus_api.domain.messaging.dto.request.SendMessageRequest;
import com.vinisnzy.connectus_api.domain.messaging.dto.response.SendMessageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Alterar a URL quando for para produção
@FeignClient(url = "https://n8n.flowmind.fun/webhook-test", name = "N8nClient")
public interface N8nClient {

    @PostMapping("/send-message")
    SendMessageResponse sendMessage(@RequestBody SendMessageRequest request);
}