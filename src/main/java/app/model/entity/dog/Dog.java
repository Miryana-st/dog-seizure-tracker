package app.model.entity.dog;

import app.model.entity.seizure.Seizure;
import app.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dogs")
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String breed;

    private String dogPicture;

    @Enumerated(EnumType.STRING)
    private GenderDog gender;

    private LocalDate dateOfBirth;

    private String food;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "dog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seizure> seizures = new ArrayList<>();
}
