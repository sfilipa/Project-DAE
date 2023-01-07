<template>
  <div v-if="this.$route.params.insurance">
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

    <!--    Header-->
    <div class="header-info">
      <span><b>{{this.$route.params.insurance.insuranceCompany}} ({{ this.$route.params.code }})</b></span>
      <span class="policy-number-span">Policy number {{this.$route.params.insurance.policyNumber}}</span>
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
        <span class="overall-data-detail">{{this.$route.params.insurance.clientName}}</span>
      </div>
      <hr>
      <div class="overall-data-row">
        <span class="overall-data-label">NIF/NIPC</span>
        <span class="overall-data-detail">{{this.$route.params.insurance.clientNif}}</span>
      </div>
      <hr>
      <div class="overall-data-row">
        <span class="overall-data-label">Initial Date</span>
        <span class="overall-data-detail">{{this.$route.params.insurance.initialDate}}</span>
      </div>
      <hr>
      <div class="overall-data-row">
        <span class="overall-data-label">Valid Until</span>
        <span class="overall-data-detail">{{this.$route.params.insurance.validUntil}}</span>
      </div>
      <hr>
      <div class="overall-data-row">
        <span class="overall-data-label">Insurance Period</span>
        <span class="overall-data-detail">From {{this.$route.params.insurance.initialDate}} To {{this.$route.params.insurance.validUntil}}</span>
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
           @click="ongoingOccurrences = !ongoingOccurrences; reportOccurrence = false; completedOccurrences = false;">
          Ongoing Occurrences</a>
        <a class="btn insurance-occurrences-btn"
           :class="{'insurance-occurrences-btn-active': completedOccurrences}"
           @click="completedOccurrences = !completedOccurrences; reportOccurrence = false; ongoingOccurrences = false;">
          Completed Occurrences</a>
      </div>

      <!--      Report an Occurrence-->
      <b-form @submit.prevent="onSubmit" :disabled="!isFormValid" v-if="reportOccurrence" class="report-an-occurrence">
        <p><b>The Affected Object:</b> &nbsp; {{this.$route.params.insurance.objectInsured}} - {{this.$route.params.insurance.insuredAssetType}}</p>
        <p><b>1. Choose the Coverage Type:</b></p>
        <div class="report-an-occurrence-div">
          <div v-if="this.$route.params.insurance.covers.length==0">
            No Coverages Registered
          </div>
          <div v-else class="items-grid" :invalid-feedback="invalidCoverageTypeFeedback" :state="isCoverageTypeValid">
            <div :state="isCoverageTypeValid" v-for="coverageType in this.$route.params.insurance.covers" class="item-grid-div" :class="{'grid-item-selected': selectedCoverageType == coverageType}"
                 @click="selectedCoverageType = coverageType">
              {{ coverageType.charAt(0).toUpperCase() +
            coverageType.split('_').join(' ').slice(1).toLowerCase() }}
            </div>
          </div>
        </div>

        <p><b>2. What Happened</b></p>
        <div :invalid-feedback="invalidDescriptionFeedback" :state="isDescriptionValid"  class="report-an-occurrence-div">
          <b-textarea :state="isDescriptionValid" class="form-control report-an-occurrence-text" placeholder="Describe here what happened"
                      v-model="description" required></b-textarea>
        </div>

        <p><b>3. Date of the Occurrence</b></p>
        <div class="report-an-occurrence-div">
          <div :invalid-feedback="invalidDateFeedback" :state="isDateValid"  class="report-an-occurrence-div">
            <b-input :state="isDateValid" class="form-control" type="date" required  v-model="date"/>
          </div>
        </div>

        <p><b>4. Documents</b></p>
        <div class="report-an-occurrence-div">
          <div class="report-an-occurrence-div">
            <input type="file" multiple="multiple">
          </div>
        </div>

        <p class="text-danger text-center" v-show="errorMsg">{{ errorMsg }}</p>

        <div style="display: flex;">
          <div class="register-occurrence-btn-div" >
            <button type="submit" class="btn register-occurrence-btn" :disabled="waitingResponse">
              Register Occurrence
            </button>
          </div>
        </div>
      </b-form>

      <!--      Ongoing Occurrences-->
      <div v-if="ongoingOccurrences" class="ongoing-occurrences">
        <div v-if="getOnGoingOccurrences().length==0" class="text-center">
          No occurrences ongoing
        </div>
        <div v-else v-for="occurrence in getOnGoingOccurrences()" >
          <Occurrence :occurrence="occurrence"></Occurrence>
        </div>
      </div>

      <!--      Completed Occurrences-->
      <div v-if="completedOccurrences" class="completed-occurrences">
        <div v-if="getCompletedOccurrences().length==0" class="text-center">
          No occurrences completed
        </div>
        <div v-else v-for="occurrence in getCompletedOccurrences()">
          <Occurrence :occurrence="occurrence"></Occurrence>
        </div>
      </div>
    </div>

    <!--    Documents-->
    <div v-if="DocumentsBtn" class="documents-content">
      <div v-for="document in documents" class="documents-content-list">
        <span>{{ document.name }}</span>
      </div>
    </div>
  </div>

</template>

<script>
import Occurrence from "~/pages/clients/components/Occurrence.vue";
export default {
  components: {
    Occurrence
  },
  name: "index.vue",
  data() {
    return {
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
      errorMsg: null,
      waitingResponse: false
    }
  },
  computed: {
    documents(){ return [
      {
        "name": "Verde"
      }
    ]},
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
        return 'The password must be between [3, 255] characters.'
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
    }
  },
  created () {
    if(!this.$route.params.insurance){
      this.$router.push('/clients/insurances')
    }
    this.$axios.$get(`/api/clients/${this.$auth.user.username}/occurrences`)
      .then((occurrences) => {
        this.occurrences = occurrences
      })
  },
  methods: {
    getOnGoingOccurrences(){
      return this.occurrences.filter(function(oc){return oc.state != 'RESOLVED' && oc.state != 'DISAPPROVED' && oc.state != 'FAILED' })
    },
    getCompletedOccurrences(){
      return this.occurrences.filter(function(oc){return oc.state == 'RESOLVED' || oc.state == 'DISAPPROVED' || oc.state == 'FAILED' })
    },
    onSubmit() {
      this.waitingResponse = true
      if(this.date) {
        const [year, month, day] = this.date.split('-');
        this.date = [month, day, year].join('/');
      }
      this.$axios.$post('/api/occurrences', {
        usernameClient: this.$auth.user.username,
        entryDate: this.date,
        insuranceCode: this.$route.params.code,
        state: 'PENDING',
        description: this.description,
        coverageType: this.selectedCoverageType
      })
        .then(() => {
          this.$router.push('/clients/occurrences')
          this.$toast.success('Your occurrence has been registered!').goAway(3000)
          this.waitingResponse = false
        })
        .catch(({ response: err }) => {
          this.errorMsg = err.data
          this.$toast.error('Occurrence couldn\'t be created, please check the errors').goAway(3000)
          this.waitingResponse = false
        })
    }
  }
}
</script>

<style scoped>

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
  background-color: white;
}

.documents-content{
  padding: 20px;
  display: flex;
  flex-direction: column;
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
