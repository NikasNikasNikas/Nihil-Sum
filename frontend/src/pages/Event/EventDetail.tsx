// @ts-ignore
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Container,
    Typography,
    CircularProgress,
    Alert,
    Box,
    Card,
    CardContent,
    Button,
    Divider,
    Chip,
    Paper,
    Grid
} from '@mui/material';
import {
    Event as EventIcon,
    LocationOn as LocationIcon,
    CalendarMonth as CalendarIcon,
    AccessTime as TimeIcon,
    ConfirmationNumber as TicketIcon,
    ArrowBack as ArrowBackIcon
} from '@mui/icons-material';
import { format } from 'date-fns';
import './EventDetail.css';

interface Venue {
    id: number;
    name: string;
    country: string;
    city: string;
    address: string;
    ownerId?: number;
    capacity?: number;
}

interface TicketTier {
    id: number;
    eventId: number;
    ticketPrice: number;
    total: number;
    tierDescription: string;
}

interface EventDetail {
    id: number;
    name: string;
    startDate: string;
    endDate?: string;
    eventDescription?: string;
    venue: Venue;
    ticketTiers: TicketTier[];
}

const EventDetail: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [event, setEvent] = useState<EventDetail | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchEventDetails = async () => {
            try {
                setLoading(true);
                const response = await fetch(`/api/event/${id}`);
                if (!response.ok) throw new Error('Failed to fetch event details');
                const data = await response.json();
                setEvent(data);
                setError(null);
            } catch (err) {
                setError('Unable to load event details.');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchEventDetails();
        }
    }, [id]);

    const handleBuyTicket = (tierId: number, tierDescription: string, price: number) => {
        // Redirect to ticket purchase page or open modal
        window.location.href = `/checkout?eventId=${event?.id}&tierId=${tierId}`;
        console.log(`Buying ticket for ${tierDescription} - $${price}`);
    };

    const handleGoBack = () => {
        navigate('/');
    };

    if (loading) {
        return (
            <Container className="loading-container">
                <CircularProgress />
                <Typography>Loading event details...</Typography>
            </Container>
        );
    }

    if (error) {
        return (
            <Container className="error-container">
                <Alert severity="error">{error}</Alert>
                <Button onClick={handleGoBack} variant="contained" sx={{ mt: 2 }}>
                    Go Back
                </Button>
            </Container>
        );
    }

    if (!event) {
        return (
            <Container className="error-container">
                <Alert severity="warning">Event not found</Alert>
                <Button onClick={handleGoBack} variant="contained" sx={{ mt: 2 }}>
                    Go Back
                </Button>
            </Container>
        );
    }

    const startDate = new Date(event.startDate);
    const endDate = event.endDate ? new Date(event.endDate) : null;

    return (
        <div className="detail-page-wrapper">
            <Container className="detail-container">
                <Button
                    onClick={handleGoBack}
                    className="back-button"
                    startIcon={<ArrowBackIcon />}
                >
                    Back to Events
                </Button>

                <Paper className="event-detail-paper">
                    {/* Gradient bar */}
                    <div className="detail-gradient-bar"></div>

                    {/* Event Header */}
                    <div className="event-header">
                        <Typography variant="h4" className="event-title">
                            Event
                        </Typography>
                        <Typography variant="h3" className="event-title">
                            {event.name}
                        </Typography>
                    </div>

                    {/* Event Dates */}
                    <div className="info-section">
                        <Typography variant="h6" className="section-title">
                            <CalendarIcon className="section-icon" /> Event Schedule
                        </Typography>
                        <Box className="dates-container">
                            <div className="date-item">
                                <Typography className="date-label">Start Date:</Typography>
                                <Typography className="date-value">
                                    {format(startDate, 'yyyy MMMM d (EEEE)')}
                                </Typography>
                                <Typography className="time-value">
                                    at {format(startDate, 'hh:mm')}
                                </Typography>
                            </div>
                            {endDate && (
                                <div className="date-item">
                                    <Typography className="date-label">End Date:</Typography>
                                    <Typography className="date-value">
                                        {format(endDate,  'yyyy MMMM d (EEEE)')}
                                    </Typography>
                                    <Typography className="time-value">
                                        at {format(endDate, 'hh:mm')}
                                    </Typography>
                                </div>
                            )}
                        </Box>
                    </div>

                    <Divider className="divider" />

                    {/* Venue Information */}
                    <div className="info-section">
                        <Typography variant="h6" className="section-title">
                            <LocationIcon className="section-icon" /> Venue
                        </Typography>
                        <div className="venue-details">
                            <Typography className="venue-name">
                                {event.venue.name}
                            </Typography>
                            <Typography className="venue-address">
                                {event.venue.address}
                            </Typography>
                            <Typography className="venue-location">
                                {event.venue.city}, {event.venue.country}
                            </Typography>
                        </div>
                    </div>

                    <Divider className="divider" />

                    {/* Description */}
                    {event.eventDescription && (
                        <>
                            <div className="info-section">
                                <Typography variant="h6" className="section-title">
                                    About This Event
                                </Typography>
                                <Typography className="event-description-text">
                                    {event.eventDescription}
                                </Typography>
                            </div>
                            <Divider className="divider" />
                        </>
                    )}

                    {/* Ticket Tiers */}
                    <div className="info-section">
                        <Typography variant="h6" className="section-title">
                            <TicketIcon className="section-icon" /> Ticket Tiers
                        </Typography>
                        <div className="ticket-tiers-grid">
                            {event.ticketTiers && event.ticketTiers.length > 0 ? (
                                event.ticketTiers.map((tier) => (
                                    <Card key={tier.id} className="tier-card">
                                        <CardContent>
                                            <Typography variant="h6" className="tier-name">
                                                {tier.tierDescription}
                                            </Typography>
                                            <Typography className="tier-price">
                                                ${tier.ticketPrice.toFixed(2)}
                                            </Typography>
                                            <Typography className="tier-availability">
                                                {tier.total} tickets available
                                            </Typography>
                                            <Button
                                                variant="contained"
                                                className="buy-button"
                                                onClick={() => handleBuyTicket(tier.id, tier.tierDescription, tier.ticketPrice)}
                                                fullWidth
                                            >
                                                Buy Tickets
                                            </Button>
                                        </CardContent>
                                    </Card>
                                ))
                            ) : (
                                <Typography className="no-tiers">
                                    No ticket tiers available for this event.
                                </Typography>
                            )}
                        </div>
                    </div>
                </Paper>
            </Container>
        </div>
    );
};

export default EventDetail;