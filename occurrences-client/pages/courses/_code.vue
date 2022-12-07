<template>
  <b-container>
    <h4>Course Details:</h4>
    <p>Code: {{ course.code }}</p>
    <p>Name: {{ course.name }}</p>
    <nuxt-link to="/courses">Back</nuxt-link>
  </b-container>
</template>

<script>
export default {
  data() {
    return {
      course: {}
    }
  },
  computed: {
    code() {
      return this.$route.params.code
    }
  },
  created() {
    this.$axios.$get(`/api/courses/${this.code}`)
      .then(course => this.course = course || {})
  },
}
</script>

<style scoped>

</style>
