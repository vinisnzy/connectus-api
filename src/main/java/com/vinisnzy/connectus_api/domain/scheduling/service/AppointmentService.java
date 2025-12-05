package com.vinisnzy.connectus_api.domain.scheduling.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.analytics.service.ActivityLogService;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
import com.vinisnzy.connectus_api.domain.crm.entity.Contact;
import com.vinisnzy.connectus_api.domain.crm.repository.ContactRepository;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.CreateAppointmentRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.RescheduleAppointmentRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.UpdateAppointmentRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.response.AppointmentResponse;
import com.vinisnzy.connectus_api.domain.scheduling.entity.Appointment;
import com.vinisnzy.connectus_api.domain.scheduling.entity.enums.AppointmentStatus;
import com.vinisnzy.connectus_api.domain.scheduling.mapper.AppointmentMapper;
import com.vinisnzy.connectus_api.domain.scheduling.repository.AppointmentRepository;
import com.vinisnzy.connectus_api.domain.scheduling.repository.ServiceRepository;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CompanyRepository companyRepository;
    private final ServiceRepository serviceRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final AppointmentMapper mapper;

    public List<AppointmentResponse> findAll(Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<Appointment> appointments = appointmentRepository.findByCompanyIdOrderByStartTimeDesc(companyId, pageable);
        return appointments.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public AppointmentResponse findById(UUID id) {
        Appointment appointment = getAppointmentOrThrow(id);
        validateAppointmentBelongsToCompany(appointment);
        return mapper.toResponse(appointment);
    }

    public List<AppointmentResponse> findByContactId(UUID contactId, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findByContactIdOrderByStartTimeDesc(contactId, pageable);
        return appointments.getContent()
                .stream()
                .filter(appointment -> appointment.getCompany().getId().equals(SecurityUtils.getCurrentCompanyIdOrThrow()))
                .map(mapper::toResponse)
                .toList();
    }

    public List<AppointmentResponse> findByAssignedUserId(UUID userId, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findByAssignedUserIdOrderByStartTimeAsc(userId, pageable);
        return appointments.getContent()
                .stream()
                .filter(appointment -> appointment.getCompany().getId().equals(SecurityUtils.getCurrentCompanyIdOrThrow()))
                .map(mapper::toResponse)
                .toList();
    }

    public List<AppointmentResponse> findByServiceId(UUID serviceId, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findByServiceIdOrderByStartTimeDesc(serviceId, pageable);
        return appointments.getContent()
                .stream()
                .filter(appointment -> appointment.getCompany().getId().equals(SecurityUtils.getCurrentCompanyIdOrThrow()))
                .map(mapper::toResponse)
                .toList();
    }

    public List<AppointmentResponse> findByStatus(AppointmentStatus status, Pageable pageable) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        Page<Appointment> appointments = appointmentRepository.findByCompanyIdAndStatusOrderByStartTimeAsc(companyId, status, pageable);
        return appointments.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<AppointmentResponse> findByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        List<Appointment> appointments = appointmentRepository.findByCompanyIdAndDateRange(companyId, startDate, endDate);
        return appointments.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public AppointmentResponse create(CreateAppointmentRequest request) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        UUID assignedUserId = SecurityUtils.getCurrentUserIdOrThrow();

        UUID serviceId = request.serviceId();
        UUID contactId = request.contactId();

        validateEndTimeAfterStartTimeInAppointment(request.startTime(), request.endTime());

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o id: " + companyId));

        com.vinisnzy.connectus_api.domain.scheduling.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado com o id: " + serviceId));

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Contato não encontrado com o id: " + contactId));

        if (!contact.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Contato não pertence à empresa atual.");
        }

        Appointment appointment = mapper.toEntity(request);
        appointment.setCompany(company);
        appointment.setService(service);
        appointment.setContact(contact);
        appointment.setReminderSent(false);

        User user = userRepository.findById(assignedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + assignedUserId));
        if (!user.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Usuário não pertence à empresa atual.");
        }
        appointment.setAssignedUser(user);

        appointment = appointmentRepository.save(appointment);

        activityLogService.log("ENTITY_CREATED", "Appointment", appointment.getId());

        return mapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse update(UUID id, UpdateAppointmentRequest request) {
        Appointment appointment = getAppointmentOrThrow(id);
        ZonedDateTime startTime = request.startTime();
        ZonedDateTime endTime = request.endTime();
        String notes = request.notes();

        validateAppointmentBelongsToCompany(appointment);

        validateEndTimeAfterStartTimeInAppointment(startTime, endTime);

        if (startTime != null) {
            appointment.setStartTime(startTime);
        }
        if (endTime != null) {
            appointment.setEndTime(endTime);
        }
        if (notes != null) {
            appointment.setNotes(notes);
        }

        appointment = appointmentRepository.save(appointment);

        activityLogService.log("ENTITY_UPDATED", "Appointment", appointment.getId());

        return mapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse assign(UUID id, UUID userId) {
        Appointment appointment = getAppointmentOrThrow(id);

        validateAppointmentBelongsToCompany(appointment);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + userId));

        if (!user.getCompany().getId().equals(SecurityUtils.getCurrentCompanyIdOrThrow())) {
            throw new IllegalStateException("Usuário não pertence à empresa atual.");
        }

        appointment.setAssignedUser(user);
        appointment = appointmentRepository.save(appointment);

        activityLogService.log("ENTITY_UPDATED", "Appointment", appointment.getId());

        return mapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse updateStatus(UUID id, AppointmentStatus newStatus) {
        Appointment appointment = getAppointmentOrThrow(id);

        validateAppointmentBelongsToCompany(appointment);

        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(newStatus);

        if (newStatus == AppointmentStatus.CANCELED && oldStatus != AppointmentStatus.CANCELED) {
            appointment.setCanceledAt(ZonedDateTime.now());
        }

        appointment = appointmentRepository.save(appointment);

        activityLogService.log("STATUS_CHANGED", "Appointment", appointment.getId());

        return mapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse confirm(UUID id) {
        Appointment appointment = getAppointmentOrThrow(id);

        validateAppointmentBelongsToCompany(appointment);

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment = appointmentRepository.save(appointment);

        activityLogService.log("STATUS_CHANGED", "Appointment", appointment.getId());

        return mapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse complete(UUID id) {
        Appointment appointment = getAppointmentOrThrow(id);

        validateAppointmentBelongsToCompany(appointment);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment = appointmentRepository.save(appointment);

        activityLogService.log("STATUS_CHANGED", "Appointment", appointment.getId());

        return mapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse cancel(UUID id, String reason) {
        Appointment appointment = getAppointmentOrThrow(id);

        validateAppointmentBelongsToCompany(appointment);

        appointment.setStatus(AppointmentStatus.CANCELED);
        appointment.setCanceledAt(ZonedDateTime.now());
        appointment.setCancellationReason(reason);

        appointment = appointmentRepository.save(appointment);

        activityLogService.log("STATUS_CHANGED", "Appointment", appointment.getId());

        return mapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse markNoShow(UUID id) {
        Appointment appointment = getAppointmentOrThrow(id);

        validateAppointmentBelongsToCompany(appointment);

        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointment = appointmentRepository.save(appointment);

        activityLogService.log("STATUS_CHANGED", "Appointment", appointment.getId());

        return mapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse reschedule(UUID id, RescheduleAppointmentRequest request) {
        Appointment appointment = getAppointmentOrThrow(id);

        validateAppointmentBelongsToCompany(appointment);

        if (request.newEndTime().isBefore(request.newStartTime())) {
            throw new IllegalStateException("O horário de término deve ser após o horário de início.");
        }

        appointment.setStartTime(request.newStartTime());
        appointment.setEndTime(request.newEndTime());

        appointment = appointmentRepository.save(appointment);

        activityLogService.log("ENTITY_UPDATED", "Appointment", appointment.getId());

        return mapper.toResponse(appointment);
    }

    @Transactional
    public void sendReminder(UUID id) {
        Appointment appointment = getAppointmentOrThrow(id);
        validateAppointmentBelongsToCompany(appointment);

        appointment.setReminderSent(true);
        appointment.setReminderSentAt(ZonedDateTime.now());

        appointmentRepository.save(appointment);
    }

    public boolean isTimeSlotAvailable(UUID userId, ZonedDateTime startTime, ZonedDateTime endTime, UUID excludeAppointmentId) {
        List<Appointment> conflicting = appointmentRepository.findConflictingAppointments(userId, startTime, endTime);

        if (excludeAppointmentId != null) {
            conflicting = conflicting.stream()
                    .filter(appointment -> !appointment.getId().equals(excludeAppointmentId))
                    .toList();
        }

        return conflicting.isEmpty();
    }

    public List<AppointmentResponse> findUpcoming(int limit) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime future = now.plusMonths(1);

        List<Appointment> appointments = appointmentRepository.findByCompanyIdAndDateRange(companyId, now, future);
        return appointments.stream()
                .limit(limit)
                .map(mapper::toResponse)
                .toList();
    }

    public Long countByStatus(AppointmentStatus status) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        return appointmentRepository.countByCompanyAndStatus(companyId, status);
    }

    public List<AppointmentResponse> getLastAppointments(UUID contactId, int limit) {
        List<Appointment> appointments = appointmentRepository.findTop10ByContactIdOrderByStartTimeDesc(contactId);
        return appointments.stream()
                .filter(appointment -> appointment.getCompany().getId().equals(SecurityUtils.getCurrentCompanyIdOrThrow()))
                .limit(limit)
                .map(mapper::toResponse)
                .toList();
    }

    private void validateEndTimeAfterStartTimeInAppointment(ZonedDateTime startTime, ZonedDateTime endTime) {
        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalStateException("O horário de término deve ser após o horário de início.");
        }
    }

    private Appointment getAppointmentOrThrow(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado com o id: " + id));
    }

    private void validateAppointmentBelongsToCompany(Appointment appointment) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        if (!appointment.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Agendamento não pertence à empresa atual.");
        }
    }
}
