package app.job;

import app.service.pdf.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MonthlySeizureReportScheduler {

    private final PdfService pdfService;

    public MonthlySeizureReportScheduler(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void generateMonthlySeizureReports() {
        pdfService.generatePreviousMonthReports();
        log.info("Monthly seizure reports generated");
    }
}