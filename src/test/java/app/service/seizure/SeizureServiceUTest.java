package app.service.seizure;

import app.exception.NotFoundException;
import app.model.dto.seizure.CreateNewSeizureRequest;
import app.model.dto.seizure.EditSeizureRequest;
import app.model.entity.dog.Dog;
import app.model.entity.seizure.Seizure;
import app.model.entity.seizure.SeizureRecovery;
import app.model.entity.seizure.SeizureSeverity;
import app.repository.seizure.SeizureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static app.exception.ExceptionMessages.SEIZURE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeizureServiceUTest {

    @Mock
    private SeizureRepository seizureRepository;

    @InjectMocks
    private SeizureService seizureService;

    @Captor
    private ArgumentCaptor<Seizure> seizureCaptor;

    @Test
    void whenFindAllSeizuresByDogId_andRepositoryReturnsSeizures_thenReturnSeizures() {

        UUID dogId = UUID.randomUUID();

        List<Seizure> seizures = List.of(
                Seizure.builder()
                        .id(UUID.randomUUID())
                        .date(LocalDate.of(2026, 8, 1))
                        .time(LocalTime.of(10, 30))
                        .build(),
                Seizure.builder()
                        .id(UUID.randomUUID())
                        .date(LocalDate.of(2026, 7, 20))
                        .time(LocalTime.of(8, 15))
                        .build()
        );

        when(seizureRepository.findAllByDog_IdOrderByDateDescTimeDesc(dogId)).thenReturn(seizures);

        List<Seizure> result = seizureService.findAllSeizuresByDog_IdOrderByDateDescTimeDesc(dogId);

        assertEquals(2, result.size());
        assertEquals(seizures, result);

        verify(seizureRepository).findAllByDog_IdOrderByDateDescTimeDesc(dogId);
    }

    @Test
    void whenFindAllSeizuresByDogId_andRepositoryReturnsEmptyList_thenReturnEmptyList() {

        UUID dogId = UUID.randomUUID();

        when(seizureRepository.findAllByDog_IdOrderByDateDescTimeDesc(dogId)).thenReturn(Collections.emptyList());

        List<Seizure> result = seizureService.findAllSeizuresByDog_IdOrderByDateDescTimeDesc(dogId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(seizureRepository).findAllByDog_IdOrderByDateDescTimeDesc(dogId);
    }

    @Test
    void whenCreateSeizure_thenCreateSeizureAndSaveIt() {

        Dog dog = Dog.builder()
                .id(UUID.randomUUID())
                .build();

        CreateNewSeizureRequest dto = CreateNewSeizureRequest.builder()
                .date(LocalDate.of(2026, 8, 1))
                .time(LocalTime.of(10, 30))
                .duration(10)
                .note("test note")
                .cluster(true)
                .severity(SeizureSeverity.MILD)
                .recovery(SeizureRecovery.FAST)
                .build();

        seizureService.createSeizureEntry(dto, dog);

        verify(seizureRepository).save(seizureCaptor.capture());

        Seizure savedSeizure = seizureCaptor.getValue();

        assertEquals(LocalDate.of(2026, 8, 1), savedSeizure.getDate());
        assertEquals(LocalTime.of(10, 30), savedSeizure.getTime());
        assertEquals(10, savedSeizure.getDuration());
        assertEquals("test note", savedSeizure.getNote());
        assertTrue(savedSeizure.isCluster());
        assertEquals(SeizureSeverity.MILD, savedSeizure.getSeverity());
        assertEquals(SeizureRecovery.FAST, savedSeizure.getRecovery());
        assertEquals(dog, savedSeizure.getDog());
    }

    @Test
    void whenGetSeizureById_andRepositoryReturnsSeizure_thenReturnSeizure() {

        UUID seizureId = UUID.randomUUID();

        Seizure seizureRetrievedFromDatabase = Seizure.builder()
                .id(seizureId)
                .duration(20)
                .build();

        when(seizureRepository.findById(seizureId)).thenReturn(Optional.of(seizureRetrievedFromDatabase));

        Seizure result = seizureService.getSeizureById(seizureId);

        assertEquals(seizureRetrievedFromDatabase, result);
        assertEquals(seizureId, result.getId());
        assertEquals(20, result.getDuration());

        verify(seizureRepository).findById(seizureId);
    }

        @Test
        void whenGetSeizureById_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

            UUID seizureId = UUID.randomUUID();

            when(seizureRepository.findById(seizureId)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> seizureService.getSeizureById(seizureId));

            assertEquals(SEIZURE_NOT_FOUND, exception.getMessage());
        }

    @Test
    void whenUpdateSeizureEntry_andRepositoryReturnsSeizure_thenUpdateSeizureDetailsAndSaveUpdatedSeizure() {

        UUID seizureId = UUID.randomUUID();

        EditSeizureRequest dto = EditSeizureRequest.builder()
                .date(LocalDate.of(2026, 8, 7))
                .time(LocalTime.of(12, 30))
                .duration(30)
                .severity(SeizureSeverity.MODERATE)
                .recovery(SeizureRecovery.NORMAL)
                .note("Updated note")
                .cluster(true)
                .build();

        Seizure seizureRetrievedFromDatabase = Seizure.builder()
                .id(seizureId)
                .date(LocalDate.of(2026, 8, 1))
                .time(LocalTime.of(10, 0))
                .duration(20)
                .severity(SeizureSeverity.MILD)
                .recovery(SeizureRecovery.FAST)
                .note("Old note")
                .cluster(false)
                .build();

        when(seizureRepository.findById(seizureId)).thenReturn(Optional.of(seizureRetrievedFromDatabase));

        seizureService.updateSeizureEntry(seizureId, dto);

        assertEquals(LocalDate.of(2026, 8, 7), seizureRetrievedFromDatabase.getDate());
        assertEquals(LocalTime.of(12, 30), seizureRetrievedFromDatabase.getTime());
        assertEquals(30, seizureRetrievedFromDatabase.getDuration());
        assertEquals(SeizureSeverity.MODERATE, seizureRetrievedFromDatabase.getSeverity());
        assertEquals(SeizureRecovery.NORMAL, seizureRetrievedFromDatabase.getRecovery());
        assertEquals("Updated note", seizureRetrievedFromDatabase.getNote());
        assertTrue(seizureRetrievedFromDatabase.isCluster());

        verify(seizureRepository).save(seizureRetrievedFromDatabase);
    }

    @Test
    void whenUpdateSeizureEntry_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID seizureId = UUID.randomUUID();

        EditSeizureRequest dto = null;

        when(seizureRepository.findById(seizureId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> seizureService.updateSeizureEntry(seizureId, dto));

        assertEquals(SEIZURE_NOT_FOUND, exception.getMessage());
        verify(seizureRepository, never()).save(any(Seizure.class));
    }

    @Test
    void whenDeleteSeizureEntry_andRepositoryReturnsSeizure_thenDeleteSeizure() {

        UUID seizureId = UUID.randomUUID();

        Dog dog = Dog.builder()
                .id(UUID.randomUUID())
                .seizures(new ArrayList<>())
                .build();

        Seizure seizureRetrievedFromDatabase = Seizure.builder()
                .id(seizureId)
                .dog(dog)
                .build();

        dog.getSeizures().add(seizureRetrievedFromDatabase);

        when(seizureRepository.findById(seizureId)).thenReturn(Optional.of(seizureRetrievedFromDatabase));

        seizureService.deleteSeizureById(seizureId);

        assertTrue(dog.getSeizures().isEmpty());

        verify(seizureRepository).delete(seizureRetrievedFromDatabase);
    }

    @Test
    void whenDeleteSeizureEntry_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID seizureId = UUID.randomUUID();

        when(seizureRepository.findById(seizureId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> seizureService.deleteSeizureById(seizureId));

        assertEquals(SEIZURE_NOT_FOUND, exception.getMessage());
        verify(seizureRepository, never()).delete(any(Seizure.class));
    }
}
