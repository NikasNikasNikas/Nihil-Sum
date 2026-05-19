import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import TicketDownloader from './pages/TicketDownloader';
import EventPage from './pages/Event/EventPage.tsx'
import EventDetail from './pages/Event/EventDetail.tsx';

const client = new QueryClient();

function App() {
  return (
      <QueryClientProvider client={client}>
        <BrowserRouter>
          <Routes>
            <Route path='/' element={<EventPage />} />
            <Route path='/tickets' element={<TicketDownloader />} />
            <Route path='/event/:id' element={<EventDetail />} />
          </Routes>
        </BrowserRouter>
      </QueryClientProvider>
  );
}

export default App;