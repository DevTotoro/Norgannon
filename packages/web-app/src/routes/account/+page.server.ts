import { error, redirect } from '@sveltejs/kit';
import { apiFetch, type MeResponse } from '$lib/server/api';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ locals, fetch }) => {
  if (!locals.user) {
    redirect(307, '/auth/signin?callbackUrl=/account');
  }

  const response = await apiFetch(fetch, '/api/me', locals.accessToken);
  if (!response.ok) {
    error(response.status, 'Could not load your profile from the API.');
  }

  const me = (await response.json()) as MeResponse;
  return { me };
};
