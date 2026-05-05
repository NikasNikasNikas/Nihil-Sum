package com.nihil.nihilsum.models;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ticket_uuid", nullable = false, unique = true)
    private UUID ticketUuid;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseStatus purchased;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_tier_id", nullable = false)
    private TicketTier ticketTier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "scanned_date")
    private LocalDateTime scannedDate;
    
    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;
}

