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

    <h3 class="text-center index-header">Experts Management</h3>

    <div v-if="clients == null" class="spinner-div" style="margin-top: 2rem">
      <div class="spinner-border"></div>
    </div>
    <div v-else>
      <div >
        <h4>Clients:</h4>
        <p v-if="clients.length === 0" class="text-center">There are no clients registered yet</p>
        <ClientBody v-else :clients="clients" @updateClients="updateClients"></ClientBody>
      </div>
    </div>
  </div>
</template>
<script>
import ClientBody from "~/pages/administrators/components/ClientBody.vue";
export default {
  components: {
    ClientBody
  },
  methods: {
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
      clients: null,
    }
  },
  created () {
    this.updateClients()
  }
}
</script>
<style scoped>

</style>
