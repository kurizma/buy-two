package com.buyone.productservice.listener;

import com.buyone.productservice.model.Reservation;
import com.buyone.productservice.repository.ReservationRepository;
import com.buyone.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationCleanupListenerTests {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ProductService productService;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private ReservationCleanupListener listener;

    @Captor
    private ArgumentCaptor<Query> queryCaptor;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = Reservation.builder()
                .id("res-1")
                .productId("prod-1")
                .quantity(5)
                .orderNumber("ORD-001")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .build();
    }

    @Test
    void cleanupExpiredReservations_withExpiredReservations_releasesStock() {
        // Arrange
        when(mongoTemplate.find(any(Query.class), eq(Reservation.class), eq("reservations")))
                .thenReturn(List.of(reservation));

        // Act
        listener.cleanupExpiredReservations();

        // Assert
        verify(productService).releaseStock("prod-1", 5);
        verify(mongoTemplate).remove(reservation);
    }

    @Test
    void cleanupExpiredReservations_withNoExpiredReservations_doesNothing() {
        // Arrange
        when(mongoTemplate.find(any(Query.class), eq(Reservation.class), eq("reservations")))
                .thenReturn(Collections.emptyList());

        // Act
        listener.cleanupExpiredReservations();

        // Assert
        verify(productService, never()).releaseStock(anyString(), anyInt());
        verify(mongoTemplate, never()).remove(any(Reservation.class));
    }

    @Test
    void cleanupExpiredReservations_withMultipleReservations_releasesAll() {
        // Arrange
        Reservation res2 = Reservation.builder()
                .id("res-2")
                .productId("prod-2")
                .quantity(3)
                .orderNumber("ORD-002")
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .build();

        when(mongoTemplate.find(any(Query.class), eq(Reservation.class), eq("reservations")))
                .thenReturn(List.of(reservation, res2));

        // Act
        listener.cleanupExpiredReservations();

        // Assert
        verify(productService).releaseStock("prod-1", 5);
        verify(productService).releaseStock("prod-2", 3);
        verify(mongoTemplate).remove(reservation);
        verify(mongoTemplate).remove(res2);
    }

    @Test
    void cleanupExpiredReservations_whenReleaseStockFails_continuesWithNextReservation() {
        // Arrange
        Reservation res2 = Reservation.builder()
                .id("res-2")
                .productId("prod-2")
                .quantity(3)
                .orderNumber("ORD-002")
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .build();

        when(mongoTemplate.find(any(Query.class), eq(Reservation.class), eq("reservations")))
                .thenReturn(List.of(reservation, res2));
        
        doThrow(new RuntimeException("Release failed"))
                .when(productService).releaseStock("prod-1", 5);

        // Act
        listener.cleanupExpiredReservations();

        // Assert - second reservation should still be processed
        verify(productService).releaseStock("prod-2", 3);
        verify(mongoTemplate).remove(res2);
        // First reservation's remove should not be called due to exception
        verify(mongoTemplate, never()).remove(reservation);
    }

    @Test
    void cleanupExpiredReservations_usesCorrectQuery() {
        // Arrange
        when(mongoTemplate.find(any(Query.class), eq(Reservation.class), eq("reservations")))
                .thenReturn(Collections.emptyList());

        // Act
        listener.cleanupExpiredReservations();

        // Assert
        verify(mongoTemplate).find(queryCaptor.capture(), eq(Reservation.class), eq("reservations"));
        Query query = queryCaptor.getValue();
        assertNotNull(query);
    }
}
