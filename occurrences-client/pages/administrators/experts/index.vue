<template>
  <div v-if="this.$auth.user && this.$auth.user.role.toLowerCase() === 'administrator'">
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

    <h3 class="text-center index-header">Experts Management</h3>

    <div class="administrator-row" style="margin-top: 1.5rem;">
      <div style="margin: auto;">
        <nuxt-link to="/administrators/experts/create" class="btn administrator-regist-btn">
          Register an Expert
        </nuxt-link>
      </div>
    </div>
    <div v-if="experts == null" class="spinner-div" style="margin-top: 2rem">
      <div class="spinner-border"></div>
    </div>
    <div v-else>
      <div >
        <h4>Experts:</h4>
        <p v-if="experts.length === 0" class="text-center">There are no experts registered yet</p>
        <ExpertBody v-else :experts="experts" @updateExperts="updateExperts"></ExpertBody>
      </div>
    </div>
  </div>

  <div v-else>
    <Unauthorized></Unauthorized>
  </div>
</template>
<script>
import ExpertBody from "~/pages/administrators/components/ExpertBody.vue";
import Unauthorized from "@/pages/components/Unauthorized";
export default {
  components: {
    Unauthorized,
    ExpertBody
  },
  methods: {
    updateExperts() {
      this.$axios.$get('/api/experts')
        .then((experts) => {
          this.experts = []
          this.experts = experts
        })
    }
  },
  data () {
    return {
      experts: null,
    }
  },
  created () {
    this.updateExperts()
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
