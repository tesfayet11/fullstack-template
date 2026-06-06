'use client';

import Link from 'next/link';
import { useAuth } from '@/lib/auth/AuthContext';

const projectName = process.env.NEXT_PUBLIC_PROJECT_NAME ?? 'App';

export function Header() {
  const { user, logout, isAuthenticated } = useAuth();

  return (
    <header className="header">
      <Link href="/" className="brand">
        {projectName}
      </Link>
      <nav className="nav">
        {isAuthenticated ? (
          <>
            <Link href="/dashboard">Dashboard</Link>
            <Link href="/profile">Profile</Link>
            <button type="button" onClick={() => void logout()}>
              Logout
            </button>
            <span className="user-email">{user?.email}</span>
          </>
        ) : (
          <>
            <Link href="/login">Login</Link>
            <Link href="/register">Register</Link>
          </>
        )}
      </nav>
    </header>
  );
}
