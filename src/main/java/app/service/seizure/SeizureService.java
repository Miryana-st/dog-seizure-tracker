package app.service.seizure;

import app.repository.seizure.SeizureRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SeizureService {

    SeizureRepository seizureRepository;

    @Autowired
    public SeizureService(SeizureRepository seizureRepository) {
        this.seizureRepository = seizureRepository;
    }
}
