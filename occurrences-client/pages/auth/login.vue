<template>
  <b-container class="login-page">
    <h3 class="text-center mb-4">Sign in to the <span class="fw-bold">Incidentalists</span></h3>
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
    <nuxt-link to="/auth/register" style="margin: auto">Don't have an account? <b>Sign Up</b></nuxt-link>
    </div>
  </b-container>
</template>

<script>
export default {
  auth: false, // public page, don’t use the authentication middleware
  data() {
    return {
      username: null,
      password: null,
      errorMsg: null
    }
  },
  methods: {
    onSubmit() {
      let promise = this.$auth.loginWith('local', {
        data: {
          username: this.username,
          password: this.password
        }
      })
      promise.then(() => {
        this.$toast.success('You are logged in!').goAway(3000)
        this.$router.push('/')
      })
      promise.catch((response) => {
        this.$auth.logout()
        let promiseAdmin = this.$auth.loginWith('admin', {
          data: {
            username: this.username,
            password: this.password
          }
        })
        promiseAdmin.then(() => {
          this.$toast.success('You are logged in!').goAway(3000)
          this.$router.push(`/`)
        })
        promiseAdmin.catch(({ response: err }) => {
          this.errorMsg = err.data
          this.$toast.error('Sorry, you can\'t login. Ensure your credentials are correct').goAway(3000)
        })
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
  padding: 5% 25%;
}

@media only screen and (min-width: 1400px) {
  .login-page{
    padding: 5% 20rem;
  }
}

@media only screen and (max-width: 1200px) {
  .login-page{
    padding: 5% 20%;
  }
}

</style>
