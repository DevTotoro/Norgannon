import { env } from '$env/dynamic/private';

const API_BASE_URL = env.API_BASE_URL ?? 'http://localhost:8080';

export interface MeResponse {
  id: string;
  email: string;
}

/**
 * Server-side fetch against the Kotlin API. Attaches the Keycloak access token
 * as a Bearer credential so the request passes the Spring resource server.
 *
 * Pass the `fetch` provided by the SvelteKit load/handler so requests are
 * traced and benefit from SvelteKit's fetch handling.
 */
export function apiFetch(
  fetchFn: typeof fetch,
  path: string,
  accessToken: string | null,
  init: RequestInit = {}
): Promise<Response> {
  const headers = new Headers(init.headers);
  if (accessToken) {
    headers.set('Authorization', `Bearer ${accessToken}`);
  }
  return fetchFn(`${API_BASE_URL}${path}`, { ...init, headers });
}
