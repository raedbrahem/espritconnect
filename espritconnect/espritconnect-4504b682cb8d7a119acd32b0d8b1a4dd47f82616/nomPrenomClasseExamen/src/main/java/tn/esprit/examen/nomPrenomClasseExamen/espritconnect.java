package tn.esprit.examen.nomPrenomClasseExamen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@EnableScheduling
@SpringBootApplication
public class espritconnect {

    public static void main(String[] args) {
        SpringApplication.run(espritconnect.class, args);
    }

}
