// @ts-ignore
import React from 'react';
import { Card, CardContent, Typography, Chip } from '@mui/material';
import { Event as EventIcon, CalendarMonth as CalendarIcon } from '@mui/icons-material';
import { format } from 'date-fns';
import { Event } from './event';
import './EventCard.css';

interface EventCardProps {
    event: Event;
}

const EventCard: React.FC<EventCardProps> = ({ event }) => {
    const startDate = new Date(event.startDate);

    const handleCardClick = () => {
        window.location.href = `/event/${event.id}`;
    };

    return (
        <Card className="event-card upcoming" onClick={handleCardClick} style={{ cursor: 'pointer' }}>
            <CardContent>
                <div className="card-header">
                    <div className="event-icon-wrapper">
                        <EventIcon className="event-icon" />
                    </div>
                    <div className="event-info">
                        <Typography variant="h5" className="event-name">
                            {event.name}
                        </Typography>

                        <Chip
                            icon={<CalendarIcon />}
                            label={format(startDate, 'yyyy-MM-dd')}
                            size="small"
                            className="date-chip"
                        />
                        {event.eventDescription && (
                            <Typography className="event-description">
                                {event.eventDescription}
                            </Typography>
                        )}
                    </div>
                </div>
            </CardContent>
        </Card>
    );
};

export default EventCard;