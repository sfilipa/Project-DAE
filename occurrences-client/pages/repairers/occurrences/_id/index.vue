<template>
  <div v-if="this.$auth.user === null">
    <b-container class="login-page">
      <h3 class="text-center mb-4">Please sign in first</h3>
      <b-form @submit.prevent="onSubmit" @reset="onReset">
        <b-form-group class="mb-3" label="Username:">
          <b-input
            name="username"
            placeholder="Enter your username"
            v-model.trim="username"
            required />
        </b-form-group>
        <b-form-group class="mb-4" label="Password:">
          <b-input
            name="password"
            type="password"
            placeholder="Enter your password"
            v-model="password"
            required />
        </b-form-group>
        <p class="text-danger text-center" v-show="errorMsg">{{ errorMsg }}</p>
        <div class="login-buttons">
          <button type="reset" class="btn btn-reset">Reset</button>
          <button type="submit" class="btn btn-submit">Login</button>
        </div>
      </b-form>
      <br>
      <div style="display: flex">

      </div>
      <div style="display: flex">
        <nuxt-link to="/auth/register" style="margin: auto">Don't have an account? <b>Sign Up</b></nuxt-link>
      </div>
    </b-container>
  </div>

  <b-container v-else-if="this.$auth.user.role.toLowerCase() === 'repairer'">
    <div v-if="this.occurrence === null" class="spinner-div">
      <div class="spinner-border"></div>
    </div>

    <div v-else>
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

      <div class="occurrence-body">
        <h3 class="text-center index-header fw-bold" style="color: red;">{{ this.occurrence.insuranceCompanyName }} ({{ this.occurrence.insuranceCode }})</h3>
        <h4 class="text-center index-header">Occurrence {{this.occurrence.id}} - Client {{this.occurrence.usernameClient}}</h4>
        <h5 class="text-center index-header" style="margin-bottom: 3rem">{{this.occurrence.state}}</h5>
        <div class="occurrence-body-div">
          <p class="occurrence-field"><span class="occurrence-label">Object Insured:</span> <span> {{this.occurrence.objectInsured}} </span></p>
          <p class="occurrence-field"><span class="occurrence-label">Coverage Type:</span>  <span> {{ this.occurrence.coverageType }} </span> </p>
          <p class="occurrence-field"><span class="occurrence-label">Description:</span>  <span> {{this.occurrence.description}} </span> </p>
          <p class="occurrence-field"><span class="occurrence-label">Entry Date:</span> {{this.occurrence.entryDate}}</p>

          <div v-if="isAssigned() &&
                          occurrence.state==='REPAIRER_WAITING_LIST'">
            <button  class="btn btn-repairer-button" @click.prevent="start(occurrence.id)" :disabled="waitingRefresh">Start</button>
          </div>
        </div>
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
  name: "index",
  components: {Unauthorized},
  auth: false,
  data(){
    return{
      username: null,
      password: null,
      errorMsg: null,
      occurrence: null
    }
  },
  mounted() {
    this.$axios.get(`api/occurrences/${this.$route.params.id}`)
      .then((response)=>{
        this.occurrence = response.data
      })
  },
  methods: {
    isAssigned(){
      return this.occurrence.usernameRepairer == this.$auth.user.username
    },
    onSubmit() {
      let promise = this.$auth.loginWith('local', {
        data: {
          username: this.username,
          password: this.password
        }
      })
      promise.then(() => {
        if(this.$auth.user.role.toLowerCase() === 'repairer') {
          this.$toast.success('You are logged in!').goAway(3000)
          this.$router.push(`/repairers/occurrences/${this.occurrence.id}`)
        }else{
          this.$toast.error('Only repairers allowed').goAway(3000)
          this.$router.push(`/`)
        }
      })
      promise.catch(({ response: err }) => {
        this.errorMsg = err.data
        this.$toast.error('Sorry, you can\'t login. Ensure your credentials are correct').goAway(3000)
      })
    },
    onReset() {
      this.username = null
      this.password = null
    }
  }
}
</script>

<style scoped>
.btn-reset:hover{
  background-color: #dcdcdc;
}

.btn-reset{
  width: 45%;
  background-color: #eaeaea;
  border: 1px solid black;
  margin-right: auto;
}

.btn-submit:hover{
  background-color: red !important;
  color: white !important;
}

.btn-submit{
  width: 45%;
  background-color: white;
  border: 1px solid black;
}

.login-buttons{
  display: flex;
  flex-direction: row;
  width: 100%;
}

.login-page{
  padding: 80px 320px;
}


.occurrence-label{
  width: 30%;
  display: inline-block;
  font-weight: bold;
}

.occurrence-field{
  width: 100%;
  height: 2.2rem;
}

.occurrence-body-div{
  margin: auto;
  width: 50%;
  margin-bottom: 2rem;
}

.occurrence-body{
  display: flex;
  flex-direction: column;
  align-items: center;
}

</style>
