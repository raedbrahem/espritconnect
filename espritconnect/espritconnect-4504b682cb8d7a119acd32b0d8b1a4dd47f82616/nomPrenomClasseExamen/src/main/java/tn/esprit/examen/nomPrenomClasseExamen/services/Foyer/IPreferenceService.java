package tn.esprit.examen.nomPrenomClasseExamen.services.Foyer;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Foyer.Preference;

import java.util.List;

public interface IPreferenceService {

    List<Preference> getAllPreferences();

    Preference getPreferenceById(Long id);

    Preference addPreference(Preference preference);

    Preference updatePreference(Long id, Preference preference);
    //List<Preference> getPreferencesByEmail(String email);
    void deletePreference(Long id);

    Preference getPreferenceByCurrentUser();// Corrigé ici
}
