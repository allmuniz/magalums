package project.allmuniz.magalums.controller.dto;

import project.allmuniz.magalums.entity.Channel;
import project.allmuniz.magalums.entity.Notification;
import project.allmuniz.magalums.entity.Status;

import java.time.LocalDateTime;

public record ScheduleNotificationDto(LocalDateTime dateTime,
                                      String destination,
                                      String message,
                                      Channel.Values channel) {

    public Notification toNotification() {
        return new Notification(
                dateTime,
                destination,
                message,
                channel.toChannel(),
                Status.Values.PENDING.toStatus()
        );
    }
}
