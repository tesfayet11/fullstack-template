'use client';

import Link from 'next/link';
import { useAuth } from '@/lib/auth/AuthContext';

const projectName = process.env.NEXT_PUBLIC_PROJECT_NAME ?? 'App';

export default function HomePage() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="card">
      <h1>Welcome to {projectName}</h1>
      <p>Full-stack template with Next.js, Spring Boot, PostgreSQL, MinIO, and JWT auth.</p>
      {isAuthenticated ? (
        <Link href="/dashboard" className="button-link">
          Go to Dashboard
        </Link>
      ) : (
        <div className="actions">
          <Link href="/login" className="button-link">
            Login
          </Link>
          <Link href="/register" className="button-link secondary">
            Register
          </Link>
        </div>
      )}
    </div>
  );
}
