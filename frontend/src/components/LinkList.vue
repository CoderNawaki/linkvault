<script setup>
import { computed } from "vue";

const props = defineProps({
  links: {
    type: Array,
    required: true,
  },
  totalLinks: {
    type: Number,
    required: true,
  },
  activeTag: {
    type: String,
    default: "",
  },
  isLoading: {
    type: Boolean,
    default: false,
  },
  errorMessage: {
    type: String,
    default: "",
  },
  deletingId: {
    type: Number,
    default: null,
  },
  togglingId: {
    type: Number,
    default: null,
  },
  searchQuery: {
    type: String,
    default: "",
  },
  showFavouritesOnly: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["clear-filters", "delete-link", "filter-tag", "toggle-favourite"]);

const summaryText = computed(() => {
  if (props.isLoading) {
    return "Loading saved links.";
  }

  if (props.errorMessage) {
    return "Saved links could not be loaded.";
  }

  const count = props.links.length;
  return `${count} saved ${count === 1 ? "link" : "links"}`;
});

const hasActiveFilters = computed(() => {
  return Boolean(props.searchQuery || props.activeTag || props.showFavouritesOnly);
});

const emptyTitle = computed(() => {
  if (props.totalLinks === 0) {
    return "Your vault is ready for its first link.";
  }

  return "No links match the current view.";
});

const emptyMessage = computed(() => {
  if (props.totalLinks === 0) {
    return "Save a URL with a short description and tags, then it will appear here for searching and filtering.";
  }

  const filters = [];

  if (props.searchQuery) {
    filters.push(`search "${props.searchQuery}"`);
  }

  if (props.activeTag) {
    filters.push(`tag "${props.activeTag}"`);
  }

  if (props.showFavouritesOnly) {
    filters.push("starred links only");
  }

  return `Nothing matched ${filters.join(", ")}. Clear the filters to see all saved links.`;
});

function getTags(tagsString) {
  if (!tagsString) return [];
  return tagsString
    .split(",")
    .map((tag) => tag.trim())
    .filter(Boolean);
}

function handleDelete(id) {
  emit("delete-link", id);
}

function handleTagClick(tag) {
  emit("filter-tag", tag);
}

function handleToggleFavourite(id) {
  emit("toggle-favourite", id);
}
</script>

<template>
  <section class="saved-links" aria-labelledby="saved-links-title">
    <div class="saved-links-header">
      <div>
        <p class="eyebrow">Saved links</p>
        <h2 id="saved-links-title">Your vault</h2>
      </div>
      <p class="list-count">{{ summaryText }}</p>
    </div>

    <ul class="links-list" aria-live="polite">
      <li v-if="isLoading" class="empty-state">Loading links...</li>
      <li v-else-if="errorMessage" class="empty-state error-state">{{ errorMessage }}</li>
      <li v-else-if="links.length === 0" class="empty-state">
        <div class="empty-state-mark" aria-hidden="true">+</div>
        <h3>{{ emptyTitle }}</h3>
        <p>{{ emptyMessage }}</p>
        <button
          v-if="totalLinks > 0 && hasActiveFilters"
          type="button"
          class="secondary-button"
          @click="$emit('clear-filters')"
        >
          Clear filters
        </button>
      </li>
      <template v-else>
        <li v-for="link in links" :key="link.id" class="link-item">
          <div>
            <div class="link-header">
              <a class="link-title" :href="link.url" target="_blank" rel="noopener noreferrer">
                {{ link.title }}
              </a>
              <button
                class="favourite-button"
                type="button"
                :class="{ 'is-favourite': link.favourite }"
                :disabled="togglingId === link.id"
                title="Toggle favourite"
                @click="handleToggleFavourite(link.id)"
              >
                <svg
                  v-if="link.favourite"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  fill="currentColor"
                  width="20"
                  height="20"
                >
                  <path
                    fill-rule="evenodd"
                    d="M10.788 3.21c.448-1.077 1.976-1.077 2.424 0l2.082 5.007 5.404.433c1.164.093 1.636 1.545.749 2.305l-4.117 3.527 1.257 5.273c.271 1.136-.964 2.033-1.96 1.425L12 18.354 7.373 21.18c-.996.608-2.231-.29-1.96-1.425l1.257-5.273-4.117-3.527c-.887-.76-.415-2.212.749-2.305l5.404-.433 2.082-5.006z"
                    clip-rule="evenodd"
                  />
                </svg>
                <svg
                  v-else
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  width="20"
                  height="20"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.562 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.562 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.562 0 01.321-.988l5.518-.442a.563.562 0 00.475-.345L11.48 3.5z"
                  />
                </svg>
              </button>
            </div>
            <p class="link-description">{{ link.description || "No description added." }}</p>
            <p v-if="link.personalNote" class="link-note">{{ link.personalNote }}</p>
          </div>

          <div class="link-meta">
            <span class="link-url">{{ link.url }}</span>
            <div class="tags-container">
              <button
                v-for="tag in getTags(link.tags)"
                :key="tag"
                class="tag clickable-tag"
                type="button"
                title="Filter by this tag"
                @click="handleTagClick(tag)"
              >
                {{ tag }}
              </button>
            </div>
            <button
              class="delete-button"
              type="button"
              :disabled="deletingId === link.id"
              @click="handleDelete(link.id)"
            >
              {{ deletingId === link.id ? "Deleting..." : "Delete" }}
            </button>
          </div>
        </li>
      </template>
    </ul>
  </section>
</template>

<style scoped>
.clickable-tag {
  cursor: pointer;
  transition: opacity 0.2s;
  border: none;
  font: inherit;
}

.clickable-tag:hover {
  opacity: 0.8;
}

.link-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.favourite-button {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  color: var(--muted);
  min-height: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.15s ease-in-out;
}

.favourite-button:hover {
  transform: scale(1.1);
  color: #eaaa08;
}

.favourite-button.is-favourite {
  color: #eaaa08;
}

.favourite-button:disabled {
  opacity: 0.5;
  cursor: wait;
}
</style>
