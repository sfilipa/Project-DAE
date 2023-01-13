<template>
  <div class="all-occurrences">
    <p style="font-size: 20px; color: red"><b>{{occurrence.objectInsured}}
      ({{ occurrence.coverageType.charAt(0).toUpperCase() + occurrence.coverageType.split('_').join(' ').slice(1).toLowerCase() }})
      - <span>{{occurrence.insuranceCompanyName}} ({{occurrence.insuranceCode}})</span></b></p>
    <div class="all-occurrences-item">
      <div class="all-occurrences-item-row" style="width: 30%;">
        <p><b>Occurrence {{ occurrence.id }} - Client {{occurrence.usernameClient}}</b></p>
        <p><b>Repairer:</b> {{occurrence.usernameRepairer==undefined ? "not associated" : occurrence.usernameRepairer}}</p>
        <p style="white-space: pre;"><b>Description:</b> <span style="overflow: auto; width: inherit; display: inherit;">{{ occurrence.description }}</span></p>
        <p style="margin-bottom: 0;"><b>Entry Date:</b> {{occurrence.entryDate}} &nbsp; </p>
        <p><b>Final Date:</b> {{occurrence.finalDate==undefined?"---":occurrence.finalDate}}</p>
      </div>

      <b-form @submit.prevent="onSubmit" :disabled="!isFormValid" class="flex-grow-1" style="margin: 0 6%; align-self: start"  v-if="occurrence.state === 'ACTIVE' && this.isAssigned()">
        <p class="fw-bold">Appointments: </p>
        <b-form-group :invalid-feedback="invalidDescriptionFeedback" :state="isDescriptionValid">
          <b-textarea :state="isDescriptionValid" class="form-control" style="margin-bottom: 20px;" placeholder="Enter some thoughts on your decision" v-model="descriptionApprovePending" required/>
        </b-form-group>

        <div style="display: flex">
          <button type="submit" value="fail" class="btn btn-active-occurrence" @click="fail(occurrence.id)">Fail</button>
          <button type="submit" value="finish" class="btn btn-active-occurrence" @click="finish(occurrence.id)" style="margin-left: auto" >Finish</button>
        </div>
      </b-form>

      <div class="all-occurrences-item-row flex-grow-1" :class="{'all-occurrences-item-last': occurrence.state == 'Approved'}" style="text-align: end;">
        <p class="text-uppercase"  style="width: 13rem; margin-left: auto;">{{ occurrence.state.split('_').join(' ') }}</p>
            <div v-if="this.isAssigned() &&
                        occurrence.state==='REPAIRER_WAITING_LIST'">
              <button  class="btn btn-repairer-button" @click.prevent="start(occurrence.id)" :disabled="waitingRefresh">Start</button>
            </div>
      </div>
    </div>

    <div>
      <nuxt-link :to="{name: `repairers-occurrences-id`, params: {id: occurrence.id}}" class="btn btn-check-details">Check Details</nuxt-link>
    </div>

    <div v-if="documents.length !== 0">
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
  </div>
</template>

<script>
export default {
  name: "Occurrence",
  props: ['occurrence', 'waitingRefresh', 'documents', 'currentPage'],
  emits: ['updateOccurrences'],
  data(){
    return {
      approveOrDisapprove: "",
      descriptionApprovePending: null,
    }
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
    isAssigned(){
      return this.occurrence.usernameRepairer === this.$auth.user.username
    },
    onSubmit() {
      console.log('teste')
    },
    start(occurence_id)
    {
      this.$axios.$patch(`/api/repairers/${this.$auth.user.username}/occurrences/${occurence_id}/start`, {
        description: 'http://localhost:3000/clients/occurrences/'+this.occurrence.id
      })
        .then(()=> {
          this.$toast.success('Occurrence started!').goAway(3000)
          this.$emit('updateOccurrences',  this.currentPage)
          this.$socket.emit('repairerStartedOccurrence', this.occurrence.usernameClient);
      })
    },
    fail(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }

      this.$axios.$patch(`/api/repairers/${this.$auth.user.username}/occurrences/${occurence_id}/fail`, {
        description: 'http://localhost:3000/clients/occurrences/'+this.occurrence.id+'&'+this.descriptionApprovePending
      })
        .then(()=> {
          this.$toast.success('Occurrence failed!').goAway(3000)
          this.$emit('updateOccurrences', this.currentPage)
          this.$socket.emit('repairerFailedOccurrence', this.occurrence.usernameClient);
        })
    },
    finish(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }

      this.$axios.$patch(`/api/repairers/${this.$auth.user.username}/occurrences/${occurence_id}/finish`, {
        description: 'http://localhost:3000/clients/occurrences/'+this.occurrence.id+'&'+this.descriptionApprovePending
      })
        .then(()=> {
          this.$toast.success('Occurrence finished!').goAway(3000)
          this.$emit('updateOccurrences',  this.currentPage)
          this.$socket.emit('repairerFinishedOccurrence', this.occurrence.usernameClient);
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

.btn-check-details:hover{
  border: 1px solid red;
  color: white !important;
  background-color: red !important;
}

.btn-check-details{
  border: 1px solid black;
  height: 2.5rem;
}

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

.all-occurrences-item-last{
  text-align: end;
  align-self: flex-end;
  margin-bottom: 1.5%;
}

.btn-active-occurrence:hover{
  background-color: red !important;
  color: white !important;
}

.btn-active-occurrence{
  border: 1px solid black;
  width: 45%;
  height: 2.5rem;
  align-self: self-end;
}

.btn-repairer-button:hover{
  background-color: red !important;
  color: white !important;
}

.btn-repairer-button{
  border: 1px solid black;
  height: 3rem;
  width: 10rem;
}

.all-occurrences-item-row{
  display: flex;
  flex-direction: column;
}

.all-occurrences-item{
  display: flex;
  flex-direction: row;
  align-items: center;
}

.all-occurrences{
  background-color: white;
  height: fit-content;
  padding: 20px;
  margin: 30px 0;
}

</style>
