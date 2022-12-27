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
    <h1>Describe the Occurrence</h1>
    <form @submit.prevent="create" :disabled="!isFormValid"  width="35%">
      <!--      <b-input v-model.trim="username" :state="isUsernameValid" required placeholder="Enter your username" />-->
      <b-form-group id="description" label="Description:" label-for="description" :invalid-feedback="invalidDescriptionFeedback" :state="isDescriptionValid" >
        <b-input v-model.trim="description" :state="isDescriptionValid" required placeholder="Enter what happened"/>
      </b-form-group>
      <p class="text-danger" v-show="errorMsg">{{ errorMsg }}</p>
      <br><br>
      <nuxt-link to="/occurrences">Return</nuxt-link>
      <button type="reset" @click="reset">RESET</button>
      <button @click.prevent="create" :disabled="!isFormValid">CREATE</button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      description: null,
    }
  },
  computed: {
    isDescriptionValid () {
      if (this.invalidDescriptionFeedback === null) {
        return null
      }
      return this.invalidDescriptionFeedback === ''
    },
    invalidDescriptionFeedback () {
      if (!this.description) {
        return null
      }
      let descriptionLen = this.description.length
      if (descriptionLen < 3 || descriptionLen > 150) {
        return 'The description must be between [3, 150] characters.'
      }
      return ''
    },
    isFormValid () {
      if (! this.isDescriptionValid) {
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
      this.$axios.$post('/api/occurrences', {
        description: this.description,
      })
        .then(() => {
          this.$router.push('/occurrences')
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
