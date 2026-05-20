<script setup>
defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  tags: {
    type: Array,
    required: true,
  },
});

const emit = defineEmits(["update:modelValue"]);

function clearFilter() {
  emit("update:modelValue", "");
}
</script>

<template>
  <div class="tag-filter-container">
    <label class="tag-filter">
      <span>Filter by tag</span>
      <select :value="modelValue" @change="$emit('update:modelValue', $event.target.value)">
        <option value="">All tags</option>
        <option v-for="tag in tags" :key="tag" :value="tag">{{ tag }}</option>
      </select>
    </label>
    <button
      v-if="modelValue"
      type="button"
      class="clear-filter-button"
      title="Clear filter"
      @click="clearFilter"
    >
      &times;
    </button>
  </div>
</template>

<style scoped>
.tag-filter-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.clear-filter-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  line-height: 1;
  cursor: pointer;
  color: var(--muted);
  padding: 0 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.clear-filter-button:hover {
  color: var(--danger);
}
</style>
