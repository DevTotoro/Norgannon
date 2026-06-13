<script lang="ts">
  import type { PageData } from './$types';

  let { data }: { data: PageData } = $props();
</script>

<section class="container w-full max-w-2xl py-10 flex flex-col gap-6">
  <h2 class="text-2xl font-bold tracking-tight">Your account</h2>

  <dl class="grid grid-cols-[auto_1fr] gap-x-6 gap-y-2 text-sm">
    <dt class="font-semibold uppercase tracking-widest text-muted-foreground">ID</dt>
    <dd class="font-mono">{data.me.id}</dd>

    <dt class="font-semibold uppercase tracking-widest text-muted-foreground">Email</dt>
    <dd>{data.me.email}</dd>
  </dl>

  <p class="text-sm text-muted-foreground">
    This data was fetched server-side from the Kotlin API's <code>/api/me</code> endpoint using your Keycloak access token.
  </p>
</section>
