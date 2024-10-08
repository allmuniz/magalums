package project.allmuniz.magalums.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.allmuniz.magalums.entity.Status;

public interface StatusRepository extends JpaRepository<Status, Long> {
}