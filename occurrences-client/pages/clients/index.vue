<template>
  <b-container v-if="this.$auth.user && this.$auth.user.role.toLowerCase() === 'client'">
    <h3 class="text-center index-header">Welcome to your occurrence management platform</h3>
    <p>&nbsp; My Insurances</p>

    <div v-if="insurances == null" class="spinner-div">
      <div class="spinner-border"></div>
    </div>

    <div v-else-if="insurances.length == 0" class="text-center">
      <div> No insurances associated, please contact your insurance Company</div>
    </div>

    <div v-else v-for="insurance in insurances" >
      <insurance :insurance="insurance"></insurance>
    </div>
  </b-container>

  <div v-else>
    <Unauthorized></Unauthorized>
  </div>
</template>

<script>
import Insurance from "~/pages/clients/components/Insurance.vue";
import Unauthorized from "@/pages/components/Unauthorized";
export default {
  components: {
    Unauthorized,
    Insurance
  },
  name: "index.vue",
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

.index-header{
  margin-bottom: 3rem;
}

</style>
