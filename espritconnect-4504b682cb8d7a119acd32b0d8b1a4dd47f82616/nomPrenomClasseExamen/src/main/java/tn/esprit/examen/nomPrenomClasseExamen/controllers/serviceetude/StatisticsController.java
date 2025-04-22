package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.StatisticsService;

import java.util.Map;
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular frontend
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/average-rating-per-tutor")
    public Map<String, Double> getAverageRatingPerTutor() {
        return statisticsService.getAverageRatingPerTutor();
    }

    @GetMapping("/number-of-sessions-per-tutor")
    public Map<String, Long> getNumberOfSessionsPerTutor() {
        return statisticsService.getNumberOfSessionsPerTutor();
    }

    @GetMapping("/total-hours-per-tutor")
    public Map<String, Double> getTotalHoursPerTutor() {
        return statisticsService.getTotalHoursPerTutor();
    }

    @GetMapping("/most-popular-subjects")
    public Map<String, Long> getMostPopularSubjects() {
        return statisticsService.getMostPopularSubjects();
    }

    @GetMapping("/busiest-times")
    public Map<String, Long> getBusiestTimes() {
        return statisticsService.getBusiestTimes();
    }
}