package com.mindp.connectus_api.domain.scheduling.repository;

import com.mindp.connectus_api.domain.core.entity.Company;
import com.mindp.connectus_api.domain.core.entity.User;
import com.mindp.connectus_api.domain.crm.entity.Contact;
import com.mindp.connectus_api.domain.scheduling.entity.Appointment;
import com.mindp.connectus_api.domain.scheduling.entity.Service;
import com.mindp.connectus_api.domain.scheduling.entity.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Page<Appointment> findByCompanyOrderByStartTimeDesc(Company company, Pageable pageable);

    Page<Appointment> findByCompanyAndStatusOrderByStartTimeAsc(Company company, AppointmentStatus status, Pageable pageable);

    Page<Appointment> findByContactOrderByStartTimeDesc(Contact contact, Pageable pageable);

    Page<Appointment> findByAssignedUserOrderByStartTimeAsc(User user, Pageable pageable);

    Page<Appointment> findByServiceOrderByStartTimeDesc(Service service, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.company = :company AND a.startTime BETWEEN :startDate AND :endDate ORDER BY a.startTime ASC")
    List<Appointment> findByCompanyAndDateRange(@Param("company") Company company,
                                                @Param("startDate") ZonedDateTime startDate,
                                                @Param("endDate") ZonedDateTime endDate);

    @Query("SELECT a FROM Appointment a WHERE a.assignedUser = :user AND a.startTime BETWEEN :startDate AND :endDate AND a.status = :status ORDER BY a.startTime ASC")
    List<Appointment> findByUserAndDateRangeAndStatus(@Param("user") User user,
                                                      @Param("startDate") ZonedDateTime startDate,
                                                      @Param("endDate") ZonedDateTime endDate,
                                                      @Param("status") AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.company = :company AND a.status = 'SCHEDULED' AND a.reminderSent = false AND a.startTime BETWEEN :now AND :reminderWindow")
    List<Appointment> findAppointmentsNeedingReminder(@Param("company") Company company,
                                                      @Param("now") ZonedDateTime now,
                                                      @Param("reminderWindow") ZonedDateTime reminderWindow);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.service = :service AND a.startTime BETWEEN :startOfDay AND :endOfDay AND a.status IN ('SCHEDULED', 'CONFIRMED')")
    Long countDailyAppointmentsByService(@Param("service") Service service,
                                        @Param("startOfDay") ZonedDateTime startOfDay,
                                        @Param("endOfDay") ZonedDateTime endOfDay);

    @Query("SELECT a FROM Appointment a WHERE a.assignedUser = :user AND a.startTime < :endTime AND a.endTime > :startTime AND a.status IN ('SCHEDULED', 'CONFIRMED')")
    List<Appointment> findConflictingAppointments(@Param("user") User user,
                                                  @Param("startTime") ZonedDateTime startTime,
                                                  @Param("endTime") ZonedDateTime endTime);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.company = :company AND a.status = :status")
    Long countByCompanyAndStatus(@Param("company") Company company, @Param("status") AppointmentStatus status);

    List<Appointment> findTop10ByContactOrderByStartTimeDesc(Contact contact);
}
