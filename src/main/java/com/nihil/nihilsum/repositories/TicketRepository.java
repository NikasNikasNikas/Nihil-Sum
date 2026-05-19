package com.nihil.nihilsum.repositories;

import com.nihil.nihilsum.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketUuid(UUID ticketUuid);
}
