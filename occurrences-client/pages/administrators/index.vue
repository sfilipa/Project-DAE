<template>
  <div v-if="this.$auth.user && this.$auth.user.role.toLowerCase() === 'administrator'">
    <h3 class="text-center index-header">Welcome to your occurrence management platform</h3>

    <div v-if="experts == null || repairers == null || clients == null" class="spinner-div" style="margin-top: 2rem">
      <div class="spinner-border"></div>
    </div>
    <div v-else>
      <!--    Navbar Buttons-->
      <div class="administrator-details-navbar" style="margin-top: 1.5rem;">
        <a class="btn administrator-details-navbar-item"
           :class="{'administrator-details-navbar-item-active': AllBtn}"
           @click="AllBtn = true; ExpertsBtn = false; RepairersBtn = false; ClientsBtn = false">
          All</a>
        <a class="btn administrator-details-navbar-item"
           :class="{'administrator-details-navbar-item-active': ExpertsBtn}"
           @click="AllBtn = false; ExpertsBtn = true; RepairersBtn = false; ClientsBtn = false">
          Experts</a>
        <a class="btn administrator-details-navbar-item"
           :class="{'administrator-details-navbar-item-active': RepairersBtn}"
           @click="AllBtn = false; ExpertsBtn = false; RepairersBtn = true; ClientsBtn = false">
          Repairers</a>
        <a class="btn administrator-details-navbar-item"
           :class="{'administrator-details-navbar-item-active': ClientsBtn}"
           @click="AllBtn = false; ExpertsBtn = false; RepairersBtn = false; ClientsBtn = true">
          Clients</a>
      </div>

      <div class="administrator-row" style="margin-top: 1.5rem;">
        <div style="margin: auto;">
          <nuxt-link to="/administrators/experts/create" class="btn administrator-regist-btn">
            Register an Expert
          </nuxt-link>
          <nuxt-link to="/administrators/repairers/create" class="btn administrator-regist-btn">
            Register a Repairer
          </nuxt-link>
        </div>
      </div>

      <div v-if="ExpertsBtn || AllBtn" style="margin: 2rem 0">
        <h4>Experts:</h4>
        <p v-if="experts.length === 0" class="text-center">There are no experts registered yet</p>
        <ExpertBody v-else :experts="experts"  @updateExperts="updateExperts"></ExpertBody>
      </div>
      <div v-if="RepairersBtn || AllBtn" style="margin: 2rem 0">
        <h4>Repairers:</h4>
        <p v-if="repairers.length === 0" class="text-center">There are no repairers registered yet</p>
        <RepairerBody v-else :repairers="repairers" @updateRepairers="updateRepairers"></RepairerBody>
      </div>
      <div v-if="ClientsBtn || AllBtn" style="margin: 2rem 0">
        <h4>Clients:</h4>
        <p v-if="clients.length === 0" class="text-center">There are no clients registered yet</p>
        <ClientBody v-else :clients="clients" @updateClients="updateClients"></ClientBody>
      </div>
    </div>
  </div>

  <div v-else>
    <Unauthorized></Unauthorized>
  </div>
</template>
<script>
import ExpertBody from "~/pages/administrators/components/ExpertBody.vue";
import RepairerBody from "~/pages/administrators/components/RepairerBody.vue";
import ClientBody from "~/pages/administrators/components/ClientBody.vue";
import Unauthorized from "@/pages/components/Unauthorized";
export default {
  components: {
    Unauthorized,
    ExpertBody,
    RepairerBody,
    ClientBody
  },
  methods: {
    updateExperts(){
      this.$axios.$get('/api/experts')
        .then((experts) => {
          this.experts = []
          this.experts = experts
        })
    },
    updateRepairers(){
      this.$axios.$get('/api/repairers')
        .then((repairers) => {
          this.repairers = []
          this.repairers = repairers
        })
    },
    updateClients(){
      this.$axios.$get('/api/clients')
        .then((clients) => {
          this.clients = []
          this.clients = clients
        })
    }
  },
  data () {
    return {
      experts: null,
      repairers: null,
      clients: null,
      AllBtn: true,
      ExpertsBtn: false,
      RepairersBtn: false,
      ClientsBtn: false,
    }
  },
  created () {
    this.updateExperts()
    this.updateRepairers()
    this.updateClients()
  }
}
</script>
<style scoped>

.administrator-details-navbar-item-active{
  background-color: red;
  color: white !important;
}

.administrator-details-navbar-item:hover{
  color: darkgray !important;
}

.administrator-details-navbar-item{
  color: white !important;
  margin: 0 20px;
}

.administrator-details-navbar{
  display: flex;
  flex-direction: row;
  align-items: center;
  background-color: #313030;
  padding: 20px;
  margin: 1rem 2.8rem;
}

.administrator-regist-btn:hover{
  background-color: red !important;
  color: white !important;
  border: 1px solid red !important;
}

.administrator-regist-btn{
  background-color: white;
  margin: 10px 20px;
  padding: 20px;
  border: 1px solid black;
  border-radius: 0px;
  width: 14rem;
}

.administrator-row{
  display: flex;
  flex-direction: row;
  align-items: center;
}

</style>
