<template>
  <div>
    <!-- easy components usage, already shipped with bootstrap css-->
    <b-container>
      <!-- try to remove :fields=”fields” to see the magic -->
      <b-table striped over :items="subjects" :fields="fields">
        <template v-slot:cell(actions)="row">
          <nuxt-link
            class="btn btn-link"
            :to="`/subjects/${row.item.code}`">Details</nuxt-link>
        </template>
      </b-table>
      <nuxt-link to="/">Back</nuxt-link>
      <br>
      <nuxt-link to="/subjects/create">Create a New Subject</nuxt-link>
      <br>
      <nuxt-link to="/subjects/update">Update a Subject</nuxt-link>
      <br>
      <nuxt-link to="/subjects/delete">Delete a Subject</nuxt-link>
    </b-container>

  </div>
</template>
<script>
export default {
  data () {
    return {
      fields: ['code', 'name', 'courseCode', 'courseName', 'courseYear', 'scholarYear', 'actions'], //nomes do DTOs
      subjects: []
    }
  },
  created () {
    this.$axios.$get('/api/subjects')
      .then((subjects) => {
        this.subjects = subjects
      })
    // this.$axios.$get('http://localhost:8080/academics/api/students')
  }
}
</script>
<style></style>
