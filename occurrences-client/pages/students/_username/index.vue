<template>
  <b-container>
    <h4>Student Details:</h4>
    <p>Username: {{ student.username }}</p>
    <p>Name: {{ student.name }}</p>
    <p>Email: {{ student.email }}</p>
    <p>Course: {{ student.courseName }}</p>
    <h4>Subjects enrolled:</h4>
    <b-table v-if="subjects.length" striped over :items="subjects" :fields="subjectFields" >
      <template v-slot:cell(actions)="row">
        <form @submit.prevent="unroll">
          <button @click.prevent="unroll(row.item.code)">Unroll</button>
        </form>
      </template>
    </b-table>
    <p v-else>No subjects enrolled.</p>
    <h4>Subjects not enrolled:</h4>
    <b-table v-if="unrolledSubjects.length" striped over :items="unrolledSubjects" :fields="unrolledSubjectsFields" >
      <template v-slot:cell(actions)="row">
        <form @submit.prevent="enroll">
          <button @click.prevent="enroll(row.item.code)">Enroll</button>
        </form>
      </template>
    </b-table>
    <p v-else>No subjects enrolled.</p>
    <nuxt-link to="/students">Back</nuxt-link>
    &nbsp;
    <nuxt-link :to="`/students/${this.username}/send-email`">Send e-mail</nuxt-link>
  </b-container>
</template>

<script>
export default {
  data() {
    return {
      student: {},
      subjectFields: ['code', 'name', 'courseCode', 'courseYear', 'scholarYear', 'actions'],
      unrolledSubjects: [],
      unrolledSubjectsFields: ['code', 'name', 'courseCode', 'courseYear', 'scholarYear', 'actions']

    }
  },
  computed: {
    username() {
      return this.$route.params.username
    },
    subjects() {
      return this.student.subjects || []
    }
  },
  created() {
    this.$axios.$get(`/api/students/${this.username}`)
      .then(student => this.student = student || {})
      .then(() => this.$axios.$get(`/api/students/${this.username}/unrolledSubjects`))
      .then(unrolledSubjects => this.unrolledSubjects = unrolledSubjects)
  },
  methods: {
    unroll(code) {
      this.$axios.$put(`/api/students/${this.username}/${code}/unroll`)
        .then(() => {
          this.$router.push(`/students`)
        })
    },enroll(code) {
      this.$axios.$put(`/api/students/${this.username}/${code}/enroll`)
        .then(() => {
          this.$router.push(`/students`)
        })
    }
  }
}
</script>

<style scoped>

</style>
