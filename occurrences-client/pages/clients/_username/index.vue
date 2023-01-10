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

      <div v-else-if="!editing" class="profile-body-div">
        <p class="profile-field"><span class="profile-label">Username:</span> <span> {{this.username}} </span></p>
        <p class="profile-field"><span class="profile-label">Name:</span> {{ this.name }}</p>
        <p class="profile-field"><span class="profile-label">Email:</span> {{this.email}}</p>
        <p class="profile-field"><span class="profile-label">Address:</span> {{this.address}}</p>
        <p class="profile-field"><span class="profile-label">NIF/NIPC:</span> {{this.nif_nipc}}</p>
        <p class="profile-field"><span class="profile-label">Phone Number:</span> {{ this.phoneNumber }}</p>

        <div class="btn-edit-profile-div">
          <button @click.prevent="editing = true;" class="btn btn-edit-profile">Edit</button>

          <a class="btn-delete" @click="showModal = true" style="margin-top: 2rem">Delete account</a>
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

        <b-form-group class="mb-3" :invalid-feedback="invalidNifNipcFeedback" :state="isNifNipcValid">
          <span class="profile-label">NIF/NIPC:</span>
          <b-input class="form-control edit-input"
                   name="nif_nipc"
                   :state="isNifNipcValid"
                   v-model.trim="newNif_nipc"
                   required />
        </b-form-group>

        <b-form-group class="mb-3" :invalid-feedback="invalidPhoneNumberFeedback" :state="isPhoneNumberValid">
          <span class="profile-label">Phone Number:</span>
          <b-input class="form-control edit-input"
                   name="phoneNumber"
                   pattern="[0-9]{9}"
                   ref="phoneNumber"
                   :state="isPhoneNumberValid"
                   v-model.trim="newPhoneNumber"
                   required />
        </b-form-group>

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

    <DeleteModal v-show="showModal" @close-modal="showModal = false" @delete="deleteClient" :message="'You sure you want to delete your account ?'"/>
  </b-container>
</template>

<script>
import DeleteModal from "~/pages/components/DeleteModal.vue"
export default {
  components: {
    DeleteModal
  },
  name: "index.vue",
  data () {
    return {
      username: null,
      name: null,
      email: null,
      nif_nipc: null,
      address: null,
      phoneNumber: null,
      newName: null,
      newEmail: null,
      newNif_nipc: null,
      newAddress: null,
      newPhoneNumber: null,
      editing: false,
      errorMsg: null,
      showModal: false
    }
  },
  created () {
    this.$axios.$get(`/api/clients/${this.$auth.user.username}`)
      .then((response) => {
        this.username = response.username;
        this.name = response.name;
        this.email = response.email;
        this.address = response.address;
        this.phoneNumber = response.phoneNumber.toString();
        this.nif_nipc = response.nif_nipc.toString();
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
    invalidNifNipcFeedback () {
      if (!this.newNif_nipc) {
        return null
      }

      let nifNipcLen = this.newNif_nipc.length
      if (nifNipcLen !== 9) {
        return 'The NIF/NIPC must have 9 digits.'
      }
      if(!this.validNif(this.newNif_nipc)){
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
    invalidPhoneNumberFeedback () {
      if (!this.newPhoneNumber) {
        return null
      }
      let phoneNumberLen = this.newPhoneNumber.length
      if (phoneNumberLen !== 9) {
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
      if (! this.isNameValid) {
        return false
      }
      if (! this.isEmailValid) {
        return false
      }
      if (! this.isAddressValid) {
        return false
      }
      if (! this.isNifNipcValid) {
        return false
      }
      if (! this.isPhoneNumberValid) {
        return false
      }
      return true
    }
  },
  methods: {
    onReset(){
      this.newName = this.name;
      this.newEmail = this.email;
      this.newAddress = this.address;
      this.newNif_nipc = this.nif_nipc;
      this.newPhoneNumber = this.phoneNumber;
    },
    onSubmit(){
      if(this.name === this.newName &&
        this.email === this.newEmail &&
        this.address === this.newAddress &&
        this.nif_nipc === this.newNif_nipc &&
        this.phoneNumber === this.newPhoneNumber){
        this.editing = false
        return
      }

      this.$axios.put(`/api/clients/${this.$auth.user.username}`, {
        name: this.newName,
        email: this.newEmail,
        address: this.newAddress,
        nif_nipc: this.newNif_nipc,
        phoneNumber: this.newPhoneNumber
      })
        .then(() => {
          this.name = this.newName
          this.email = this.newEmail
          this.address = this.newAddress
          this.nif_nipc = this.newNif_nipc
          this.phoneNumber = this.newPhoneNumber
          this.$toast.success(`Client ${this.newName} updated!`).goAway(3000)
          this.editing = false
        })
        .catch(({ response: err }) => {
          this.errorMsg = err.data
          this.$toast.error('Sorry, client couldn\'t be updated. Ensure you don\'t have any errors').goAway(3000)
        })
    },
    validEmail(email) {
      return /^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/.test(email);
    },
    validNif(nif_nipc){
      return /^[1,2,3,5,6,9].[0-9]{7}/.test(nif_nipc);
    },
    deleteClient(){
      this.$axios.$delete(`api/clients/${this.$auth.user.username}`)
        .then(() => {
          this.$auth.logout()
          this.$router.push('/auth/login')
          this.$toast.success(`Account deleted!`).goAway(3000)
        })
    }
  }
}
</script>

<style scoped>

.btn-delete:hover{
  color: #620000 !important;
  text-decoration: underline;
  cursor: pointer;
}

.btn-delete{
  color: #940000 !important;
  text-decoration: underline;
  cursor: pointer;
}

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
</style>
