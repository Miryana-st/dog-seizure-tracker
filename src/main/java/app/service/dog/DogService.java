package app.service.dog;

import app.repository.dog.DogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DogService {

    DogRepository dogRepository;

    @Autowired
    public DogService(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }
}
