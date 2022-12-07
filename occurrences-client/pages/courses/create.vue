<template>
  <form @submit.prevent="create">
    <div>
      code: <input v-model="code" type="text">
    </div>
    <div>
      name: <input v-model="name" type="text">
    </div>
    <nuxt-link to="/courses">Return</nuxt-link>
    <button type="reset">RESET</button>
    <button @click.prevent="create">CREATE</button>
  </form>
</template>

<script>
export default {
  data() {
    return {
      code: null,
      name: null
    }
  },
  methods: {
    create() {
      this.$axios.$post('/api/courses', {
        code: this.code,
        name: this.name
      })
        .then(() => {
          this.$router.push('/courses')
        })
    }
  }
}
</script>

<style scoped>

</style>
