package com.nihil.nihilsum.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.nihil.nihilsum.dtos.TicketInfoResponseDTO;
import com.nihil.nihilsum.dtos.TicketTierDTO;
import com.nihil.nihilsum.models.*;
import com.nihil.nihilsum.repositories.EventRepository;
import com.nihil.nihilsum.repositories.TicketRepository;
import com.nihil.nihilsum.repositories.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository _ticketRepository;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    public TicketInfoResponseDTO getTicketInfo(Long ticketId) {
        Ticket ticket = _ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        //if (ticket.getPurchased() != PurchaseStatus.PURCHASED) {
        //    throw new RuntimeException("Ticket is not valid for download. Status: " + ticket.getPurchased());
        //}

        Event event = ticket.getEvent();
        Venue venue = event.getVenue();
        TicketTier ticketTier = ticket.getTicketTier();

        return TicketInfoResponseDTO.builder()
                .id(ticket.getId())
                .eventName(getEventName(event))
                .eventDescription(event.getEventDescription())
                .eventStartDate(event.getStartDate())
                .eventEndDate(event.getEndDate())
                .venueName(getVenueName(venue))
                .venueAddress(venue.getAddress())
                .tierDescription(ticketTier.getTierDescription())
                .ticketPrice(ticketTier.getTicketPrice())
                .purchaseDate(ticket.getPurchaseDate())
                .build();
    }

    public byte[] generateTicketPdf(Long ticketId) {
        TicketInfoResponseDTO ticketInfo = getTicketInfo(ticketId);
        String ticketUuid = getTicketUuid(ticketId);
        byte[] qrCodeBytes = generateQRCodeBytes(ticketUuid, 300);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            PdfFont boldFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            Paragraph title = new Paragraph("EVENT TICKET")
                    .setFont(boldFont)
                    .setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            // Add event.ts name
            Paragraph eventName = new Paragraph(ticketInfo.getEventName())
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(30);
            document.add(eventName);

            // Create info table
            Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            String dateTimeString = formatDateTime(ticketInfo.getEventStartDate());
            if (ticketInfo.getEventEndDate() != null) {
                dateTimeString += " - " + formatDateTime(ticketInfo.getEventEndDate());
            }

            addTableRow(table, "Ticket ID:", ticketUuid, boldFont);
            addTableRow(table, "Event:", ticketInfo.getEventName(), boldFont);
            addTableRow(table, "Description:", ticketInfo.getEventDescription(), boldFont);
            addTableRow(table, "Date & Time:", dateTimeString, boldFont);
            addTableRow(table, "Venue:", ticketInfo.getVenueName(), boldFont);
            addTableRow(table, "Address:", ticketInfo.getVenueAddress(), boldFont);
            addTableRow(table, "Ticket Type:", ticketInfo.getTierDescription(), boldFont);
            addTableRow(table, "Price:", "$" + ticketInfo.getTicketPrice(), boldFont);
            addTableRow(table, "Purchase Date:", formatDateTime(ticketInfo.getPurchaseDate()), boldFont);

            document.add(table);

            // Add QR code directly from byte array
            if (qrCodeBytes != null && qrCodeBytes.length > 0) {
                Image qrImage = new Image(com.itextpdf.io.image.ImageDataFactory.create(qrCodeBytes));
                qrImage.setWidth(300);
                qrImage.setHeight(300);
                qrImage.setMarginTop(20);
                qrImage.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                document.add(qrImage);
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    private String getTicketUuid(Long ticketId) {
        Ticket ticket = _ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        return ticket.getTicketUuid().toString();
    }

    private void addTableRow(Table table, String label, String value, PdfFont boldFont) {
        Cell labelCell = new Cell().add(new Paragraph(label).setFont(boldFont));
        labelCell.setBorder(Border.NO_BORDER);
        labelCell.setPadding(5);

        Cell valueCell = new Cell().add(new Paragraph(value != null ? value : "N/A"));
        valueCell.setBorder(Border.NO_BORDER);
        valueCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }


    private byte[] generateQRCodeBytes(String data, int size) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    private String getEventName(Event event) {
        return event.getName();
    }

    private String getVenueName(Venue venue) {
        return  venue.getName();
    }

    public long getRemainingTickets(TicketTierDTO tier) {
        long soldTickets = _ticketRepository.countByTicketTierId(tier.getId());
        long totalTickets = tier.getTotal();
        long remainingTickets = totalTickets - soldTickets;
        return Math.max(remainingTickets, 0);
    }

}