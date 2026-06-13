import { sequence } from '@sveltejs/kit/hooks';
import type { Handle } from '@sveltejs/kit';
import { handle as authHandle } from '$lib/server/auth';

const populateLocals: Handle = async ({ event, resolve }) => {
  const session = await event.locals.auth();

  event.locals.user = session?.user ? { name: session.user.name ?? null, email: session.user.email ?? null } : null;
  event.locals.accessToken = session?.accessToken ?? null;
  event.locals.authError = session?.error ?? null;

  return resolve(event);
};

export const handle = sequence(authHandle, populateLocals);
