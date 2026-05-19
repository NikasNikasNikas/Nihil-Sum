package com.nihil.nihilsum.controllers;

import com.nihil.nihilsum.dtos.TicketInfoResponseDTO;
import com.nihil.nihilsum.services.TicketService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/{ticketId}/info")
    public ResponseEntity<TicketInfoResponseDTO> getTicketInfo(@PathVariable Long ticketId) {
        TicketInfoResponseDTO ticketInfo = ticketService.getTicketInfo(ticketId);
        return ResponseEntity.ok(ticketInfo);
    }

    @GetMapping("/{ticketUUID}/infoUUID")
    public ResponseEntity<TicketInfoResponseDTO> getTicketInfo(@PathVariable UUID ticketUUID) {
        TicketInfoResponseDTO ticketInfo = ticketService.getTicketInfoByUUID(ticketUUID);
        return ResponseEntity.ok(ticketInfo);
    }

    @GetMapping("/{ticketId}/pdf")
    public ResponseEntity<byte[]> generateTicketPdf(@PathVariable Long ticketId) {
        byte[] pdfBytes = ticketService.generateTicketPdf(ticketId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket_" + ticketId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}