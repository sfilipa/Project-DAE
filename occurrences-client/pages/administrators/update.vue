<template>
  <form @submit.prevent="update">
    <div>
      Username: <input v-model="username" type="text">
    </div>
    <div>
      Password: <input v-model="password" type="password">
    </div>
    <div>
      Name: <input v-model="name" type="text">
    </div>
    <div>
      Email: <input v-model="email" type="email">
    </div>
    <nuxt-link to="/administrators">Return</nuxt-link>
    <button type="reset">RESET</button>
    <button @click.prevent="update">UPDATE</button>
  </form>
</template>

<script>
export default {
  data() {
    return {
      username: null,
      password: null,
      name: null,
      email: null
    }
  },
  methods: {
    update() {
      this.$axios.$put(`/api/administrators/${this.username}`, {
        username: this.username,
        password: this.password,
        name: this.name,
        email: this.email
      })
        .then(() => {
          this.$router.push('/administrators')
        })
    }
  }
}
</script>

<style scoped>

</style>
