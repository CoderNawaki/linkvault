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

defineEmits(["update:modelValue"]);
</script>

<template>
  <label class="tag-filter">
    <span>Filter by tag</span>
    <select :value="modelValue" @change="$emit('update:modelValue', $event.target.value)">
      <option value="">All tags</option>
      <option v-for="tag in tags" :key="tag" :value="tag">{{ tag }}</option>
    </select>
  </label>
</template>
