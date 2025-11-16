package com.vinisnzy.connectus_api.domain.scheduling.mapper;

import com.vinisnzy.connectus_api.domain.scheduling.dto.request.CreateAppointmentRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.request.RescheduleAppointmentRequest;
import com.vinisnzy.connectus_api.domain.scheduling.dto.response.AppointmentResponse;
import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.crm.entity.Contact;
import com.vinisnzy.connectus_api.domain.scheduling.entity.Appointment;
import com.vinisnzy.connectus_api.domain.scheduling.entity.Service;
import com.vinisnzy.connectus_api.domain.scheduling.entity.enums.AppointmentStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "service", source = "serviceId")
    @Mapping(target = "contact", source = "contactId")
    @Mapping(target = "assignedUser", source = "assignedUserId")
    @Mapping(target = "status", constant = "SCHEDULED")
    @Mapping(target = "reminderSent", constant = "false")
    @Mapping(target = "reminderSentAt", ignore = true)
    @Mapping(target = "canceledAt", ignore = true)
    @Mapping(target = "cancellationReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Appointment toEntity(CreateAppointmentRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "contact", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    @Mapping(target = "startTime", source = "newStartTime")
    @Mapping(target = "endTime", source = "newEndTime")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "reminderSent", ignore = true)
    @Mapping(target = "reminderSentAt", ignore = true)
    @Mapping(target = "canceledAt", ignore = true)
    @Mapping(target = "cancellationReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void reschedule(RescheduleAppointmentRequest request, @MappingTarget Appointment appointment);

    @Mapping(target = "status", source = "status")
    AppointmentResponse toResponse(Appointment appointment);

    default Company mapCompany(java.util.UUID companyId) {
        if (companyId == null) return null;
        Company company = new Company();
        company.setId(companyId);
        return company;
    }

    default Service mapService(java.util.UUID serviceId) {
        if (serviceId == null) return null;
        Service service = new Service();
        service.setId(serviceId);
        return service;
    }

    default Contact mapContact(java.util.UUID contactId) {
        if (contactId == null) return null;
        Contact contact = new Contact();
        contact.setId(contactId);
        return contact;
    }

    default User mapUser(java.util.UUID userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }

    default String mapAppointmentStatusToString(AppointmentStatus status) {
        if (status == null) return null;
        return status.name();
    }

    default java.time.ZonedDateTime map(java.time.LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.atZone(java.time.ZoneId.systemDefault());
    }
}
