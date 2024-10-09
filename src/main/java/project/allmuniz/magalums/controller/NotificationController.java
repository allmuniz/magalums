package project.allmuniz.magalums.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.allmuniz.magalums.controller.dto.ScheduleNotificationDto;
import project.allmuniz.magalums.entity.Notification;
import project.allmuniz.magalums.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification", description = "Notification Manager")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(description = "Scheduling",
            summary = "Notification Scheduling")
    public ResponseEntity<Void> scheduleNotification(@RequestBody ScheduleNotificationDto dto) {
        notificationService.scheduleNotification(dto);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{notificationId}")
    @Operation(description = "Search",
            summary = "Search for a notification")
    public ResponseEntity<Notification> getNotification(@PathVariable("notificationId") long notificationId) {
        var notification = notificationService.findById(notificationId);
        return notification.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{notificationId}")
    @Operation(description = "Canceling",
            summary = "Canceling a notification")
    public ResponseEntity<Void> cancelNotification(@PathVariable("notificationId") long notificationId) {
        notificationService.cancelNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}
