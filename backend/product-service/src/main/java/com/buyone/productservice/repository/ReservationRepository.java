package com.buyone.productservice.repository;

import com.buyone.productservice.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    void deleteByOrderNumber(String orderNumber);  // Bulk delete
}
