package project.allmuniz.magalums.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.allmuniz.magalums.entity.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
}
