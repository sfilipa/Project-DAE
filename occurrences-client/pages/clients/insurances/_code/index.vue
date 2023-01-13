<template>
  <div v-if="this.$auth.user && this.$auth.user.role.toLowerCase() === 'client'">
    <nuxt-link
      class="btn pb-3 pr-5 text-uppercase"
      :to="`/clients/insurances`">
      <div>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-chevron-left" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        &nbsp; My Insurances
      </div>
    </nuxt-link>

    <div v-if="this.insurance === null" class="spinner-div">
      <div class="spinner-border"></div>
    </div>

    <div v-else>
      <!--    Header-->
      <div class="header-info">
        <span><b>{{this.insurance.insuranceCompany}} ({{ this.insurance.code }})</b></span>
        <span class="policy-number-span">Policy number {{this.insurance.policyNumber}}</span>
      </div>

      <!--    Navbar Buttons-->
      <div class="insurance-details-navbar">
        <a class="btn insurance-details-navbar-item"
           :class="{'insurance-details-navbar-item-active': OverallDataBtn}"
           @click="OverallDataBtn = true; OccurrencesBtn = false; DocumentsBtn = false;">
          Overall Data</a>
        <a class="btn insurance-details-navbar-item"
           :class="{'insurance-details-navbar-item-active': OccurrencesBtn}"
           @click="OverallDataBtn = false; OccurrencesBtn = true; DocumentsBtn = false;">
          Occurrences</a>
        <a class="btn insurance-details-navbar-item"
           :class="{'insurance-details-navbar-item-active': DocumentsBtn}"
           @click="OverallDataBtn = false; OccurrencesBtn = false; DocumentsBtn = true;">
          Documents</a>
      </div>

      <!--    Overall Data-->
      <div v-if="OverallDataBtn" class="overall-data">
        <div class="overall-data-row">
          <span class="overall-data-label">Insurance Holder</span>
          <span class="overall-data-detail">{{this.insurance.clientName}}</span>
        </div>
        <hr>
        <div class="overall-data-row">
          <span class="overall-data-label">NIF/NIPC</span>
          <span class="overall-data-detail">{{this.insurance.clientNif}}</span>
        </div>
        <hr>
        <div class="overall-data-row">
          <span class="overall-data-label">Initial Date</span>
          <span class="overall-data-detail">{{this.insurance.initialDate}}</span>
        </div>
        <hr>
        <div class="overall-data-row">
          <span class="overall-data-label">Valid Until</span>
          <span class="overall-data-detail">{{this.insurance.validUntil}}</span>
        </div>
        <hr>
        <div class="overall-data-row">
          <span class="overall-data-label">Insurance Period</span>
          <span class="overall-data-detail">From {{this.insurance.initialDate}} To {{this.insurance.validUntil}}</span>
        </div>
      </div>

      <!--    Occurrences-->
      <div v-if="OccurrencesBtn">
        <div class="insurance-details-content">
          <a class="btn insurance-occurrences-btn"
             :class="{'insurance-occurrences-btn-active': reportOccurrence}"
             @click="reportOccurrence = !reportOccurrence; ongoingOccurrences = false; completedOccurrences = false;">
            Report An Occurrence</a>
          <a class="btn insurance-occurrences-btn"
             :class="{'insurance-occurrences-btn-active': ongoingOccurrences}"
             @click="ongoingOccurrences = !ongoingOccurrences; reportOccurrence = false; completedOccurrences = false; stateToFilter=''; coverageToFilter='';">
            Ongoing Occurrences</a>
          <a class="btn insurance-occurrences-btn"
             :class="{'insurance-occurrences-btn-active': completedOccurrences}"
             @click="completedOccurrences = !completedOccurrences; reportOccurrence = false; ongoingOccurrences = false; stateToFilter=''; coverageToFilter='';">
            Completed Occurrences</a>
        </div>

        <!--      Report an Occurrence-->
        <b-form @submit.prevent="onSubmit" :disabled="!isFormValid" v-if="reportOccurrence" class="report-an-occurrence">
          <p><b>The Affected Object:</b> &nbsp; {{this.insurance.objectInsured}} - {{this.insurance.insuredAssetType}}</p>
          <p><b>1. Choose the Coverage Type:</b></p>
          <div class="report-an-occurrence-div">
            <div v-if="this.insurance.covers.length==0">
              No Coverages Registered
            </div>
            <div v-else class="items-grid" :invalid-feedback="invalidCoverageTypeFeedback" :state="isCoverageTypeValid">
              <div :state="isCoverageTypeValid" v-for="coverageType in this.insurance.covers" class="item-grid-div" :class="{'grid-item-selected': selectedCoverageType == coverageType}"
                   @click="selectedCoverageType = coverageType">
                {{ coverageType.charAt(0).toUpperCase() +
              coverageType.split('_').join(' ').slice(1).toLowerCase() }}
              </div>
            </div>
          </div>

          <p><b>2. What Happened</b></p>
          <div :invalid-feedback="invalidDescriptionFeedback" :state="isDescriptionValid"  class="report-an-occurrence-div">
            <b-textarea :state="isDescriptionValid" class="form-control report-an-occurrence-text" placeholder="Describe here what happened"
                        v-model="description"
                        :disabled="waitingResponse"
                        required></b-textarea>
          </div>

          <p><b>3. Date of the Occurrence</b></p>
          <div class="report-an-occurrence-div">
            <div :invalid-feedback="invalidDateFeedback" :state="isDateValid"  class="report-an-occurrence-div">
              <b-input :state="isDateValid"
                       :disabled="waitingResponse"
                       class="form-control" type="date" required  v-model="date"/>
            </div>
          </div>

          <p><b>4. Documents</b></p>
          <div class="report-an-occurrence-div">
            <div class="report-an-occurrence-div">
              <input type="file" ref="documents" multiple="multiple" @change="inputDocumentsChanged" :disabled="waitingResponse">
            </div>
            <div v-if="documents.length">
              <span class="fw-bold ms-4 me-3">All files:</span>
              <span v-for="document in documents" class="document-name">
                <span>{{ document.name }}</span>
                <button class="btn btn-remove-file" style="height: 31px; width: 31px;" @click.prevent="removeDocument(document)" :disabled="waitingResponse">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-x cross-bi" viewBox="0 0 16 16">
                    <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                  </svg>
                </button>
              </span>
            </div>
          </div>

          <p class="text-danger text-center" v-show="errorMsg">{{ errorMsg }}</p>

          <div style="display: flex;">
            <div class="register-occurrence-btn-div" >
              <button type="submit" class="btn register-occurrence-btn" :disabled="waitingResponse">
                <div style="display: flex">
                    <div class="me-3 ms-3" v-if="waitingResponse">
                      <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                    </div>
                    <div class="text-center" style="width: 100%"><span>Register Occurrence</span></div>

                </div>
              </button>
            </div>
          </div>
        </b-form>

        <!--      Ongoing Occurrences-->
        <div v-if="ongoingOccurrences" class="ongoing-occurrences">
          <div v-if="getOnGoingOccurrences().length==0" class="text-center">
            No occurrences ongoing
          </div>
          <div v-else>
            <div class="filters-div">
              <span class="me-4 ms-4">Filter by State:</span>
              <b-select class="form-select filter-select" v-model="stateToFilter">
                <option value="">Select a State</option>
                <option v-for="state in occurrenceStates.filter(stat => stat !== 'RESOLVED' &&
                                                                        stat !== 'DISAPPROVED' &&
                                                                        stat !== 'FAILED')"
                        :value="state"> {{ state.charAt(0).toUpperCase() + state.split('_').join(' ').slice(1).toLowerCase() }} </option>
              </b-select>

              <span class="me-4 ms-5">Filter by Coverage Type</span>
              <b-select class="form-select filter-select" v-model="coverageToFilter">
                <option value="">Select a Coverage Type</option>
                <option v-for="coverage in this.insurance.covers" :value="coverage"> {{coverage.charAt(0).toUpperCase() + coverage.split('_').join(' ').slice(1).toLowerCase() }} </option>
              </b-select>
            </div>
            <div v-for="occurrence in getOnGoingOccurrences().filter(oc => (stateToFilter.length === 0 || oc.state === stateToFilter) && (coverageToFilter.length === 0 || oc.coverageType === coverageToFilter))" >
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
          </div>
        </div>

        <!--      Completed Occurrences-->
        <div v-if="completedOccurrences" class="completed-occurrences">
          <div v-if="getCompletedOccurrences().length==0" class="text-center">
            No occurrences completed
          </div>
          <div v-else>
            <div class="filters-div">
              <span class="me-4 ms-4">Filter by State:</span>
              <b-select class="form-select filter-select" v-model="stateToFilter">
                <option value="">Select a State</option>
                <option v-for="state in occurrenceStates.filter(stat => stat === 'RESOLVED' ||
                                                                        stat === 'DISAPPROVED' ||
                                                                        stat === 'FAILED')"
                        :value="state"> {{ state.charAt(0).toUpperCase() + state.split('_').join(' ').slice(1).toLowerCase() }} </option>
              </b-select>

              <span class="me-4 ms-5">Filter by Coverage Type:</span>
              <b-select class="form-select filter-select" v-model="coverageToFilter">
                <option value="">Select a Coverage Type</option>
                <option v-for="coverage in this.insurance.covers" :value="coverage"> {{coverage.charAt(0).toUpperCase() + coverage.split('_').join(' ').slice(1).toLowerCase() }} </option>
              </b-select>
            </div>
            <div v-for="occurrence in getCompletedOccurrences().filter(oc => (stateToFilter.length === 0 || oc.state === stateToFilter) && (coverageToFilter.length === 0 || oc.coverageType === coverageToFilter))">
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
          </div>
        </div>
      </div>

      <!--    Documents-->
      <div v-if="DocumentsBtn">
        <div v-if="allDocuments.length === 0" class="text-center" style="margin-top: 2rem">
          <span >No documents registered</span>
        </div>

        <div v-for="occurrence in occurrences">
          <div v-if="hasDocuments(occurrence.id)" class="documents-content">
            <p style="font-size: 20px; color: red"><b>{{occurrence.objectInsured}}
              ({{ occurrence.coverageType.charAt(0).toUpperCase() + occurrence.coverageType.split('_').join(' ').slice(1).toLowerCase() }})
              - <span>{{occurrence.insuranceCode}}</span></b></p>
            <p class="fw-bold" style="margin-bottom: 0">Occurence {{occurrence.id}} - Documents</p>
            <div class="documents-content-list">
              <a @click.prevent="downloadDocument(document)" class="document-link" v-for="document in allDocuments.find(oc => oc.occurrence_id === occurrence.id).documents">
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
      </div>
    </div>

  </div>

  <div v-else>
    <Unauthorized></Unauthorized>
  </div>

