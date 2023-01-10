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

    <h3 class="text-center index-header">Regist an Expert</h3>

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
                     :state="isEmailValid"
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
          <b-form-group class="mb-3">
            <span class="regist-label">Insurance Company:</span>
            <select class="form-select mb-2 regist-textbox" v-model="company_username" @focus="errorMsg = null">
              <option disabled value="">Select Insurance Company</option>
              <option v-for="company in insuranceCompanies">{{company}}</option>
            </select>
          </b-form-group>
        </div>
      </div>
      <p class="text-danger text-center" v-show="errorMsg">{{ errorMsg }}</p>
      <div class="register-buttons">
        <div style="margin-left: auto; width: inherit;">
          <button type="reset" class="btn btn-reset" :disabled="waitingResponse">Reset</button>
          <button type="submit" class="btn btn-submit" :disabled="waitingResponse">Register</button>
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
      company_username: "",
      errorMsg: null,
      insuranceCompanies: null,
      waitingResponse: false
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
      if(!this.validEmail(this.email)){
        return 'The email should follow the format -@-.--'
      }
      return ''
    },
    isEmailValid () {
      if (this.invalidEmailFeedback === null) {
        return null
      }
      return this.invalidEmailFeedback === ''
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
      return true
    }
  },
  methods: {
    onSubmit() {
      if(this.company_username === ""){
        this.errorMsg = "Please select an Insurance Company"
        return
      }
      this.waitingResponse = true
      let promise= this.$axios.post('api/experts', {
          username: this.username,
          password: this.password,
          name: this.name,
          email: this.email,
          company_username: this.company_username,
        }
      )
      promise.then(() => {
        this.$toast.success(`Expert ${this.name} created!`).goAway(3000)
        this.$router.push('/')
      })
      promise.catch(({ response: err }) => {
        this.errorMsg = err.data
        this.$toast.error('Sorry, expert couldn\'t be registered. Ensure you don\'t have any errors').goAway(3000)
        this.waitingResponse = false
      })

    },
    onReset() {
      this.username = null
      this.password = null
      this.name = null
      this.email = null
      this.company_username = null
    },
    validEmail(email) {
      return /^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/.test(email);
    }
  },
  created() {
    this.$axios.get(`https://63a9db1a594f75dc1dc27d9b.mockapi.io/insuranceCompanies`)
      .then((companies)=>{
        this.insuranceCompanies = companies.data.map(insurance => {return insurance.name})
      })
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
