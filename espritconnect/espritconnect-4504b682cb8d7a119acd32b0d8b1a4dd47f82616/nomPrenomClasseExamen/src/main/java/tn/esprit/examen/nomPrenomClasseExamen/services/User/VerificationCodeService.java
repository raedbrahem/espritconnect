package tn.esprit.examen.nomPrenomClasseExamen.services.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Utilisateur.VerificationCode;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.User.VerificationCodeRepository;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class VerificationCodeService {

    @Autowired
    private VerificationCodeRepository codeRepository;

    public String generateCode(String email) {
        String code = String.valueOf(new Random().nextInt(899999) + 100000);

        VerificationCode verification = new VerificationCode();
        verification.setEmail(email);
        verification.setCode(code);
        verification.setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 min

        codeRepository.deleteByEmail(email); // Supprimer ancien
        codeRepository.save(verification);
        return code;
    }

    public boolean verifyCode(String email, String code) {
        Optional<VerificationCode> optional = codeRepository.findByEmailAndCode(email, code);
        if (optional.isPresent()) {
            VerificationCode vc = optional.get();
            return vc.getExpiration().after(new Date());
        }
        return false;
    }
}
