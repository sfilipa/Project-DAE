<template>
  <form @submit.prevent="delete_">
    <div>
      username: <input v-model="username" type="text">
    </div>
    <nuxt-link to="/students">Return</nuxt-link>
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
      this.$axios.$delete(`/api/students/${this.username}`)
        .then(() => {
          this.$router.push('/students')
        })
    }
  }
}
</script>

<style scoped>

</style>
