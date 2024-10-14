package project.allmuniz.magalums.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import project.allmuniz.magalums.controller.dto.ScheduleNotificationDto;
import project.allmuniz.magalums.entity.Channel;
import project.allmuniz.magalums.entity.Notification;
import project.allmuniz.magalums.service.NotificationService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testScheduleNotification() throws Exception {
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

        String jsonRequest = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isAccepted());

        verify(notificationService).scheduleNotification(dto);
    }

    @Test
    public void testGetNotificationFound() throws Exception {
        long notificationId = 1L;
        Notification notification = new Notification();
        notification.setId(notificationId);

        when(notificationService.findById(notificationId)).thenReturn(Optional.of(notification));

        mockMvc.perform(MockMvcRequestBuilders.get("/notifications/{notificationId}", notificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(notification)));

        verify(notificationService).findById(notificationId);
    }

    @Test
    public void testGetNotificationNotFound() throws Exception {
        long notificationId = 1L;

        when(notificationService.findById(notificationId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/notifications/{notificationId}", notificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(notificationService).findById(notificationId);
    }

    @Test
    public void testCancelNotificationSuccess() throws Exception {
        long notificationId = 1L;

        // Simule que o cancelamento da notificação não lança exceção
        doNothing().when(notificationService).cancelNotification(notificationId);

        // Executar a requisição DELETE
        mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/{notificationId}", notificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Espera status 204 No Content

        // Verifica se o método foi chamado uma vez
        verify(notificationService).cancelNotification(notificationId);
    }

    @Test
    public void testCancelNotificationNotFound() throws Exception {
        long notificationId = 1L;

        doReturn(Optional.empty()).when(notificationService).findById(notificationId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/{notificationId}", notificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(notificationService).cancelNotification(notificationId);
    }
}
