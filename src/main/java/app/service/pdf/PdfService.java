package app.service.pdf;

import app.exception.PdfGenerationException;
import app.model.entity.seizure.Seizure;
import app.service.seizure.SeizureService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

@Service
public class PdfService {

    private final TemplateEngine templateEngine;
    private final SeizureService seizureService;

    public PdfService(
            TemplateEngine templateEngine,
            SeizureService seizureService) {

        this.templateEngine = templateEngine;
        this.seizureService = seizureService;
    }

    public byte[] generateSeizureReport(UUID dogId) {

        try {
            List<Seizure> seizures = seizureService.findAllSeizuresByDog_IdOrderByDateDescTimeDesc(dogId);

            Context context = new Context();
            context.setVariable("seizures", seizures);
            context.setVariable("dogId", dogId);

            String html = templateEngine.process("pdf/seizures-report", context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, new ClassPathResource("static/").getURL().toString());
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new PdfGenerationException("Failed to generate PDF report for dog with ID: " + dogId, e);
        }
    }
}