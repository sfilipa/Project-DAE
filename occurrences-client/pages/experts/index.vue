<template>
  <b-container v-if="this.$auth.user && this.$auth.user.role.toLowerCase() === 'expert'">
    <h3 class="text-center index-header">Welcome to your occurrence management platform</h3>
    <p>&nbsp; Current Occurrences</p>

    <div>
      <div v-if="occurrences == null" class="spinner-div">
        <div class="spinner-border"></div>
      </div>

      <div v-else-if="occurrences.length == 0" class="text-center">
        <span>No occurrences registered yet</span>
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

        <div v-for="occurrence in occurrences.filter(oc => (stateToFilter.length === 0 || oc.state === stateToFilter) && (coverageToFilter.length === 0 || oc.coverageType === coverageToFilter))" >
          <Occurrence :occurrence="occurrence"
                      :documents="hasDocuments(occurrence.id) ? allDocuments.find(oc => oc.occurrence_id === occurrence.id).documents : []"
                      :isAssigned="isAssigned(occurrence.id)"
                      :waitingRefresh="waitingRefresh"
                      @updateOccurrences="updateOccurrences"></Occurrence>
        </div>
      </div>
    </div>
  </b-container>

  <div v-else>
    <Unauthorized></Unauthorized>
  </div>
</template>

<script>
import Occurrence from "~/pages/experts/components/Occurrence.vue";
import Unauthorized from "@/pages/components/Unauthorized";
export default {
  mounted() {
    this.$socket.on('update', () => {
      this.updateOccurrences()
      this.$toast.info('Occurrences as been updated!').goAway(3000)
  })},
  components: {
    Unauthorized,
    Occurrence
  },
  data () {
    return {
      occurrences: null,
      occurrencesAssigned: [],
      waitingRefresh: false,
      stateToFilter: "",
      coverageToFilter: "",
      occurrenceStates: [],
      occurrenceCoverages: [],
      allDocuments: []
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
      this.occurrenceStates = []
      this.occurrenceCoverages = []
      this.$axios.$get(`/api/occurrences/`)
        .then((occurrences) => {
          this.occurrences = occurrences
          this.$axios.$get(`/api/experts/${this.$auth.user.username}/occurrences/assigned`)
            .then((occurrencesAssigned) => {
              this.occurrencesAssigned = occurrencesAssigned
              this.waitingRefresh = false
            })
          this.occurrences.forEach(occurrence => {
            if(this.occurrenceStates.indexOf(occurrence.state) === -1){
              this.occurrenceStates.push(occurrence.state)
            }

            if(this.occurrenceCoverages.indexOf(occurrence.coverageType) === -1){
              this.occurrenceCoverages.push(occurrence.coverageType)
            }

            this.$axios.$get(`api/documents/${occurrence.id}/exists`)
              .then((response)=> {
                if (response) {
                  this.allDocuments = []
                  this.$axios.$get(`api/documents/${occurrence.id}`)
                    .then((response) => {
                      this.allDocuments.push(
                        {
                          occurrence_id: occurrence.id,
                          documents: response
                        }
                      )
                    })
                }
              })
          })
        })
    },
    hasDocuments(occurrence_id){
      return this.allDocuments.map(oc => oc.occurrence_id).indexOf(occurrence_id) !== -1
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


.index-header{
  margin-bottom: 3rem;
}

</style>
