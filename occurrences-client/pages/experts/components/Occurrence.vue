<template>
  <div class="all-occurrences">
    <p style="font-size: 20px; color: red"><b>{{occurrence.objectInsured}}
      ({{ occurrence.coverageType.charAt(0).toUpperCase() + occurrence.coverageType.split('_').join(' ').slice(1).toLowerCase() }})
      - <span>{{occurrence.insuranceCompanyName}} ({{occurrence.insuranceCode}})</span></b></p>
    <div class="all-occurrences-item">
      <div class="all-occurrences-item-row" style="width: 30%;">
        <p><b>Occurrence {{ occurrence.id }} - Client {{occurrence.usernameClient}}</b></p>
        <p><b>Repairer:</b> {{occurrence.usernameRepairer==undefined ? "not associated" : occurrence.usernameRepairer}}</p>
        <p style="white-space: pre;"><b>Description:</b> <br>{{ occurrence.description }}</p>
        <p style="margin-bottom: 0;"><b>Entry Date:</b> {{occurrence.entryDate}} &nbsp; </p>
        <p><b>Final Date:</b> {{occurrence.finalDate==undefined?"---":occurrence.finalDate}}</p>
      </div>

      <b-form @submit.prevent="onSubmit" :disabled="!isFormValid" class="flex-grow-1" style="margin: 0 6%"
              v-if="occurrence.state.toLowerCase() === 'pending' && isAssigned">
        <p>Appointments on Occurrence: </p>
        <b-form-group :invalid-feedback="invalidDescriptionFeedback" :state="isDescriptionValid">
          <b-textarea :state="isDescriptionValid" class="form-control" style="margin-bottom: 20px;" placeholder="Enter some thoughts on your decision" v-model="descriptionApprovePending" required/>
        </b-form-group>

        <div style="display: flex">
          <button type="submit" value="disapprove" class="btn btn-approve-occurrence" @click="disapprove(occurrence.id)">Disapprove</button>
          <button type="submit" value="approve" class="btn btn-approve-occurrence" @click="approve(occurrence.id)" style="margin-left: auto" >Approve</button>
        </div>
      </b-form>

      <b-form @submit.prevent="onSubmit" :disabled="!isFormValid" class="flex-grow-1" style="margin: 0 6%"
              v-if="occurrence.state.toLowerCase() === 'waiting_for_approval_of_repairer_by_expert' && isAssigned">
        <p>Appointments on Repairer: </p>
        <b-form-group :invalid-feedback="invalidDescriptionFeedback" :state="isDescriptionValid">
          <b-textarea :state="isDescriptionValid" class="form-control" style="margin-bottom: 20px;" placeholder="Enter some thoughts on your decision" v-model="descriptionApprovePending" required/>
        </b-form-group>

        <div style="display: flex">
          <button type="submit" value="disapprove" class="btn btn-approve-occurrence" @click="rejectRepairer(occurrence.id)">Reject</button>
          <button type="submit" value="approve" class="btn btn-approve-occurrence" @click="acceptRepairer(occurrence.id)" style="margin-left: auto" >Accept</button>
        </div>
      </b-form>

      <div class="all-occurrences-item-row flex-grow-1" :class="{'all-occurrences-item-last': occurrence.state == 'Approved'}" style="text-align: end;">
        <p class="text-uppercase" style="width: 13rem; margin-left: auto;">{{ occurrence.state.split('_').join(' ') }}</p>
        <div v-if="!isAssigned &&
                    occurrence.state!=='REPAIRER_WAITING_LIST' &&
                    occurrence.state!=='ACTIVE' &&
                    occurrence.state!=='FAILED' &&
                    occurrence.state!=='RESOLVED' &&
                    occurrence.state!=='DISAPPROVED' &&
                    occurrence.insuranceCompanyName === this.company_username">
          <button  class="btn btn-associate-repairers" @click.prevent="assign(occurrence.id)" :disabled="waitingRefresh">Assign</button>
        </div>
        <div v-else-if="!isAssigned &&
                        occurrence.state!=='REPAIRER_WAITING_LIST' &&
                        occurrence.state!=='ACTIVE' &&
                        occurrence.state!=='FAILED' &&
                        occurrence.state!=='RESOLVED' &&
                        occurrence.state!=='DISAPPROVED' &&
                        occurrence.insuranceCompanyName === this.company_username">
          <button class="btn btn-associate-repairers" @click.prevent="unassign(occurrence.id)" :disabled="waitingRefresh">Unassign</button>
        </div>
      </div>
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
  props: ['occurrence', 'isAssigned', 'waitingRefresh', 'documents'],
  emits: ['updateOccurrences'],
  data(){
    return {
      descriptionApprovePending: null,
      approveOrDisapprove: "",
      company_username: null,
    }
  },
  created() {
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
        return 'The username must be between [3, 15] characters.'
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
      console.log('teste')
    },
    approve(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/approve`, {
        description: this.descriptionApprovePending
      }).then(()=> {
        this.descriptionApprovePending = "";
        this.$emit('updateOccurrences')
        this.$socket.emit('occurrenceApproved', this.occurrence.usernameClient);
      })

    },
    disapprove(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/disapprove`, {
        description: this.descriptionApprovePending}
      ).then(()=> {
        this.descriptionApprovePending = "";
        this.$emit('updateOccurrences')
        this.$socket.emit('occurrenceDisapproved', this.occurrence.usernameClient);
      })
    },
    acceptRepairer(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/acceptRepairer`, {
        description: this.descriptionApprovePending
      }).then(()=> {
        this.descriptionApprovePending = "";
        this.$emit('updateOccurrences')
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
        description: this.descriptionApprovePending}
      ).then(()=> {
        this.descriptionApprovePending = "";
        this.$emit('updateOccurrences')
        this.$socket.emit('occurrenceRepairerDisapproved', this.occurrence.usernameClient);
      })
    },
    assign(occurence_id)
    {
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/assign`)
        .then(()=> {
          this.$emit('updateOccurrences')
        })
    },
    unassign(occurence_id)
    {
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/unassign`)
        .then(()=> {
          this.$emit('updateOccurrences')
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


.all-occurrences-item-last{
  text-align: end;
  align-self: flex-end;
  margin-bottom: 1.5%;
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
