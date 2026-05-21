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
  tags: "",
  description: "",
  personalNote: "",
});

function submitForm() {
  emit("create-link", {
    title: form.title.trim(),
    url: form.url.trim(),
    tags: form.tags.trim(),
    description: form.description.trim(),
    personalNote: form.personalNote.trim(),
  });

  form.title = "";
  form.url = "";
  form.tags = "";
  form.description = "";
  form.personalNote = "";
}
</script>

<template>
  <form class="link-form" @submit.prevent="submitForm">
    <div class="field-grid">
      <label>
        <span>Title</span>
        <input v-model="form.title" type="text" maxlength="120" placeholder="Vue docs" />
      </label>

      <label>
        <span>URL</span>
        <input v-model="form.url" type="url" required placeholder="https://vuejs.org" />
      </label>
    </div>

    <div class="field-grid">
      <label>
        <span>Tags (comma separated)</span>
        <input v-model="form.tags" type="text" maxlength="255" placeholder="frontend, docs, vue" />
      </label>

      <label>
        <span>Description</span>
        <input v-model="form.description" type="text" maxlength="200" placeholder="Why this link matters" />
      </label>
    </div>

    <label>
      <span>Personal note</span>
      <textarea
        v-model="form.personalNote"
        maxlength="1000"
        rows="4"
        placeholder="How you plan to use this link"
      ></textarea>
    </label>

    <div class="form-actions">
      <button type="submit" :disabled="disabled">Save link</button>
    </div>
  </form>
</template>
