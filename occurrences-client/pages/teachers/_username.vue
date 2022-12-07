<template>
  <b-container>
    <h4>Teacher Details:</h4>
    <p>Username: {{ teacher.username }}</p>
    <p>Name: {{ teacher.name }}</p>
    <p>Email: {{ teacher.email }}</p>
    <p>Office: {{ teacher.office }}</p>
    <h4>Subjects associated:</h4>
    <b-table v-if="subjects.length" striped over :items="subjects" :fields="subjectFields" >
      <template v-slot:cell(actions)="row">
        <form @submit.prevent="disassociate">
          <button @click.prevent="disassociate(row.item.code)">Disassociate</button>
        </form>
      </template>
    </b-table>
    <p v-else>No subjects associated.</p>
    <h4>Subjects not associated:</h4>
    <b-table v-if="disassociatedSubjects.length" striped over :items="disassociatedSubjects" :fields="disassociatedSubjectsFields" >
      <template v-slot:cell(actions)="row">
        <form @submit.prevent="associate">
          <button @click.prevent="associate(row.item.code)">Associate</button>
        </form>
      </template>
    </b-table>
    <p v-else>No subjects disassociated.</p>
    <nuxt-link to="/teachers">Back</nuxt-link>
  </b-container>
</template>

<script>
export default {
  data() {
    return {
      teacher: {},
      subjects: [],
      subjectFields: ['code', 'name', 'courseCode', 'courseYear', 'scholarYear', 'actions'],
      disassociatedSubjects: [],
      disassociatedSubjectsFields: ['code', 'name', 'courseCode', 'courseYear', 'scholarYear', 'actions']

    }
  },
  computed: {
    username() {
      return this.$route.params.username
    }
  },
  created() {
    this.$axios.$get(`/api/teachers/${this.username}`)
      .then(teacher => this.teacher = teacher || {})
      .then(() => this.$axios.$get(`/api/teachers/${this.username}/subjects`))
      .then(subjects => this.subjects = subjects)
      .then(() => this.$axios.$get(`/api/teachers/${this.username}/disassociatedSubjects`))
      .then(disassociatedSubjects => this.disassociatedSubjects = disassociatedSubjects)
  },
  methods: {
    disassociate(code) {
      this.$axios.$put(`/api/teachers/${this.username}/${code}/disassociate`)
        .then(() => {
          this.$router.push(`/teachers`)
        })
    },associate(code) {
      this.$axios.$put(`/api/teachers/${this.username}/${code}/associate`)
        .then(() => {
          this.$router.push(`/teachers`)
        })
    }
  }
}
</script>

<style scoped>

</style>
