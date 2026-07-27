package app.init;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "test-svc", url = "http://localhost:8086/api/v1")
public interface TestClient {

    @GetMapping("/medications")
    ResponseEntity<String> getMedications(@RequestParam() UUID id);
}
