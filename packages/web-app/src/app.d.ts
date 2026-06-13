// See https://svelte.dev/docs/kit/types#app.d.ts
// for information about these interfaces

export interface SessionUser {
  name: string | null;
  email: string | null;
}

declare global {
  namespace App {
    // interface Error {}
    interface Locals {
      user: SessionUser | null;
      accessToken: string | null;
      authError: string | null;
    }
    interface PageData {
      user: SessionUser | null;
    }
    // interface PageState {}
    // interface Platform {}
  }
}

// Extend the Auth.js session and JWT with the Keycloak tokens we carry through
// the BFF. The session is what `locals.auth()` resolves to; the JWT is the
// encrypted server-side token where we persist the access/refresh/id tokens.
declare module '@auth/core/types' {
  interface Session {
    accessToken?: string;
    error?: string;
  }
}

declare module '@auth/core/jwt' {
  interface JWT {
    accessToken?: string;
    refreshToken?: string;
    idToken?: string;
    expiresAt?: number;
    error?: string;
  }
}

export {};
