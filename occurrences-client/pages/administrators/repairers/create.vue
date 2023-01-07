<template>
  <div>
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

    <h3 class="text-center index-header">Regist a Repairer</h3>

    <b-form @submit.prevent="onSubmit" @reset="onReset" :disabled="!isFormValid" class="regist-section">
      <div class="regist-body">
        <div class="regist-body-div">
          <b-form-group class="mb-3" :invalid-feedback="invalidUsernameFeedback" :state="isUsernameValid">
            <span class="regist-label">Username:</span>
            <b-input class="form-control regist-textbox"
                     :state="isUsernameValid"
                     v-model.trim="username"
                     required/>
          </b-form-group>
          <b-form-group class="mb-3" :invalid-feedback="invalidNameFeedback" :state="isNameValid">
            <span class="regist-label">Name:</span>
            <b-input class="form-control regist-textbox"
                     :state="isNameValid"
                     v-model.trim="name"
                     required/>
          </b-form-group>
          <b-form-group class="mb-3" :invalid-feedback="invalidEmailFeedback" :state="isEmailValid">
            <span class="regist-label">Email:</span>
            <b-input class="form-control regist-textbox"
                     name="email"
                     type="email"
                     ref="email"
                     :state="isEmailValid"
                     pattern=".+@gmail.com"
                     v-model.trim="email"
                     required/>
          </b-form-group>
          <b-form-group class="mb-3" :invalid-feedback="invalidPasswordFeedback" :state="isPasswordValid">
            <span class="regist-label">Password:</span>
            <b-input class="form-control regist-textbox"
                     :state="isPasswordValid"
                     v-model.trim="password"
                     type="password"
                     required/>
          </b-form-group>
          <b-form-group class="mb-3" :invalid-feedback="invalidAddressFeedback" :state="isAddressValid">
            <span class="regist-label">Address:</span>
            <b-input class="form-control regist-textbox"
                     :state="isAddressValid"
                     v-model.trim="address"
                     required/>
          </b-form-group>
        </div>
      </div>
      <p class="text-danger text-center" v-show="errorMsg">{{ errorMsg }}</p>
      <div class="register-buttons">
        <div style="margin-left: auto; width: inherit;">
          <button type="reset" class="btn btn-reset">Reset</button>
          <button type="submit" class="btn btn-submit">Register</button>
        </div>
      </div>
    </b-form>
  </div>
</template>

<script>
export default {
  name: "create",
  data() {
    return{
      username: null,
      password: null,
      name: null,
      email: null,
      company_username: null,
      errorMsg: null,
      isFormValid: false
    }
  },
  computed: {
    invalidUsernameFeedback () {
      if (!this.username) {
        return null
      }
      let usernameLen = this.username.length
      if (usernameLen < 3 || usernameLen > 15) {
        return 'The username must be between [3, 15] characters.'
      }
      return ''
    },
    isUsernameValid () {
      if (this.invalidUsernameFeedback === null) {
        return null
      }
      return this.invalidUsernameFeedback === ''
    },
    invalidPasswordFeedback () {
      if (!this.password) {
        return null
      }
      let passwordLen = this.password.length
      if (passwordLen < 3 || passwordLen > 255) {
        return 'The password must be between [3, 255] characters.'
      }
      return ''
    },
    isPasswordValid () {
      if (this.invalidPasswordFeedback === null) {
        return null
      }
      return this.invalidPasswordFeedback === ''
    },
    invalidNameFeedback () {
      if (!this.name) {
        return null
      }
      let nameLen = this.name.length
      if (nameLen < 3 || nameLen > 25) {
        return 'The password must be between [3, 25] characters.'
      }
      return ''
    },
    isNameValid () {
      if (this.invalidNameFeedback === null) {
        return null
      }
      return this.invalidNameFeedback === ''
    },
    invalidEmailFeedback () {
      if (!this.email) {
        return null
      }
// asks the component if it’s valid. We don’t need to use a regex for the e-mail. The input field already does the job for us, because it is of type “email” and validates that the user writes an e-mail that belongs to the domain of IPLeiria.
      if(!this.$refs.email.checkValidity()){
        return 'The email should end with "@gmail.com"'
      }
      return ''
    },
    isEmailValid () {
      if (this.invalidEmailFeedback === null) {
        return null
      }
      return this.invalidEmailFeedback === ''
    },
    invalidAddressFeedback () {
      if (!this.address) {
        return null
      }
      let addressLen = this.address.length
      if (addressLen < 3 || addressLen > 25) {
        return 'The address must be between [3, 25] characters.'
      }
      return ''
    },
    isAddressValid () {
      if (this.invalidAddressFeedback === null) {
        return null
      }
      return this.invalidAddressFeedback === ''
    },
    isFormValid () {
      if (! this.isUsernameValid) {
        return false
      }
      if (! this.isPasswordValid) {
        return false
      }
      if (! this.isNameValid) {
        return false
      }
      if (! this.isEmailValid) {
        return false
      }
      if (! this.isAddressValid) {
        return false
      }
      return true
    }
  },
  methods: {
    onSubmit() {
      let promise= this.$axios.post('api/repairers', {
          username: this.username,
          password: this.password,
          name: this.name,
          email: this.email,
          address: this.address,
        }
      )
      promise.then(() => {
        this.$toast.success(`Repairer ${this.name} created!`).goAway(3000)
        this.$router.push('/')
      })
      promise.catch(({ response: err }) => {
        this.errorMsg = err.data
        this.$toast.error('Sorry, repairer couldn\'t be registered. Ensure you don\'t have any errors').goAway(3000)
      })

    },
    onReset() {
      this.username = null
      this.password = null
      this.name = null
      this.email = null
      this.address = null
    }
  }
}
</script>

<style scoped>

.d-block{
  margin-left: 31% !important;
}

.btn-reset:hover{
  background-color: #dcdcdc;
}

.btn-reset{
  width: 15rem;
  background-color: #eaeaea;
  border: 1px solid black;
  margin-right: 20px;
}

.btn-submit:hover{
  background-color: red !important;
  color: white !important;
}

.btn-submit{
  width: 15rem;
  background-color: white;
  border: 1px solid black;
}

.register-buttons{
  display: flex;
  flex-direction: row;
  padding: 0 6%;
}

.regist-textbox{
  width: 68%;
  display: inline;
}

.regist-label{
  width: 30%;
  display: inline-block;
  font-weight: bold;
}

.regist-field{
  width: 100%;
}

.regist-body-div{
  margin: auto;
  width: 90%;
}

.regist-body{
  display: flex;
  flex-direction: column;
  align-items: center;
}

.regist-section{
  margin: 0 12%;
}

.index-header{
  margin-bottom: 3rem;
}
</style>
