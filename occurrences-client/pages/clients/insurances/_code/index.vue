<template>
  <div>
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

    <div class="header-info">
      <span><b>{{this.$route.params.insurance.insuranceCompany}} ({{ this.$route.params.code }})</b></span>
      <span class="policy-number-span">Policy number {{this.$route.params.insurance.policyNumber}}</span>
    </div>

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
<!--      <hr>-->
<!--      <div class="overall-data-row">-->
<!--        <span class="overall-data-label">Payment Type</span>-->
<!--        <span class="overall-data-detail">Bank Charge</span>-->
<!--      </div>-->
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
      <div v-if="reportOccurrence" class="report-an-occurrence">
        <p><b>The Affected Object:</b> &nbsp; {{this.$route.params.insurance.objectInsured}} - {{this.$route.params.insurance.insuredAssetType}}</p>
        <p><b>1. Choose the Coverage Type:</b></p>
        <div class="report-an-occurrence-div">
          <div v-if="this.$route.params.insurance.covers.length==0">
            No Coverages Registered
          </div>
          <div v-else class="items-grid">
            <div v-for="coverageType in this.$route.params.insurance.covers" class="item-grid-div" :class="{'grid-item-selected': selectedCoverageType == coverageType}"
                 @click="selectedCoverageType = coverageType">
              {{ coverageType.charAt(0).toUpperCase() +
            coverageType.split('_').join(' ').slice(1).toLowerCase() }}
            </div>
          </div>
        </div>

        <p><b>1. What Happened</b></p>
        <div class="report-an-occurrence-div">
          <textarea class="form-control report-an-occurrence-text" placeholder="Describe here what happened" v-model="description"></textarea>
        </div>

        <p><b>2. Date of the Occurrence</b></p>
        <div class="report-an-occurrence-div">
          <div class="report-an-occurrence-div">
            <input class="form-control" placeholder="Enter the date" v-model="date">
          </div>
        </div>

        <p><b>3. Documents</b></p>
        <div class="report-an-occurrence-div">
          <div class="report-an-occurrence-div">
            <input type="file" multiple="multiple">
          </div>
        </div>

        <div style="display: flex;">
          <div class="register-occurrence-btn-div" >
            <button @click.prevent="register" class="btn register-occurrence-btn">
              Register Occurrence
            </button>
          </div>
        </div>
      </div>

      <!--      Ongoing Occurrences-->
      <div v-if="ongoingOccurrences" class="ongoing-occurrences">
        <div v-if="ongoingOccurrencesArray.length==0">
          No occurrences ongoing
        </div>
        <div v-else v-for="occurrence in ongoingOccurrencesArray" class="ongoing-occurrences-item">
          <div class="ongoing-occurrences-item-row" style="width: 30%;">
            <p style="font-size: 20px"><b>{{occurrence.affectedObject}} - {{occurrence.insuredAssetType}}</b></p>
            <p>Occurrence {{ occurrence.code }}</p>
            <p>Insurance: {{occurrence.insurance}}</p>
            <p>Expert Associated: {{occurrence.expert}}</p>
          </div>

          <div class="ongoing-occurrences-item-row" style="align-self: flex-end;">
            <p>Date: {{occurrence.date}}</p>
            <p>Description: {{ occurrence.description }}</p>
          </div>

          <div class="ongoing-occurrences-item-row flex-grow-1" :class="{'ongoing-occurrences-item-last': occurrence.state == 'Approved'}" style="text-align: end;">
            <p class="text-uppercase">{{ occurrence.state }}</p>
            <button v-if="occurrence.state == 'Approved'" class="btn btn-associate-repairers" @click.prevent="associateRepairer">Associate Repairer Service</button>
          </div>
        </div>
      </div>

      <!--      Completed Occurrences-->
      <div v-if="completedOccurrences" class="completed-occurrences">
        <div v-if="completedOccurrencesArray.length==0">
          No occurrences completed
        </div>
        <div v-else v-for="occurrence in completedOccurrencesArray" class="ongoing-occurrences-item">
          <div class="ongoing-occurrences-item-row" style="width: 30%;">
            <p style="font-size: 20px"><b>{{occurrence.affectedObject}} - {{occurrence.insuredAssetType}}</b></p>
            <p>Occurrence {{ occurrence.code }}</p>
            <p>Insurance: {{occurrence.insurance}}</p>
            <p>Expert Associated: {{occurrence.expert}}</p>
          </div>

          <div class="ongoing-occurrences-item-row" style="align-self: flex-end;">
            <p>Date: {{occurrence.date}}</p>
            <p>Description: {{ occurrence.description }}</p>
          </div>

          <div class="ongoing-occurrences-item-row flex-grow-1" :class="{'ongoing-occurrences-item-last': occurrence.state == 'Approved'}" style="text-align: end;">
            <p class="text-uppercase">{{ occurrence.state }}</p>
          </div>
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
export default {
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
      selectedCoverageType: "",
      description: "",
      date: "",
      expertName: "",
    }
  },
  computed: {
    documents(){ return [
      {
        "name": "Verde"
      }
    ]},
    ongoingOccurrencesArray(){
      return [
        {
          "code": "01",
          "insuredAssetType": "Car",
          "affectedObject": "Car Laguna",
          "date": "27/12/2022",
          "client": "Ana Martins",
          "state": "Pending",
          "expert": "José Areia",
          "description": "Car crash in rua nº10 xxedjoejojde",
          "insurance": this.$route.params.name
        },
        {
          "code": "02",
          "insuredAssetType": "Car",
          "affectedObject": "Car Laguna",
          "date": "20/12/2022",
          "client": "Ana Martins",
          "state": "Approved",
          "expert": "José Areia",
          "description": "Car crash in rua nº11 aqnisnqion",
          "insurance": this.$route.params.name
        }
      ]
    },
    completedOccurrencesArray(){
      return [
        {
          "code": "00",
          "insuredAssetType": "Car",
          "affectedObject": "Car Laguna",
          "date": "15/12/2022",
          "client": "Ana Martins",
          "state": "Completed",
          "expert": "José Areia",
          "description": "Car crash in rua nº10 xxedjidwjdjdojde",
          "insurance": this.$route.params.name
        }
      ]
    }
  },
  created () {
    // this.$axios.$get('/api/occurrences')
    //   .then((occurrences) => {
    //     this.occurrences = occurrences
    //   })
    // this.$axios.$get('http://localhost:8080/occurrences/api/students')
  },
  methods: {
    register() {
      this.$axios.$post('/api/occurrences', {
        client: "auth client - not done",
        date: this.date,
        insuredAssetType: this.selectedCoverageType,
        state: 'Pending',
        description: this.description,
        insurance: this.$route.params.name,
        expert: this.expertName,
      })
        .then(() => {
          this.$router.push('/insurances')
        })
        .catch((error) => {
          this.errorMsg = error.response.data
        })
    },
    associateRepairer(){
      //Enviar mail ao repairer com o link api/occurrences/{id}
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
