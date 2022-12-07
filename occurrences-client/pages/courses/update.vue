<template>
  <form @submit.prevent="update">
    <div>
      code: <input v-model="code" type="text">
    </div>
    <div>
      name: <input v-model="name" type="text">
    </div>
    <nuxt-link to="/courses">Return</nuxt-link>
    <button type="reset">RESET</button>
    <button @click.prevent="update">UPDATE</button>
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
    update() {
      this.$axios.$put(`/api/courses/${this.code}`, {
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
