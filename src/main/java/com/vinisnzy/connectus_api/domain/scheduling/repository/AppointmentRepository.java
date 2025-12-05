package com.vinisnzy.connectus_api.domain.scheduling.repository;

import com.vinisnzy.connectus_api.domain.scheduling.entity.Appointment;
import com.vinisnzy.connectus_api.domain.scheduling.entity.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @EntityGraph(attributePaths = {"contact", "service", "assignedUser", "assignedUser.company", "assignedUser.role"})
    @Override
    Optional<Appointment> findById(UUID id);

    @EntityGraph(attributePaths = {"contact", "service", "assignedUser", "assignedUser.company", "assignedUser.role"})
    Page<Appointment> findByCompanyIdOrderByStartTimeDesc(UUID companyId, Pageable pageable);

    @EntityGraph(attributePaths = {"contact", "service", "assignedUser", "assignedUser.company", "assignedUser.role"})
    Page<Appointment> findByCompanyIdAndStatusOrderByStartTimeAsc(UUID companyId, AppointmentStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"contact", "service", "assignedUser", "assignedUser.company", "assignedUser.role"})
    Page<Appointment> findByContactIdOrderByStartTimeDesc(UUID contactId, Pageable pageable);

    @EntityGraph(attributePaths = {"contact", "service", "assignedUser", "assignedUser.company", "assignedUser.role"})
    Page<Appointment> findByAssignedUserIdOrderByStartTimeAsc(UUID userId, Pageable pageable);

    @EntityGraph(attributePaths = {"contact", "service", "assignedUser", "assignedUser.company", "assignedUser.role"})
    Page<Appointment> findByServiceIdOrderByStartTimeDesc(UUID serviceId, Pageable pageable);

    @EntityGraph(attributePaths = {"contact", "service", "assignedUser", "assignedUser.company", "assignedUser.role"})
    @Query("""
                SELECT a FROM Appointment a
                WHERE a.company.id = :companyId
                  AND a.startTime BETWEEN :startDate AND :endDate
                ORDER BY a.startTime ASC
            """)
    List<Appointment> findByCompanyIdAndDateRange(
            @Param("companyId") UUID companyId,
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate
    );

    @EntityGraph(attributePaths = {"contact", "service", "assignedUser", "assignedUser.company", "assignedUser.role"})
    @Query("""
                SELECT a
                FROM Appointment a
                WHERE a.assignedUser.id = :userId
                  AND a.startTime BETWEEN :startDate AND :endDate
                  AND a.status = :status
                ORDER BY a.startTime ASC
            """)
    List<Appointment> findByUserIdAndDateRangeAndStatus(
            @Param("userId") UUID userId,
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate,
            @Param("status") AppointmentStatus status
    );

    @EntityGraph(attributePaths = {"contact", "service", "assignedUser", "assignedUser.company", "assignedUser.role"})
    @Query("""
                SELECT a
                FROM Appointment a
                WHERE a.company.id = :companyId
                  AND a.status = 'SCHEDULED'
                  AND a.reminderSent = false
                  AND a.startTime BETWEEN :now AND :reminderWindow
            """)
    List<Appointment> findAppointmentsNeedingReminder(
            @Param("companyId") UUID companyId,
            @Param("now") ZonedDateTime now,
            @Param("reminderWindow") ZonedDateTime reminderWindow
    );

    @Query("""
                SELECT COUNT(a)
                FROM Appointment a
                WHERE a.service.id = :serviceId
                  AND a.startTime BETWEEN :startOfDay AND :endOfDay
                  AND a.status IN ('SCHEDULED', 'CONFIRMED')
            """)
    Long countDailyAppointmentsByService(
            @Param("serviceId") UUID serviceId,
            @Param("startOfDay") ZonedDateTime startOfDay,
            @Param("endOfDay") ZonedDateTime endOfDay
    );

    @EntityGraph(attributePaths = {"contact", "service", "assignedUser", "assignedUser.company", "assignedUser.role"})
    @Query("""
                SELECT a
                FROM Appointment a
                WHERE a.assignedUser.id = :userId
                  AND a.startTime < :endTime
                  AND a.endTime > :startTime
                  AND a.status IN ('SCHEDULED', 'CONFIRMED')
            """)
    List<Appointment> findConflictingAppointments(
            @Param("userId") UUID userId,
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime
    );

    @Query("""
                SELECT COUNT(a)
                FROM Appointment a
                WHERE a.company.id = :companyId
                  AND a.status = :status
            """)
    Long countByCompanyAndStatus(
            @Param("companyId") UUID companyId,
            @Param("status") AppointmentStatus status
    );

    @EntityGraph(attributePaths = {"contact", "service", "assignedUser", "assignedUser.company", "assignedUser.role"})
    List<Appointment> findTop10ByContactIdOrderByStartTimeDesc(UUID contactId);
}
