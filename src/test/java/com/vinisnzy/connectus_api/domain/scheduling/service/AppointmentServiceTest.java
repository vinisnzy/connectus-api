package com.vinisnzy.connectus_api.domain.scheduling.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
import com.vinisnzy.connectus_api.domain.crm.entity.Contact;
import com.vinisnzy.connectus_api.domain.crm.repository.ContactRepository;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.CreateAppointmentRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.UpdateAppointmentRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.response.AppointmentResponse;
import com.vinisnzy.connectus_api.domain.scheduling.entity.Appointment;
import com.vinisnzy.connectus_api.domain.scheduling.entity.enums.AppointmentStatus;
import com.vinisnzy.connectus_api.domain.scheduling.mapper.AppointmentMapper;
import com.vinisnzy.connectus_api.domain.scheduling.repository.AppointmentRepository;
import com.vinisnzy.connectus_api.domain.scheduling.repository.ServiceRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Unit Tests")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentMapper mapper;

    @InjectMocks
    private AppointmentService appointmentService;

    private UUID companyId;
    private UUID appointmentId;
    private UUID serviceId;
    private UUID contactId;
    private UUID userId;
    private Company company;
    private com.vinisnzy.connectus_api.domain.scheduling.entity.Service service;
    private Contact contact;
    private User user;
    private Appointment appointment;
    private AppointmentResponse appointmentResponse;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
        contactId = UUID.randomUUID();
        userId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);

        service = new com.vinisnzy.connectus_api.domain.scheduling.entity.Service();
        service.setId(serviceId);
        service.setCompany(company);
        service.setName("Test Service");

        contact = new Contact();
        contact.setId(contactId);
        contact.setCompany(company);

        user = new User();
        user.setId(userId);
        user.setCompany(company);

        appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setCompany(company);
        appointment.setService(service);
        appointment.setContact(contact);
        appointment.setAssignedUser(user);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setStartTime(ZonedDateTime.now().plusDays(1));
        appointment.setEndTime(ZonedDateTime.now().plusDays(1).plusHours(1));

        appointmentResponse = AppointmentResponse.builder()
                .id(appointmentId)
                .status("SCHEDULED")
                .build();
    }

    @Test
    @DisplayName("Should find all appointments for company")
    void shouldFindAllAppointments() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Appointment> appointmentPage = new PageImpl<>(List.of(appointment));

            when(appointmentRepository.findByCompanyIdOrderByStartTimeDesc(companyId, pageable))
                    .thenReturn(appointmentPage);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            List<AppointmentResponse> result = appointmentService.findAll(pageable);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.getFirst()).isEqualTo(appointmentResponse);
            verify(appointmentRepository).findByCompanyIdOrderByStartTimeDesc(companyId, pageable);
        }
    }

    @Test
    @DisplayName("Should find appointment by id")
    void shouldFindAppointmentById() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            AppointmentResponse result = appointmentService.findById(appointmentId);

            // Assert
            assertThat(result).isEqualTo(appointmentResponse);
            verify(appointmentRepository).findById(appointmentId);
        }
    }

    @Test
    @DisplayName("Should throw exception when appointment not found")
    void shouldThrowExceptionWhenAppointmentNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> appointmentService.findById(appointmentId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Agendamento não encontrado");

            verify(appointmentRepository).findById(appointmentId);
        }
    }

    @Test
    @DisplayName("Should create appointment successfully")
    void shouldCreateAppointment() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            CreateAppointmentRequest request = new CreateAppointmentRequest(
                    contactId,
                    serviceId,
                    userId,
                    ZonedDateTime.now().plusDays(1),
                    ZonedDateTime.now().plusDays(1).plusHours(1),
                    "Test notes"
            );

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
            when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
            when(mapper.toEntity(request)).thenReturn(appointment);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            AppointmentResponse result = appointmentService.create(request);

            // Assert
            assertThat(result).isEqualTo(appointmentResponse);
            verify(appointmentRepository).save(any(Appointment.class));
        }
    }

    @Test
    @DisplayName("Should cancel appointment successfully")
    void shouldCancelAppointment() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            String reason = "Client request";

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(appointmentRepository.save(appointment)).thenReturn(appointment);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            AppointmentResponse result = appointmentService.cancel(appointmentId, reason);

            // Assert
            assertThat(result).isEqualTo(appointmentResponse);
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELED);
            assertThat(appointment.getCancellationReason()).isEqualTo(reason);
            assertThat(appointment.getCanceledAt()).isNotNull();
            verify(appointmentRepository).save(appointment);
        }
    }

    @Test
    @DisplayName("Should confirm appointment successfully")
    void shouldConfirmAppointment() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(appointmentRepository.save(appointment)).thenReturn(appointment);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            AppointmentResponse result = appointmentService.confirm(appointmentId);

            // Assert
            assertThat(result).isEqualTo(appointmentResponse);
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
            verify(appointmentRepository).save(appointment);
        }
    }

    @Test
    @DisplayName("Should complete appointment successfully")
    void shouldCompleteAppointment() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(appointmentRepository.save(appointment)).thenReturn(appointment);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            AppointmentResponse result = appointmentService.complete(appointmentId);

            // Assert
            assertThat(result).isEqualTo(appointmentResponse);
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
            verify(appointmentRepository).save(appointment);
        }
    }

    @Test
    @DisplayName("Should find appointments by contact id")
    void shouldFindAppointmentsByContactId() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Appointment> appointmentPage = new PageImpl<>(List.of(appointment));

            when(appointmentRepository.findByContactIdOrderByStartTimeDesc(contactId, pageable))
                    .thenReturn(appointmentPage);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            List<AppointmentResponse> result = appointmentService.findByContactId(contactId, pageable);

            // Assert
            assertThat(result).hasSize(1);
            verify(appointmentRepository).findByContactIdOrderByStartTimeDesc(contactId, pageable);
        }
    }

    @Test
    @DisplayName("Should find appointments by assigned user id")
    void shouldFindAppointmentsByAssignedUserId() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Appointment> appointmentPage = new PageImpl<>(List.of(appointment));

            when(appointmentRepository.findByAssignedUserIdOrderByStartTimeAsc(userId, pageable))
                    .thenReturn(appointmentPage);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            List<AppointmentResponse> result = appointmentService.findByAssignedUserId(userId, pageable);

            // Assert
            assertThat(result).hasSize(1);
            verify(appointmentRepository).findByAssignedUserIdOrderByStartTimeAsc(userId, pageable);
        }
    }

    @Test
    @DisplayName("Should find appointments by status")
    void shouldFindAppointmentsByStatus() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Appointment> appointmentPage = new PageImpl<>(List.of(appointment));

            when(appointmentRepository.findByCompanyIdAndStatusOrderByStartTimeAsc(companyId, AppointmentStatus.SCHEDULED, pageable))
                    .thenReturn(appointmentPage);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            List<AppointmentResponse> result = appointmentService.findByStatus(AppointmentStatus.SCHEDULED, pageable);

            // Assert
            assertThat(result).hasSize(1);
            verify(appointmentRepository).findByCompanyIdAndStatusOrderByStartTimeAsc(companyId, AppointmentStatus.SCHEDULED, pageable);
        }
    }

    @Test
    @DisplayName("Should find appointments by date range")
    void shouldFindAppointmentsByDateRange() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            ZonedDateTime startDate = ZonedDateTime.now();
            ZonedDateTime endDate = ZonedDateTime.now().plusDays(7);

            when(appointmentRepository.findByCompanyIdAndDateRange(companyId, startDate, endDate))
                    .thenReturn(List.of(appointment));
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            List<AppointmentResponse> result = appointmentService.findByDateRange(startDate, endDate);

            // Assert
            assertThat(result).hasSize(1);
            verify(appointmentRepository).findByCompanyIdAndDateRange(companyId, startDate, endDate);
        }
    }

    @Test
    @DisplayName("Should update appointment successfully")
    void shouldUpdateAppointment() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            UpdateAppointmentRequest request = new UpdateAppointmentRequest(
                    appointmentId,
                    ZonedDateTime.now().plusDays(2),
                    ZonedDateTime.now().plusDays(2).plusHours(1),
                    "Updated notes"
            );

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(appointmentRepository.save(appointment)).thenReturn(appointment);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            AppointmentResponse result = appointmentService.update(request);

            // Assert
            assertThat(result).isEqualTo(appointmentResponse);
            verify(appointmentRepository).save(appointment);
        }
    }

    @Test
    @DisplayName("Should throw exception when updating appointment with end time before start time")
    void shouldThrowExceptionWhenUpdatingAppointmentWithInvalidTimeRange() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            UpdateAppointmentRequest request = new UpdateAppointmentRequest(
                    appointmentId,
                    ZonedDateTime.now().plusDays(2).plusHours(2),
                    ZonedDateTime.now().plusDays(2),
                    "Updated notes"
            );

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

            // Act & Assert
            assertThatThrownBy(() -> appointmentService.update(request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("horário de término deve ser após");
        }
    }

    @Test
    @DisplayName("Should assign user to appointment")
    void shouldAssignUserToAppointment() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            UUID newUserId = UUID.randomUUID();
            User newUser = new User();
            newUser.setId(newUserId);
            newUser.setCompany(company);

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
            when(appointmentRepository.save(appointment)).thenReturn(appointment);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            AppointmentResponse result = appointmentService.assign(appointmentId, newUserId);

            // Assert
            assertThat(result).isEqualTo(appointmentResponse);
            assertThat(appointment.getAssignedUser()).isEqualTo(newUser);
            verify(appointmentRepository).save(appointment);
        }
    }

    @Test
    @DisplayName("Should throw exception when assigning user from different company")
    void shouldThrowExceptionWhenAssigningUserFromDifferentCompany() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            UUID newUserId = UUID.randomUUID();
            Company differentCompany = new Company();
            differentCompany.setId(UUID.randomUUID());
            User newUser = new User();
            newUser.setId(newUserId);
            newUser.setCompany(differentCompany);

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));

            // Act & Assert
            assertThatThrownBy(() -> appointmentService.assign(appointmentId, newUserId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Usuário não pertence à empresa atual.");
        }
    }

    @Test
    @DisplayName("Should update appointment status")
    void shouldUpdateAppointmentStatus() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(appointmentRepository.save(appointment)).thenReturn(appointment);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            AppointmentResponse result = appointmentService.updateStatus(appointmentId, AppointmentStatus.CONFIRMED);

            // Assert
            assertThat(result).isEqualTo(appointmentResponse);
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
            verify(appointmentRepository).save(appointment);
        }
    }

    @Test
    @DisplayName("Should mark appointment as no show")
    void shouldMarkAppointmentAsNoShow() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(appointmentRepository.save(appointment)).thenReturn(appointment);
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            AppointmentResponse result = appointmentService.markNoShow(appointmentId);

            // Assert
            assertThat(result).isEqualTo(appointmentResponse);
            assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.NO_SHOW);
            verify(appointmentRepository).save(appointment);
        }
    }

    @Test
    @DisplayName("Should check if time slot is available")
    void shouldCheckIfTimeSlotIsAvailable() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            ZonedDateTime startTime = ZonedDateTime.now().plusDays(1);
            ZonedDateTime endTime = ZonedDateTime.now().plusDays(1).plusHours(1);

            when(appointmentRepository.findConflictingAppointments(userId, startTime, endTime))
                    .thenReturn(List.of());

            // Act
            boolean result = appointmentService.isTimeSlotAvailable(userId, startTime, endTime, null);

            // Assert
            assertThat(result).isTrue();
        }
    }

    @Test
    @DisplayName("Should return false when time slot is not available")
    void shouldReturnFalseWhenTimeSlotIsNotAvailable() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            ZonedDateTime startTime = ZonedDateTime.now().plusDays(1);
            ZonedDateTime endTime = ZonedDateTime.now().plusDays(1).plusHours(1);

            when(appointmentRepository.findConflictingAppointments(userId, startTime, endTime))
                    .thenReturn(List.of(appointment));

            // Act
            boolean result = appointmentService.isTimeSlotAvailable(userId, startTime, endTime, null);

            // Assert
            assertThat(result).isFalse();
        }
    }

    @Test
    @DisplayName("Should find upcoming appointments")
    void shouldFindUpcomingAppointments() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(appointmentRepository.findByCompanyIdAndDateRange(eq(companyId), any(ZonedDateTime.class), any(ZonedDateTime.class)))
                    .thenReturn(List.of(appointment));
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            List<AppointmentResponse> result = appointmentService.findUpcoming(10);

            // Assert
            assertThat(result).hasSize(1);
            verify(appointmentRepository).findByCompanyIdAndDateRange(eq(companyId), any(ZonedDateTime.class), any(ZonedDateTime.class));
        }
    }

    @Test
    @DisplayName("Should count appointments by status")
    void shouldCountAppointmentsByStatus() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(appointmentRepository.countByCompanyAndStatus(companyId, AppointmentStatus.SCHEDULED))
                    .thenReturn(5L);

            // Act
            Long result = appointmentService.countByStatus(AppointmentStatus.SCHEDULED);

            // Assert
            assertThat(result).isEqualTo(5L);
            verify(appointmentRepository).countByCompanyAndStatus(companyId, AppointmentStatus.SCHEDULED);
        }
    }

    @Test
    @DisplayName("Should get last appointments for contact")
    void shouldGetLastAppointmentsForContact() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            when(appointmentRepository.findTop10ByContactIdOrderByStartTimeDesc(contactId))
                    .thenReturn(List.of(appointment));
            when(mapper.toResponse(appointment)).thenReturn(appointmentResponse);

            // Act
            List<AppointmentResponse> result = appointmentService.getLastAppointments(contactId, 5);

            // Assert
            assertThat(result).hasSize(1);
            verify(appointmentRepository).findTop10ByContactIdOrderByStartTimeDesc(contactId);
        }
    }

    @Test
    @DisplayName("Should throw exception when creating appointment with end time before start time")
    void shouldThrowExceptionWhenCreatingAppointmentWithInvalidTimeRange() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            // Arrange
            mockedSecurity.when(SecurityUtils::getCurrentCompanyIdOrThrow).thenReturn(companyId);
            CreateAppointmentRequest request = new CreateAppointmentRequest(
                    contactId,
                    serviceId,
                    userId,
                    ZonedDateTime.now().plusDays(1).plusHours(2),
                    ZonedDateTime.now().plusDays(1),
                    "Test notes"
            );

            // Act & Assert
            assertThatThrownBy(() -> appointmentService.create(request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("O horário de término deve ser após o horário de início.");
        }
    }
}
