package tn.esprit.examen.nomPrenomClasseExamen.services.Foyer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Foyer;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.ReservationF;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.ReservationF.StatutReservation;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.IFoyerRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Foyer.ReservationFRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationFService {

    @Autowired
    private ReservationFRepository reservationRepo;

    @Autowired
    private IFoyerRepository foyerRepo;

    @Autowired
    private UserRepository userRepo;

    public ReservationF creerReservation(Long foyerId, Long demandeurId,
                                         LocalDate dateDebut, LocalDate dateFin,
                                         String message) {
        if (dateDebut.isAfter(dateFin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date début après date fin");
        }
        if (dateDebut.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date dans le passé");
        }

        Foyer foyer = foyerRepo.findById(foyerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foyer non trouvé"));
        User demandeur = userRepo.findById(demandeurId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        if (foyer.getUser().getId().equals(demandeurId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de réserver son propre foyer");
        }

        if (hasReservationConflict(foyerId, dateDebut, dateFin)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Foyer déjà réservé à ces dates");
        }

        ReservationF reservation = new ReservationF();
        reservation.setDemandeur(demandeur);
        reservation.setFoyer(foyer);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setMessageDemande(message);
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        return reservationRepo.save(reservation);
    }

    public ReservationF accepterReservation(Long reservationId, Long proprietaireId) {
        ReservationF reservation = getValidReservation(reservationId, proprietaireId);

        if (hasReservationConflict(reservation.getFoyer().getId(), reservation.getDateDebut(), reservation.getDateFin())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflit avec une autre réservation confirmée");
        }

        reservation.setStatut(StatutReservation.CONFIRMEE);
        return reservationRepo.save(reservation);
    }

    public void refuserReservation(Long reservationId, Long proprietaireId) {
        ReservationF reservation = getValidReservation(reservationId, proprietaireId);
        reservation.setStatut(StatutReservation.ANNULEE);
        reservationRepo.save(reservation);
    }

    public void annulerReservation(Long reservationId, Long proprietaireId) {
        ReservationF reservation = getValidReservation(reservationId, proprietaireId);
        reservation.setStatut(StatutReservation.ANNULEE);
        reservationRepo.save(reservation);
    }

    public void supprimerParDemandeur(Long reservationId, Long demandeurId) {
        ReservationF reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation non trouvée"));

        if (!reservation.getDemandeur().getId().equals(demandeurId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez pas supprimer cette réservation");
        }

        reservationRepo.deleteById(reservationId);
    }


    public List<ReservationF> getReservationsForUserFoyers(Long userId) {
        return reservationRepo.findByFoyerOwnerId(userId);
    }

    public List<ReservationF> getReservationsParDemandeur(Long demandeurId) {
        return reservationRepo.findByDemandeurId(demandeurId);
    }

    private boolean hasReservationConflict(Long foyerId, LocalDate debut, LocalDate fin) {
        return reservationRepo.existsConflictingReservation(foyerId, debut, fin);
    }

    private ReservationF getValidReservation(Long reservationId, Long proprietaireId) {
        ReservationF reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation non trouvée"));

        if (!reservation.getFoyer().getUser().getId().equals(proprietaireId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action non autorisée pour ce foyer");
        }

        return reservation;
    }
}
