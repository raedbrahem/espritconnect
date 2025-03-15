package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import java.util.Map;

public interface IStatisticsService {
    Map<Long, Double> getAverageRatingPerTutor();
    Map<Long, Long> getNumberOfSessionsPerTutor();
    Map<Long, Double> getTotalHoursPerTutor();
    Map<String, Long> getMostPopularSubjects();
    Map<String, Long> getBusiestTimes();
}