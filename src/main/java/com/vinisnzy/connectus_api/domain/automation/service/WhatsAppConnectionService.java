package com.vinisnzy.connectus_api.domain.automation.service;

import com.vinisnzy.connectus_api.domain.automation.dto.request.CreateWhatsAppConnectionRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.request.UpdateWhatsAppConnectionRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.request.WhatsAppQRCodeRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.response.WhatsAppConnectionResponse;
import com.vinisnzy.connectus_api.domain.automation.dto.response.WhatsAppQRCodeResponse;
import com.vinisnzy.connectus_api.domain.automation.entity.WhatsAppConnection;
import com.vinisnzy.connectus_api.domain.automation.mapper.WhatsAppConnectionMapper;
import com.vinisnzy.connectus_api.domain.automation.repository.WhatsAppConnectionRepository;
import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.infra.client.N8nClient;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import com.vinisnzy.connectus_api.infra.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WhatsAppConnectionService {

    private final WhatsAppConnectionRepository whatsAppConnectionRepository;
    private final CompanyRepository companyRepository;

    private final WhatsAppConnectionMapper mapper;

    private final N8nClient n8nClient;

    private final WebSocketService webSocketService;

    public WhatsAppConnectionResponse create(CreateWhatsAppConnectionRequest request) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + companyId));
        WhatsAppConnection whatsAppConnection = mapper.toEntity(request, company);
        whatsAppConnectionRepository.save(whatsAppConnection);
        return mapper.toResponse(whatsAppConnection);
    }

    public List<WhatsAppConnectionResponse> getAll() {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        return whatsAppConnectionRepository.findByCompanyId(companyId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public WhatsAppConnectionResponse getById(UUID id) {
        WhatsAppConnection whatsAppConnection = whatsAppConnectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conexão WhatsApp não encontrada com id: " + id));
        return mapper.toResponse(whatsAppConnection);
    }

    public WhatsAppConnectionResponse update(UpdateWhatsAppConnectionRequest request) {
        WhatsAppConnection whatsAppConnection = whatsAppConnectionRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Conexão WhatsApp não encontrada com id: " + request.id()));
        whatsAppConnection.setDisplayName(request.connectionName());
        whatsAppConnectionRepository.save(whatsAppConnection);
        return mapper.toResponse(whatsAppConnection);
    }

    public void delete(UUID id) {
        whatsAppConnectionRepository.deleteById(id);
    }

    public void sendConnectionToUser(UUID whatsAppConnectionId) {
        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
        WhatsAppConnection whatsAppConnection = whatsAppConnectionRepository.findById(whatsAppConnectionId)
                .orElseThrow(() -> new EntityNotFoundException("Conexão WhatsApp não encontrada com id: " + whatsAppConnectionId));
        WhatsAppConnectionResponse payload = mapper.toResponse(whatsAppConnection);
        webSocketService.sendToUser(userId, "/queue/whatsapp-connections", payload);
    }

    public void sendQRCodeToUser(UUID whatsAppConnectionId) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
        WhatsAppQRCodeResponse payload = n8nClient.getQrCode(new WhatsAppQRCodeRequest(companyId, userId, whatsAppConnectionId));
        webSocketService.sendToUser(userId, "/queue/whatsapp-connections", payload);
    }

    public Boolean isConnected(UUID whatsAppConnectionId) {
        WhatsAppConnectionResponse whatsAppConnectionResponse = getById(whatsAppConnectionId);
        return whatsAppConnectionResponse.lastConnectedAt() != null;
    }
}
