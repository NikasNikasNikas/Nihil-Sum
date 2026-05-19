import { useEffect, useRef, useState } from 'react';
import './TicketChecker.css';
import QrScanner from 'qr-scanner';
import { fetchTicketInfoWithUUID } from '../../services/digitalTicketService';
import { TicketInfo } from '../../types/digitalTicket';

const TicketChecker: React.FC = () => {
    const videoRef = useRef<HTMLVideoElement | null>(null);
    const scannerRef = useRef<QrScanner | null>(null);

    const isProcessingRef = useRef(false);
    const scanCooldownRef = useRef(false);

    const [ticketInfo, setTicketInfo] = useState<TicketInfo | null>(null);
    const [showModal, setShowModal] = useState<boolean>(false);

    const checkTicket = async (uuid: string): Promise<void> => {
        try {
            const data: TicketInfo = await fetchTicketInfoWithUUID(uuid);

            console.log("ticket data:", data);

            setTicketInfo(data);
            setShowModal(true);

            scannerRef.current?.stop();
        } catch (e) {
            console.log(e);
        }
    };

    const closeModal = async (): Promise<void> => {
        setShowModal(false);
        setTicketInfo(null);

        isProcessingRef.current = false;
        scanCooldownRef.current = true;
        await scannerRef.current?.start();

        setTimeout(() => {
            scanCooldownRef.current = false;
        }, 500);
    };

    const renderTicketModal = (): JSX.Element | null => {
        if (!showModal || !ticketInfo) return null;

        return (
            <div className="modalOverlay">
                <div className="ticketModal">

                    <h2>{ticketInfo.eventName}</h2>

                    <p>
                        <strong>Description:</strong> {ticketInfo.eventDescription}
                    </p>

                    <p>
                        <strong>Last Scan:</strong> {ticketInfo.lastScannedDate}
                    </p>

                    <p>
                        <strong>Venue:</strong> {ticketInfo.venueName}
                    </p>

                    <p>
                        <strong>Address:</strong> {ticketInfo.venueAddress}
                    </p>

                    <p>
                        <strong>Tier:</strong> {ticketInfo.tierDescription}
                    </p>

                    <p>
                        <strong>Price:</strong> ${ticketInfo.ticketPrice}
                    </p>

                    <button onClick={closeModal}>
                        Close
                    </button>

                </div>
            </div>
        );
    };

    useEffect(() => {
        if (!videoRef.current) return;

        scannerRef.current = new QrScanner(
            videoRef.current,
            (result: { data: string }) => {
                if (isProcessingRef.current) return;
                if (scanCooldownRef.current) return;

                isProcessingRef.current = true;
                checkTicket(result.data);
            },
            {
                highlightScanRegion: true,
                highlightCodeOutline: true,
            }
        );

        scannerRef.current.start();

        return () => {
            scannerRef.current?.stop();
            scannerRef.current?.destroy();
        };
    }, []);

    return (
        <div id="mainChecker">

            <video
                ref={videoRef}
                className="cameraVideo"
            />

            {renderTicketModal()}

        </div>
    );
};

export default TicketChecker;