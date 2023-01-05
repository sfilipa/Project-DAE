<template>
  <b-container v-if="this.$auth.user">
    <h3 class="text-center index-header">Welcome to your occurrence management platform</h3>
    <p>&nbsp; Current Occurrences</p>

    <div>
      <div v-if="occurrences.length == 0">
        <span>No Occurrences Registered</span>
      </div>

      <div v-else v-for="occurrence in occurrences" class="all-occurrences">
        <div class="all-occurrences-item">
          <div class="all-occurrences-item-row" style="width: 30%;">
            <p style="font-size: 20px"><b>{{occurrence.objectInsured}} - <span>{{occurrence.insuranceCode}}</span></b></p>
            <p>Occurrence {{ occurrence.id }}</p>
            <p>Repairer: {{occurrence.usernameRepairer==undefined ? "not associated" : occurrence.usernameRepairer}}</p>
            <p>Description: {{ occurrence.description }}</p>
            <p>Entry Date: {{occurrence.entryDate}} &nbsp; Final Date: {{occurrence.finalDate==undefined?"---":occurrence.finalDate}}</p>
          </div>
          <div class="flex-grow-1" style="margin: 0 6%"  v-if="occurrence.state.toLowerCase() == 'pending' && isAssigned(occurrence.id)">
            <p>Appointments: </p>
            <textarea class="form-control" style="margin-bottom: 20px;" placeholder="Enter some thoughts on your decision" v-model="descriptionApprovePending"/>
            <div style="display: flex">
              <button  class="btn btn-approve-occurrence" @click.prevent="disapprove(occurrence.id)">Disapprove</button>
              <button  class="btn btn-approve-occurrence" style="margin-left: auto" @click.prevent="approve(occurrence.id)">Approve</button>
            </div>
          </div>
          <div class="all-occurrences-item-row flex-grow-1" :class="{'all-occurrences-item-last': occurrence.state == 'Approved'}" style="text-align: end;">
            <p class="text-uppercase">{{ occurrence.state }}</p>
            <div v-if="!isAssigned(occurrence.id)">
              <button  class="btn btn-associate-repairers" @click.prevent="assign(occurrence.id)" :disabled="waitingRefresh">Assign</button>
            </div>
            <div v-else>
              <button class="btn btn-associate-repairers" @click.prevent="unassign(occurrence.id)" :disabled="waitingRefresh">Unassign</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </b-container>
</template>

<script>
export default {
  name: "index.vue",
  data () {
    return {
      occurrences: [],
      occurrencesAssigned: [],
      waitingRefresh: false,
      descriptionApprovePending: ""
    }
  },
  created () {
    this.updateOccurrences()

  },
  methods: {
    isAssigned(occurrence_id){
      return this.occurrencesAssigned.map(object => object.id).indexOf(occurrence_id) !== -1
    },
    updateOccurrences(){
      this.waitingRefresh = true
      this.$axios.$get(`/api/occurrences/`)
        .then((occurrences) => {
          this.occurrences = occurrences
          this.$axios.$get(`/api/experts/${this.$auth.user.username}/occurrences/assigned`)
            .then((occurrencesAssigned) => {
              this.occurrencesAssigned = occurrencesAssigned
              this.waitingRefresh = false
            })
        })
    },
    approve(occurence_id)
    {
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/approve`, {
        description: this.descriptionApprovePending
      })
      this.description = "";
      this.updateOccurrences()
    },
    disapprove(occurence_id)
    {
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/disapprove`, {
        description: this.descriptionApprovePending}
      )
      this.description = "";
      this.updateOccurrences()
    },
    assign(occurence_id)
    {
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/assign`)
      this.updateOccurrences()
    },
    unassign(occurence_id)
    {
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/unassign`)
      this.updateOccurrences()
    }
  }
}
</script>

<style scoped>

.all-occurrences-pending{
  display: flex;
  flex-direction: row;
  align-items: center;
}

.appointments-text{
  width: 50%;
  display: inline;
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

.index-header{
  margin-bottom: 3rem;
}

</style>
