package app.model.dto.seizure;

import app.model.entity.seizure.SeizureSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeizureSummaryDto {

    private int totalSeizures;

    private double averageDuration;

    private int clusterSeizures;

    private int longestDuration;

    private Map<SeizureSeverity, Long> severityCount;
}
