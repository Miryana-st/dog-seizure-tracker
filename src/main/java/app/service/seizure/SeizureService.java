package app.service.seizure;

import app.model.dto.seizure.CreateNewSeizureRequest;
import app.model.dto.seizure.EditSeizureRequest;
import app.model.entity.dog.Dog;
import app.model.entity.seizure.Seizure;
import app.repository.seizure.SeizureRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SeizureService {

    SeizureRepository seizureRepository;

    @Autowired
    public SeizureService(SeizureRepository seizureRepository) {
        this.seizureRepository = seizureRepository;
    }

    @Transactional
    public void createSeizureEntry(CreateNewSeizureRequest createNewSeizureRequest, Dog dog) {

        Seizure seizure = Seizure.builder()
                .dog(dog)
                .date(createNewSeizureRequest.getDate())
                .time(createNewSeizureRequest.getTime())
                .duration(createNewSeizureRequest.getDuration())
                .severity(createNewSeizureRequest.getSeverity())
                .recovery(createNewSeizureRequest.getRecovery())
                .build();

        seizureRepository.save(seizure);
    }

    public List<Seizure> findAllByDog_IdOrderByDateDescTimeDesc(UUID dogId) {
        return seizureRepository.findAllByDog_IdOrderByDateDescTimeDesc(dogId);
    }

    public Seizure getSeizureById(UUID seizureId) {
        return seizureRepository.findById(seizureId)
                .orElseThrow(() -> new RuntimeException("Seizure with id [%s] not found!".formatted(seizureId)));
    }

    @Transactional
    public void updateSeizureEntry(UUID id, EditSeizureRequest editSeizureRequest) {
        Seizure seizure = seizureRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Seizure with id [%s] not found!".formatted(id)));

        seizure.setDate(editSeizureRequest.getDate());
        seizure.setTime(editSeizureRequest.getTime());
        seizure.setDuration(editSeizureRequest.getDuration());
        seizure.setSeverity(editSeizureRequest.getSeverity());
        seizure.setRecovery(editSeizureRequest.getRecovery());

        seizureRepository.save(seizure);
    }

    @Transactional
    public void deleteSeizureById(UUID seizureId) {

        Seizure seizureToDelete = seizureRepository.findById(seizureId)
                .orElseThrow(() -> new RuntimeException("Seizure not found"));

        seizureToDelete.getDog().getSeizures().remove(seizureToDelete);

        seizureRepository.delete(seizureToDelete);
    }
}