</template>

<script>
import Occurrence from "~/pages/clients/components/Occurrence.vue";
import Unauthorized from "@/pages/components/Unauthorized";
export default {
  components: {
    Unauthorized,
    Occurrence
  },
  name: "index.vue",
  data() {
    return {
      insurance: null,
      OverallDataBtn: !this.$route.params.OccurrencesBtn && !this.$route.params.DocumentsBtn,
      OccurrencesBtn:  this.$route.params.OccurrencesBtn,
      DocumentsBtn: this.$route.params.DocumentsBtn,
      reportOccurrence: true,
      ongoingOccurrences: false,
      completedOccurrences: false,
      occurrences: [],
      selectedCoverageType: null,
      description: null,
      date: null,
      expertName: "",
      documents: [],
      errorMsg: null,
      waitingResponse: false,
      allDocuments: [],
      stateToFilter: "",
      coverageToFilter: "",
      occurrenceStates: [],
      entrustedRepairersPerInsurance: [],
      otherRepairersPerInsurance: []

    }
  },
  mounted() {
    this.$axios.get(`https://63a9db1a594f75dc1dc27d9b.mockapi.io/insurances?code=${this.$route.params.code}`)
      .then((response)=>{
        this.insurance = response.data[0]
      })
  },
  computed: {
    invalidCoverageTypeFeedback () {
      if (!this.selectedCoverageType) {
        return null
      }
      return ''
    },
    isCoverageTypeValid () {
      if (this.invalidCoverageTypeFeedback === null) {
        return null
      }
      return this.invalidCoverageTypeFeedback === ''
    },
    invalidDescriptionFeedback () {
      if (!this.description) {
        return null
      }
      let descriptionLen = this.description.length
      if (descriptionLen < 3 || descriptionLen > 255) {
        return 'The description must be between [3, 255] characters.'
      }
      return ''
    },
    isDescriptionValid () {
      if (this.invalidDescriptionFeedback === null) {
        return null
      }
      return this.invalidDescriptionFeedback === ''
    },
    invalidDateFeedback () {
      if (!this.date) {
        return null
      }
      return ''
    },
    isDateValid () {
      if (this.invalidDateFeedback === null) {
        return null
      }
      return this.invalidDateFeedback === ''
    },
    isFormValid () {
      if (! this.isCoverageTypeValid) {
        return false
      }
      if (! this.isDescriptionValid) {
        return false
      }
      if (! this.isDateValid) {
        return false
      }
      return true
    },
  },
  created () {
    this.$axios.$get(`/api/clients/${this.$auth.user.username}/occurrences?limit=1000`)
      .then((occurrences) => {
        if(!this.$route.params.code){
          this.occurrences = occurrences.data
          return
        }

        const codeParams = this.$route.params.code
        this.occurrences = occurrences.data.filter(function(oc){
          return oc.insuranceCode === codeParams
        })

        this.occurrences.forEach((occurrence)=>{
          if(this.occurrenceStates.indexOf(occurrence.state) === -1){
            this.occurrenceStates.push(occurrence.state)
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

  },
  methods: {
    getOnGoingOccurrences(){
      return this.occurrences.filter(function(oc){return oc.state !== 'RESOLVED' &&
                                                          oc.state !== 'DISAPPROVED' &&
                                                          oc.state !== 'FAILED'})
    },
    getCompletedOccurrences(){
      return this.occurrences.filter(function(oc){return (oc.state === 'RESOLVED' ||
                                                          oc.state === 'DISAPPROVED' ||
                                                          oc.state === 'FAILED')})
    },
    onSubmit() {
      if(this.documents == null){
        this.errorMsg = "Please select a coverage type first"
        return
      }

      this.waitingResponse = true
      if(this.date) {
        const [year, month, day] = this.date.split('-');
        this.date = [day, month, year].join('/');
      }
      this.$axios.$post('/api/occurrences', {
        usernameClient: this.$auth.user.username,
        entryDate: this.date,
        insuranceCode: this.$route.params.code,
        state: 'PENDING',
        description: this.description,
        coverageType: this.selectedCoverageType,
      })
        .then((response) => {
          this.$toast.success('Your occurrence has been registered!').goAway(3000)

          // Socket Emit Occurrence Created
          this.$socket.emit('occurrenceCreated');

          if(!this.documents || this.documents.length === 0) {
            this.$router.push('/clients/occurrences')
            this.waitingResponse = false
          }

          // Upload documents
          this.$axios.post(`/api/documents/${response.id}`, this.formData(), {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
            .then(() => {
              this.$router.push('/clients/occurrences')
              this.waitingResponse = false
            })
            .catch(({response: err}) => {
              this.errorMsg = err.data
              if(!this.documents || this.documents.length === 0){
                return
              }
              this.$toast.error('Documents couldn\'t be uploaded').goAway(3000)
              this.waitingResponse = false
            })
        })
        .catch(({ response: err }) => {
          if(err) {
            this.errorMsg = err.data
          }
          this.$toast.error('Occurrence couldn\'t be created, please check the errors').goAway(3000)
          this.waitingResponse = false
        })
    },
    inputDocumentsChanged() {
      this.documents = [...this.$refs.documents.files]
    },
    removeDocument(document){
      const index = this.documents.indexOf(document)
      if(index !== -1)
        this.documents.splice(index,1)
    },
    formData() {
      let formData = new FormData()
      formData.append('username', this.$auth.user.username)
      this.documents.forEach((document)=>{
        formData.append("file",document, document.name)
      })
      return formData
    },
    hasDocuments(occurrence_id){
      return this.allDocuments.map(oc => oc.occurrence_id).indexOf(occurrence_id) !== -1
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

.btn-remove-file:disabled{
  border: 0;
}

.btn-remove-file{
  border: 0
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

.document-link{
  cursor: pointer;
  margin: 0.7rem 0;
  width: 25%;
  position: relative;
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

.cross-bi{
  position: relative;
  top: -0.46rem;
  right: 0.3rem;
}

.document-name:hover{
  background-color: #cbcbcb;
}

.document-name{
  background-color: #e0e0e0;
  padding: 1px 3px 0px 16px;
  border-radius: 25px;
  font-size: 15px;
  margin: 4px 5px;
  display: inline-block;
}

.register-occurrence-btn:hover{
  background-color: red !important;
  color: white !important;
}

.register-occurrence-btn{
  border: 1px solid black;
  height: 3rem;
  width: 16rem;
}

.register-occurrence-btn-div{
  margin-left: auto;
  margin-right: 4%;
}

.report-an-occurrence-text{
  height: 120px;
  padding: 20px;
  margin: auto;
  width: 95%;
}

.grid-item-selected{
  border: 2px solid red;
  color: red !important;
  font-weight: bold;
}

.item-grid-div{
  padding: 20px;
  text-align: center;
  height: 5rem;
  margin: 0 29px;
  border-radius: 2%;
  background-image: linear-gradient(to top left, #f2f2f2, rgba(255, 255, 255, 0.84));
  box-shadow: 0 10px 20px 0 rgba(216, 216, 216, 0.2), 0 6px 10px 0 rgba(0, 0, 0, 0.19);
  cursor: pointer;
  width: 80%;
}

.items-grid{
  display: grid;
  padding: 10px;
  width: auto;
  margin: auto;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
}

.documents-content-list{
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
  max-height: 12rem;
  overflow: auto;
}

.documents-content{
  padding: 20px;
  display: flex;
  flex-direction: column;
  background-color: white;
  margin: 40px 20px;
}

.overall-data-detail{
  font-weight: bold;
  width: 40%;
}

.overall-data-label{
  color: gray;
  width: 40%;
}

.overall-data-row{
  display: flex;
  flex-direction: row;
  margin: 20px;
}

.overall-data{
  background-color: white;
  height: fit-content;
  padding: 20px;
  margin: 0 20px;
}

.completed-occurrences{
  height: fit-content;
  margin: 20px;
}

.ongoing-occurrences{
  height: fit-content;
  margin: 20px;
}

.report-an-occurrence-div{
  padding: 0 20px 20px 20px;
}

.report-an-occurrence{
  background-color: white;
  height: fit-content;
  margin: 20px;
  padding: 40px;
}

.insurance-occurrences-btn-active{
  background-color: red !important;
  color: white !important;
}

.insurance-occurrences-btn{
  background-color: white;
  margin: 10px 20px;
  padding: 20px;
  border-radius: 0px;
  width: 30%;
}

.insurance-details-content{
  display: flex;
  flex-direction: row;
}

.insurance-details-navbar-item-active{
  background-color: red;
  color: white !important;
}

.insurance-details-navbar-item:hover{
  color: darkgray !important;
}

.insurance-details-navbar-item{
  color: white !important;
  margin: 0 20px;
}

.insurance-details-navbar{
  display: flex;
  flex-direction: row;
  align-items: center;
  background-color: #313030;
  padding: 20px;
  margin: 20px;
}

.policy-number-span{
  font-size: 15px;
}

.header-info{
  display: flex;
  flex-direction: column;
  padding: 20px;
}

</style>
