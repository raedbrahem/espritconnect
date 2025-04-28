package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import java.util.Map;

public interface IStatisticsService {
    Map<String, Double> getAverageRatingPerTutor();
    Map<String, Long> getNumberOfSessionsPerTutor();
    Map<String, Double> getTotalHoursPerTutor();
    Map<String, Long> getMostPopularSubjects();
    Map<String, Long> getBusiestTimes();
}