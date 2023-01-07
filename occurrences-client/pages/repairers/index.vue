<template>
  <b-container v-if="this.$auth.user">
    <h3 class="text-center index-header">Welcome to your occurrence management platform</h3>
    <p>&nbsp; Current Occurrences</p>

    <div>
      <div v-if="occurrences == null" class="spinner-div">
        <div class="spinner-border"></div>
      </div>

      <div v-else-if="occurrences.length == 0" class="text-center">
        <span>No occurrences registered yet</span>
      </div>

      <div v-else v-for="occurrence in occurrences" >
        <Occurrence :occurrence="occurrence" :isAssigned="isAssigned(occurrence.id)" :waitingRefresh="waitingRefresh" @updateOccurrences="updateOccurrences"></Occurrence>
      </div>
    </div>
  </b-container>
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
      occurrencesAssigned: [],
      waitingRefresh: false
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
          this.$axios.$get(`/api/repairers/${this.$auth.user.username}/occurrences/assigned`)
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

.index-header{
  margin-bottom: 3rem;
}

</style>
