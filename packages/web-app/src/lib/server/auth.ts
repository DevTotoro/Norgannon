import { SvelteKitAuth, type SvelteKitAuthConfig } from '@auth/sveltekit';
import Keycloak from '@auth/sveltekit/providers/keycloak';
import { env } from '$env/dynamic/private';

function required(name: string): string {
  const value = env[name];
  if (!value) {
    throw new Error(`Missing required environment variable: ${name}`);
  }
  return value;
}

function keycloakIssuer(): string {
  return `http://localhost:${required('KEYCLOAK_HTTP_PORT')}/realms/${required('KEYCLOAK_REALM_NAME')}`;
}

interface RefreshedTokens {
  access_token: string;
  expires_in: number;
  refresh_token?: string;
  id_token?: string;
}

async function refreshAccessToken(issuer: string, refreshToken: string): Promise<RefreshedTokens> {
  const response = await fetch(`${issuer}/protocol/openid-connect/token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({
      grant_type: 'refresh_token',
      client_id: required('KEYCLOAK_WEB_CLIENT_ID'),
      client_secret: required('KEYCLOAK_WEB_CLIENT_SECRET'),
      refresh_token: refreshToken
    })
  });

  const tokens: unknown = await response.json();
  if (!response.ok) {
    throw tokens;
  }
  return tokens as RefreshedTokens;
}

// Lazy initialization: config (and env reads) run per-request, never at build time.
export const { handle, signIn, signOut } = SvelteKitAuth(() => {
  const issuer = keycloakIssuer();

  const config = {
    secret: env.AUTH_SECRET,
    trustHost: true,
    providers: [
      Keycloak({
        clientId: required('KEYCLOAK_WEB_CLIENT_ID'),
        clientSecret: required('KEYCLOAK_WEB_CLIENT_SECRET'),
        issuer
      })
    ],
    callbacks: {
      async jwt({ token, account, profile }) {
        // Initial sign-in: persist the Keycloak tokens and identity onto the JWT.
        if (account) {
          token.accessToken = account.access_token;
          token.refreshToken = account.refresh_token;
          token.idToken = account.id_token;
          token.expiresAt = account.expires_at;
          if (profile) {
            token.name = profile.name ?? profile.preferred_username ?? null;
            token.email = profile.email ?? null;
          }
          return token;
        }

        // Access token still fresh (30s safety margin) — reuse it.
        const expiresAtMs = (token.expiresAt ?? 0) * 1000;
        if (Date.now() < expiresAtMs - 30_000) {
          return token;
        }

        // Expired: rotate using the refresh token.
        if (!token.refreshToken) {
          return { ...token, error: 'RefreshAccessTokenError' };
        }
        try {
          const refreshed = await refreshAccessToken(issuer, token.refreshToken);
          return {
            ...token,
            accessToken: refreshed.access_token,
            expiresAt: Math.floor(Date.now() / 1000) + refreshed.expires_in,
            refreshToken: refreshed.refresh_token ?? token.refreshToken,
            idToken: refreshed.id_token ?? token.idToken,
            error: undefined
          };
        } catch (error) {
          console.error('Failed to refresh Keycloak access token', error);
          // Surface the failure so the UI can force a re-login.
          return { ...token, error: 'RefreshAccessTokenError' };
        }
      },
      session({ session, token }) {
        session.accessToken = token.accessToken;
        session.error = token.error;
        session.user.name = token.name ?? session.user.name;
        session.user.email = token.email ?? session.user.email;
        return session;
      }
    },
    events: {
      // RP-initiated logout: end the Keycloak SSO session too, not just the local cookie.
      async signOut(message) {
        const token = 'token' in message ? message.token : null;
        const idToken = token?.idToken;
        if (!idToken) {
          return;
        }
        const params = new URLSearchParams({ id_token_hint: idToken });
        try {
          await fetch(`${issuer}/protocol/openid-connect/logout?${params.toString()}`);
        } catch (error) {
          console.error('Failed to end Keycloak session on sign-out', error);
        }
      }
    }
  } satisfies SvelteKitAuthConfig;

  return Promise.resolve(config);
});
