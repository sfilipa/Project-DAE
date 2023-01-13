<template>
  <div class="occurrences-div">
    <p style="font-size: 20px; margin-bottom: 1rem; color: red"><b>{{occurrence.objectInsured}}
      ({{ occurrence.coverageType.charAt(0).toUpperCase() + occurrence.coverageType.split('_').join(' ').slice(1).toLowerCase() }})
      - <span>{{occurrence.insuranceCode}}</span></b></p>
    <div class="occurrences-item" >
      <div class="occurrences-item-row" style="width: 30%;">
        <span><b>Occurrence</b> {{ occurrence.id }}</span>
        <span><b>Repairer:</b> {{occurrence.usernameRepairer==undefined ? "not associated" : occurrence.usernameRepairer}}</span>
        <p style="white-space: pre;"><b>Description:</b> <br>{{ occurrence.description }}</p>
      </div>

      <div class="occurrences-item-row" style="align-self: flex-start; margin: 0 2rem;">
        <p style="margin-bottom: 0;"><b>Entry Date:</b> {{occurrence.entryDate}} &nbsp; </p>
        <p><b>Final Date:</b> {{occurrence.finalDate==undefined?"---":occurrence.finalDate}}</p>
      </div>

      <div class="occurrences-item-row" style="align-self: flex-start; margin-left: 2rem;">
        <div v-if="occurrence.expertsDTO.length !== 0">
          <p style="margin-bottom: 0"><b>Experts Associated:</b></p>
          <p v-for="expert in occurrence.expertsDTO" style="margin-bottom: 0px">-- {{expert.name}}</p>
        </div>
        <div v-else>
          No experts associated
        </div>
      </div>

      <div class="occurrences-item-row flex-grow-1" style="text-align: end; width: 10rem;">
        <p class="text-uppercase">{{ occurrence.state.split('_').join(' ') }}</p>
        <div v-if="occurrence.state==='REPAIRER_WAITING_LIST' ||
                        occurrence.state==='WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT'">
          <button class="btn btn-associate-repairers" @click.prevent="unassignRepairer(occurrence.id)" :disabled="waitingResponse">Unassign Repairer</button>
        </div>
      </div>
    </div>

    <div>
      <nuxt-link :to="{name: `clients-occurrences-id`, params: {id: occurrence.id}}" class="btn btn-check-details">Check Details</nuxt-link>
    </div>

    <div v-if="this.documents.length !== 0">
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

    <div v-if="occurrence.state.toUpperCase() == 'APPROVED'">
      <hr>
      <div class="repair-row">
          <div class="repair-column">
            <p >Select an entrusted Repair Service</p>
            <p style="margin-bottom: 0;">Or solicitate a Repair Service (needs approval)
              <input :disabled="otherRepairers == null || otherRepairers.length==0" v-model="checked" class="checkbox form-check-input" type="checkbox" id="other" name="other" value="other"></p>
          </div>

          <div class="repair-column flex-grow-1" style="padding: 0 5%">
            <span v-if="this.entrustedRepairers == null" style="margin-left: 2px">---</span>
            <span v-else-if="this.entrustedRepairers.length==0" style="margin-left: 2px">No entrusted repairers available</span>
            <select v-else class="form-select mb-2"  v-model="insuranceRepairer" @focus="errorMsg = null" :disabled="checked">
              <option disabled value="">Select Entrusted Repairer</option>
              <option v-for="repairerService in this.entrustedRepairers">{{repairerService}}</option>
            </select>
            <div class="repair-row">
              <span v-if="this.otherRepairers == null" style="margin-left: 2px" >---</span>
              <span v-else-if="this.otherRepairers.length==0" style="margin-left: 2px">No other repairers available</span>
              <select v-else class="form-select mb-2" v-model="solicitedRepairer" @focus="errorMsg = null" :disabled="!checked">
                <option disabled value="">Select Solicited Repairer</option>
                <option v-for="repairerService in this.otherRepairers">{{repairerService.username}}</option>
              </select>
            </div>
          </div>

          <div>
            <button class="btn btn-associate-repairers select-repairer-error-msg"  @click.prevent="associateRepairer" :disabled="waitingResponse">Assign Repairer</button>
          </div>
      </div>
      <p class="text-danger text-center select-repairer-error-msg" v-show="errorMsg">{{ errorMsg }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "Occurrence",
  props: ['occurrence', 'documents', 'otherRepairers', 'entrustedRepairers'],
  data(){
    return {
      checked: false,
      insuranceRepairer: "",
      solicitedRepairer: "",
      waitingResponse: false,
      errorMsg: null,
    }
  },
  methods: {
    associateRepairer(){
      this.waitingResponse = true
      //Enviar mail ao repairer com o link api/occurrences/{id}
      if(!this.checked){
        if(this.insuranceRepairer === ""){
          this.errorMsg = "Select an entrusted repairer first"
          this.waitingResponse = false
          return
        }
        //Without need for approval
        this.$axios.$patch(`api/clients/${this.$auth.user.username}/occurrences/${this.occurrence.id}/${this.insuranceRepairer}/assign`, {
          description: 'http://localhost:3000/repairers/occurrences/'+this.occurrence.id
        })
          .then(()=>{
            this.$router.push('/clients/insurances')
            this.$toast.success('Request made!').goAway(3000)
            this.waitingResponse = false

            // Emit event to repairer
            this.$socket.emit('repairerAssignedWithoutNeedForApproval', this.insuranceRepairer);
          })
          .catch(()=>{
            this.waitingResponse = false
          })
      }else{
        if(this.solicitedRepairer === ""){
          this.errorMsg = "Select a solicitated repairer first"
          this.waitingResponse = false
          return
        }
        //With need of approval - for expert
        this.$axios.$patch(`api/clients/${this.$auth.user.username}/occurrences/${this.occurrence.id}/${this.solicitedRepairer}/assign`, {
          description: 'http://localhost:3000/experts/occurrences/'+this.occurrence.id
        })
          .then(()=>{
            this.$router.push('/clients/insurances')
            this.$toast.success('Request made!').goAway(3000)
            this.waitingResponse = false

            // Emit event to expert
            this.$socket.emit('repairerAssignedNeedsApproval');
          })
          .catch(()=>{
            this.waitingResponse = false
          })
      }
    },
    unassignRepairer(){
      this.waitingResponse = true
      let descriptionURl = ''
      if(this.occurrence.state === 'REPAIRER_WAITING_LIST'){
        descriptionURl = 'http://localhost:3000/repairers/occurrences/'+this.occurrence.id
      }else if(this.occurrence.state === 'WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT'){
        descriptionURl = 'http://localhost:3000/experts/occurrences/'+this.occurrence.id
      }
      this.$axios.$patch(`api/clients/${this.$auth.user.username}/occurrences/${this.occurrence.id}/${this.occurrence.usernameRepairer}/unassign`, {
        description: descriptionURl
      })
        .then(()=>{
          this.$router.push('/clients/insurances')
          this.$toast.success('Request made!').goAway(3000)
          this.waitingResponse = false

          // Emit event to repairer
          this.$socket.emit('repairerAssignedWithoutNeedForApproval', this.insuranceRepairer);
        })
        .catch(()=>{
          this.waitingResponse = false
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

.select-repairer-error-msg{
  margin-bottom: 0 !important;
  margin-top: 1rem !important;
}

.checkbox{
  margin-left: 5px;
  width: 20px;
  height: 20px;
}

.repair-column{
  display: flex;
  flex-direction: column;
}

.repair-row{
  display: flex;
  flex-direction: row;
  align-items: center;
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

.occurrences-item-last{
  text-align: end;
  align-self: flex-end;
  margin-bottom: 1.5%;
}

.occurrences-item-row{
  display: flex;
  flex-direction: column;
}

.occurrences-div{
  background-color: white;
  height: fit-content;
  padding: 20px;
  margin: 30px 0;
}

.occurrences-item{
  display: flex;
  flex-direction: row;
  align-items: center;
}

</style>
