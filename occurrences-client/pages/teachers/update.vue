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
    <div>
      Office: <input v-model="office" type="text">
    </div>
    <nuxt-link to="/teachers">Return</nuxt-link>
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
      email: null,
      office: null
    }
  },
  methods: {
    update() {
      this.$axios.$put(`/api/teachers/${this.username}`, {
        username: this.username,
        password: this.password,
        name: this.name,
        email: this.email,
        office: this.office
      })
        .then(() => {
          this.$router.push('/teachers')
        })
    }
  }
}
</script>

<style scoped>

</style>
