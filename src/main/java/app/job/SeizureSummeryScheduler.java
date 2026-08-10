package app.job;

import app.model.dto.seizure.SeizureSummaryDto;
import app.model.entity.dog.Dog;
import app.service.dog.DogService;
import app.service.seizure.SeizureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SeizureSummeryScheduler {

    private final SeizureService seizureService;
    private final DogService dogService;


    public SeizureSummeryScheduler(SeizureService seizureService, DogService dogService) {
        this.seizureService = seizureService;
        this.dogService = dogService;
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void displaySeizureSummery() {

        List<Dog> dogs = dogService.getAllDogs();

        for (Dog dog : dogs) {
            SeizureSummaryDto summery = seizureService.generateSeizureSummaryForDog(dog);
            log.info("Seizure summary for {}: {}", dog.getName(), summery);
        }
    }
}
