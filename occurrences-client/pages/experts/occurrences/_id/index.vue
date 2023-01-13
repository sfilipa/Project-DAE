<template>
<!--  Login-->
  <div v-if="this.$auth.user === null || !this.$auth.user.role">
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

<!--  Occurrence Details-->
  <b-container v-else-if="this.role === 'expert' || this.$auth.user.role.toLowerCase()==='expert'">
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
        <h5 class="text-center index-header" style="margin-bottom: 3rem">{{this.occurrence.state.split('_').join(' ')}}</h5>
        <div class="occurrence-body-div">
          <p class="occurrence-field"><span class="occurrence-label">Object Insured:</span> <span> {{this.occurrence.objectInsured}} </span></p>
          <p class="occurrence-field"><span class="occurrence-label">Coverage Type:</span>  <span> {{ this.occurrence.coverageType }} </span> </p>
          <p class="occurrence-field" style="height: auto; display: flex">
            <span class="occurrence-label" style="width: 35%">Description:</span>  <span style="white-space: pre; overflow: auto"> {{this.occurrence.description}} </span>
          </p>
          <p class="occurrence-field"><span class="occurrence-label">Entry Date:</span> {{this.occurrence.entryDate}}</p>
          <p class="occurrence-field"><span class="occurrence-label">Final Date:</span> {{this.occurrence.finalDate ? this.occurrence.finalDate:'---'}}</p>

          <div v-if="this.documents.length !== 0" style="margin-bottom: 2rem">
            <hr>
            <div class="repair-row" style="flex-wrap: wrap">
              <span class="fw-bold me-4">Documents: </span>
              <a @click.prevent="downloadDocument(document)" class="document-link" v-for="document in this.documents">
                {{ document.filename.length > 25 ? document.filename.substring(0, 20)+"..." : document.filename }}
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
                  <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/>
                  <path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"/>
                </svg>
                <div class="tooltiptext" v-if="document.filename.length > 20">{{document.filename}}</div>
              </a>
            </div>
          </div>

          <b-form @submit.prevent="onSubmitDescription" :disabled="!isFormValid"
                  v-if="occurrence.state.toLowerCase() === 'pending' && isAssigned">
            <p class="fw-bold">Appointments on Occurrence: </p>
            <b-form-group :invalid-feedback="invalidDescriptionFeedback" :state="isDescriptionValid">
              <b-textarea :state="isDescriptionValid" class="form-control" style="margin-bottom: 20px;" placeholder="Enter some thoughts on your decision" v-model="descriptionApprovePending" required/>
            </b-form-group>

            <div style="display: flex">
              <button type="submit" value="disapprove" class="btn btn-approve-occurrence" @click="disapprove(occurrence.id)">Disapprove</button>
              <button type="submit" value="approve" class="btn btn-approve-occurrence" @click="approve(occurrence.id)" style="margin-left: auto" >Approve</button>
            </div>
          </b-form>

          <b-form @submit.prevent="onSubmitDescription" :disabled="!isFormValid"
                  v-if="occurrence.state.toLowerCase() === 'waiting_for_approval_of_repairer_by_expert' && isAssigned">
            <p class="fw-bold">Appointments on Repairer: </p>
            <b-form-group :invalid-feedback="invalidDescriptionFeedback" :state="isDescriptionValid">
              <b-textarea :state="isDescriptionValid" class="form-control" style="margin-bottom: 20px;" placeholder="Enter some thoughts on your decision" v-model="descriptionApprovePending" required/>
            </b-form-group>

            <div style="display: flex">
              <button type="submit" value="disapprove" class="btn btn-approve-occurrence" @click="rejectRepairer(occurrence.id)">Reject</button>
              <button type="submit" value="approve" class="btn btn-approve-occurrence" @click="acceptRepairer(occurrence.id)" style="margin-left: auto" >Accept</button>
            </div>
          </b-form>

          <div style="text-align: center; margin: 2rem 0;">
            <div v-if="!isAssigned && !hasParticipated &&
                    occurrence.state!=='REPAIRER_WAITING_LIST' &&
                    occurrence.state!=='ACTIVE' &&
                    occurrence.state!=='FAILED' &&
                    occurrence.state!=='RESOLVED' &&
                    occurrence.state!=='DISAPPROVED' &&
                    occurrence.insuranceCompanyName === this.company_username">
              <button  class="btn btn-associate-repairers" @click="assign(occurrence.id)" :disabled="waitingRefresh">Assign</button>
            </div>
            <div v-else-if="!hasParticipated &&
                        occurrence.state!=='REPAIRER_WAITING_LIST' &&
                        occurrence.state!=='ACTIVE' &&
                        occurrence.state!=='FAILED' &&
                        occurrence.state!=='RESOLVED' &&
                        occurrence.state!=='DISAPPROVED' &&
                        occurrence.insuranceCompanyName === this.company_username">
              <button class="btn btn-associate-repairers" @click="unassign(occurrence.id)" :disabled="waitingRefresh">Unassign</button>
            </div>
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
      occurrence: null,
      role: null,
      waitingRefresh: false,
      descriptionApprovePending: null,
      approveOrDisapprove: "",
      isAssigned: null,
      hasParticipated: false,
      company_username: null,
      documents: []
    }
  },
  mounted() {
    this.$axios.get(`api/occurrences/${this.$route.params.id}`)
      .then((response)=>{
        this.occurrence = response.data
        this.isAssigned = this.occurrence.expertsDTO.map(exp => exp.username).indexOf(this.$auth.user.username) !== -1
        if(this.isAssigned){
          this.hasParticipated = true
        }

        this.$axios.$get(`api/documents/${this.occurrence.id}/exists`)
          .then((response)=> {
            if (response) {
              this.documents = []
              this.$axios.$get(`api/documents/${this.occurrence.id}`)
                .then((response) => {
                  this.documents = response
                })
            }
          })
      })
    this.$axios.$get(`/api/experts/${this.$auth.user.username}`)
      .then((response) => {
        this.company_username = response.company_username;
      })


  },
  computed: {
    invalidDescriptionFeedback () {
      if (!this.descriptionApprovePending) {
        return null
      }
      let descriptionApprovePendingLen = this.descriptionApprovePending.length
      if (descriptionApprovePendingLen < 3 || descriptionApprovePendingLen > 15) {
        return 'The description must be between [3, 15] characters.'
      }
      return ''
    },
    isDescriptionValid () {
      if (this.invalidDescriptionFeedback === null) {
        return null
      }
      return this.invalidDescriptionFeedback === ''
    },
    isFormValid () {
      if (!this.isDescriptionValid) {
        return false
      }
      return true
    }
  },
  methods: {
    onSubmit() {
      this.$auth.loginWith('local', {
        data: {
          username: this.username,
          password: this.password
        }
      })
      .then(() => {
        if(this.$auth.user.role.toLowerCase() === 'expert') {
          this.role = this.$auth.user.role.toLowerCase()
          this.$toast.success('You are logged in!').goAway(3000)
          this.$router.push(`/experts/occurrences/${this.occurrence.id}`)
        }else{
          this.$auth.logout()
          this.onReset()
          this.$toast.error('Only experts allowed').goAway(3000)
        }
      })
      .catch(({ response: err }) => {
        this.errorMsg = err.data
        this.onReset()
        this.$toast.error('Only experts allowed').goAway(3000)
      })
    },
    onReset() {
      this.username = null
      this.password = null
    },
    onSubmitDescription() {
      console.log('teste')
    },
    approve(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/approve`, {
        description: 'http://localhost:3000/clients/occurrences/'+this.occurrence.id+'&'+this.descriptionApprovePending
      }).then(()=> {
        this.descriptionApprovePending = "";
        this.$axios.get(`api/occurrences/${this.$route.params.id}`)
          .then((response)=>{
            this.occurrence = response.data
            this.isAssigned = this.occurrence.expertsDTO.map(exp => exp.username).indexOf(this.$auth.user.username) !== -1
            this.hasParticipated = true
          })
        this.$socket.emit('occurrenceApproved', this.occurrence.usernameClient);
      })

    },
    disapprove(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/disapprove`, {
        description: 'http://localhost:3000/clients/occurrences/'+this.occurrence.id+'&'+this.descriptionApprovePending}
      ).then(()=> {
        this.descriptionApprovePending = "";
        this.$axios.get(`api/occurrences/${this.$route.params.id}`)
          .then((response)=>{
            this.occurrence = response.data
            this.isAssigned = this.occurrence.expertsDTO.map(exp => exp.username).indexOf(this.$auth.user.username) !== -1
            this.hasParticipated = true
          })
        this.$socket.emit('occurrenceDisapproved', this.occurrence.usernameClient);
      })
    },
    acceptRepairer(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/acceptRepairer`, {
        description: 'http://localhost:3000/repairers/occurrences/'+this.occurrence.id+'&'+this.descriptionApprovePending
      }).then(()=> {
        this.descriptionApprovePending = "";
        this.$axios.get(`api/occurrences/${this.$route.params.id}`)
          .then((response)=>{
            this.occurrence = response.data
            this.isAssigned = this.occurrence.expertsDTO.map(exp => exp.username).indexOf(this.$auth.user.username) !== -1
            this.hasParticipated = true
          })
        const users = {
          usernameClient: this.occurrence.usernameClient,
          usernameRepairer: this.occurrence.usernameRepairer
        }
        this.$socket.emit('occurrenceRepairerApproved', users);
      })

    },
    rejectRepairer(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/rejectRepairer`, {
        description: 'http://localhost:3000/clients/occurrences/'+this.occurrence.id+'&'+this.descriptionApprovePending}
      ).then(()=> {
        this.descriptionApprovePending = "";
        this.$axios.get(`api/occurrences/${this.$route.params.id}`)
          .then((response)=>{
            this.occurrence = response.data
            this.isAssigned = this.occurrence.expertsDTO.map(exp => exp.username).indexOf(this.$auth.user.username) !== -1
            this.hasParticipated = true
          })
        this.$socket.emit('occurrenceRepairerDisapproved', this.occurrence.usernameClient);
      })
    },
    assign(occurence_id)
    {
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/assign`)
        .then(()=> {
          this.$axios.get(`api/occurrences/${this.$route.params.id}`)
            .then((response)=>{
              this.occurrence = response.data
              this.isAssigned = true
            })
        })
    },
    unassign(occurence_id)
    {
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/unassign`)
        .then(()=> {
          this.$axios.get(`api/occurrences/${this.$route.params.id}`)
            .then((response)=>{
              this.occurrence = response.data
              this.isAssigned = false
            })
        })
    },
    downloadDocument(documentToDownload){
      this.$axios.$get(`api/documents/download/${documentToDownload.id}`, { responseType:
          'arraybuffer'})
        .then(file => {
          const url = window.URL.createObjectURL(new Blob([file]))
          const link = document.createElement('a')
          link.href = url
          link.setAttribute('download', documentToDownload.filename)
          document.body.appendChild(link)
          link.click()
        })
    }
  }
}
</script>

