<template>
  <b-container v-if="this.$auth.user && this.$auth.user.role.toLowerCase() === 'administrator'">
    <nuxt-link
      class="btn pb-3 pr-5 text-uppercase"
      :to="`/`">
      <div>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-chevron-left" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        &nbsp; Back to HomePage
      </div>
    </nuxt-link>
    <h3 class="text-center index-header">Profile</h3>
    <div class="profile-body">
      <div v-if="this.name == null" class="spinner-div">
        <div class="spinner-border"></div>
      </div>

      <div v-else class="profile-body-div">
        <p class="profile-field"><span class="profile-label">Username:</span> {{this.username}}</p>
        <p class="profile-field"><span class="profile-label">Name:</span> {{ this.name }}</p>
        <p class="profile-field"><span class="profile-label">Email:</span> {{this.email}}</p>
      </div>
    </div>
  </b-container>

  <div v-else>
    <Unauthorized></Unauthorized>
  </div>
</template>

<script>
import Unauthorized from "@/pages/components/Unauthorized";
export default {
  name: "index.vue",
  components: {Unauthorized},
  data () {
    return {
      username: null,
      name: null,
      email: null,
    }
  },
  created () {
    this.$axios.$get(`/api/administrators/${this.$auth.user.username}`)
      .then((response) => {
        this.username = response.username;
        this.name = response.name;
        this.email = response.email;
      })
  }
}
</script>

<style scoped>

.profile-label{
  width: 30%;
  display: inline-block;
  font-weight: bold;
}

.profile-field{
  width: 100%;
  height: 2.2rem;
}

.profile-body-div{
  margin: auto;
  width: 50%;
  margin-bottom: 2rem;
}

.profile-body{
  display: flex;
  flex-direction: column;
  align-items: center;
}
.index-header{
  margin-bottom: 3rem;
}
</style>
