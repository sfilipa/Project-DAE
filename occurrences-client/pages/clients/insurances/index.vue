<template>
  <div>
    <nuxt-link
      class="btn pb-3 pr-5 text-uppercase"
      :to="`/clients`">
      <div>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-chevron-left" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        &nbsp; Back to HomePage
      </div>
    </nuxt-link>

    <div style="padding: 10px; margin-bottom: 20px;">
      <h4><b>My Insurances</b></h4>
    </div>
    <div v-if="insurances.length == 0">
      <span>No Insurances Registered</span>
    </div>

    <div v-for="insurance in insurances" class="insurance-box">
      <div class="details-left">
        <span class="text-uppercase pb-3 pr-5"><b>{{ insurance.objectInsured }} - {{ insurance.insuranceCompany }}</b></span>
        <span class="pb-3 pr-5">{{insurance.initialDate}} - {{ insurance.validUntil }}</span>
        <nuxt-link
          class="btn btn-check-details pb-3 pr-5"
          :to="{name: `clients-insurances-code`, params: {code: insurance.code, insurance: insurance}}">Check Insurance Details
        </nuxt-link>
      </div>
      <div class="flex-grow-1 details-right">
        <nuxt-link
          class="btn btn-register-occurrence-right pb-3 pr-5"
          :to="{name: `clients-insurances-code`, params: {code:insurance.code, insurance: insurance, OccurrencesBtn: true}}">
          <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" fill="currentColor" class="bi bi-exclamation-triangle mb-2" viewBox="0 0 16 16">
            <path d="M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.146.146 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.163.163 0 0 1-.054.06.116.116 0 0 1-.066.017H1.146a.115.115 0 0 1-.066-.017.163.163 0 0 1-.054-.06.176.176 0 0 1 .002-.183L7.884 2.073a.147.147 0 0 1 .054-.057zm1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566z"/>
            <path d="M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995z"/>
          </svg>
          <span>Occurrence</span>
        </nuxt-link>

        <nuxt-link
          class="btn btn-register-occurrence-right pb-3 pr-5 mx-4"
          :to="{name: `clients-insurances-code`, params: {code:insurance.code, insurance: insurance, DocumentsBtn: true}}">
          <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" fill="currentColor" class="bi bi-file-earmark-text mb-2" viewBox="0 0 16 16">
            <path d="M5.5 7a.5.5 0 0 0 0 1h5a.5.5 0 0 0 0-1h-5zM5 9.5a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5zm0 2a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 0 1h-2a.5.5 0 0 1-.5-.5z"/>
            <path d="M9.5 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.5L9.5 0zm0 1v2A1.5 1.5 0 0 0 11 4.5h2V14a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h5.5z"/>
          </svg>
          <span>Documents</span>
        </nuxt-link>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data () {
    return {
      insurances: []
    }
  },
  created () {
    this.$axios.$get(`/api/clients/${this.$auth.user.username}/insurances`)
      .then((insurances) => {
        this.insurances = insurances
      })
  }
}
</script>
<style scoped>

.btn-register-occurrence-right{
  height: 80%;
  width: 20%;
  border-radius: 10px;
  background-color: #e8e8e8;
  padding: 20px;
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
  padding: 10px;
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
