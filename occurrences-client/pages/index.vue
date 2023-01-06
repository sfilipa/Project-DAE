<template>
  <div v-if="this.$auth.user">
    <ClientHome v-if="this.$auth.user.role.toLowerCase() == 'client'"></ClientHome>
    <ExpertHome v-else-if="this.$auth.user.role.toLowerCase() == 'expert'"></ExpertHome>
    <RepairerHome v-else-if="this.$auth.user.role.toLowerCase() == 'repairer'"></RepairerHome>
    <AdminHome v-else-if="this.$auth.user.role.toLowerCase() == 'admin'"></AdminHome>
  </div>
</template>

<script>
import ClientHome from "~/pages/clients/index.vue";
import ExpertHome from "~/pages/experts/index.vue";
import RepairerHome from "~/pages/repairers/index.vue";
import AdminHome from "~/pages/administrators/index.vue";
export default {
  components: {
    ClientHome,
    ExpertHome,
    RepairerHome,
    AdminHome
  },
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
    border-radius: 10px;
    background-color: #e8e8e8;
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
