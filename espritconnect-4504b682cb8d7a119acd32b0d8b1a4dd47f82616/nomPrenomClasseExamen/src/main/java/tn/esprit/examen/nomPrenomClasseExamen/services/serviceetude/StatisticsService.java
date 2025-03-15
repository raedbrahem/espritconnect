package tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude;

import org.springframework.beans.factory.annotation.Autowired;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude.CommentaireRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude.ITutoringRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude.ServiceEtudeRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.serviceetude.rating_etudeRepository;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService implements IStatisticsService {
    @Autowired
    private ServiceEtudeRepository serviceEtudeRepository;

    @Autowired
    private ITutoringRepository tutoringEventRepository;

    @Autowired
    private rating_etudeRepository ratingEtudeRepository;

    @Autowired
    private CommentaireRepository commentaireRepository;

    // Example: Calculate average rating per tutor
    @Override
    public Map<Long, Double> getAverageRatingPerTutor() {
        List<Object[]> results = ratingEtudeRepository.findAverageRatingPerTutor();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((Number) result[0]).longValue(), // tutorId
                        result -> ((Number) result[1]).doubleValue() // avgRating
                ));
    }

    // Example: Calculate number of tutoring sessions per tutor
    @Override
    public Map<Long, Long> getNumberOfSessionsPerTutor() {
        List<Object[]> results = tutoringEventRepository.countSessionsByTutor();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((Number) result[0]).longValue(), // tutorId
                        result -> ((Number) result[1]).longValue() // sessionCount
                ));
    }

    // Example: Calculate total hours spent tutoring per tutor
    @Override
    public Map<Long, Double> getTotalHoursPerTutor() {
        List<Object[]> results = tutoringEventRepository.sumTutoringHoursByTutor();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((Number) result[0]).longValue(), // tutorId
                        result -> ((Number) result[1]).doubleValue() // totalHours
                ));
    }

    // Example: Calculate most popular subjects
    @Override
    public Map<String, Long> getMostPopularSubjects() {
        List<Object[]> results = serviceEtudeRepository.countBySubject();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0], // subject
                        result -> ((Number) result[1]).longValue() // count
                ));
    }

    @Override
    public Map<String, Long> getBusiestTimes() {
        List<Object[]> results = tutoringEventRepository.countSessionsByTimeSlot();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> "hour_" + result[0].toString(), // hour
                        result -> ((Number) result[1]).longValue() // sessionCount
                ));
    }
}