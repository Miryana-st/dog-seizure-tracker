package app.model.entity.seizure;

import app.model.entity.dog.Dog;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @NotBlank
    private String duration;

    @Enumerated(EnumType.STRING)
    private SeizureSeverity severity;

    @Enumerated(EnumType.STRING)
    private SeizureRecovery recovery;

    @ManyToOne
    @JoinColumn(name = "dog_id")
    private Dog dog;
}
