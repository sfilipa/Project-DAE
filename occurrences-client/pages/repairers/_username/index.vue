<template>
  <b-container v-if="this.$auth.user && this.$auth.user.role.toLowerCase() === 'repairer'">
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

      <div v-else v-show="!editing && !editPassword" class="profile-body-div">
        <p class="profile-field"><span class="profile-label">Username:</span> <span> {{this.username}} </span></p>
        <p class="profile-field"><span class="profile-label">Name:</span> {{ this.name }}</p>
        <p class="profile-field"><span class="profile-label">Email:</span> {{this.email}}</p>
        <p class="profile-field"><span class="profile-label">Address:</span> {{this.address}}</p>
        <div class="btn-edit-profile-div">
          <div>
            <button @click.prevent="editPassword = true" class="btn btn-profile-button">Change Password</button>
            <button @click.prevent="editing = true" class="btn btn-profile-button">Edit</button>
          </div>
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

        <b-form-group class="mb-3" :invalid-feedback="invalidAddressFeedback" :state="isAddressValid">
          <span class="profile-label">Address:</span>
          <b-input class="form-control edit-input" :state="isAddressValid" v-model.trim="newAddress" required/>
        </b-form-group>

        <p class="text-danger text-center" v-show="errorMsg">{{ errorMsg }}</p>

        <div class="edit-profile-buttons">
          <div style="margin: auto">
            <button @click.prevent="onReset" class="btn btn-profile-button btn-edit-profile-reset">Reset</button>
            <button type="submit" class="btn btn-profile-button">Save</button>
          </div>
        </div>

        <div class="btn-edit-profile-div">
          <button @click.prevent="editing = false; onReset();" class="btn btn-profile-button">Cancel</button>
        </div>
      </b-form>

      <b-form v-show="editPassword" @submit.prevent="onSubmitPassword" :disabled="!isPasswordFormValid" class="profile-body-div">
        <b-form-group class="mb-3" :invalid-feedback="invalidOldPasswordFeedback" :state="isOldPasswordValid">
          <span class="profile-label">Old Password:</span>
          <b-input class="form-control edit-input" type="password" :state="isOldPasswordValid" v-model.trim="oldPassword" required/>
        </b-form-group>

        <b-form-group class="mb-3" :invalid-feedback="invalidNewPasswordFeedback" :state="isNewPasswordValid">
          <span class="profile-label">New Password:</span>
          <b-input class="form-control edit-input" type="password" :state="isNewPasswordValid" v-model.trim="newPassword" required/>
        </b-form-group>

        <b-form-group class="mb-3" :invalid-feedback="invalidConfirmationPasswordFeedback" :state="isConfirmationPasswordValid">
          <span class="profile-label">Confirm Password:</span>
          <b-input class="form-control edit-input" type="password" :state="isConfirmationPasswordValid" v-model.trim="confirmationPassword" required/>
        </b-form-group>

        <p class="text-danger text-center" v-show="errorMsg">{{ errorMsg }}</p>

        <div class="edit-profile-buttons">
          <div style="margin: auto; margin-top: 4rem">
            <button @click.prevent="onResetPassword" class="btn btn-profile-button btn-edit-profile-reset">Reset</button>
            <button type="submit" class="btn btn-profile-button">Change</button>
          </div>
        </div>

        <div class="btn-edit-profile-div">
          <button @click.prevent="editPassword = false; onResetPassword()" class="btn btn-profile-button">Cancel</button>
        </div>
      </b-form>
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
      address: null,
      newUsername: null,
      newName: null,
      newEmail: null,
      newAddress: null,
      editing: false,
      errorMsg: null,
      editPassword: false,
      oldPassword: null,
      newPassword: null,
      confirmationPassword: null
    }
  },
  created () {
    this.$axios.$get(`/api/repairers/${this.$auth.user.username}`)
      .then((response) => {
        this.username = response.username;
        this.name = response.name;
        this.email = response.email;
        this.address = response.address;
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
        return 'The name must be between [3, 25] characters.'
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
      if (! this.isAddressValid) {
        return false
      }
      return true
    },
    invalidOldPasswordFeedback () {
      if (!this.oldPassword) {
        return null
      }
      let passwordLen = this.oldPassword.length
      if (passwordLen < 3 || passwordLen > 25) {
        return 'The password must be between [3, 25] characters.'
      }
      return ''
    },
    isOldPasswordValid () {
      if (this.invalidOldPasswordFeedback === null) {
        return null
      }
      return this.invalidOldPasswordFeedback === ''
    },
    invalidNewPasswordFeedback () {
      if (!this.newPassword) {
        return null
      }
      let passwordLen = this.newPassword.length
      if (passwordLen < 3 || passwordLen > 25) {
        return 'The password must be between [3, 25] characters.'
      }
      return ''
    },
    isNewPasswordValid () {
      if (this.invalidNewPasswordFeedback === null) {
        return null
      }
      return this.invalidNewPasswordFeedback === ''
    },
    invalidConfirmationPasswordFeedback () {
      if (!this.confirmationPassword) {
        return null
      }
      let passwordLen = this.confirmationPassword.length
      if (passwordLen < 3 || passwordLen > 25) {
        return 'The password must be between [3, 25] characters.'
      }

      if (this.confirmationPassword !== this.newPassword) {
        return 'Passwords don\'t match.'
      }
      return ''
    },
    isConfirmationPasswordValid () {
      if (this.invalidConfirmationPasswordFeedback === null) {
        return null
      }
      return this.invalidConfirmationPasswordFeedback === ''
    },
    isPasswordFormValid () {
      if (! this.isOldPasswordValid) {
        return false
      }
      if (! this.isNewPasswordValid) {
        return false
      }
      if (! this.isConfirmationPasswordValid) {
        return false
      }
      return true
    },
  },
  methods:{
    onReset(){
      this.newName = this.name;
      this.newEmail = this.email;
      this.newAddress = this.address;
    },
    onSubmit(){
      if(this.name === this.newName &&
        this.email === this.newEmail &&
        this.address === this.newAddress){
        this.editing = false
        return
      }

      this.$axios.put(`/api/repairers/${this.$auth.user.username}`, {
        name: this.newName,
        email: this.newEmail,
        address: this.newAddress,
      })
      .then(() => {
        this.name = this.newName
        this.email = this.newEmail
        this.address = this.newAddress
        this.$toast.success(`Repairer ${this.newName} updated!`).goAway(3000)
        this.editing = false
      })
      .catch(({ response: err }) => {
        this.errorMsg = err.data
        this.$toast.error('Sorry, repairer couldn\'t be updated. Ensure you don\'t have any errors').goAway(3000)
      })
    },
    validEmail(email) {
      return /^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/.test(email);
    },
    onResetPassword(){
      this.oldPassword = null;
      this.newPassword = null;
      this.confirmationPassword = null;
    },
    onSubmitPassword(){
      this.$axios.patch(`/api/repairers/${this.$auth.user.username}/password`, {
        oldPassword: this.oldPassword,
        password: this.newPassword,
      })
        .then(() => {
          this.$toast.success(`Password updated!`).goAway(3000)
          this.editPassword = false
        })
        .catch(({ response: err }) => {
          this.errorMsg = err.data
          this.$toast.error('Sorry, password couldn\'t be updated. Ensure you don\'t have any errors').goAway(3000)
        })
    },
  }
}
</script>

<style scoped>

.edit-input{
  width: 65%;
  display: inline;
}

.btn-edit-profile-reset{
  background-color: #eaeaea !important;
}

.edit-profile-buttons{
  display: flex;
  flex-direction: row;
}

.btn-profile-button:hover{
  background-color: red !important;
  color: white !important;
  border: 1px solid red !important;
}

.btn-profile-button{
  border: 1px solid black;
  height: 3rem;
  width: 12rem;
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

@media only screen and (max-width: 1300px) {
  .profile-body, input{
    font-size: 15px;
  }

  .btn-profile-button{
    width: 10rem;
    font-size: 15px;
    height: 2.5rem;
  }

  .profile-body-div{
    width: 75%;
  }
}
</style>
