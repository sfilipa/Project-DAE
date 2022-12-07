<template>
  <div>
    <!-- easy components usage, already shipped with bootstrap css-->
    <b-container>
      <!-- try to remove :fields=”fields” to see the magic -->
      <b-table striped over :items="students" :fields="fields">
        <template v-slot:cell(actions)="row">
          <nuxt-link
            class="btn btn-link"
            :to="`/students/${row.item.username}`">Details
          </nuxt-link>
          <nuxt-link :to="`/students/${row.item.username}/send-email`">Send e-mail</nuxt-link>

        </template>
      </b-table>
      <nuxt-link to="/">Back</nuxt-link>
      <br>
      <nuxt-link to="/students/create">Create a New Student</nuxt-link>
      <br>
      <nuxt-link to="/students/update">Update a Student</nuxt-link>
      <br>
      <nuxt-link to="/students/delete">Delete a Student</nuxt-link>
    </b-container>

  </div>
</template>
<script>
export default {
  data () {
    return {
      fields: ['username', 'name', 'email', 'courseCode', 'courseName', 'actions'], //nomes do DTOs
      students: []
    }
  },
  created () {
    this.$axios.$get('/api/students')
      .then((students) => {
        this.students = students
      })
    // this.$axios.$get('http://localhost:8080/occurrences/api/students')
  }
}
</script>
<style></style>
