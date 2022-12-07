<template>
  <div>
    <!-- easy components usage, already shipped with bootstrap css-->
    <b-container>
      <!-- try to remove :fields=”fields” to see the magic -->
      <b-table striped over :items="administrators" :fields="fields">
        <template v-slot:cell(actions)="row">
          <nuxt-link
            class="btn btn-link"
            :to="`/administrators/${row.item.username}`">Details</nuxt-link>
        </template>
      </b-table>
      <nuxt-link to="/">Back</nuxt-link>
      <br>
      <nuxt-link to="/administrators/create">Create a New Administrator</nuxt-link>
      <br>
      <nuxt-link to="/administrators/update">Update a Administrator</nuxt-link>
      <br>
      <nuxt-link to="/administrators/delete">Delete a Administrator</nuxt-link>
    </b-container>

  </div>
</template>
<script>
export default {
  data () {
    return {
      fields: ['username', 'name', 'email', 'actions'], //nomes do DTOs
      administrators: []
    }
  },
  created () {
    this.$axios.$get('/api/administrators')
      .then((administrators) => {
        this.administrators = administrators
      })
    // this.$axios.$get('http://localhost:8080/academics/api/students')
  }
}
</script>
<style></style>
