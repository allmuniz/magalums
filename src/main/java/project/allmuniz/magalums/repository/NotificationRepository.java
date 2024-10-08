package project.allmuniz.magalums.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.allmuniz.magalums.entity.Notification;
import project.allmuniz.magalums.entity.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStatusInAndDateTimeBefore(List<Status> status, LocalDateTime dateTime);
}
