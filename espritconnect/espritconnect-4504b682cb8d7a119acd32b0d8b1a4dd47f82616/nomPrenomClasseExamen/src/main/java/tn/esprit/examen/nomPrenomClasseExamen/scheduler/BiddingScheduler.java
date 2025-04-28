package tn.esprit.examen.nomPrenomClasseExamen.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.esprit.examen.nomPrenomClasseExamen.services.marketplace.BidService;

@Component
public class BiddingScheduler {

    private final BidService bidService;

    public BiddingScheduler(BidService bidService) {
        this.bidService = bidService;
    }

    /**
     * Scheduled task to check every minute if there are expired products
     * and create orders for them automatically
     */
    @Scheduled(fixedRate = 60000) // 1 minute in milliseconds
    public void checkAndUpdateExpiredProducts() {
        bidService.checkAndUpdateExpiredProducts();
        System.out.println("Scheduled task: Checked for expired products and created orders at " + java.time.LocalDateTime.now());
    }
}
