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

<!--    Occurrences-->
    <div v-if="OccurrencesBtn">
      <div class="insurance-details-content">
        <a class="btn insurance-occurrences-btn"
           :class="{'insurance-occurrences-btn-active': reportOccurrence}"
           @click="reportOccurrence = true; ongoingOccurrences = false; completedOccurrences = false;">
          Report An Occurrence</a>
        <a class="btn insurance-occurrences-btn"
           :class="{'insurance-occurrences-btn-active': ongoingOccurrences}"
           @click="ongoingOccurrences = true; reportOccurrence = false; completedOccurrences = false;">
          Ongoing Occurrences</a>
        <a class="btn insurance-occurrences-btn"
           :class="{'insurance-occurrences-btn-active': completedOccurrences}"
           @click="completedOccurrences = true; reportOccurrence = false; ongoingOccurrences = false;">
          Completed Occurrences</a>
      </div>

<!--      Report an Occurrence-->
      <div v-if="reportOccurrence" class="report-an-occurrence">
<!--        <p><b>1. Choose an Asset Type</b></p>-->
<!--        <div class="report-an-occurrence-div">-->
<!--          <div v-if="assets.length==0">-->
<!--            No Vehicles Registered-->
<!--          </div>-->
<!--          <div v-else class="items-grid">-->
<!--            <div v-for="asset in assets" class="item-grid-div" :class="{'grid-item-selected': selectedAsset == asset}"-->
<!--                 @click="selectedAsset = asset">-->
<!--              {{ asset.name }}-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->

        <p><b>1. Choose the Affected Object</b></p>
        <div class="report-an-occurrence-div">
          <div v-if="assetObjects.length==0">
            No Objects Registered
          </div>
          <div v-else class="items-grid">
            <div v-for="assetObject in assetObjects" class="item-grid-div" :class="{'grid-item-selected': selectedAssetObject == assetObject}"
                 @click="selectedAssetObject = assetObject">
              {{ assetObject.name }}
              <p class="policy-number-span">{{ assetObject.type }}</p>
            </div>
          </div>
        </div>

        <p><b>2. What Happened</b></p>
        <div class="report-an-occurrence-div">
          <textarea class="form-control report-an-occurrence-text" placeholder="Describe here what happened" v-model="description"></textarea>
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
        <div v-if="occurrences.length==0">
          No occurrences registered
        </div>
        <div v-else v-for="occurrence in occurrences">
          {{ occurrence.name }}
        </div>
      </div>

<!--      Completed Occurrences-->
      <div v-if="completedOccurrences" class="completed-occurrences">
        <div v-if="occurrences.length==0">
          No occurrences registered
        </div>
        <div v-else v-for="occurrence in occurrences">
          {{ occurrence.name }}
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
      OverallDataBtn: true,
      OccurrencesBtn: false,
      DocumentsBtn: false,
      reportOccurrence: true,
      ongoingOccurrences: false,
      completedOccurrences: false,
      occurrences: [],
      selectedAssetObject: {},
      description: ""
    }
  },
  computed: {
    documents(){ return [
      {
        "name": "Verde"
      }
    ]},
    assetObjects(){
      return [
        {
          "name": "Car Laguna",
          "type": "Car"
        },
        {
          "name": "Electronic IPhone",
          "type": "Electronic"
        }
      ]
    }
  },
  created () {
    this.$axios.$get('/api/occurrences')
      .then((occurrences) => {
        this.occurrences = occurrences
      })
    // this.$axios.$get('http://localhost:8080/occurrences/api/students')
  },
  methods: {
    register() {
      this.$axios.$post('/api/occurrences', {
        client: "auth client - not done",
        date: this.date,
        insuredAssetType: this.selectedAssetObject.type,
        state: 'PENDING',
        description: this.description,
        insurance: this.name,
      })
        .then(() => {
          this.$router.push('/insurances')
        })
        .catch((error) => {
          this.errorMsg = error.response.data
        })
    }
  }
}
</script>

<style scoped>
  .register-occurrence-btn{
    background-color: red;
    color: white;
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
    border: 2px solid black;
    font-weight: bold;
  }

  .item-grid-div{
    padding: 20px;
    text-align: center;
    height: 8rem;
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
