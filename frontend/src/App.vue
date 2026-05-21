<script setup>
import { computed, onMounted, ref } from "vue";
import LinkForm from "./components/LinkForm.vue";
import LinkList from "./components/LinkList.vue";
import TagFilter from "./components/TagFilter.vue";
import { createLink, deleteLink, getLinks } from "./services/api";

const links = ref([]);
const activeTag = ref("");
const searchQuery = ref("");
const statusMessage = ref("");
const loadError = ref("");
const isLoading = ref(false);
const deletingId = ref(null);

const availableTags = computed(() => {
  const allTags = links.value.flatMap((link) => {
    if (!link.tags) return [];
    return link.tags.split(",").map((tag) => tag.trim()).filter(Boolean);
  });

  return [...new Set(allTags)].sort((a, b) => a.localeCompare(b));
});

const filteredLinks = computed(() => {
  let result = links.value;

  if (activeTag.value) {
    result = result.filter((link) => {
      if (!link.tags) return false;
      const tags = link.tags.split(",").map((tag) => tag.trim()).filter(Boolean);
      return tags.includes(activeTag.value);
    });
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter((link) => {
      return (
        link.title?.toLowerCase().includes(query) ||
        link.description?.toLowerCase().includes(query) ||
        link.url?.toLowerCase().includes(query)
      );
    });
  }

  return result;
});

async function loadLinks({ clearStatus = true } = {}) {
  isLoading.value = true;
  loadError.value = "";

  if (clearStatus) {
    statusMessage.value = "";
  }

  try {
    links.value = await getLinks();
  } catch (error) {
    loadError.value = error.message;
  } finally {
    isLoading.value = false;
  }
}

async function handleCreateLink(payload) {
  statusMessage.value = "Saving link...";

  try {
    await createLink(payload);
    statusMessage.value = "Link saved.";
    await loadLinks({ clearStatus: false });
  } catch (error) {
    statusMessage.value = error.message;
  }
}

async function handleDeleteLink(id) {
  if (!window.confirm("Are you sure you want to delete this link?")) {
    return;
  }

  deletingId.value = id;
  statusMessage.value = "Deleting link...";

  try {
    await deleteLink(id);
    statusMessage.value = "Link deleted.";
    await loadLinks({ clearStatus: false });
  } catch (error) {
    statusMessage.value = error.message;
  } finally {
    deletingId.value = null;
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
        <div class="search-box">
          <input
            v-model="searchQuery"
            type="search"
            placeholder="Search links..."
            aria-label="Search links"
          />
        </div>
        <button class="secondary-button" type="button" :disabled="isLoading" @click="loadLinks">
          Refresh
        </button>
      </div>

      <p class="status" role="status">{{ statusMessage }}</p>
      <LinkList
        :links="filteredLinks"
        :deleting-id="deletingId"
        :is-loading="isLoading"
        :error-message="loadError"
        @delete-link="handleDeleteLink"
        @filter-tag="(tag) => (activeTag = tag)"
      />
    </section>
  </main>
</template>
