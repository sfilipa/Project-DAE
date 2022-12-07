<template>
  <form @submit.prevent="update">
    <div>
      Code: <input v-model="code" type="text">
    </div>
    <div>
      Name: <input v-model="name" type="text">
    </div>
    <div>
      Course Year: <input v-model="courseYear" type="text">
    </div>
    <div>
      Scholar Year: <input v-model="scholarYear" type="text">
    </div>
    <div>
      Course:
      <select v-model="courseCode">
        <template v-for="course in courses">
          <option :key="course.code" :value="course.code">
            {{ course.name }}
          </option>
        </template>
      </select>
    </div>
    <nuxt-link to="/subjects">Return</nuxt-link>
    <button type="reset">RESET</button>
    <button @click.prevent="update">UPDATE</button>
  </form>
</template>

<script>
export default {
  data() {
    return {
      code: null,
      name: null,
      scholarYear: null,
      courseYear: null,
      courseCode: null,
      courses: []
    }
  },
  created() {
    this.$axios.$get('/api/courses')
      .then(courses => {
        this.courses = courses
      })
  },
  methods: {
    update() {
      this.$axios.$put(`/api/subjects/${this.code}`, {
        code: this.code,
        name: this.name,
        courseYear: this.courseYear,
        scholarYear: this.scholarYear,
        courseCode: this.courseCode
      })
        .then(() => {
          this.$router.push('/subjects')
        })
    }
  }
}
</script>

<style scoped>

</style>
