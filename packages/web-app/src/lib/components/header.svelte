<script lang="ts">
  import { Button } from '$lib/components/ui/button';
  import { signIn, signOut } from '@auth/sveltekit/client';
  import { page } from '$app/state';

  const user = $derived(page.data.user);
</script>

<header class="container sticky top-0 h-16 w-full flex justify-between items-center">
  <h1 class="text-3xl font-bold leading-tight tracking-tighter"><a href="/">The Video Club</a></h1>

  {#if user}
    <div class="flex items-center gap-4">
      <span class="text-sm">Welcome, {user.name ?? user.email}</span>
      <Button variant="outline" onclick={() => signOut({ redirectTo: '/' })}>Sign out</Button>
    </div>
  {:else}
    <Button onclick={() => signIn('keycloak', { redirectTo: '/account' })}>Sign in</Button>
  {/if}
</header>
