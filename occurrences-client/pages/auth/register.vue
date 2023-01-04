<template>
  <b-container class="register-page">
    <h3 class="text-center mb-4">Sign up in the Company</h3>
    <b-form @submit.prevent="onSubmit" @reset="onReset" :disabled="!isFormValid">
      <div style="display: flex">
        <div class="register-column">
          <b-form-group class="mb-3" label="Username:" :invalid-feedback="invalidUsernameFeedback" :state="isUsernameValid">
            <b-input
              name="username"
              :state="isUsernameValid"
              placeholder="Enter your username"
              v-model.trim="username"
              required />
          </b-form-group>
          <b-form-group class="mb-3" label="Name:" :invalid-feedback="invalidNameFeedback" :state="isNameValid" >
            <b-input
              name="name"
              :state="isNameValid"
              placeholder="Enter your name"
              v-model.trim="name"
              required />
          </b-form-group>
          <b-form-group class="mb-3" label="Email:" :invalid-feedback="invalidEmailFeedback" :state="isEmailValid" >
            <b-input
              name="email"
              type="email"
              ref="email"
              :state="isEmailValid"
              pattern=".+@gmail.com"
              placeholder="Enter your email"
              v-model.trim="email"
              required />
          </b-form-group>
          <b-form-group class="mb-4" label="Password:" :invalid-feedback="invalidPasswordFeedback" :state="isPasswordValid">
            <b-input
              name="password"
              type="password"
              :state="isPasswordValid"
              placeholder="Enter your password"
              v-model="password"
              required />
          </b-form-group>
        </div>
        <div class="register-column">
          <b-form-group class="mb-3" label="NIF / NIPC:"  :invalid-feedback="invalidNifNipcFeedback" :state="isNifNipcValid" >
            <b-input
              name="nif_nipc"
              ref="nif_nipc"
              pattern="^[1,2,3,5,6,9].[0-9]{7}"
              :state="isNifNipcValid"
              placeholder="Enter your NIF or NIPC"
              v-model.trim="nif_nipc"
              required />
          </b-form-group>
          <b-form-group class="mb-3" label="Address:" :invalid-feedback="invalidAddressFeedback" :state="isAddressValid">
            <b-input
              name="address"
              placeholder="Enter your address"
              :state="isAddressValid"
              v-model.trim="address"
              required />
          </b-form-group>
          <b-form-group class="mb-3" label="Phone Number:" :invalid-feedback="invalidPhoneNumberFeedback" :state="isPhoneNumberValid">
            <b-input
              name="phoneNumber"
              pattern="[0-9]{9}"
              ref="phoneNumber"
              placeholder="Enter your phone number"
              :state="isPhoneNumberValid"
              v-model.trim="phoneNumber"
              required />
          </b-form-group>
        </div>
      </div>
      <p class="text-danger" v-show="errorMsg">{{ errorMsg }}</p>
      <div class="register-buttons">
        <div style="margin-left: auto; width: inherit;">
          <button type="reset" class="btn btn-reset">Reset</button>
          <button type="submit" class="btn btn-submit">Register</button>
        </div>
      </div>
    </b-form>
    <br>
    <div style="display: flex">
      <nuxt-link to="/auth/login" style="margin: auto">Already have an account? <b>Sign In</b></nuxt-link>
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
      name: null,
      email: null,
      address: null,
      phoneNumber: null,
      nif_nipc: null,
      errorMsg: false
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
    invalidNifNipcFeedback () {
      if (!this.nif_nipc) {
        return null
      }
      let nifNipcLen = this.nif_nipc.length
      if (nifNipcLen != 9) {
        return 'The NIF/NIPC must have 9 digits.'
      }
      if(!this.$refs.nif_nipc.checkValidity()){
        return 'The NIF/NIPC start with 1,2,3,5,6 or 9'
      }
      return ''
    },
    isNifNipcValid(){
      if(this.invalidNifNipcFeedback === null){
        return null;
      }
      return  this.invalidNifNipcFeedback === ''
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
    invalidPhoneNumberFeedback () {
      if (!this.phoneNumber) {
        return null
      }
      let phoneNumberLen = this.phoneNumber.length
      if (phoneNumberLen != 9) {
        return 'The Phone Number must have 9 digits.'
      }
      return ''
    },
    isPhoneNumberValid(){
      if(this.invalidPhoneNumberFeedback === null){
        return null;
      }
      return  this.invalidPhoneNumberFeedback === ''
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
      if (! this.isNifNipcValid) {
        return false
      }
      if (! this.isAddressValid) {
        return false
      }
      if (! this.isEmailValid) {
        return false
      }
      if (! this.phoneNumber) {
        return false
      }
      return true
    }
  },
  methods: {
    onSubmit() {
      let promise= this.$axios.post('api/clients', {
          data: {
            username: this.username,
            password: this.password,
            name: this.name,
            email: this.email,
            address: this.address,
            phoneNumber: this.phoneNumber,
            nif_nipc: this.nif_nipc,
          }
        })
      promise.then(() => {
        this.$toast.success('You are now registered!').goAway(3000)
        this.$router.push('/')
      })
      promise.catch(() => {
        this.$toast.error('Sorry, you cant sign up. Ensure you don\'t have any errors').goAway(3000)
      })

    },
    onReset() {
      this.username = null
      this.password = null
      this.name = null
      this.email = null
      this.address = null
      this.phoneNumber = null
      this.nif_nipc = null
    }
  }
}
</script>

<style scoped>
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
}

.register-column{
  width: 50%;
  padding: 20px;
}

.register-page{
  padding: 0px 120px;
}
</style>
