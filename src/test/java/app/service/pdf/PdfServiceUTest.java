package app.service.pdf;

import app.exception.PdfGenerationException;
import app.model.entity.seizure.Seizure;
import app.service.seizure.SeizureService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PdfServiceUTest {

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private SeizureService seizureService;

    @InjectMocks
    private PdfService pdfService;

    @Test
    void whenGenerateSeizureReport_andDataIsValid_thenReturnPdfBytes() {

        UUID dogId = UUID.randomUUID();

        List<Seizure> seizures = List.of(Seizure.builder()
                        .id(UUID.randomUUID())
                        .build());

        when(seizureService.getAllSeizuresByDog_IdOrderByDateDescTimeDesc(dogId)).thenReturn(seizures);

        when(templateEngine.process(eq("pdf/seizures-report"), any(Context.class))).thenReturn("<html><body>Test PDF</body></html>");

        byte[] result = pdfService.generateSeizureReport(dogId);

        assertNotNull(result);
        assertTrue(result.length > 0);

        verify(seizureService).getAllSeizuresByDog_IdOrderByDateDescTimeDesc(dogId);

        verify(templateEngine).process(eq("pdf/seizures-report"), any(Context.class));
    }

    @Test
    void whenGenerateSeizureReport_andSeizureServiceThrowsException_thenThrowPdfGenerationException() {

        UUID dogId = UUID.randomUUID();

        when(seizureService.getAllSeizuresByDog_IdOrderByDateDescTimeDesc(dogId)).thenThrow(new RuntimeException());

        assertThrows(PdfGenerationException.class, () -> pdfService.generateSeizureReport(dogId));
    }
}
