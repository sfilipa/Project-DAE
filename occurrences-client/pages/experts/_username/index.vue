<template>
  <b-container>
    <h3 class="text-center index-header">Profile</h3>
    <div class="profile-body">
      <div class="profile-body-div">
        <p class="profile-field"><span class="profile-label">Username:</span> {{this.username}}</p>
        <p class="profile-field"><span class="profile-label">Name:</span> {{ this.name }}</p>
        <p class="profile-field"><span class="profile-label">Email:</span> {{this.email}}</p>
        <p class="profile-field"><span class="profile-label">Insurance Company:</span> {{this.company_username}}</p>
      </div>
    </div>
  </b-container>
</template>

<script>
export default {
  name: "index.vue",
  data () {
    return {
      username: "",
      name: "",
      email: "",
      company_username: "",
    }
  },
  created () {
    this.$axios.$get(`/api/experts/${this.$auth.user.username}`)
      .then((response) => {
        this.username = response.username;
        this.name = response.name;
        this.email = response.email;
        this.company_username = response.company_username;
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
}

.profile-body-div{
  margin: auto;
  width: 90%;
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
