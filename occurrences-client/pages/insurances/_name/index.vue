<template>
  <div>
    <nuxt-link
      class="btn pb-3 pr-5 text-uppercase"
      :to="`/insurances`">
      <div>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-chevron-left" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        &nbsp; My Insurances
      </div>
    </nuxt-link>

    <div class="header-info">
        <span><b>Insurance Name</b></span>
        <span class="policy-number-span">Policy number --n</span>
    </div>

    <div class="insurance-details-navbar">
      <a class="btn insurance-details-navbar-item"
         @click="OverallDataBtn = true; OccurrencesBtn = false; DocumentsBtn = false;">
        Overall Data</a>
      <a class="btn insurance-details-navbar-item"
         @click="OverallDataBtn = false; OccurrencesBtn = true; DocumentsBtn = false;">
        Occurrences</a>
      <a class="btn insurance-details-navbar-item"
         @click="OverallDataBtn = false; OccurrencesBtn = false; DocumentsBtn = true;">
        Documents</a>
    </div>

    <div v-if="OverallDataBtn" class="overall-data">
      <div class="overall-data-row">
        <span class="overall-data-label">Insurance Holder</span>
        <span class="overall-data-detail">Ana Martins</span>
      </div>
      <hr>
      <div class="overall-data-row">
        <span class="overall-data-label">Address</span>
        <span class="overall-data-detail">Rua X nº 11 anina swnonw</span>
      </div>
      <hr>
      <div class="overall-data-row">
        <span class="overall-data-label">Initial Date</span>
        <span class="overall-data-detail">27-12-2022</span>
      </div>
      <hr>
      <div class="overall-data-row">
        <span class="overall-data-label">Renewal Date</span>
        <span class="overall-data-detail">10-01-2023</span>
      </div>
      <hr>
      <div class="overall-data-row">
        <span class="overall-data-label">Insurance Period</span>
        <span class="overall-data-detail">From 27-12-2022 To 10-01-2023</span>
      </div>
      <hr>
      <div class="overall-data-row">
        <span class="overall-data-label">Payment Type</span>
        <span class="overall-data-detail">Bank Charge</span>
      </div>
    </div>

    <div v-if="OccurrencesBtn">
      <div class="insurance-details-content">
        <a class="btn insurance-occurrences-btn"
           @click="reportOccurrence = true; ongoingOccurrences = false; completedOccurrences = false;">
          Report An Occurrence</a>
        <a class="btn insurance-occurrences-btn"
           @click="ongoingOccurrences = true; reportOccurrence = false; completedOccurrences = false;">
          Ongoing Occurrences</a>
        <a class="btn insurance-occurrences-btn"
           @click="completedOccurrences = true; reportOccurrence = false; ongoingOccurrences = false;">
          Completed Occurrences</a>
      </div>

      <div v-if="reportOccurrence" class="report-an-occurrence">

      </div>

      <div v-if="ongoingOccurrences" class="ongoing-occurrences">

      </div>

      <div v-if="completedOccurrences" class="completed-occurrences">

      </div>
    </div>

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
      OverallDataBtn: true,
      OccurrencesBtn: false,
      DocumentsBtn: false,
      reportOccurrence: false,
      ongoingOccurrences: false,
      completedOccurrences: false
    }
  },
  computed: {
    documents(){ return [
      {
        "name": "Verde"
      }
    ]}
  }
}
</script>

<style scoped>
  .documents-content-list{
    height: fit-content;
    padding: 20px;
    background-color: white;
  }

  .documents-content{
    height: fit-content;
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
    background-color: green;
    height: 120px;
    margin: 20px;
  }

  .ongoing-occurrences{
    background-color: blue;
    height: 120px;
    margin: 20px;
  }

  .report-an-occurrence{
    background-color: red;
    height: 120px;
    margin: 20px;
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
    padding: 10px;
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
