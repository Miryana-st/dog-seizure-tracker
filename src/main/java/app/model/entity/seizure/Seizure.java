package app.model.entity.seizure;

import app.model.entity.dog.Dog;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seizures")
public class Seizure {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate date;

    private LocalTime time;

    private LocalTime duration;

    @Enumerated(EnumType.STRING)
    private SeizureSeverity severity;

    @Enumerated(EnumType.STRING)
    private SeizureRecovery recovery;

    @ManyToOne
    @JoinColumn(name = "dog_id")
    private Dog dog;
}
