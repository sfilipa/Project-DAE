<template>
  <form @submit.prevent="delete_">
    <div>
      code: <input v-model="code" type="text">
    </div>
    <nuxt-link to="/subjects">Return</nuxt-link>
    <button type="reset">RESET</button>
    <button @click.prevent="delete_">DELETE</button>
  </form>
</template>

<script>
export default {
  data() {
    return {
      code: null
    }
  },
  methods: {
    delete_() {
      this.$axios.$delete(`/api/subjects/${this.code}`)
        .then(() => {
          this.$router.push('/subjects')
        })
    }
  }
}
</script>

<style scoped>

</style>
