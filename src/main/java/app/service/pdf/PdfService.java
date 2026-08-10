package app.service.pdf;

import app.exception.PdfGenerationException;
import app.model.entity.dog.Dog;
import app.model.entity.seizure.Seizure;
import app.service.dog.DogService;
import app.service.seizure.SeizureService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class PdfService {

    private final TemplateEngine templateEngine;
    private final SeizureService seizureService;
    private final DogService dogService;

    public PdfService(TemplateEngine templateEngine, SeizureService seizureService, DogService dogService) {
        this.templateEngine = templateEngine;
        this.seizureService = seizureService;
        this.dogService = dogService;
    }

    public byte[] generateSeizureReport(UUID dogId) {
        try {
            List<Seizure> seizures = seizureService.getAllSeizuresByDog_IdOrderByDateDescTimeDesc(dogId);
            Context context = new Context();
            context.setVariable("seizures", seizures);
            context.setVariable("dogId", dogId);
            String html = templateEngine.process("pdf/seizures-report", context);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, new ClassPathResource("static/").getURL().toString());
            builder.toStream(outputStream);
            builder.run();
            log.info("Seizure report generated successfully for dog with id: {}", dogId);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new PdfGenerationException("Failed to generate PDF report for dog with ID: " + dogId, e);
        }
    }

    public void generateMonthlySeizureReport(UUID dogId, LocalDate month) {
        try {
            LocalDate firstDayOfMonth = month.withDayOfMonth(1);
            LocalDate lastDayOfMonth = month.withDayOfMonth(month.lengthOfMonth());
            List<Seizure> seizures = seizureService.getAllSeizuresByDog_IdOrderByDateDescTimeDesc(dogId).stream().filter(seizure -> !seizure.getDate().isBefore(firstDayOfMonth) && !seizure.getDate().isAfter(lastDayOfMonth)).toList();
            Context context = new Context();
            context.setVariable("seizures", seizures);
            context.setVariable("dogId", dogId);
            context.setVariable("month", month);
            String html = templateEngine.process("pdf/monthly-seizure-report", context);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, new ClassPathResource("static/").getURL().toString());
            builder.toStream(outputStream);
            builder.run();
            saveMonthlyReport(outputStream.toByteArray(), dogId, month);
            log.info("Monthly seizure report generated for dog {} for {}", dogId, month);
        } catch (Exception e) {
            throw new PdfGenerationException("Failed to generate monthly seizure report for dog with ID: " + dogId, e);
        }
    }

    public void generatePreviousMonthReports() {
        LocalDate previousMonth = LocalDate.now().minusMonths(1);
        List<Dog> dogs = dogService.getAllDogs();
        for (Dog dog : dogs) {
            generateMonthlySeizureReport(dog.getId(), previousMonth);
        }
    }

    public boolean previousMonthReportExists(UUID dogId) {
        LocalDate previousMonth = LocalDate.now().minusMonths(1);
        return Files.exists(getMonthlyReportPath(dogId, previousMonth));
    }

    public Resource getPreviousMonthReport(UUID dogId) {
        LocalDate previousMonth = LocalDate.now().minusMonths(1);
        return new FileSystemResource(getMonthlyReportPath(dogId, previousMonth));
    }

    private void saveMonthlyReport(byte[] pdf, UUID dogId, LocalDate month) throws IOException {
        Path directory = Paths.get("generated-reports");
        Files.createDirectories(directory);
        Path file = getMonthlyReportPath(dogId, month);
        Files.write(file, pdf);
    }

    private Path getMonthlyReportPath(UUID dogId, LocalDate month) {
        return Paths.get("generated-reports", "seizure-report-" + dogId + "-" + month.getYear() + "-" + month.getMonthValue() + ".pdf");
    }
}