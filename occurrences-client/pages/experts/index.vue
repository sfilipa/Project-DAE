<template>
  <b-container v-if="this.$auth.user">
    <h3 class="text-center index-header">Welcome to your occurrence management platform</h3>
    <p>&nbsp; Current Occurrences</p>

    <div>
      <div v-if="occurrences.length == 0">
        <span>No Occurrences Registered</span>
      </div>

      <div v-else v-for="occurrence in occurrences" class="ongoing-occurrences-item">
        <div class="ongoing-occurrences-item-row" style="width: 30%;">
          <p style="font-size: 20px"><b>{{occurrence.objectInsured}} - <span>{{occurrence.insuranceCode}}</span></b></p>
          <p>Occurrence {{ occurrence.id }}</p>
          <p>Repairer: {{occurrence.usernameRepairer==undefined ? "not associated" : occurrence.usernameRepairer}}</p>
        </div>

        <div class="ongoing-occurrences-item-row" style="align-self: flex-end;">
          <p>Entry Date: {{occurrence.entryDate}} &nbsp; Final Date: {{occurrence.finalDate==undefined?"---":occurrence.finalDate}}</p>
          <p>Description: {{ occurrence.description }}</p>
          <!--        <p>Documents: <span v-for="document in occurrence.documents"> {{ document.filename }};</span></p>-->
        </div>

        <div class="ongoing-occurrences-item-row flex-grow-1" :class="{'ongoing-occurrences-item-last': occurrence.state == 'Approved'}" style="text-align: end;">
          <p class="text-uppercase">{{ occurrence.state }}</p>
          <div v-if="occurrence.state.toLowerCase() == 'pending'">
            <button  class="btn btn-associate-repairers" @click.prevent="disapprove">Disapprove</button>
            <button  class="btn btn-associate-repairers" @click.prevent="approve">Approve</button>
          </div>
        </div>
        <button v-if="" class="btn btn-associate-repairers" @click.prevent="assign">Assign</button>
        <button  class="btn btn-associate-repairers" @click.prevent="unassign">Unassign</button>
      </div>
    </div>
  </b-container>
</template>

<script>
export default {
  name: "index.vue",
  data () {
    return {
      fields: ['name', 'actions'], //nomes do DTOs
      // insurances: [],
      occurrences: []
    }
  },
  created () {
    this.$axios.$get(`/api/occurrences/`)
      .then((occurrences) => {
        this.occurrences = occurrences
      })
  },
  methods: {
    approve(occurence_code)
    {
      this.$axios.$patch(`/api/experts/${this.auth.user.username}/occurrences/${occurence_code}/approve`)
    },
    disapprove(occurence_code)
    {
      this.$axios.$patch(`/api/experts/${this.auth.user.username}/occurrences/${occurence_code}/disapprove`)
    }
  }
}
</script>

<style scoped>

.ongoing-occurrences-item-last{
  text-align: end;
  align-self: flex-end;
  margin-bottom: 1.5%;
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

.ongoing-occurrences-item-row{
  display: flex;
  flex-direction: column;
}

.ongoing-occurrences-item{
  background-color: white;
  height: fit-content;
  padding: 20px;
  margin: 30px 0;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.index-header{
  margin-bottom: 3rem;
}

</style>
