package com.buyone.productservice.listener;

import com.buyone.productservice.model.Reservation;
import com.buyone.productservice.repository.ReservationRepository;
import com.buyone.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCleanupListener {
    
    private final ReservationRepository reservationRepository;
    private final ProductService productService;
    private final MongoTemplate mongoTemplate;
    
    @Scheduled(fixedRate = 60000)  // Run every 60 seconds // milliseconds unit
    public void cleanupExpiredReservations() {
        log.info("Starting cleanup...");
        
        // using index from mongoDB's query to find reservations from createdAt
        Query expiredQuery = new Query(Criteria.where("createdAt")
                .lt(LocalDateTime.now().minusMinutes(1)));
        
        List<Reservation> expired = mongoTemplate.find(expiredQuery, Reservation.class, "reservations");
        
        log.info("Found {} expired reservations", expired.size());
        
        for (Reservation res : expired) {
            try {
                productService.releaseStock(res.getProductId(), res.getQuantity());
                mongoTemplate.remove(res);
                
                log.info("Released {}x {} (order={})",
                        res.getQuantity(), res.getProductId(), res.getOrderNumber());
            } catch (Exception e) {
                log.error("Failed {}: {}", res.getId(), e.getMessage());
            }
        }

        log.info("Cleanup complete");
    }
}
