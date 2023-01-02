<template>
  <b-container>
    <h3 class="text-center index-header">Profile</h3>
    <div class="profile-body">
      <div class="profile-body-div">
        <p class="profile-field"><span class="profile-label">Username:</span> {{this.username}}</p>
        <p class="profile-field"><span class="profile-label">Name:</span> {{ this.name }}</p>
        <p class="profile-field"><span class="profile-label">Email:</span> {{this.email}}</p>
        <p class="profile-field"><span class="profile-label">Address:</span> {{ this.address }}</p>
        <p class="profile-field"><span class="profile-label">Phone Number:</span> {{ this.phoneNumber }}</p>
        <p class="profile-field profile-password"><span class="profile-label">Password:</span> <span :class="{'reveal-password': !revealPass}">{{revealPass ? this.password : ""}}</span>
          <button @click="revealPass=!revealPass" class="btn">
            <svg v-if="!revealPass" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-eye bi-profile" viewBox="0 0 16 16">
              <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z"/>
              <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"/>
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-eye-slash bi-profile" viewBox="0 0 16 16">
              <path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/>
              <path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/>
              <path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z"/>
            </svg>
          </button>
        </p>
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
      password: "",
      name: "",
      email: "",
      address: "",
      phoneNumber: null,
      revealPass: false
    }
  },
  computed: {
    insurances(){ return [
      {
        "name": "Carros Liber"
      },
      {
        "name": "Carros Logo"
      }
    ]}
  },
  created () {
    this.$axios.$get(`/api/clients/${this.$auth.user.username}`)
      .then((response) => {
        this.username = response.username;
        this.password = response.password;
        this.name = response.name;
        this.email = response.email;
        this.address = response.address;
        this.phoneNumber = response.phoneNumber;
      })
  }
}
</script>

<style scoped>

.bi-profile{
  top: -1px;
  position: relative;
}

.profile-password{
  display: flex;
  flex-direction: row;
  align-items: center;
}

.profile-label{
  width: 30%;
  display: inline-block;
  font-weight: bold;
}

.profile-field{
  width: 100%;
}

.reveal-password{
  background-color: #c4c4c4;
  color: white;
  width: 50%;
  height: 30px;
  display: inline-block;
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
