package com.reservation.reservation;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReservationService {

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation getReservationById(Long id) {
        ReservationEntity repo = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation By id: " + id));

        return toDomainREservation(repo);

    }

    public List<Reservation> getAllReservation() {
        List<ReservationEntity> allEntities = repository.findAll();

        return allEntities.stream().map(this::toDomainREservation).toList();
    }

    public Reservation createReservation(Reservation reservataionToCreate) {
        if (reservataionToCreate.id() != null) {
            throw new IllegalArgumentException("Id shout be empty");
        }
        if (reservataionToCreate.status() != null) {
            throw new IllegalArgumentException("Status shout be empty");

        }

        var entityToSave = new ReservationEntity(null,
                reservataionToCreate.userId(),
                reservataionToCreate.roomId(),
                reservataionToCreate.endDate(),
                reservataionToCreate.startDate(),
                ReservationStatus.PENDING);

        var savedEntity = repository.save(entityToSave);
        return toDomainREservation(savedEntity);
    }

    public Reservation updateReservation(Long id) {

        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation By id: " + id));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Cannot modify reservation status: " + reservationEntity.getStatus());
        }
        var reservationToSave = new ReservationEntity(reservationEntity.getId(), reservationEntity.getUserId(),
                reservationEntity.getRoomId(), reservationEntity.getEndDate(), reservationEntity.getStartDate(),
                ReservationStatus.PENDING);

        var updatedEntity = repository.save(reservationToSave);
        return toDomainREservation(updatedEntity);
    }

    public void deleteReservation(Long id) {
        if (!repository.findById(id).isPresent()) {
            throw new EntityNotFoundException("Not found reservation By id: " + id);
        }
        repository.deleteById(id);
    }

    public Reservation approveReservation(Long id) {

        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation By id: " + id));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Cannot approve reservation status");
        }
        var isCOnflict = isReservationConflict(reservationEntity);

        if (isCOnflict) {
            throw new IllegalArgumentException("Cannot approve reservation because is conflict");
        }

        reservationEntity.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationEntity);

        return toDomainREservation(reservationEntity);
    }

    private boolean isReservationConflict(ReservationEntity reservation) {
        var allReservation = repository.findAll();
        for (ReservationEntity existingReservation : allReservation) {
            if (reservation.getId().equals(existingReservation.getId())) {
                continue;
            }
            if (!reservation.getRoomId().equals(existingReservation.getRoomId())) {
                continue;
            }
            if (existingReservation.getStatus().equals(ReservationStatus.APPROVED)) {
                continue;
            }
            if (reservation.getStartDate().isBefore(existingReservation.getEndDate())
                    && existingReservation.getEndDate().isBefore(reservation.getEndDate())) {
                return true;
            }
        }
        return false;
    }

    private Reservation toDomainREservation(ReservationEntity reservation) {
        return new Reservation(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getEndDate(),
                reservation.getStartDate(),
                reservation.getStatus());

    }

}
