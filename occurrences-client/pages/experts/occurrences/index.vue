<template>
  <div>
    <nuxt-link
      class="btn pb-3 pr-5 text-uppercase"
      :to="`/experts`">
      <div>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-chevron-left" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        &nbsp; Back to HomePage
      </div>
    </nuxt-link>

    <div style="padding: 10px; margin-bottom: 20px;">
      <h4><b>My Occurrences</b></h4>
    </div>

    <div v-if="assignedOccurrences == null" class="spinner-div">
      <div class="spinner-border"></div>
    </div>

    <div v-else-if="assignedOccurrences.length == 0" class="text-center">
      <span>No occurrences assigned yet</span>
    </div>

    <div v-else>
      <div class="filters-div">
        <span class="me-4 ms-4">Filter by State:</span>
        <b-select class="form-select filter-select" v-model="stateToFilter">
          <option value="">Select a State</option>
          <option v-for="state in occurrenceStates"
                  :value="state"> {{ state.charAt(0).toUpperCase() + state.split('_').join(' ').slice(1).toLowerCase() }} </option>
        </b-select>

        <span class="me-4 ms-5">Filter by Coverage Type</span>
        <b-select class="form-select filter-select" v-model="coverageToFilter">
          <option value="">Select a Coverage Type</option>
          <option v-for="coverage in occurrenceCoverages" :value="coverage"> {{coverage.charAt(0).toUpperCase() + coverage.split('_').join(' ').slice(1).toLowerCase() }} </option>
        </b-select>
      </div>


      <div v-for="occurrence in assignedOccurrences.filter(oc => (stateToFilter.length === 0 || oc.state === stateToFilter) && (coverageToFilter.length === 0 || oc.coverageType === coverageToFilter))">
        <Occurrence :occurrence="occurrence" :isAssigned="true" :waitingRefresh="waitingRefresh" @updateOccurrences="updateOccurrences"></Occurrence>
      </div>
    </div>


  </div>
</template>
<script>
import Occurrence from "~/pages/experts/components/Occurrence.vue";
export default {
  components: {
    Occurrence
  },
  data () {
    return {
      assignedOccurrences: null,
      waitingRefresh: false,
      stateToFilter: "",
      coverageToFilter: "",
      occurrenceStates: [],
      occurrenceCoverages: []
    }
  },
  created () {
    this.updateOccurrences()
  },
  methods: {
    updateOccurrences(){
      this.waitingRefresh = true
      this.occurrenceStates = []
      this.occurrenceCoverages = []
      this.$axios.$get(`/api/experts/${this.$auth.user.username}/occurrences/assigned`)
        .then((assignedOccurrences) => {
          this.assignedOccurrences = assignedOccurrences
          this.waitingRefresh = false

          this.assignedOccurrences.forEach(occurrence => {
            if(this.occurrenceStates.indexOf(occurrence.state) === -1){
              this.occurrenceStates.push(occurrence.state)
            }
            if(this.occurrenceCoverages.indexOf(occurrence.coverageType) === -1){
              this.occurrenceCoverages.push(occurrence.coverageType)
            }
          })
        })
    },
  }
}
</script>
<style scoped>
.filter-select{
  width: 27%;
  display: inline-block;
}

.filters-div{
  background-color: #313030;
  padding: 14px;
  color: white;
}

</style>
