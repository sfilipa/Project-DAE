<template>
  <b-container>
    <h4>Administrator Details:</h4>
    <p>Username: {{ administrator.username }}</p>
    <p>Name: {{ administrator.name }}</p>
    <p>Email: {{ administrator.email }}</p>
    <nuxt-link to="/administrators">Back</nuxt-link>
  </b-container>
</template>

<script>
export default {
  data() {
    return {
      administrator: {}
    }
  },
  computed: {
    username() {
      return this.$route.params.username
    }
  },
  created() {
    this.$axios.$get(`/api/administrators/${this.username}`)
      .then(administrator => this.administrator = administrator || {})
  }
}
</script>

<style scoped>

</style>
