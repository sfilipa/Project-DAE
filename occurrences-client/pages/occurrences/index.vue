<template>
  <div>
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

    <div v-else v-for="occurrence in occurrences" class="insurance-box">
      <div class="details-left">
        <span class="text-uppercase pb-3 pr-5"><b>{{ occurrence.name }}</b></span>
      </div>
      <div class="flex-grow-1 details-right">

      </div>
    </div>
  </div>
</template>
<script>
export default {
  data () {
    return {
      fields: ['name', 'actions'], //nomes do DTOs
      // insurances: []
    }
  },
  computed: {
    occurrences(){ return [
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

  .details-right{
    display: flex;
    flex-direction: column;
    width: fit-content;
    align-items: center;
    border: 1px solid black;
    border-radius: 0 25px 25px 0;
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
    height: 170px;
    background-color: rgb(255, 255, 255);
    border-radius: 25px;
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
