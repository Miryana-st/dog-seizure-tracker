package app.repository.seizure;

import app.model.entity.seizure.Seizure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeizureRepository extends JpaRepository<Seizure, UUID> {
    List<Seizure> findAllByDog_IdOrderByDateDescTimeDesc(UUID dogId);
}
