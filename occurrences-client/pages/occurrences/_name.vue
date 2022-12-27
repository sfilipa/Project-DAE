<template>
  <div>
    <!-- easy components usage, already shipped with bootstrap css-->
    <b-container>
      <!-- try to remove :fields=”fields” to see the magic -->
      <b-table striped over :items="occurrences" :fields="fields">
        <template v-slot:cell(actions)="row">
          <nuxt-link
            class="btn btn-link"
            :to="`/occurrences/update`">Update</nuxt-link>
        </template>
      </b-table>
      <nuxt-link to="/insurances">Back</nuxt-link>
      <br>
      <nuxt-link to="/occurrences/create">Create an Occurrence</nuxt-link>
      <br>
    </b-container>

  </div>
</template>
<script>
export default {
  data () {
    return {
      fields: ['description', 'date', 'state', 'insuredAssetType', 'insurance', 'actions'], //nomes do DTOs
      occurrences: []
    }
  },
  computed: {
    insurance_name() {
      return this.$route.params.name
    }
  },
  created () {
    this.$axios.$get(`/api/occurrences/${this.insurance_name}`)
      .then((occurrences) => {
        this.occurrences = occurrences
      })
  }
}
</script>
<style></style>
