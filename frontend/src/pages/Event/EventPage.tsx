// @ts-ignore
import React, { useState, useEffect } from 'react';
import { Container, Typography, CircularProgress, Alert, Box } from '@mui/material';
import { Event as EventIcon } from '@mui/icons-material';
import EventCard from './EventCard';
import { Event } from './event';
import './EventPage.css';

const EventPage: React.FC = () => {
    const [events, setEvents] = useState<Event[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchEvents = async () => {
            try {
                setLoading(true);
                const response = await fetch('/api/eventActive');
                if (!response.ok) throw new Error('Failed to fetch events');
                const data = await response.json();

                const sorted = [...data].sort(
                    (a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime()
                );

                setEvents(sorted);
                setError(null);
            } catch (err) {
                setError('Unable to load events.');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchEvents();
    }, []);

    if (loading) {
        return (
            <Container className="loading-container">
                <CircularProgress />
                <Typography>Loading events...</Typography>
            </Container>
        );
    }

    if (error) {
        return (
            <Container className="error-container">
                <Alert severity="error">{error}</Alert>
            </Container>
        );
    }

    return (
        <div className="page-wrapper">
            <Container className="app-container">
                <div className="header">
                    <EventIcon className="header-icon" />
                    <Typography variant="h4" className="header-title">
                        Events
                    </Typography>
                    <Typography className="header-subtitle">
                        {events.length} event{events.length !== 1 ? 's' : ''}
                    </Typography>
                    <Typography className="header-text">
                        From soonest to furthest
                    </Typography>
                </div>

                <Box className="events-list">
                    {events.length === 0 ? (
                        <Typography className="no-events">No events found</Typography>
                    ) : (
                        events.map((event) => (
                            <EventCard key={event.id} event={event} />
                        ))
                    )}
                </Box>
            </Container>
        </div>
    );
};

export default EventPage;