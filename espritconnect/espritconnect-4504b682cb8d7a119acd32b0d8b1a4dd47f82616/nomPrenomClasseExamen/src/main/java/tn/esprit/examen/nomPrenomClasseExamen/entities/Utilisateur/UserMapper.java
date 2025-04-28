package tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setPhotoProfil(user.getPhotoProfil());
        return dto;
    }
}
