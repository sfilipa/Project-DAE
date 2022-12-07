<template>
  <b-container>
    <h1>Welcome to Academics Management</h1>
    Please visit our
    <nuxt-link to="/students" class="btn btn-link">Students</nuxt-link>
    <nuxt-link to="/subjects" class="btn btn-link">Subjects</nuxt-link>
    <nuxt-link to="/teachers" class="btn btn-link">Teachers</nuxt-link>
    <nuxt-link to="/administrators" class="btn btn-link">Administrators</nuxt-link>
    <nuxt-link to="/courses" class="btn btn-link">Courses</nuxt-link>
  </b-container>
</template>

<script>
export default {
  name: "index.vue"
}
</script>

<style scoped>

</style>
