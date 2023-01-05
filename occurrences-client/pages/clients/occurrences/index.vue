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
      <h4><b>My Occurrences</b></h4>
    </div>

    <div v-if="occurrences.length == 0">
      <span>No Occurrences Registered</span>
    </div>

    <div v-else v-for="occurrence in occurrences" >
      <Occurrence :occurrence="occurrence"></Occurrence>
    </div>
  </div>
</template>
<script>
import Occurrence from "~/pages/clients/components/Occurrence.vue";
export default {
  components: {
    Occurrence
  },
  data () {
    return {
      fields: ['name', 'actions'], //nomes do DTOs
      // insurances: [],
      occurrences: []
    }
  },
  created () {
    this.$axios.$get(`/api/clients/${this.$auth.user.username}/occurrences`)
      .then((occurrences) => {
        this.occurrences = occurrences
      })
  }
}
</script>
<style scoped>

.ongoing-occurrences-item-last{
  text-align: end;
  align-self: flex-end;
  margin-bottom: 1.5%;
}

.btn-associate-repairers:hover{
  background-color: red !important;
  color: white !important;
}

.btn-associate-repairers{
  border: 1px solid black;
  width: fit-content;
  height: 3rem;
  align-self: self-end;
}

.ongoing-occurrences-item-row{
  display: flex;
  flex-direction: column;
}

.ongoing-occurrences-item{
  background-color: white;
  height: fit-content;
  padding: 20px;
  margin: 30px 0;
  display: flex;
  flex-direction: row;
  align-items: center;
}

</style>
