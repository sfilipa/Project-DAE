<template>
  <b-container class="login-page">
    <h3 class="text-center mb-4">Login into Company</h3>
    <b-form @submit.prevent="onSubmit" @reset="onReset">
      <b-form-group class="mb-3" label="Username">
        <b-input
          name="username"
          placeholder="Enter your username"
          v-model.trim="username"
          required />
      </b-form-group>
      <b-form-group class="mb-4" label="Password">
        <b-input
          name="password"
          type="password"
          placeholder="Enter your password"
          v-model="password"
          required />
      </b-form-group>
      <div class="login-buttons">
        <button type="reset" class="btn btn-reset">Reset</button>
        <button type="submit" class="btn btn-submit">Submit</button>
      </div>
    </b-form>
  </b-container>
</template>

<script>
export default {
  auth: false, // public page, don’t use the authentication middleware
  data() {
    return {
      username: null,
      password: null
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
      promise.then((response) => {
        this.$toast.success('You are logged in!').goAway(3000)
        console.log(this.$auth.user, response.data)
        // const { token } = response.data;
        // this.$axios.defaults.headers.common = { Authorization: `Bearer ${token}` };
        // this.$auth.user.setToken('Bearer',response.data)
        // this.$auth.setUserToken(response.data)
        this.$router.push('/')
        // this.$router.push(`/${this.$auth.user.role.toLowerCase()}s`)
      })
      promise.catch(() => {
        this.$toast.error('Sorry, you cant login. Ensure your credentials are correct').goAway(3000)
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


</style>
