<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from "vue";
import LinkForm from "./components/LinkForm.vue";
import LinkList from "./components/LinkList.vue";
import TagFilter from "./components/TagFilter.vue";
import { createLink, deleteLink, getLinks, toggleFavourite } from "./services/api";

const links = ref([]);
const activeTag = ref(localStorage.getItem("activeTag") || "");
const searchQuery = ref(localStorage.getItem("searchQuery") || "");
const showFavouritesOnly = ref(localStorage.getItem("showFavouritesOnly") === "true");
const sortBy = ref(localStorage.getItem("sortBy") || "newest");
const statusMessage = ref("");
const loadError = ref("");
const isLoading = ref(false);
const deletingId = ref(null);
const togglingId = ref(null);
const searchInput = ref(null);

watch(activeTag, (newTag) => {
  localStorage.setItem("activeTag", newTag);
});

watch(searchQuery, (newQuery) => {
  localStorage.setItem("searchQuery", newQuery);
});

watch(showFavouritesOnly, (newVal) => {
  localStorage.setItem("showFavouritesOnly", newVal);
});

watch(sortBy, (newVal) => {
  localStorage.setItem("sortBy", newVal);
});

const availableTags = computed(() => {
  const allTags = links.value.flatMap((link) => {
    if (!link.tags) return [];
    return link.tags.split(",").map((tag) => tag.trim()).filter(Boolean);
  });

  return [...new Set(allTags)].sort((a, b) => a.localeCompare(b));
});

const filteredLinks = computed(() => {
  let result = [...links.value];

  if (showFavouritesOnly.value) {
    result = result.filter((link) => link.favourite);
  }

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
        link.personalNote?.toLowerCase().includes(query) ||
        link.url?.toLowerCase().includes(query)
      );
    });
  }

  // Sorting
  result.sort((a, b) => {
    if (sortBy.value === "newest") {
      return new Date(b.createdAt) - new Date(a.createdAt);
    } else if (sortBy.value === "oldest") {
      return new Date(a.createdAt) - new Date(b.createdAt);
    } else if (sortBy.value === "title") {
      return (a.title || "").localeCompare(b.title || "");
    }
    return 0;
  });

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

async function handleToggleFavourite(id) {
  togglingId.value = id;
  try {
    const updatedLink = await toggleFavourite(id);
    const index = links.value.findIndex((link) => link.id === id);
    if (index !== -1) {
      links.value[index] = updatedLink;
    }
  } catch (error) {
    statusMessage.value = error.message;
  } finally {
    togglingId.value = null;
  }
}

function handleExportJSON() {
  const jsonString = JSON.stringify(links.value, null, 2);
  const blob = new Blob([jsonString], { type: "application/json" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  const date = new Date().toISOString().split("T")[0];

  link.href = url;
  link.download = `linkvault-export-${date}.json`;
  link.click();
  URL.revokeObjectURL(url);
}

function handleKeydown(event) {
  if (
    event.key === "/" &&
    document.activeElement.tagName !== "INPUT" &&
    document.activeElement.tagName !== "TEXTAREA"
  ) {
    event.preventDefault();
    searchInput.value?.focus();
  }
}

onMounted(() => {
  loadLinks();
  window.addEventListener("keydown", handleKeydown);
});

onUnmounted(() => {
  window.removeEventListener("keydown", handleKeydown);
});
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
            ref="searchInput"
            v-model="searchQuery"
            type="search"
            placeholder="Search links... (Press / to focus)"
            aria-label="Search links"
          />
          <button
            v-if="searchQuery"
            type="button"
            class="clear-search"
            title="Clear search"
            @click="searchQuery = ''"
          >
            &times;
          </button>
        </div>
        <div class="sort-tool">
          <select v-model="sortBy" aria-label="Sort links">
            <option value="newest">Newest first</option>
            <option value="oldest">Oldest first</option>
            <option value="title">Title (A-Z)</option>
          </select>
        </div>
        <button
          class="secondary-button favourite-filter"
          :class="{ active: showFavouritesOnly }"
          type="button"
          @click="showFavouritesOnly = !showFavouritesOnly"
        >
          {{ showFavouritesOnly ? "Starred only" : "All links" }}
        </button>
        <div class="actions-group">
          <button class="secondary-button" type="button" title="Download backup" @click="handleExportJSON">
            Export
          </button>
          <button class="secondary-button" type="button" :disabled="isLoading" @click="loadLinks">
            Refresh
          </button>
        </div>
      </div>

      <p class="status" role="status">{{ statusMessage }}</p>
      <LinkList
        :links="filteredLinks"
        :search-query="searchQuery"
        :deleting-id="deletingId"
        :toggling-id="togglingId"
        :is-loading="isLoading"
        :error-message="loadError"
        @delete-link="handleDeleteLink"
        @toggle-favourite="handleToggleFavourite"
        @filter-tag="(tag) => (activeTag = tag)"
      />
    </section>
  </main>
</template>

<style scoped>
.favourite-filter.active {
  background: #fef9c3;
  border-color: #eaaa08;
  color: #854d0e;
}

.sort-tool select {
  width: auto;
  min-width: 140px;
}

.actions-group {
  display: flex;
  gap: 8px;
}
</style>
