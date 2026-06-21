package app.model.dto.seizure;

import app.model.entity.seizure.Seizure;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SeizureDtoMapper {

    public static EditSeizureRequest fromSeizure(Seizure seizure) {

        return EditSeizureRequest.builder()
                .date(seizure.getDate())
                .time(seizure.getTime())
                .duration(seizure.getDuration())
                .severity(seizure.getSeverity())
                .recovery(seizure.getRecovery())
                .build();
    }
}
