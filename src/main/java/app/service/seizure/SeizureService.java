package app.service.seizure;

import app.exception.NotFoundException;
import app.model.dto.seizure.CreateNewSeizureRequest;
import app.model.dto.seizure.EditSeizureRequest;
//import app.model.dto.seizure.SeizureDto;
import app.model.entity.dog.Dog;
import app.model.entity.seizure.Seizure;
import app.repository.seizure.SeizureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static app.exception.ExceptionMessages.SEIZURE_NOT_FOUND;

@Slf4j
@Service
public class SeizureService {

    private final SeizureRepository seizureRepository;

    @Autowired
    public SeizureService(SeizureRepository seizureRepository) {

        this.seizureRepository = seizureRepository;
    }

    @Transactional
    public Seizure createSeizureEntry(CreateNewSeizureRequest createNewSeizureRequest, Dog dog) {

        Seizure seizure = Seizure.builder()
                .dog(dog)
                .date(createNewSeizureRequest.getDate())
                .time(createNewSeizureRequest.getTime())
                .duration(createNewSeizureRequest.getDuration())
                .note(createNewSeizureRequest.getNote())
                .cluster(createNewSeizureRequest.isCluster())
                .severity(createNewSeizureRequest.getSeverity())
                .recovery(createNewSeizureRequest.getRecovery())
                .build();

        Seizure save = seizureRepository.save(seizure);

        log.info("Creating seizure entry for dog with id: {}", dog.getId());

        return save;
    }

    @Transactional
    public void updateSeizureEntry(UUID id, EditSeizureRequest editSeizureRequest) {

        Seizure seizure = seizureRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException(SEIZURE_NOT_FOUND));

        seizure.setDate(editSeizureRequest.getDate());
        seizure.setTime(editSeizureRequest.getTime());
        seizure.setDuration(editSeizureRequest.getDuration());
        seizure.setSeverity(editSeizureRequest.getSeverity());
        seizure.setRecovery(editSeizureRequest.getRecovery());
        seizure.setNote(editSeizureRequest.getNote());
        seizure.setCluster(editSeizureRequest.isCluster());

        seizureRepository.save(seizure);
        log.info("Updating seizure with id: {}", id);
    }

    @Transactional
    public void deleteSeizureById(UUID seizureId) {

        Seizure seizureToDelete = seizureRepository.findById(seizureId)
                .orElseThrow(() -> new NotFoundException(SEIZURE_NOT_FOUND));

        seizureToDelete.getDog().getSeizures().remove(seizureToDelete);

        seizureRepository.delete(seizureToDelete);
        log.info("Deleting seizure with id: {}", seizureId);
    }

    public List<Seizure> findAllSeizuresByDog_IdOrderByDateDescTimeDesc(UUID dogId) {

        return seizureRepository.findAllByDog_IdOrderByDateDescTimeDesc(dogId);
    }

    public Seizure getSeizureById(UUID seizureId) {

        return seizureRepository.findById(seizureId)
                .orElseThrow(() -> new NotFoundException(SEIZURE_NOT_FOUND));
    }

//    public List<SeizureDto> getSeizuresByDogId(UUID dogId) {
//
//        List<Seizure> seizures = seizureRepository.findAllByDog_IdOrderByDateDescTimeDesc(dogId);
//
//        return seizures.stream()
//                .map(seizure -> SeizureDto.builder()
//                        .date(seizure.getDate())
//                        .duration(seizure.getDuration())
//                        .severity(seizure.getSeverity())
//                        .recovery(seizure.getRecovery())
//                        .cluster(seizure.isCluster())
//                        .build())
//                .toList();
//    }
}
