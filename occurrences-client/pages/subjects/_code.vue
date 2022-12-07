<template>
  <b-container>
    <h4>Subject Details:</h4>
    <p>Code: {{ subject.code }}</p>
    <p>Name: {{ subject.name }}</p>
    <p>Course: {{ subject.courseName }}</p>
    <p>Course Year: {{ subject.courseYear }}</p>
    <p>Scholar Year: {{ subject.scholarYear }}</p>
    <h4>Students enrolled:</h4>
    <b-table v-if="students.length" striped over :items="students" :fields="studentFields"/>
    <p v-else>No students enrolled.</p>
    <h4>Teachers associated:</h4>
    <b-table v-if="teachers.length" striped over :items="teachers" :fields="teacherFields"/>
    <p v-else>No teachers associated.</p>
    <nuxt-link to="/subjects">Back</nuxt-link>
  </b-container>
</template>

<script>
export default {
  data() {
    return {
      subject: {},
      students: [],
      studentFields: ['username', 'name', 'email'],
      teachers: [],
      teacherFields: ['username', 'name', 'email', 'office']
    }
  },
  computed: {
    code() {
      return this.$route.params.code
    }
  },
  created() {
    this.$axios.$get(`/api/subjects/${this.code}`)
      .then(subject => this.subject = subject || {})
      .then(() => this.$axios.$get(`/api/subjects/${this.code}/students`))
      .then(students => this.students = students)
      .then(() => this.$axios.$get(`/api/subjects/${this.code}/teachers`))
      .then(teachers => this.teachers = teachers)
  }

}
</script>

<style scoped>

</style>
