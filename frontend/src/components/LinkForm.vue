<script setup>
import { reactive } from "vue";

defineProps({
  disabled: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["create-link"]);

const form = reactive({
  title: "",
  url: "",
  tag: "",
  description: "",
});

function submitForm() {
  emit("create-link", {
    title: form.title.trim(),
    url: form.url.trim(),
    tag: form.tag.trim(),
    description: form.description.trim(),
  });

  form.title = "";
  form.url = "";
  form.tag = "";
  form.description = "";
}
</script>

<template>
  <form class="link-form" @submit.prevent="submitForm">
    <div class="field-grid">
      <label>
        <span>Title</span>
        <input v-model="form.title" type="text" required maxlength="120" placeholder="Vue docs" />
      </label>

      <label>
        <span>URL</span>
        <input v-model="form.url" type="url" required placeholder="https://vuejs.org" />
      </label>
    </div>

    <div class="field-grid">
      <label>
        <span>Tag</span>
        <input v-model="form.tag" type="text" maxlength="40" placeholder="frontend" />
      </label>

      <label>
        <span>Description</span>
        <input v-model="form.description" type="text" maxlength="200" placeholder="Why this link matters" />
      </label>
    </div>

    <div class="form-actions">
      <button type="submit" :disabled="disabled">Save link</button>
    </div>
  </form>
</template>
