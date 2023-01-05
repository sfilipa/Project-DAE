<template>
  <div>
    <nuxt-link
      class="btn pb-3 pr-5 text-uppercase"
      :to="`/experts`">
      <div>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-chevron-left" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        &nbsp; Back to HomePage
      </div>
    </nuxt-link>

    <div style="padding: 10px; margin-bottom: 20px;">
      <h4><b>Current Occurrences</b></h4>
    </div>

    <div v-if="occurrences == null" class="spinner-div">
      <div class="spinner-border"></div>
    </div>

    <div v-else-if="occurrences.length == 0" class="text-center">
      <span>No occurrences registered yet</span>
    </div>

    <div v-else v-for="occurrence in occurrences">
      <Occurrence :occurrence="occurrence" :isAssigned="isAssigned(occurrence.id)" @updateOccurrences="updateOccurrences"></Occurrence>
    </div>
  </div>
</template>
<script>
import Occurrence from "~/pages/experts/components/Occurrence.vue";
export default {
  components: {
    Occurrence
  },
  data () {
    return {
      occurrences: null,
      occurrencesAssigned: []
    }
  },
  created () {
    this.updateOccurrences()
  },
  methods: {
    isAssigned(occurrence_id){
      return this.occurrencesAssigned.map(object => object.id).indexOf(occurrence_id) !== -1
    },
    updateOccurrences(){
      this.waitingRefresh = true
      this.$axios.$get(`/api/occurrences/`)
        .then((occurrences) => {
          this.occurrences = occurrences
          this.$axios.$get(`/api/experts/${this.$auth.user.username}/occurrences/assigned`)
            .then((occurrencesAssigned) => {
              this.occurrencesAssigned = occurrencesAssigned
              this.waitingRefresh = false
            })
        })
    },
  }
}
</script>
<style scoped>

</style>
