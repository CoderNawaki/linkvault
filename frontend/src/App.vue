<script setup>
import { computed, onMounted, ref } from "vue";
import LinkForm from "./components/LinkForm.vue";
import LinkList from "./components/LinkList.vue";
import TagFilter from "./components/TagFilter.vue";
import { createLink, deleteLink, getLinks } from "./services/api";

const links = ref([]);
const activeTag = ref("");
const statusMessage = ref("");
const isLoading = ref(false);

const availableTags = computed(() => {
  const tags = links.value
    .map((link) => link.tag)
    .filter(Boolean);

  return [...new Set(tags)].sort((first, second) => first.localeCompare(second));
});

const filteredLinks = computed(() => {
  if (!activeTag.value) {
    return links.value;
  }

  return links.value.filter((link) => link.tag === activeTag.value);
});

async function loadLinks() {
  isLoading.value = true;
  statusMessage.value = "";

  try {
    links.value = await getLinks();
  } catch (error) {
    statusMessage.value = error.message;
  } finally {
    isLoading.value = false;
  }
}

async function handleCreateLink(payload) {
  statusMessage.value = "Saving link...";

  try {
    await createLink(payload);
    statusMessage.value = "Link saved.";
    await loadLinks();
  } catch (error) {
    statusMessage.value = error.message;
  }
}

async function handleDeleteLink(id) {
  statusMessage.value = "Deleting link...";

  try {
    await deleteLink(id);
    statusMessage.value = "Link deleted.";
    await loadLinks();
  } catch (error) {
    statusMessage.value = error.message;
  }
}

onMounted(loadLinks);
</script>

<template>
  <main class="app-shell">
    <section class="hero" aria-labelledby="page-title">
      <p class="eyebrow">Personal URL manager</p>
      <h1 id="page-title">LinkVault</h1>
      <p class="lede">Save useful links, tag them, and keep your references easy to find.</p>
    </section>

    <section class="workspace" aria-label="Link manager">
      <LinkForm :disabled="isLoading" @create-link="handleCreateLink" />

      <div class="list-tools">
        <TagFilter v-model="activeTag" :tags="availableTags" />
        <button class="secondary-button" type="button" :disabled="isLoading" @click="loadLinks">
          Refresh
        </button>
      </div>

      <p class="status" role="status">{{ statusMessage }}</p>
      <LinkList :links="filteredLinks" :is-loading="isLoading" @delete-link="handleDeleteLink" />
    </section>
  </main>
</template>
