package com.vinisnzy.connectus_api.domain.automation.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.automation.dto.request.UpdateWhatsAppConnectionRequest;
import com.vinisnzy.connectus_api.domain.automation.dto.response.WhatsAppConnectionResponse;
import com.vinisnzy.connectus_api.domain.automation.entity.WhatsAppConnection;
import com.vinisnzy.connectus_api.domain.automation.mapper.WhatsAppConnectionMapper;
import com.vinisnzy.connectus_api.domain.automation.repository.WhatsAppConnectionRepository;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WhatsAppConnectionService Unit Tests")
class WhatsAppConnectionServiceTest {

    @Mock
    private WhatsAppConnectionRepository whatsAppConnectionRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private WhatsAppConnectionMapper mapper;

    @InjectMocks
    private WhatsAppConnectionService whatsAppConnectionService;

    private UUID companyId;
    private UUID connectionId;
    private Company company;
    private WhatsAppConnection connection;
    private WhatsAppConnectionResponse connectionResponse;
    private UpdateWhatsAppConnectionRequest updateConnectionRequest;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        connectionId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);

        connection = new WhatsAppConnection();
        connection.setId(connectionId);
        connection.setCompany(company);

        connectionResponse = WhatsAppConnectionResponse.builder()
                .id(connectionId)
                .build();

        updateConnectionRequest = new UpdateWhatsAppConnectionRequest(
                connectionId,
                "Test connection"
        );
    }

    @Test
    @DisplayName("Should find connection by company")
    void shouldFindConnectionByCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(whatsAppConnectionRepository.findByCompanyId(companyId)).thenReturn(List.of(connection));
            when(mapper.toResponse(connection)).thenReturn(connectionResponse);

            List<WhatsAppConnectionResponse> result = whatsAppConnectionService.getAll();

            assertThat(result).isEqualTo(List.of(connectionResponse));
        }
    }

    @Test
    @DisplayName("Should return empty list when no connections found")
    void shouldReturnEmptyListWhenNoConnectionsFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(whatsAppConnectionRepository.findByCompanyId(companyId)).thenReturn(List.of());

            List<WhatsAppConnectionResponse> result = whatsAppConnectionService.getAll();

            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("Should find connection by id")
    void shouldFindConnectionById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(whatsAppConnectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
            when(mapper.toResponse(connection)).thenReturn(connectionResponse);

            WhatsAppConnectionResponse result = whatsAppConnectionService.getById(connectionId);

            assertThat(result).isEqualTo(connectionResponse);
            verify(whatsAppConnectionRepository).findById(connectionId);
        }
    }

    @Test
    @DisplayName("Should throw exception when connection not found by id")
    void shouldThrowExceptionWhenConnectionNotFoundById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(whatsAppConnectionRepository.findById(connectionId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> whatsAppConnectionService.getById(connectionId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Conexão WhatsApp não encontrada");
        }
    }

    @Test
    @DisplayName("Should update connection successfully")
    void shouldUpdateConnection() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(whatsAppConnectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
            when(whatsAppConnectionRepository.save(connection)).thenReturn(connection);
            when(mapper.toResponse(connection)).thenReturn(connectionResponse);

            WhatsAppConnectionResponse result = whatsAppConnectionService.update(updateConnectionRequest);

            assertThat(result).isEqualTo(connectionResponse);
            verify(whatsAppConnectionRepository).save(connection);
        }
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent connection")
    void shouldThrowExceptionWhenUpdatingNonExistentConnection() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(whatsAppConnectionRepository.findById(connectionId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> whatsAppConnectionService.update(updateConnectionRequest))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Should delete connection successfully")
    void shouldDeleteConnection() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);

            whatsAppConnectionService.delete(connectionId);

            verify(whatsAppConnectionRepository).deleteById(connectionId);
        }
    }

    @Test
    @DisplayName("Should return true when connection is connected")
    void shouldReturnTrueWhenConnectionIsConnected() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            WhatsAppConnectionResponse connectedResponse = WhatsAppConnectionResponse.builder()
                    .id(connectionId)
                    .lastConnectedAt(LocalDateTime.now())
                    .build();

            when(whatsAppConnectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
            when(mapper.toResponse(connection)).thenReturn(connectedResponse);

            Boolean result = whatsAppConnectionService.isConnected(connectionId);

            assertThat(result).isTrue();
        }
    }

    @Test
    @DisplayName("Should return false when connection is not connected")
    void shouldReturnFalseWhenConnectionIsNotConnected() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            WhatsAppConnectionResponse disconnectedResponse = WhatsAppConnectionResponse.builder()
                    .id(connectionId)
                    .lastConnectedAt(null)
                    .build();

            when(whatsAppConnectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
            when(mapper.toResponse(connection)).thenReturn(disconnectedResponse);

            Boolean result = whatsAppConnectionService.isConnected(connectionId);

            assertThat(result).isFalse();
        }
    }
}
