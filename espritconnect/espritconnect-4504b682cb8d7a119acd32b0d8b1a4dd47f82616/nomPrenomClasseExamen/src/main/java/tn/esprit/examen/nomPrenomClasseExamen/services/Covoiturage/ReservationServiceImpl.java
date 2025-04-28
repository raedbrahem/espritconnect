package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import kotlin.jvm.internal.MagicApiIntrinsics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Notification;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Paiement;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Reservation;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.Trajet;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.NotificationRepositoryy;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.PaiementRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.ReservationRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.TrajetRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tn.esprit.examen.nomPrenomClasseExamen.services.LearnIT.EmailService;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReservationServiceImpl implements IServiceReservation {

    private final ReservationRepository reservationRepository;
    @Autowired
    private NotificationRepositoryy notificationRepositoryy;
    @Autowired
    private SseEmitterService sseEmitterService;
    @Autowired
    private EmailServicee emailService;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }
    @Autowired
    private TrajetRepository trajetRepository;// Assurez-vous d'avoir ce repository
    @Autowired
    private PaiementRepository paiementRepository;
    @Autowired
    private SseEmitterService notificationService;
    @PersistenceContext
    private EntityManager entityManager;



    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(Reservation.class);

    @Override
    public List<Reservation> retrieveAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        for (Reservation reservation : reservations) {
            // Ajoutez l'ID du trajet dans chaque réservation
            if (reservation.getTrajet() != null) {
                reservation.getTrajet().getId_trajet(); // Vous pouvez l'utiliser ou le renvoyer dans une projection si nécessaire
            }
        }
        return reservations;
    }

    @Override
    public Reservation retrieveReservation(Long id_reservation) {
        return reservationRepository.findById(id_reservation).orElse(null);
    }

    @Transactional
    @Override
    public Reservation addReservation(Reservation reservation, Long idTrajet) {
        try {
            // Récupération de l'utilisateur connecté
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Récupération du trajet
            Trajet trajet = trajetRepository.findByIdWithConducteur(idTrajet)
                    .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

            // ✅ Validation des données de la réservation
            int placesDemandées = reservation.getNombrePlacesReservees();
            int placesDisponibles = trajet.getPlacesDisponibles();

            if (placesDemandées <= 0) {
                throw new RuntimeException("Le nombre de places réservées doit être supérieur à zéro.");
            }

            if (placesDisponibles <= 0) {
                throw new RuntimeException("Aucune place disponible pour ce trajet.");
            }

            if (placesDemandées > placesDisponibles) {
                throw new RuntimeException("Le nombre de places demandées dépasse les places disponibles (" + placesDisponibles + ").");
            }

            // Mise à jour du nombre de places restantes
            trajet.setPlacesDisponibles(placesDisponibles - placesDemandées);
            trajetRepository.save(trajet);

            // Compléter les informations de la réservation
            reservation.setEtudiant(user);
            reservation.setTrajet(trajet);
            reservation.setDateReservation(LocalDateTime.now());

            // Sauvegarder la réservation
            Reservation savedReservation = reservationRepository.save(reservation);

            // ✅ Envoi de l'email avec QR Code
            String mapUrl = "https://www.google.com/maps/dir/" +
                    trajet.getDepartLat() + "," + trajet.getDepartLng() + "/" +
                    trajet.getArriveeLat() + "," + trajet.getArriveeLng();

            BitMatrix matrix = new MultiFormatWriter().encode(mapUrl, BarcodeFormat.AZTEC.QR_CODE, 250, 250);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

            emailService.sendQrCodeEmail(
                    user.getEmail(),
                    "Confirmation de votre réservation",
                    "Merci pour votre réservation ! Voici votre trajet sur Google Maps : " + mapUrl,
                    qrImage
            );

            // 🔥 Gestion du paiement si nécessaire
            if (reservation.getMontant() != null && reservation.getMontant() > 0) {
                handlePaiement(savedReservation);
            }

            // 🔥 Envoyer une notification au conducteur
            createAndSendNotification(savedReservation, trajet.getConducteur());

            return savedReservation;

        } catch (Exception e) {
            log.error("Erreur  de l'ajout de la réservation : {}", e.getMessage());
            throw new RuntimeException(e.getMessage(), e); // ✅ Renvoyer le vrai message ici
        }
    }



    private void handlePaiement(Reservation reservation) {
        if (effectuerPaiement(reservation.getEtudiant(), reservation.getMontant())) {
            Paiement paiement = new Paiement();
            paiement.setCreatedAt(LocalDateTime.now());
            paiement.setMontant(reservation.getMontant());
            paiement.setUtilisateur(reservation.getEtudiant());
            paiement.setReservation(reservation);
            paiementRepository.save(paiement);
            log.info("Paiement enregistré pour la réservation ID: {}", reservation.getId_reservation());
        }
    }
    private void createAndSendNotification(Reservation reservation, User conducteur) {
        Notification notifConducteur = new Notification();
        notifConducteur.setMessage(String.format(
                "%s %s a réservé %d place(s) sur votre trajet",
                reservation.getEtudiant().getNom(),
                reservation.getEtudiant().getPrenom(),
                reservation.getNombrePlacesReservees()
        ));
        notifConducteur.setDateCreation(LocalDateTime.now());
        notifConducteur.setLue(false);
        notifConducteur.setUser(conducteur);  // ✅ correct : on affecte bien au conducteur
        notifConducteur.setReservation(reservation);

        Notification savedNotifConducteur = notificationRepositoryy.save(notifConducteur);

        if (sseEmitterService != null) {
            try {
                sseEmitterService.sendNotification(conducteur.getId(), savedNotifConducteur);
            } catch (Exception e) {
                log.error("Erreur SSE pour conducteur : {}", e.getMessage());
            }
        }
    }
    @Transactional
    @Override
    public void removeReservation(Long id_reservation) {
        Reservation reservation = reservationRepository.findByIdWithTrajet(id_reservation)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        Trajet trajet = reservation.getTrajet();

        if (trajet != null) {
            int placesReservees = reservation.getNombrePlacesReservees();
            int placesDisponiblesActuelles = trajet.getPlacesDisponibles();
            int nouvellesPlacesDisponibles = Math.min(4, Math.max(0, placesDisponiblesActuelles + placesReservees));

            trajet.setPlacesDisponibles(nouvellesPlacesDisponibles);
            trajet.getReservations().remove(reservation);
            trajetRepository.save(trajet);
        }

        // 1️⃣ Supprimer les notifications liées AVANT de supprimer la réservation
        notificationRepositoryy.deleteByReservationId(id_reservation);
        log.info("✅ Notifications liées à la réservation {} supprimées.", id_reservation);

        // 2️⃣ Très important : détacher pour éviter Hibernate de faire un update avant delete
        entityManager.detach(reservation);

        // 3️⃣ Puis suppression de la réservation
        reservationRepository.delete(reservation);
        log.info("✅ Réservation ID {} supprimée avec succès.", id_reservation);
    }



    @Override
    public Reservation modifyReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
    public Optional<Reservation> findById(Long id_reservation) {
        return reservationRepository.findById(id_reservation);
    }


    public List<Reservation> getAllReservationsByTrajetId(Long id_trajet) {
        try {
            log.info("Tentative de récupération des réservations pour le trajet ID: {}", id_trajet);
            List<Reservation> reservations = reservationRepository.findByTrajetId(id_trajet);
            log.info("Nombre de réservations trouvées: {}", reservations.size());
            return reservations;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des réservations pour le trajet ID: {}", id_trajet, e);
            throw new RuntimeException("Erreur lors de la récupération des réservations: " + e.getMessage(), e);
        }

    }


    public boolean effectuerPaiement(User utilisateur, Long montant) {
        // Simule un paiement : dans un vrai cas, intégration Stripe, PayPal, etc.
        if (montant <= 0) {
            throw new RuntimeException("Montant invalide pour le paiement");
        }

        // Logique de paiement fictive : succès
        logger.info("Paiement de " + montant + " pour l'utilisateur : " + utilisateur.getEmail());
        return true;
    }

    @Override
    public List<Reservation> getAllReservationByUserId(Long userId) {
        try {
            log.info("Tentative de récupération des réservations pour l'utilisateur ID: {}", userId);

            // Vérifier si l'utilisateur existe
            if (!userRepository.existsById(userId)) {
                throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId);
            }

            List<Reservation> reservations = reservationRepository.findByEtudiantId(userId);
            log.info("Nombre de réservations trouvées pour l'utilisateur ID {}: {}", userId, reservations.size());

            return reservations;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des réservations pour l'utilisateur ID: {}", userId, e);
            throw new RuntimeException("Erreur lors de la récupération des réservations: " + e.getMessage(), e);
        }
    }





    @Override
    public Map<String, Object> getReservationStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Global statistics
        stats.put("totalReservations", reservationRepository.count());
        stats.put("totalRevenue", getTotalRevenue());

        // 2. Reservations by state
        stats.put("reservationsByState", getReservationsByState());

        // 3. Daily evolution
        stats.put("dailyEvolution", getDailyReservationStats());

        // 4. Top 3 trips
        stats.put("topTrajets", getTopTrajets(3));

        return stats;
    }

    private Long getTotalRevenue() {
        return reservationRepository.findAll().stream()
                .mapToLong(r -> r.getMontant() != null ? r.getMontant() : 0L)
                .sum();
    }

    private Map<String, Long> getReservationsByState() {
        return reservationRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getEtat().name(),
                        Collectors.counting()
                ));
    }

    private List<Map<String, Object>> getDailyReservationStats() {
        return reservationRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDateReservation().toLocalDate(),
                        Collectors.summarizingLong(Reservation::getMontant)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> dayStats = new HashMap<>();
                    dayStats.put("date", entry.getKey());
                    dayStats.put("count", entry.getValue().getCount());
                    dayStats.put("totalAmount", entry.getValue().getSum());
                    return dayStats;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getTopTrajets(int limit) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getTrajet() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getTrajet().getId_trajet(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> trajetStats = new HashMap<>();
                    trajetStats.put("trajetId", entry.getKey());
                    trajetStats.put("reservationCount", entry.getValue());
                    return trajetStats;
                })
                .collect(Collectors.toList());
    }

}
