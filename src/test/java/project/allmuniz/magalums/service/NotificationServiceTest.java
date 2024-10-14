package project.allmuniz.magalums.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import project.allmuniz.magalums.controller.dto.ScheduleNotificationDto;
import project.allmuniz.magalums.entity.Channel;
import project.allmuniz.magalums.entity.Notification;
import project.allmuniz.magalums.entity.Status;
import project.allmuniz.magalums.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testScheduleNotification() {
        LocalDateTime dateTime = LocalDateTime.now();
        String destination = "test@example.com";
        String message = "Test message";
        Channel.Values channelValue = Channel.Values.EMAIL;

        ScheduleNotificationDto dto = new ScheduleNotificationDto(
                dateTime,
                destination,
                message,
                channelValue
        );

        notificationService.scheduleNotification(dto);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(dateTime, capturedNotification.getDateTime());
        assertEquals(destination, capturedNotification.getDestination());
        assertEquals(message, capturedNotification.getMessage());
        assertEquals(channelValue.toChannel().getDescription(), capturedNotification.getChannel().getDescription());
        assertEquals(Status.Values.PENDING.toStatus().getDescription(), capturedNotification.getStatus().getDescription());
    }

    @Test
    public void testFindByIdNotificationFound() {
        Long notificationId = 1L;
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setDateTime(LocalDateTime.now());
        notification.setDestination("test@example.com");
        notification.setMessage("Test message");
        notification.setChannel(Channel.Values.EMAIL.toChannel());
        notification.setStatus(Status.Values.PENDING.toStatus());

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        Optional<Notification> foundNotification = notificationService.findById(notificationId);

        assertEquals(true, foundNotification.isPresent());
        assertEquals(notificationId, foundNotification.get().getId());
        assertEquals(notification.getDestination(), foundNotification.get().getDestination());
    }

    @Test
    public void testFindByIdNotificationNotFound() {
        Long notificationId = 1L;

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        Optional<Notification> foundNotification = notificationService.findById(notificationId);

        assertFalse(foundNotification.isPresent());
    }

    @Test
    public void testCancelNotificationSuccessful() {
        Long notificationId = 1L;
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setDateTime(LocalDateTime.now());
        notification.setDestination("test@example.com");
        notification.setMessage("Test message");
        notification.setChannel(Channel.Values.EMAIL.toChannel());
        notification.setStatus(Status.Values.PENDING.toStatus());

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        notificationService.cancelNotification(notificationId);

        assertEquals(Status.Values.CANCELED.toStatus().getDescription(), notification.getStatus().getDescription());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    public void testCheckAndSend() {
        LocalDateTime dateTime = LocalDateTime.now().minusHours(1); // Um horário no passado
        Notification pendingNotification = new Notification();
        pendingNotification.setId(1L);
        pendingNotification.setStatus(Status.Values.PENDING.toStatus());

        Notification errorNotification = new Notification();
        errorNotification.setId(2L);
        errorNotification.setStatus(Status.Values.ERROR.toStatus());

        when(notificationRepository.findByStatusInAndDateTimeBefore(anyList(), any(LocalDateTime.class)))
                .thenReturn(List.of(pendingNotification, errorNotification));

        notificationService.checkAndSend(dateTime);

        assertEquals(Status.Values.SUCCESS.toStatus().getDescription(), pendingNotification.getStatus().getDescription());
        assertEquals(Status.Values.SUCCESS.toStatus().getDescription(), errorNotification.getStatus().getDescription());
        verify(notificationRepository, times(2)).save(Mockito.any(Notification.class));
    }

    @Test
    public void testCheckAndSendNoNotifications() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);

        when(notificationRepository.findByStatusInAndDateTimeBefore(
                List.of(Status.Values.PENDING.toStatus(), Status.Values.ERROR.toStatus()),
                dateTime
        )).thenReturn(List.of());

        notificationService.checkAndSend(dateTime);

        verify(notificationRepository, times(0)).save(Mockito.any(Notification.class));
    }
}