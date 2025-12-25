package com.reservation.reservation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    @Query("select r FROM ReservationEntity r where r.status = :status")
    List<ReservationEntity> findAllByStatusIs(ReservationStatus status);

}
