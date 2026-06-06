'use client';

import { ProtectedRoute } from '@/components/ProtectedRoute';
import { useAuth } from '@/lib/auth/AuthContext';

function ProfileContent() {
  const { user } = useAuth();

  return (
    <div className="card">
      <h1>Profile</h1>
      {user && (
        <dl className="profile">
          <dt>Email</dt>
          <dd>{user.email}</dd>
          <dt>User ID</dt>
          <dd>{user.id}</dd>
          <dt>Member since</dt>
          <dd>{new Date(user.createdAt).toLocaleString()}</dd>
        </dl>
      )}
    </div>
  );
}

export default function ProfilePage() {
  return (
    <ProtectedRoute>
      <ProfileContent />
    </ProtectedRoute>
  );
}
