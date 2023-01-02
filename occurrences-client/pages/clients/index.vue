<template>
  <b-container>
    <h3 class="text-center index-header">Welcome to your occurrence management platform</h3>
    <p>&nbsp; My Insurances</p>

    <div v-for="insurance in insurances" class="insurance-box">
      <div class="details-left">
        <span class="text-uppercase pb-3 pr-5"><b>{{ insurance.name }}</b></span>
        <span class="pb-3 pr-5">Term</span>
        <nuxt-link
          class="btn btn-check-details pb-3 pr-5"
          :to="`/insurances/${insurance.name}`">Check Insurance Details
        </nuxt-link>
      </div>
      <div class="flex-grow-1 details-right">
        <nuxt-link
          class="btn btn-register-occurrence-right pb-3 pr-5"
          :to="{name: `insurances-name`, params: {name:insurance.name, OccurrencesBtn: true}}">
          <span>Register Occurrence</span>
        </nuxt-link>

        <nuxt-link
          class="btn btn-register-occurrence-right pb-3 pr-5 mx-4"
          :to="{name: `insurances-name`, params: {name:insurance.name, DocumentsBtn: true}}">
          <span>Documents</span>
        </nuxt-link>
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
      // insurances: []
    }
  },
  computed: {
    insurances(){ return [
      {
        "name": "Carros Liber"
      },
      {
        "name": "Carros Logo"
      }
    ]}
  },
  created () {
    // this.$axios.$get('/api/administrators')
    //   .then((administrators) => {
    //     this.administrators = administrators
    //   })
    // this.$axios.$get('http://localhost:8080/occurrences/api/students')
  }
}
</script>

<style scoped>

.btn-register-occurrence-right{
  height: 80%;
  width: 20%;
  border-radius: 0px;
  background-color: lavenderblush;
  padding: 20px;
}

.index-header{
  margin-bottom: 3rem;
}

.btn-check-details:active, .btn-check-details:hover{
  background-color: red !important;
  color: white !important;
}

.btn-check-details{
  font-size: 18px;
  width: fit-content;
  height: fit-content;
  border: 1px solid gray;
}

.details-right{
  display: flex;
  flex-direction: row;
  width: fit-content;
  align-items: center;
  border: 1px solid black;
  border-radius: 0 25px 25px 0;
  padding: 20px;
}

.details-left{
  display: flex;
  flex-direction: column;
  width: 30%;
  height: 100%;
  align-items: center;
  border-top: 1px solid;
  border-bottom: 1px solid;
  border-left: 1px solid;
  border-radius: 25px 0 0 25px;
  padding: 10px;
}

.insurance-box{
  display: flex;
  flex-direction: row;
  height: 180px;
  background-color: rgb(255, 255, 255);
  border-radius: 25px;
  margin-bottom: 3%;
}

@media only screen and (max-width: 1200px) {

  .insurance-box{
    flex-direction: column;
    height: 340px;
  }

  .details-left{
    width: 100%;
    height: fit-content;
    border-top: 1px solid;
    border-bottom: 0;
    border-left: 1px solid;
    border-right: 1px solid;
    border-radius: 25px 25px 0 0;
    padding: 10px;
  }

  .details-right{
    display: block;
    width: 100%;
    border-radius: 0 0 25px 25px;
  }
}

</style>
