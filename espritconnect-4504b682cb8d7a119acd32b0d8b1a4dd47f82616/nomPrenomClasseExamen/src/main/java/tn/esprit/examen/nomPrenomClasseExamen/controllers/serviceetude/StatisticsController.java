package tn.esprit.examen.nomPrenomClasseExamen.controllers.serviceetude;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.Role;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.services.User.UserService;
import tn.esprit.examen.nomPrenomClasseExamen.services.serviceetude.StatisticsService;

import java.util.Map;
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular frontend
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private UserService userService;

    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Helper method to check if the user has the ADMIN role
    private void checkIfUserIsAdmin() {
        User user = getAuthenticatedUser();
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("You are not authorized to access this resource.");
        }
    }

    @GetMapping("/average-rating-per-tutor")
    public Map<String, Double> getAverageRatingPerTutor() {
        checkIfUserIsAdmin(); // Check if user is admin
        return statisticsService.getAverageRatingPerTutor();
    }

    @GetMapping("/number-of-sessions-per-tutor")
    public Map<String, Long> getNumberOfSessionsPerTutor() {
        checkIfUserIsAdmin(); // Check if user is admin
        return statisticsService.getNumberOfSessionsPerTutor();
    }

    @GetMapping("/total-hours-per-tutor")
    public Map<String, Double> getTotalHoursPerTutor() {
        checkIfUserIsAdmin(); // Check if user is admin
        return statisticsService.getTotalHoursPerTutor();
    }

    @GetMapping("/most-popular-subjects")
    public Map<String, Long> getMostPopularSubjects() {
        checkIfUserIsAdmin(); // Check if user is admin
        return statisticsService.getMostPopularSubjects();
    }

    @GetMapping("/busiest-times")
    public Map<String, Long> getBusiestTimes() {
        checkIfUserIsAdmin(); // Check if user is admin
        return statisticsService.getBusiestTimes();
    }
}
