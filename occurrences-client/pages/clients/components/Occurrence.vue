<template>
  <div class="occurrences-div">
    <p style="font-size: 20px"><b>{{occurrence.objectInsured}}
      ({{ occurrence.coverageType.charAt(0).toUpperCase() + occurrence.coverageType.split('_').join(' ').slice(1).toLowerCase() }})
      - <span>{{occurrence.insuranceCode}}</span></b></p>
    <div class="occurrences-item" >
      <div class="occurrences-item-row" style="width: 30%;">
        <p>Occurrence {{ occurrence.id }}</p>
        <p>Repairer: {{occurrence.usernameRepairer==undefined ? "not associated" : occurrence.usernameRepairer}}</p>
      </div>

      <div class="occurrences-item-row" style="align-self: flex-end;">
        <p>Entry Date: {{occurrence.entryDate}} &nbsp; Final Date: {{occurrence.finalDate==undefined?"---":occurrence.finalDate}}</p>
        <p>Description: {{ occurrence.description }}</p>
        <!--        <p>Documents: <span v-for="document in occurrence.documents"> {{ document.filename }};</span></p>-->
      </div>

      <div class="occurrences-item-row flex-grow-1" :class="{'occurrences-item-last': occurrence.state == 'Approved'}" style="text-align: end;">
        <p class="text-uppercase">{{ occurrence.state }}</p>
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
            <span v-if="entrustedRepairers == null" style="margin-left: 2px">---</span>
            <span v-else-if="entrustedRepairers.length==0" style="margin-left: 2px">No entrusted repairers available</span>
            <select v-else class="form-select mb-2"  v-model="insuranceRepairer" @focus="errorMsg = null">
              <option disabled value="">Select Entrusted Repairer</option>
              <option v-for="repairerService in entrustedRepairers">{{repairerService}}</option>
            </select>
            <div class="repair-row">
              <span v-if="otherRepairers == null" style="margin-left: 2px" >---</span>
              <span v-else-if="otherRepairers.length==0" style="margin-left: 2px">No other repairers available</span>
              <select v-else class="form-select mb-2" v-model="solicitedRepairer" @focus="errorMsg = null">
                <option disabled value="">Select Solicited Repairer</option>
                <option v-for="repairerService in otherRepairers">{{repairerService}}</option>
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
  props: ['occurrence'],
  data(){
    return {
      entrustedRepairers: null,
      otherRepairers: null,
      checked: false,
      insuranceRepairer: "",
      solicitedRepairer: "",
      waitingResponse: false,
      errorMsg: null
    }
  },
  created () {
    if(this.occurrence.state.toUpperCase() == 'APPROVED') {
      this.$axios.$get(`api/mock/insuranceCompanies/name/${this.occurrence.insuranceCompanyName}/repairers`)
        .then((entrustedRepairers) => {
          this.entrustedRepairers = []
          this.entrustedRepairers = entrustedRepairers
          this.$axios.$get(`api/repairers`)
            .then((repairers) => {
              this.otherRepairers = []
              this.otherRepairers = repairers.filter(function (rep) {
                return !entrustedRepairers.includes(rep.username)
              })
            })
        })
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
        //Without need for approval - send mail to repairer
        this.$axios.$patch(`api/clients/${this.$auth.user.username}/occurrences/${this.occurrence.id}/${this.insuranceRepairer}/assign`)
          .then(()=>{
            this.$router.push('/clients/insurances')
            this.$toast.success('Request made!').goAway(3000)
            this.waitingResponse = false
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
        this.$axios.$patch(`api/clients/${this.$auth.user.username}/occurrences/${this.occurrence}/${this.solicitedRepairer}/assign`)
          .then(()=>{
            this.$router.push('/clients/insurances')
            this.$toast.success('Request made!').goAway(3000)
            this.waitingResponse = false
          })
          .catch(()=>{
            this.waitingResponse = false
          })
      }
    },
  }
}
</script>

<style scoped>

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
