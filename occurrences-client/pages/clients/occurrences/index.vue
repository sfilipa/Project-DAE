<template>
  <div v-if="this.$auth.user && this.$auth.user.role.toLowerCase() === 'client'">
    <nuxt-link
      class="btn pb-3 pr-5 text-uppercase"
      :to="`/`">
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

    <div v-if="occurrences.length == 0">
      <span>No Occurrences Registered</span>
    </div>

    <div v-else>
      <div class="filters-div">
        <div style="display: inline">
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

      <div v-for="occurrence in occurrences.filter(oc => (stateToFilter.length === 0 || oc.state === stateToFilter) && (coverageToFilter.length === 0 || oc.coverageType === coverageToFilter))" >
        <Occurrence :occurrence="occurrence"
                    :documents="hasDocuments(occurrence.id) ? allDocuments.find(oc => oc.occurrence_id === occurrence.id).documents : []"
                    :entrustedRepairers="
                    entrustedRepairersPerInsurance.map(obj => obj.insurance).indexOf(occurrence.insuranceCompanyName) !==-1 ?
                    entrustedRepairersPerInsurance.find(obj => obj.insurance === occurrence.insuranceCompanyName).repairers :
                      []"
                    :otherRepairers="
                    otherRepairersPerInsurance.map(obj => obj.insurance).indexOf(occurrence.insuranceCompanyName) !==-1 ?
                    otherRepairersPerInsurance.find(obj => obj.insurance === occurrence.insuranceCompanyName).repairers :
                      []"></Occurrence>
      </div>

      <Paginate :page-count="pageCount"
                :current-page="currentPage"
                :active-limit="activeLimit"
                @updateLimit="updateLimit"
                @updateCurrentPage="updateCurrentPage"></Paginate>

    </div>
  </div>

  <div v-else>
    <Unauthorized></Unauthorized>
  </div>
</template>
<script>
import Occurrence from "~/pages/clients/components/Occurrence.vue";
import Unauthorized from "@/pages/components/Unauthorized";
import Paginate from "@/pages/components/Paginate";
export default {
  mounted() {
    this.$socket.on('occurrenceApproved', () => {
      this.fetchOccurrences()
      this.$toast.success('Occurrences as been Approved!').goAway(3000)
    }),
    this.$socket.on('occurrenceDisapproved', () => {
      this.fetchOccurrences()
      this.$toast.error('Occurrences as been Disapproved!').goAway(3000)
    }),
    this.$socket.on('repairerStartedOccurrence', () => {
      this.fetchOccurrences()
      this.$toast.info('Occurrences as Started by Repairer!').goAway(3000)
    }),
    this.$socket.on('repairerFailedOccurrence', () => {
      this.fetchOccurrences()
      this.$toast.error('Occurrences as been Failed by Repairer!').goAway(3000)
    }),
    this.$socket.on('repairerFinishedOccurrence', () => {
      this.fetchOccurrences()
      this.$toast.success('Occurrences as been Finished by Repairer!').goAway(3000)
    })
  },
  components: {
    Paginate,
    Unauthorized,
    Occurrence
  },
  data () {
    return {
      occurrences: [],
      stateToFilter: "",
      coverageToFilter: "",
      occurrenceStates: [],
      occurrenceCoverages: [],
      allDocuments: [],
      currentPage: 1,
      totalCount: 1,
      perPage: 10,
      pageCount: 1,
      activeLimit: 10,
      otherRepairersPerInsurance: [],
      entrustedRepairersPerInsurance: []
    }
  },
  watch: {
    currentPage(newPage) {
      this.fetchOccurrences(newPage)
    },
    activeLimit() {
      this.fetchOccurrences(null)
    }
  },
  created () {
    this.fetchOccurrences(1)
  },
  methods: {
    updateLimit(newLimit){
      this.activeLimit = newLimit
    },
    updateCurrentPage(currentPage){
      if(currentPage!=null) {
        this.currentPage = currentPage
      }
    },
    hasDocuments(occurrence_id){
      return this.allDocuments.map(oc => oc.occurrence_id).indexOf(occurrence_id) !== -1
    },
    fetchOccurrences(currentPage) {
      if(!currentPage){
        currentPage = 1
        this.currentPage = 1
      }

      this.$axios.$get(`/api/clients/${this.$auth.user.username}/occurrences?limit=${this.activeLimit}&page=${currentPage}`)
      .then((occurrences) => {
        this.totalCount = occurrences.metadata.totalCount
        this.perPage = occurrences.metadata.count
        this.pageCount = occurrences.metadata.pageCount
        this.occurrences = occurrences.data

        this.allDocuments = []
        this.entrustedRepairersPerInsurance = []
        this.otherRepairersPerInsurance = []

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

          if(occurrence.state.toUpperCase() == 'APPROVED') {
            this.$axios.$get(`api/mock/insuranceCompanies/name/${occurrence.insuranceCompanyName}/repairers`)
              .then((entrustedRepairers) => {
                if (this.entrustedRepairersPerInsurance.map(obj => obj.insurance).indexOf(occurrence.insuranceCompanyName) === -1) {
                  this.entrustedRepairersPerInsurance.push(
                    {
                      insurance: occurrence.insuranceCompanyName,
                      repairers: entrustedRepairers
                    }
                  )
                }

                this.$axios.$get(`api/repairers`)
                  .then((repairers) => {

                    if (this.otherRepairersPerInsurance.map(obj => obj.insurance).indexOf(occurrence.insuranceCompanyName) === -1) {
                      this.otherRepairersPerInsurance.push(
                        {
                          insurance: occurrence.insuranceCompanyName,
                          repairers: repairers.filter(function (rep) {
                            return !entrustedRepairers.includes(rep.username)
                          })
                        }
                      )
                    }

                  })
              })
          }
        })
      })
    }
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
