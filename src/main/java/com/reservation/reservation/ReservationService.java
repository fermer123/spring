package com.reservation.reservation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    private final Map<Long, Reservation> reservationMap;
    private final AtomicLong idCounter;

    public ReservationService() {
        reservationMap = new HashMap<>();
        idCounter = new AtomicLong();
    }

    public Reservation getReservationById(Long id) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation By id: " + id);
        }
        return reservationMap.get(id);
    }

    public List<Reservation> getAllReservation() {
        return reservationMap.values().stream().toList();
    }

    public Reservation createReservation(Reservation reservataionToCreate) {
        if (reservataionToCreate.id() != null) {
            throw new IllegalArgumentException("Id shout be empty");
        }
        if (reservataionToCreate.status() != null) {
            throw new IllegalArgumentException("Status shout be empty");

        }
        Long newId = idCounter.incrementAndGet();
        var newReservation = new Reservation(newId, reservataionToCreate.userId(),
                reservataionToCreate.roomId(), reservataionToCreate.endDate(), reservataionToCreate.startDate(),
                ReservationStatus.PENDING);
        reservationMap.put(newReservation.id(), newReservation);
        return newReservation;
    }

    public Reservation updateReservation(Reservation reservataionToCreate) {
        if (!reservationMap.containsKey(reservataionToCreate.id())) {
            throw new IllegalArgumentException("Id shout be empty");
        }
        var reservation = reservationMap.get(reservataionToCreate.id());
        if (reservation.status() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Cannot modify reservation status");
        }
        var updatedReservation = new Reservation(reservation.id(), reservation.userId(),
                reservation.roomId(), reservation.endDate(), reservation.startDate(),
                ReservationStatus.PENDING);

        reservationMap.put(reservataionToCreate.id(), updatedReservation);
        return reservation;
    }

    public void deleteReservation(Long id) {
        if (!reservationMap.containsKey(id)) {
            throw new IllegalArgumentException("Id shout be empty");
        }
        reservationMap.remove(id);
    }

    public Reservation approveReservation(Long id) {
        if (!reservationMap.containsKey(id)) {
            throw new IllegalArgumentException("Id shout be empty");
        }
        var reservation = reservationMap.get(id);
        if (reservation.status() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Cannot approve reservation status");
        }
        var isCOnflict = isReservationConflict(reservation);
        if (isCOnflict) {
            throw new IllegalArgumentException("Cannot approve reservation because is conflict");
        }
        var approveReservation = new Reservation(reservation.id(), reservation.userId(),
                reservation.roomId(), reservation.endDate(), reservation.startDate(),
                ReservationStatus.APPROVED);
        reservationMap.put(id, approveReservation);
        return approveReservation;
    }

    private boolean isReservationConflict(Reservation reservation) {
        for (Reservation existingReservation : reservationMap.values()) {
            if (reservation.id().equals(existingReservation.id())) {
                continue;
            }
            if (!reservation.roomId().equals(existingReservation.roomId())) {
                continue;
            }
            if (existingReservation.status().equals(ReservationStatus.APPROVED)) {
                continue;
            }
            if (reservation.startDate().isBefore(existingReservation.endDate())
                    && existingReservation.endDate().isBefore(reservation.endDate())) {
                return true;
            }
        }
        return false;
    }

}
