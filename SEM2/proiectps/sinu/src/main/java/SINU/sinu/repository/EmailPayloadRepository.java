package SINU.sinu.repository;

import SINU.sinu.entities.EmailPayload;
import SINU.sinu.entities.Professor.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailPayloadRepository extends JpaRepository<EmailPayload, Integer> {
}
