<template>
  <div>
    <!-- easy components usage, already shipped with bootstrap css-->
    <b-container>
      <b-table striped over :items="courses" :fields="fields">
        <template v-slot:cell(actions)="row">
          <nuxt-link
            class="btn btn-link"
            :to="`/courses/${row.item.code}`">Details
          </nuxt-link>
        </template>
      </b-table>
      <nuxt-link to="/">Back</nuxt-link>
      <br>
      <nuxt-link to="/courses/create">Create a New Course</nuxt-link>
      <br>
      <nuxt-link to="/courses/update">Update a Course</nuxt-link>
      <br>
      <nuxt-link to="/courses/delete">Delete a Course</nuxt-link>
    </b-container>

  </div>
</template>
<script>
export default {
  data () {
    return {
      fields: ['code', 'name', 'actions'], //nomes do DTOs
      courses: []
    }
  },
  created () {
    this.$axios.$get('/api/courses')
      .then((courses) => {
        this.courses = courses
      })
  }
}
</script>
<style></style>
