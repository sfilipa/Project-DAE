<template>
  <b-container>
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

      <div v-else v-show="!editing" class="profile-body-div">
        <p class="profile-field"><span class="profile-label">Username:</span> <span> {{this.username}} </span></p>
        <p class="profile-field"><span class="profile-label">Name:</span> {{ this.name }}</p>
        <p class="profile-field"><span class="profile-label">Email:</span> {{this.email}}</p>
        <p class="profile-field"><span class="profile-label">Insurance Company:</span> {{this.company_username}}</p>
        <div class="btn-edit-profile-div">
          <button @click.prevent="editing = true" class="btn btn-edit-profile">Edit</button>
        </div>
      </div>

      <b-form v-show="editing" @submit.prevent="onSubmit" :disabled="!isFormValid" class="profile-body-div">
        <p><span class="profile-label">Username:</span> <span> {{this.username}} </span></p>

        <b-form-group class="mb-3" :invalid-feedback="invalidNameFeedback" :state="isNameValid">
          <span class="profile-label">Name:</span>
          <b-input class="form-control edit-input" :state="isNameValid" v-model.trim="newName" required/>
        </b-form-group>

        <b-form-group class="mb-3" :invalid-feedback="invalidEmailFeedback" :state="isEmailValid">
          <span class="profile-label">Email:</span>
          <b-input class="form-control edit-input"
                   name="newEmail"
                   type="email"
                   :state="isEmailValid"
                   v-model.trim="newEmail"
                   required/>
        </b-form-group>

        <p class="profile-field"><span class="profile-label">Insurance Company:</span> {{this.company_username}}</p>

        <p class="text-danger text-center" v-show="errorMsg">{{ errorMsg }}</p>

        <div class="edit-profile-buttons">
          <div style="margin: auto">
            <button @click.prevent="onReset" class="btn btn-edit-profile btn-edit-profile-reset">Reset</button>
            <button type="submit" class="btn btn-edit-profile">Save</button>
          </div>
        </div>

        <div class="btn-edit-profile-div">
          <button @click.prevent="editing = false; onReset();" class="btn btn-edit-profile">Cancel</button>
        </div>
      </b-form>
    </div>
  </b-container>
</template>

<script>
export default {
  name: "index.vue",
  data () {
    return {
      username: null,
      name: null,
      email: null,
      company_username: null,
      newName: null,
      newEmail: null,
      errorMsg: null,
      editing: false
    }
  },
  created () {
    this.$axios.$get(`/api/experts/${this.$auth.user.username}`)
      .then((response) => {
        this.username = response.username;
        this.name = response.name;
        this.email = response.email;
        this.company_username = response.company_username;
        this.onReset()
      })
  },
  computed: {
    invalidNameFeedback () {
      if (!this.newName) {
        return null
      }
      let nameLen = this.newName.length
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
      if (!this.newEmail) {
        return null
      }
      if(!this.validEmail(this.newEmail)){
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
    invalidAddressFeedback () {
      if (!this.newAddress) {
        return null
      }
      let addressLen = this.newAddress.length
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
      if (! this.isNameValid) {
        return false
      }
      if (! this.isEmailValid) {
        return false
      }
      return true
    }
  },
  methods:{
    onReset(){
      this.newName = this.name;
      this.newEmail = this.email;
    },
    onSubmit(){
      if(this.name === this.newName &&
        this.email === this.newEmail){
        this.editing = false
        return
      }

      this.$axios.put(`/api/experts/${this.$auth.user.username}`, {
        name: this.newName,
        email: this.newEmail
      })
        .then(() => {
          this.name = this.newName
          this.email = this.newEmail
          this.$toast.success(`Expert ${this.newName} updated!`).goAway(3000)
          this.editing = false
        })
        .catch(({ response: err }) => {
          this.errorMsg = err.data
          this.$toast.error('Sorry, expert couldn\'t be updated. Ensure you don\'t have any errors').goAway(3000)
        })
    },
    validEmail(email) {
      return /^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/.test(email);
    }
  }
}
</script>

<style scoped>

.edit-input{
  width: 64%;
  display: inline;
}

.btn-edit-profile-reset{
  background-color: #eaeaea !important;
}

.edit-profile-buttons{
  display: flex;
  flex-direction: row;
}

.btn-edit-profile:hover{
  background-color: red !important;
  color: white !important;
  border: 1px solid red !important;
}

.btn-edit-profile{
  border: 1px solid black;
  height: 3rem;
  width: 9rem;
  background-color: white;
  margin: 0 1rem;
}

.btn-edit-profile-div{
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 2rem;
}

.profile-label{
  width: 35%;
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
