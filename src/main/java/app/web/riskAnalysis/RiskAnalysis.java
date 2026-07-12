package app.web.riskAnalysis;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/risk-analysis")
public class RiskAnalysis {

    @GetMapping
    public String riskAnalysisAllDogs() {
        return "risk-analysis-dogs";
    }


    @GetMapping("/{dogId}")
    public String riskAnalysisForDog() {
        return "risk-analysis";
    }
}
