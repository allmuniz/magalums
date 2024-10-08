package project.allmuniz.magalums.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.allmuniz.magalums.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
