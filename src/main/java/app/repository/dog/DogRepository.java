package app.repository.dog;

import app.model.entity.dog.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DogRepository extends JpaRepository<Dog, UUID> {
    List<Dog> findAllByOwner_Id(UUID ownerId);
}
