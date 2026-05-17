import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import TicketDownloader from './pages/TicketDownloader';

const client = new QueryClient();

function App() {
  return (
      <QueryClientProvider client={client}>
        <BrowserRouter>
          <Routes>
            {/* The '/' path is a placeholder */}
            <Route path='/' element={<TicketDownloader />} />
            <Route path='/tickets' element={<TicketDownloader />} />
          </Routes>
        </BrowserRouter>
      </QueryClientProvider>
  );
}

export default App;