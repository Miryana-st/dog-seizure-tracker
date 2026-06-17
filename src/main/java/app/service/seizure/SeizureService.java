package app.service.seizure;

import app.model.dto.seizure.CreateNewSeizureRequest;
import app.model.entity.dog.Dog;
import app.model.entity.seizure.Seizure;
import app.repository.seizure.SeizureRepository;
import app.service.dog.DogService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SeizureService {

    private final DogService dogService;
    SeizureRepository seizureRepository;

    @Autowired
    public SeizureService(SeizureRepository seizureRepository, DogService dogService) {
        this.seizureRepository = seizureRepository;
        this.dogService = dogService;
    }

    public void create(CreateNewSeizureRequest createNewSeizureRequest, Dog dog) {

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

    public List<Seizure> getAllSeizuresByDogId(UUID dogId) {
        return seizureRepository.findAllByDog_Id(dogId);
    }
}
