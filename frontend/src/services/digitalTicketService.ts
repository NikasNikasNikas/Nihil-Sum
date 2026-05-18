// frontend/src/services/ticketService.ts
import { TicketInfo } from '../types/digitalTicket';

const API_BASE_URL = '/api/tickets';

export async function fetchTicketInfo(ticketId: string): Promise<TicketInfo> {
    const response = await fetch(`${API_BASE_URL}/${ticketId}/info`);
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Failed to fetch ticket information');
    }
    return response.json();
}

export async function downloadTicketPdf(ticketId: string): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/${ticketId}/pdf`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/pdf',
        },
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Failed to generate PDF');
    }

    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `ticket_${ticketId}.pdf`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
}