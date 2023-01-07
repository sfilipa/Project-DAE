<template>
  <div>
    <h3 class="text-center index-header">Welcome to your occurrence management platform</h3>
    <div class="administrator-row" style="margin-top: 1.5rem;">
      <div style="margin: auto;">
        <nuxt-link to="/administrators/experts/create" class="btn administrator-regist-btn">
          Regist an Expert
        </nuxt-link>
        <nuxt-link to="/administrators/repairers/create" class="btn administrator-regist-btn">
          Regist a Repairer
        </nuxt-link>
      </div>
    </div>
    <div v-if="experts == null || repairers == null" class="spinner-div">
      <div class="spinner-border"></div>
    </div>
    <div v-else>
      <div >
        <h4>Experts:</h4>
        <p v-if="experts.length == 0">There are no experts registered yet</p>
        <p v-for="expert in experts">{{expert.name}}</p>
      </div>
      <div >
        <h4>Repairers:</h4>
        <p v-if="repairers.length == 0">There are no repairers registered yet</p>
        <p v-for="repairer in repairers">{{repairer.name}}</p>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data () {
    return {
      experts: null,
      repairers: null
    }
  },
  created () {
    this.$axios.$get('/api/experts')
      .then((experts) => {
        this.experts = []
        this.experts = experts
      })
    this.$axios.$get('/api/repairers')
      .then((repairers) => {
        this.repairers = []
        this.repairers = repairers
      })
  }
}
</script>
<style scoped>

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
