<script lang="ts">
  import '../app.css';

  import Header from '$lib/components/header.svelte';

  let { children } = $props();
</script>

<div class="min-h-svh">
  <div class="w-full px-4 flex flex-col items-center">
    <Header />
    {@render children()}
  </div>
</div>
