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
              <input :disabled="otherRepairers.length==0" class="checkbox form-check-input" type="checkbox" id="other" name="other" value="Other"></p>
          </div>

          <div class="repair-column flex-grow-1" style="padding: 0 5%">
            <span v-if="entrustedRepairers.length==0" style="margin-left: 2px">No other repairers available</span>
            <select v-else class="form-select mb-2">
              <option v-for="repairerService in entrustedRepairers">{{repairerService}}</option>
            </select>
            <div class="repair-row">
              <span v-if="otherRepairers.length==0" style="margin-left: 2px">No other repairers available</span>
              <select class="form-select mb-2" v-else>
                <option v-for="repairerService in otherRepairers">{{repairerService}}</option>
              </select>
            </div>
          </div>
          <div>
            <button class="btn btn-associate-repairers"  @click.prevent="associateRepairer">Associate Repairer</button>
          </div>

      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Occurrence",
  props: ['occurrence'],
  data(){
    return {
      entrustedRepairers: [],
      otherRepairers: []
    }
  },
  created () {
    this.$axios.$get(`api/mock/insuranceCompanies/name/${this.occurrence.insuranceCompanyName}/repairers`)
      .then((entrustedRepairers) => {
        this.entrustedRepairers = entrustedRepairers
        this.$axios.$get(`api/repairers`)
          .then((repairers) => {
            this.otherRepairers = repairers.filter(function(rep){return !this.entrustedRepairers.includes(rep)})
          })
      })
  },
  methods: {
    associateRepairer(){
      //Enviar mail ao repairer com o link api/occurrences/{id}
    },
  }
}
</script>

<style scoped>


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
