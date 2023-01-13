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
          <div style="display:inline;">
            <span class="me-4 ms-4">Filter by State:</span>
            <b-select class="form-select filter-select" v-model="stateToFilter">
              <option value="">Select a State</option>
              <option v-for="state in occurrenceStates"
                      :value="state"> {{ state.charAt(0).toUpperCase() + state.split('_').join(' ').slice(1).toLowerCase() }} </option>
            </b-select>
          </div>

          <div style="display: inline">
            <span class="me-4 ms-4">Filter by Coverage Type:</span>
            <b-select class="form-select filter-select" v-model="coverageToFilter">
              <option value="">Select a Coverage Type</option>
              <option v-for="coverage in occurrenceCoverages" :value="coverage"> {{coverage.charAt(0).toUpperCase() + coverage.split('_').join(' ').slice(1).toLowerCase() }} </option>
            </b-select>
          </div>
        </div>

        <div v-for="occurrence in occurrences.filter(oc => (stateToFilter.length === 0 || oc.state === stateToFilter) && (coverageToFilter.length === 0 || oc.coverageType === coverageToFilter))">
          <Occurrence :occurrence="occurrence"
                      :documents="hasDocuments(occurrence.id) ? allDocuments.find(oc => oc.occurrence_id === occurrence.id).documents : []"
                      :isAssigned="isAssigned(occurrence.id)"
                      :waitingRefresh="waitingRefresh"
                      @updateOccurrences="updateOccurrences"></Occurrence>
        </div>

        <Paginate :page-count="pageCount" :current-page="currentPage" @updateCurrentPage="updateCurrentPage"></Paginate>
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
import Paginate from "@/pages/components/Paginate";
export default {
  mounted() {
    this.$socket.on('update', () => {
      this.updateOccurrences()
      this.$toast.info('Occurrences as been updated!').goAway(3000)
    }),
    this.$socket.on('repairerAssignedNeedsApproval', () => {
      this.updateOccurrences()
      this.$toast.info('Repairer needs Approval!').goAway(3000)
  })
  },
  components: {
    Paginate,
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
      allDocuments: [],
      currentPage: 1,
      totalCount: 1,
      perPage: 10,
      pageCount: 1
    }
  },
  watch: {
    currentPage(newPage) {
      this.updateOccurrences(newPage)
    }
  },
  created () {
    this.updateOccurrences(1)
  },
  methods: {
    updateCurrentPage(currentPage){
      if(currentPage!=null) {
        this.currentPage = currentPage
      }
    },
    isAssigned(occurrence_id){
      return this.occurrencesAssigned.map(object => object.id).indexOf(occurrence_id) !== -1
    },
    updateOccurrences(currentPage){
      this.waitingRefresh = true
      this.occurrenceStates = []
      this.occurrenceCoverages = []

      if(!currentPage){
        currentPage = 1
      }

      this.$axios.$get(`/api/occurrences?page=${currentPage}`)
        .then((occurrences) => {
          this.totalCount = occurrences.metadata.totalCount
          this.perPage = occurrences.metadata.count
          this.pageCount = occurrences.metadata.pageCount
          this.occurrences = occurrences.data
          this.$axios.$get(`/api/experts/${this.$auth.user.username}/occurrences/assigned`)
            .then((occurrencesAssigned) => {
              this.occurrencesAssigned = occurrencesAssigned.data
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


.index-header{
  margin-bottom: 3rem;
}

.filter-select{
  width: 27%;
  display: inline-block;
}

.filters-div{
  background-color: #313030;
  padding: 14px;
  color: white;
}

@media only screen and (max-width: 1300px) {
  .filter-select{
    width: 24%;
  }
}

@media only screen and (max-width: 1100px) {
  .filter-select{
    width: 22%;
  }
}

@media only screen and (max-width: 1000px) {
  .filters-div{
    font-size: 14px;
  }

  .filters-div div{
    display: block !important;
  }

  .filters-div span{
    width: 25% !important;
    display: inline-block;
    margin: 1rem 0;
  }

  .filter-select{
    width: 60%;
    font-size: 14px;
  }
}

</style>
