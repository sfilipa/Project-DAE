<template>
<!--  <form @submit.prevent="create" :disabled="!isFormValid">-->
<!--    <div>-->
<!--      username: <input v-model="username" type="text" :state="isUsernameValid" required minlength="4" maxlength="25" placeholder="Enter your username">-->
<!--    </div>-->
<!--    <div>-->
<!--      password: <input v-model="password" type="password" :state="isPasswordValid" required placeholder="Enter your password" minlength="8" maxlength="30">-->
<!--    </div>-->
<!--    <div>-->
<!--      name: <input v-model.trim="name" type="text" :state="isNameValid" required placeholder="Enter your name" minlength="4" maxlength="30">-->
<!--    </div>-->
<!--    <div>-->
<!--      email: <input ref="email" v-model.trim="email" type="email" :state="isEmailValid" required pattern=".+@my.ipleiria.pt" placeholder="Enter your e-mail">-->
<!--    </div>-->
<!--    <div>-->
<!--      course:-->
<!--      <select v-model="courseCode" :options="courses" :state="isCourseValid" required value-field="code" text-field="name">>-->
<!--        <template v-for="course in courses">-->
<!--          <option :key="course.code" :value="course.code">-->
<!--            {{ course.name }}-->
<!--          </option>-->
<!--        </template>-->
<!--      </select>-->
<!--    </div>-->
<!--    <p v-show="errorMsg" class="text-danger">-->
<!--      {{ errorMsg }}-->
<!--    </p>-->
<!--    <nuxt-link to="/students">Return</nuxt-link>-->
<!--    <button type="reset" @click="errorMsg = false">RESET</button>-->
<!--    <button @click.prevent="create">CREATE</button>-->
<!--  </form>-->
  <div class="body-template-c1">
    <h1>Create a new Student</h1>
    <form @submit.prevent="create" :disabled="!isFormValid"  width="35%">
<!--      <b-input v-model.trim="username" :state="isUsernameValid" required placeholder="Enter your username" />-->
      <b-form-group id="username" label="Username:" label-for="username" :invalid-feedback="invalidUsernameFeedback" :state="isUsernameValid" >
        <b-input v-model.trim="username" :state="isUsernameValid" required placeholder="Enter your username"/>
      </b-form-group>
      <b-form-group id="password" label="Password:" label-for="password" :invalid-feedback="invalidPasswordFeedback" :state="isPasswordValid" >
        <b-input v-model="password"  type="password" :state="isPasswordValid" required placeholder="Enter your password" />
      </b-form-group>
      <b-form-group id="name" label="Name:" label-for="name" :invalid-feedback="invalidNameFeedback" :state="isNameValid" >
      <b-input v-model.trim="name" :state="isNameValid" required placeholder="Enter your name" />
      </b-form-group>
      <b-form-group id="email" label="Email:" label-for="email" :invalid-feedback="invalidEmailFeedback" :state="isEmailValid" >
        <b-input ref="email" v-model.trim="email" type="email" :state="isEmailValid" required pattern=".+@my.ipleiria.pt" placeholder="Enter your e-mail" />
      </b-form-group>
        <b-select v-model="courseCode" :options="courses" :state="isCourseValid" required value-field="code" text-field="name">
        <template v-slot:first>
          <option :value="null" disabled>-- Please select the Course --</option>
        </template>
        <template v-for="course in courses">
          <option :key="course.code" :value="course.code">
            {{ course.name }}
          </option>
        </template>
      </b-select>
      <p class="text-danger" v-show="errorMsg">{{ errorMsg }}</p>
      <br><br>
      <nuxt-link to="/students">Return</nuxt-link>
      <button type="reset" @click="reset">RESET</button>
      <button @click.prevent="create" :disabled="!isFormValid">CREATE</button>
    </form>
    </div>
</template>

<script>
  export default {
    data() {
      return {
        username: null,
        password: null,
        name: null,
        email: null,
        courseCode: null,
        courses: [],
        errorMsg: false
      }
    },
    created() {
      this.$axios.$get('/api/courses')
        .then(courses => {
          this.courses = courses
        })
    },
    computed: {
      invalidUsernameFeedback () {
        if (!this.username) {
          return null
        }
        let usernameLen = this.username.length
        if (usernameLen < 3 || usernameLen > 15) {
          return 'The username must be between [3, 15] characters.'
        }
        return ''
      },
      isUsernameValid () {
        if (this.invalidUsernameFeedback === null) {
          return null
        }
        return this.invalidUsernameFeedback === ''
      },
      invalidPasswordFeedback () {
        if (!this.password) {
          return null
        }
        let passwordLen = this.password.length
        if (passwordLen < 3 || passwordLen > 255) {
          return 'The password must be between [3, 255] characters.'
        }
        return ''
      },
      isPasswordValid () {
        if (this.invalidPasswordFeedback === null) {
          return null
        }
        return this.invalidPasswordFeedback === ''
      },
      invalidNameFeedback () {
        if (!this.name) {
          return null
        }
        let nameLen = this.name.length
        if (nameLen < 3 || nameLen > 25) {
          return 'The password must be between [3, 25] characters.'
        }
        return ''
      },
      isNameValid () {
        if (this.invalidNameFeedback === null) {
          return null
        }
        return this.invalidNameFeedback === ''
      },
      invalidEmailFeedback () {
        if (!this.email) {
          return null
        }
// asks the component if it’s valid. We don’t need to use a regex for the e-mail. The input field already does the job for us, because it is of type “email” and validates that the user writes an e-mail that belongs to the domain of IPLeiria.
        if(!this.$refs.email.checkValidity()){
          return 'The email should end with "@my.ipleiria.pt"'
        }
        return ''
      },
      isEmailValid () {
        if (this.invalidEmailFeedback === null) {
          return null
        }
        return this.invalidEmailFeedback === ''
      },
      isCourseValid () {
        if (!this.courseCode) {
          return null
        }
        return this.courses.some(course => this.courseCode === course.code)
      },
      isFormValid () {
        if (! this.isUsernameValid) {
          return false
        }
        if (! this.isPasswordValid) {
          return false
        }
        if (! this.isNameValid) {
          return false
        }
        if (! this.isEmailValid) {
          return false
        }
        if (! this.isCourseValid) {
          return false
        }
        return true
      }
    },
    methods: {
      reset () {
        this.errorMsg = false
      },
      create() {
        this.$axios.$post('/api/students', {
          username: this.username,
          password: this.password,
          name: this.name,
          email: this.email,
          courseCode: this.courseCode
        })
          .then(() => {
            this.$router.push('/students')
          })
          .catch((error) => {
            this.errorMsg = error.response.data
          })
      }
    }
  }
</script>

<style scoped>
  form{
    width: 35%;
  }

  .body-template-c1{
    margin-left: 15px;
  }
</style>
