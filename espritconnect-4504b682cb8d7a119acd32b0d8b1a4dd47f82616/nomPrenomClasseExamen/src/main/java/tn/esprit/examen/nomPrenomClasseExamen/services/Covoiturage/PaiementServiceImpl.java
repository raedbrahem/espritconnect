package tn.esprit.examen.nomPrenomClasseExamen.services.Covoiturage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Covoiturage.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.PaiementRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.Covoiturage.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class PaiementServiceImpl implements IServicePaiement{
    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Paiement effectuerPaiement(Long reservationId, double montant, String moyenPaiement) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation non trouvée"));

        // Créer un objet Paiement
        Paiement paiement = new Paiement();
        paiement.setMontant(montant);
        paiement.setMoyenPaiement(moyenPaiement.equals("CARTEBANCAIRE") ? MoyenPaiement.CARTEBANCAIRE : MoyenPaiement.CASH);
        paiement.setStatutPaiement(StatutPaiement.EN_ATTENTE);  // Par défaut, le paiement est en attente
        paiement.setDate_transaction(LocalDateTime.now());
        paiement.setReservation(reservation);

        // Sauvegarder le paiement dans la base de données
        paiement = paiementRepository.save(paiement);

        // Mettre à jour l'état de la réservation
        reservation.setEtat(EtatReservation.CONFIRME); // On peut choisir de changer l'état selon le statut du paiement
        reservationRepository.save(reservation);

        return paiement;
    }
    @Override
    public Paiement mettreAJourStatutPaiement(Long paiementId, StatutPaiement statut) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement non trouvé"));

        paiement.setStatutPaiement(statut);
        return paiementRepository.save(paiement);
    }

}
