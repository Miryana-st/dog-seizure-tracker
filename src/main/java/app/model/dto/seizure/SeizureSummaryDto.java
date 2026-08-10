package app.model.dto.seizure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeizureSummaryDto {

    private int totalSeizures;

    private double averageDuration;

    private int clusterSeizures;

    private int longestDuration;
}