<style scoped>


.document-link{
  cursor: pointer;
  width: fit-content;
  position: relative;
  margin-right: 2rem;
  margin-top: 0.1rem;
}

.document-link .tooltiptext {
  visibility: hidden;
  background-color: black;
  color: white !important;
  border-radius: 6px;
  padding: 5px 0;
  position: absolute;
  z-index: 1;
  top: 1.8rem;
  font-size: 14px;
  width: 20rem;
  text-align: center;
  opacity: 0;
  transition: opacity 0.5s;
}

.document-link .tooltiptext::after {
  content: "";
  position: absolute;
  bottom: 100%;
  left: 50%;
  margin-left: -5rem;
  border-width: 5px;
  border-style: solid;
  border-color: transparent transparent black transparent;
}

.document-link:hover{
  color: red !important;
}

.document-link:hover .tooltiptext {
  visibility: visible !important;
  opacity: 1;
}

.btn-approve-occurrence:hover{
  background-color: red !important;
  color: white !important;
}

.btn-approve-occurrence{
  border: 1px solid black;
  width: 45%;
  height: 2.5rem;
  align-self: self-end;
  background-color: white;
}

.btn-associate-repairers:hover{
  background-color: red !important;
  color: white !important;
}

.btn-associate-repairers{
  border: 1px solid black;
  width: fit-content;
  height: 3rem;
  align-self: self-end;
  background-color: white;
}

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
  width: 55%;
  margin-bottom: 2rem;
}

.occurrence-body{
  display: flex;
  flex-direction: column;
  align-items: center;
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
