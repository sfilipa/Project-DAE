<template>
  <div>
    <!-- easy components usage, already shipped with bootstrap css-->
    <b-container>
      <!-- try to remove :fields=”fields” to see the magic -->
      <b-table striped over :items="teachers" :fields="fields">
        <template v-slot:cell(actions)="row">
          <nuxt-link
            class="btn btn-link"
            :to="`/teachers/${row.item.username}`">Details</nuxt-link>
        </template>
      </b-table>
      <nuxt-link to="/">Back</nuxt-link>
      <br>
      <nuxt-link to="/teachers/create">Create a New Teacher</nuxt-link>
      <br>
      <nuxt-link to="/teachers/update">Update a Teacher</nuxt-link>
      <br>
      <nuxt-link to="/teachers/delete">Delete a Teacher</nuxt-link>
    </b-container>

  </div>
</template>
<script>
export default {
  data () {
    return {
      fields: ['username', 'name', 'email', 'office', 'actions'], //nomes do DTOs
      teachers: []
    }
  },
  created () {
    this.$axios.$get('/api/teachers')
      .then((teachers) => {
        this.teachers = teachers
      })
    // this.$axios.$get('http://localhost:8080/academics/api/students')
  }
}
</script>
<style></style>
