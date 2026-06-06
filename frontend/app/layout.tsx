import type { Metadata } from 'next';
import { Header } from '@/components/Header';
import { Providers } from './providers';
import './globals.css';

export const metadata: Metadata = {
  title: 'Full-Stack Template',
  description: 'React + Spring Boot full-stack template',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>
        <Providers>
          <div className="app">
            <Header />
            <main className="main">{children}</main>
          </div>
        </Providers>
      </body>
    </html>
  );
}
