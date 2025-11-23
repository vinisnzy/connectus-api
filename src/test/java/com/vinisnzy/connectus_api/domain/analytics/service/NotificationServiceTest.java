package com.vinisnzy.connectus_api.domain.analytics.service;

import com.vinisnzy.connectus_api.api.exception.EntityNotFoundException;
import com.vinisnzy.connectus_api.domain.analytics.dto.response.NotificationResponse;
import com.vinisnzy.connectus_api.domain.analytics.entity.Notification;
import com.vinisnzy.connectus_api.domain.analytics.mapper.NotificationMapper;
import com.vinisnzy.connectus_api.domain.analytics.repository.NotificationRepository;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationMapper mapper;

    @InjectMocks
    private NotificationService notificationService;

    private UUID userId;
    private UUID notificationId;
    private User user;
    private Notification notification;
    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        notification = new Notification();
        notification.setId(notificationId);
        notification.setUser(user);
        notification.setTitle("Test Notification");
        notification.setMessage("This is a test notification.");
        notification.setIsRead(false);

        notificationResponse = NotificationResponse.builder()
                .id(notificationId)
                .title("Test Notification")
                .message("This is a test notification.")
                .build();
    }

    @Test
    @DisplayName("Should find all notifications for user")
    void shouldFindAllNotificationsForUser() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserIdOrThrow).thenReturn(userId);
            Page<Notification> notificationPage = new PageImpl<>(List.of(notification));

            when(notificationRepository.findByUserIdOrderByCreatedAt(eq(userId), any())).thenReturn(notificationPage);
            when(mapper.toResponse(notification)).thenReturn(notificationResponse);

            List<NotificationResponse> result = notificationService.getNotificationsForUser(userId, PageRequest.of(0, 10));

            assertThat(result).hasSize(1);
            verify(notificationRepository).findByUserIdOrderByCreatedAt(eq(userId), any());
        }
    }

    @Test
    @DisplayName("Should mark notification as read")
    void shouldMarkNotificationAsRead() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserIdOrThrow).thenReturn(userId);
            when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
            when(notificationRepository.save(notification)).thenReturn(notification);
            when(mapper.toResponse(notification)).thenReturn(notificationResponse);

            NotificationResponse result = notificationService.markNotificationAsRead(notificationId);

            assertThat(result).isEqualTo(notificationResponse);
            assertThat(notification.getIsRead()).isTrue();
            verify(notificationRepository).save(notification);
        }
    }

    @Test
    @DisplayName("Should throw exception when notification not found")
    void shouldThrowExceptionWhenNotificationNotFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserIdOrThrow).thenReturn(userId);
            when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> notificationService.markNotificationAsRead(notificationId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Should return empty list when no notifications found")
    void shouldReturnEmptyListWhenNoNotificationsFound() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserIdOrThrow).thenReturn(userId);
            when(notificationRepository.findByUserIdOrderByCreatedAt(eq(userId), any())).thenReturn(new PageImpl<>(List.of()));

            List<NotificationResponse> result = notificationService.getNotificationsForUser(userId, PageRequest.of(0, 10));

            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("Should count unread notifications for user")
    void shouldCountUnreadNotificationsForUser() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserIdOrThrow).thenReturn(userId);
            when(notificationRepository.countByUserIdAndIsReadFalse(userId)).thenReturn(5L);

            Long result = notificationService.countUnreadNotificationsForUser(userId);

            assertThat(result).isEqualTo(5L);
            verify(notificationRepository).countByUserIdAndIsReadFalse(userId);
        }
    }

    @Test
    @DisplayName("Should return zero when no unread notifications")
    void shouldReturnZeroWhenNoUnreadNotifications() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserIdOrThrow).thenReturn(userId);
            when(notificationRepository.countByUserIdAndIsReadFalse(userId)).thenReturn(0L);

            Long result = notificationService.countUnreadNotificationsForUser(userId);

            assertThat(result).isZero();
        }
    }
}
