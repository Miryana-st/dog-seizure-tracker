package app.model.dto.seizure;

import app.model.entity.seizure.Seizure;
import app.model.entity.seizure.SeizureRecovery;
import app.model.entity.seizure.SeizureSeverity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class SeizureDtoMapperUTest {

    @Test
    void fromSeizureToEditSeizureRequest_whenSeizureWithDetailsIsPassed_thenDtoIsReturnedWithSameDetails() {

        Seizure seizure = Seizure.builder()
                .date(LocalDate.of(2020, 1, 1))
                .time(LocalTime.of(12, 0))
                .duration(20)
                .note("Test Note")
                .cluster(false)
                .severity(SeizureSeverity.MILD)
                .recovery(SeizureRecovery.FAST)
                .build();

        EditSeizureRequest result = SeizureDtoMapper.fromSeizure(seizure);

        assertEquals(LocalDate.of(2020, 1, 1), result.getDate());
        assertEquals(LocalTime.of(12, 0), result.getTime());
        assertEquals(20, result.getDuration());
        assertEquals("Test Note", result.getNote());
        assertFalse(result.isCluster());
        assertEquals(SeizureSeverity.MILD, result.getSeverity());
        assertEquals(SeizureRecovery.FAST, result.getRecovery());
    }
}
