import { Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export function HomePage() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="card">
      <h1>Welcome to {'template'}</h1>
      <p>
        Full-stack template with React, Spring Boot, PostgreSQL, MinIO, and JWT auth.
      </p>
      {isAuthenticated ? (
        <Link to="/dashboard" className="button-link">
          Go to Dashboard
        </Link>
      ) : (
        <div className="actions">
          <Link to="/login" className="button-link">
            Login
          </Link>
          <Link to="/register" className="button-link secondary">
            Register
          </Link>
        </div>
      )}
    </div>
  );
}
