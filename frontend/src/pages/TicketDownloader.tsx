import {useState } from 'react';
import { fetchTicketInfo, downloadTicketPdf } from '../services/digitalTicketService';
import { TicketInfo } from '../types/digitalTicket';

const TicketDownloader: React.FC = () => {
    const [ticketId, setTicketId] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [ticketInfo, setTicketInfo] = useState<TicketInfo | null>(null);

    const handleFetchTicket = async () => {
        if (!ticketId.trim()) {
            setError('Please enter a Ticket ID');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const info = await fetchTicketInfo(ticketId);
            setTicketInfo(info);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'An error occurred');
            setTicketInfo(null);
        } finally {
            setLoading(false);
        }
    };

    const handleDownloadPdf = async () => {
        if (!ticketId.trim()) return;

        setLoading(true);
        setError(null);

        try {
            await downloadTicketPdf(ticketId);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to download PDF');
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleString();
    };

    return (
        <div className="ticket-downloader">
            <div className="ticket-downloader-container">
                <h1>Digital Ticket Downloader</h1>

                <div className="input-group">
                    <input
                        type="text"
                        placeholder="Enter Ticket ID"
                        value={ticketId}
                        onChange={(e) => setTicketId(e.target.value)}
                        className="ticket-input"
                        disabled={loading}
                    />
                    <button
                        onClick={handleFetchTicket}
                        disabled={loading}
                        className="fetch-button"
                    >
                        {loading ? 'Loading...' : 'Get Ticket'}
                    </button>
                </div>

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                {ticketInfo && (
                    <div className="ticket-preview">
                        <h2>Ticket Preview</h2>
                        <div className="ticket-card">
                            <div className="ticket-header">
                                <h3>{ticketInfo.eventName}</h3>
                                <span className="tier-badge">{ticketInfo.tierDescription}</span>
                            </div>

                            <div className="ticket-body">
                                <div className="ticket-info">
                                    <div className="info-row">
                                        <span className="label">Event:</span>
                                        <span className="value">{ticketInfo.eventName}</span>
                                    </div>
                                    <div className="info-row">
                                        <span className="label">Description:</span>
                                        <span className="value">{ticketInfo.eventDescription}</span>
                                    </div>
                                    <div className="info-row">
                                        <span className="label">Date & Time:</span>
                                        <span className="value">
                      {formatDate(ticketInfo.eventStartDate)}
                                            {ticketInfo.eventEndDate && ` - ${formatDate(ticketInfo.eventEndDate)}`}
                    </span>
                                    </div>
                                    <div className="info-row">
                                        <span className="label">Venue:</span>
                                        <span className="value">{ticketInfo.venueName}</span>
                                    </div>
                                    <div className="info-row">
                                        <span className="label">Address:</span>
                                        <span className="value">{ticketInfo.venueAddress}</span>
                                    </div>
                                    <div className="info-row">
                                        <span className="label">Ticket Type:</span>
                                        <span className="value">{ticketInfo.tierDescription}</span>
                                    </div>
                                    <div className="info-row">
                                        <span className="label">Price:</span>
                                        <span className="value">${ticketInfo.ticketPrice.toFixed(2)}</span>
                                    </div>
                                    <div className="info-row">
                                        <span className="label">Purchase Date:</span>
                                        <span className="value">{formatDate(ticketInfo.purchaseDate)}</span>
                                    </div>
                                </div>
                            </div>

                            <button
                                onClick={handleDownloadPdf}
                                disabled={loading}
                                className="download-button"
                            >
                                {loading ? 'Generating PDF...' : 'Download PDF Ticket'}
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default TicketDownloader;