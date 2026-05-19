<script setup>
import { computed } from "vue";

const props = defineProps({
  links: {
    type: Array,
    required: true,
  },
  isLoading: {
    type: Boolean,
    default: false,
  },
  errorMessage: {
    type: String,
    default: "",
  },
});

defineEmits(["delete-link"]);

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
      <li v-else-if="links.length === 0" class="empty-state">No links saved yet.</li>
      <template v-else>
        <li v-for="link in links" :key="link.id" class="link-item">
          <div>
            <a class="link-title" :href="link.url" target="_blank" rel="noopener noreferrer">
              {{ link.title }}
            </a>
            <p class="link-description">{{ link.description || "No description added." }}</p>
          </div>

          <div class="link-meta">
            <span class="link-url">{{ link.url }}</span>
            <span v-if="link.tag" class="tag">{{ link.tag }}</span>
            <button class="delete-button" type="button" @click="$emit('delete-link', link.id)">Delete</button>
          </div>
        </li>
      </template>
    </ul>
  </section>
</template>
