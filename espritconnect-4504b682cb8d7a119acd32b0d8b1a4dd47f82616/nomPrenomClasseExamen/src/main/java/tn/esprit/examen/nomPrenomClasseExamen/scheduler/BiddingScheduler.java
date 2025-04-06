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

    // Scheduled task to check every minute if there are expired products
    @Scheduled(fixedRate = 2000) // 1 minute in milliseconds
    public void checkAndUpdateExpiredProducts() {
        bidService.checkAndUpdateExpiredProducts();
    }
}
