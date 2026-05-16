<script setup>
defineProps({
  links: {
    type: Array,
    required: true,
  },
  isLoading: {
    type: Boolean,
    default: false,
  },
});

defineEmits(["delete-link"]);
</script>

<template>
  <ul class="links-list" aria-live="polite">
    <li v-if="isLoading" class="empty-state">Loading links...</li>
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
</template>
