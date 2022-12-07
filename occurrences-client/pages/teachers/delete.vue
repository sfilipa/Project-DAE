<template>
  <form @submit.prevent="delete_">
    <div>
      username: <input v-model="username" type="text">
    </div>
    <nuxt-link to="/teachers">Return</nuxt-link>
    <button type="reset">RESET</button>
    <button @click.prevent="delete_">DELETE</button>
  </form>
</template>

<script>
export default {
  data() {
    return {
      username: null
    }
  },
  methods: {
    delete_() {
      this.$axios.$delete(`/api/teachers/${this.username}`)
        .then(() => {
          this.$router.push('/teachers')
        })
    }
  }
}
</script>

<style scoped>

</style>
