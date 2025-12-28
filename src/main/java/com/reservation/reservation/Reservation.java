package com.reservation.reservation;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record Reservation(
                @Null Long id,
                @NotNull Long userId,
                @NotNull Long roomId,
                @FutureOrPresent @NotNull LocalDate endDate,
                @FutureOrPresent @NotNull LocalDate startDate,
                ReservationStatus status) {
}
