import { Link, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export function Layout() {
  const { user, logout, isAuthenticated } = useAuth();

  return (
    <div className="app">
      <header className="header">
        <Link to="/" className="brand">
          {'template'}
        </Link>
        <nav className="nav">
          {isAuthenticated ? (
            <>
              <Link to="/dashboard">Dashboard</Link>
              <Link to="/profile">Profile</Link>
              <button type="button" onClick={() => void logout()}>
                Logout
              </button>
              <span className="user-email">{user?.email}</span>
            </>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">Register</Link>
            </>
          )}
        </nav>
      </header>
      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}
