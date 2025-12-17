package com.reservation.reservation;

import java.time.LocalDate;

public record Reservation(
        Long id,
        Long userId,
        Long roomId,
        LocalDate endDate,
        LocalDate startDate,
        ReservationStatus status) {
}
