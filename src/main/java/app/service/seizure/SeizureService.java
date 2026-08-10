package app.service.seizure;

import app.exception.NotFoundException;
import app.model.dto.seizure.CreateNewSeizureRequest;
import app.model.dto.seizure.EditSeizureRequest;
import app.model.dto.seizure.SeizureSummaryDto;
import app.model.entity.dog.Dog;
import app.model.entity.seizure.Seizure;
import app.repository.seizure.SeizureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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
    @Caching(evict = {
            @CacheEvict(value = "seizuresByDogId", allEntries = true),
            @CacheEvict(value = "seizureById", allEntries = true),
            @CacheEvict(value = "dogById", allEntries = true)
    })
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
    @Caching(evict = {
            @CacheEvict(value = "seizureById", key = "#id"),
            @CacheEvict(value = "seizuresByDogId", allEntries = true),
            @CacheEvict(value = "dogById", allEntries = true)
    })
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
    @Caching(evict = {
            @CacheEvict(value = "seizureById", key = "#seizureId"),
            @CacheEvict(value = "seizuresByDogId", allEntries = true),
            @CacheEvict(value = "dogById", allEntries = true)
    })
    public void deleteSeizureById(UUID seizureId) {

        Seizure seizureToDelete = seizureRepository.findById(seizureId)
                .orElseThrow(() -> new NotFoundException(SEIZURE_NOT_FOUND));

        seizureToDelete.getDog().getSeizures().remove(seizureToDelete);

        seizureRepository.delete(seizureToDelete);
        log.info("Deleting seizure with id: {}", seizureId);
    }

    @Cacheable(value = "seizuresByDogId", key = "#dogId")
    public List<Seizure> getAllSeizuresByDog_IdOrderByDateDescTimeDesc(UUID dogId) {

        return seizureRepository.findAllByDog_IdOrderByDateDescTimeDesc(dogId);
    }

    @Cacheable(value = "seizureById", key = "#seizureId")
    public Seizure getSeizureById(UUID seizureId) {

        return seizureRepository.findById(seizureId)
                .orElseThrow(() -> new NotFoundException(SEIZURE_NOT_FOUND));
    }

    public SeizureSummaryDto generateSeizureSummaryForDog(Dog dog) {

        LocalDate today = LocalDate.now();

        LocalDate startOfLastWeek = today.minusMonths(1).withDayOfMonth(1);

        LocalDate endOfLastWeek = today.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

        List<Seizure> seizuresForLastWeek = dog.getSeizures()
                .stream()
                .filter(seizure ->
                        !seizure.getDate().isBefore(startOfLastWeek) && !seizure.getDate().isAfter(endOfLastWeek))
                .toList();

        if (seizuresForLastWeek.isEmpty()) {
            return SeizureSummaryDto.builder()
                    .totalSeizures(0)
                    .averageDuration(0)
                    .clusterSeizures(0)
                    .longestDuration(0)
                    .build();
        }

        double averageDuration = seizuresForLastWeek.stream()
                .mapToInt(Seizure::getDuration)
                .average()
                .orElse(0);

        int longestDuration = seizuresForLastWeek
                .stream()
                .mapToInt(Seizure::getDuration)
                .max()
                .orElse(0);

        int clusterSeizuresCount = (int) seizuresForLastWeek.stream().filter(Seizure::isCluster).count();

        return SeizureSummaryDto.builder()
                .totalSeizures(seizuresForLastWeek.size())
                .averageDuration(averageDuration)
                .clusterSeizures(clusterSeizuresCount)
                .longestDuration(longestDuration)
                .build();
    }

}
