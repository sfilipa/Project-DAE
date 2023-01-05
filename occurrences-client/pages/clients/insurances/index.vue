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

    <div v-if="insurances == null" class="spinner-div">
      <div class="spinner-border"></div>
    </div>

    <div v-else-if="insurances.length == 0" class="text-center">
      <div> No insurances associated, please contact your insurance Company</div>
    </div>

    <div v-else v-for="insurance in insurances">
      <insurance :insurance="insurance"></insurance>
    </div>
  </div>
</template>
<script>
import Insurance from "~/pages/clients/components/Insurance.vue";
export default {
  components: {
    Insurance
  },
  data () {
    return {
      insurances: null
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

</style>
