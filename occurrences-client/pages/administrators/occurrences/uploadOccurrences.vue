<template>
  <div v-if="this.$auth.user && this.$auth.user.role.toLowerCase() === 'administrator'">
    <nuxt-link
      class="btn pb-3 pr-5 text-uppercase"
      :to="`/`">
      <div>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-chevron-left"
             viewBox="0 0 16 16">
          <path fill-rule="evenodd"
                d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        &nbsp; Back to HomePage
      </div>
    </nuxt-link>

    <div style="padding: 10px; margin-bottom: 20px;">
      <h4><b>Upload Occurrences</b></h4>
    </div>

    <!--      Report an Occurrence-->
    <b-form @submit.prevent="onSubmit" class="report-an-occurrence">
      <p><b>Documents</b></p>
      <div class="report-an-occurrence-div">
        <div class="report-an-occurrence-div">
          <input type="file" ref="documents" multiple="multiple" @change="inputDocumentsChanged" @click="cleanError">
        </div>
      </div>

      <p class="text-danger text-center" v-show="errorMsg">{{ errorMsg }}</p>

      <div style="display: flex;">
        <div class="register-occurrence-btn-div">
          <button type="submit" class="btn register-occurrence-btn" :disabled="waitingResponse">
            <div style="display: flex">
              <div class="me-3 ms-3" v-if="waitingResponse">
                <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
              </div>
              <div class="text-center" style="width: 100%"><span>Register Occurrences</span></div>

            </div>
          </button>
        </div>
      </div>
    </b-form>

  </div>
  <div v-else>
    <Unauthorized></Unauthorized>
  </div>
</template>
<script>
import Occurrence from "~/pages/experts/components/Occurrence.vue";
import Unauthorized from "@/pages/components/Unauthorized";

export default {
  mounted() {
    this.$socket.on('update', () => {
      this.updateOccurrences()
      this.$toast.info('Occurrences as been updated!').goAway(3000)
    })
  },
  components: {
    Unauthorized,
    Occurrence
  },
  data() {
    return {
      document: null,
      errorMsg: null,
      waitingResponse: false,
    }
  },
  created() {
  },
  watch: {},
  methods: {
    inputDocumentsChanged() {
      this.document = this.$refs.documents.files[0]
    },
    cleanError() {
      this.errorMsg = null
    },
    removeDocument() {
      this.document = null
    },
    onSubmit() {
      this.errorMsg = null
      if (this.document == null) {
        this.errorMsg = "Please choose a file"
        return
      }

      this.waitingResponse = true
      this.$axios.$post('/api/documents/uploadOccurrencesFromFile', this.formData(), {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
        .then((response) => {
          this.waitingResponse = false
          this.$toast.success(response).goAway(3000)

          // Socket Emit Occurrence Created
          this.$socket.emit('occurrenceCreated');
          this.$router.push('/administrators/occurrences/current')
        })
        .catch(({ response: err }) => {
          if (err) {
            this.errorMsg = err.data
          }
          this.$toast.error('Occurrences couldn\'t be created, please check the errors').goAway(3000)
          this.waitingResponse = false
        })
    },
    formData() {
      let formData = new FormData()
      formData.append("file", this.document, this.document.name)
      return formData
    },
  }
}
</script>
<style scoped>

.btn-remove-file:disabled {
  border: 0;
}

.btn-remove-file {
  border: 0
}

.document-link {
  cursor: pointer;
  margin: 0.7rem 0;
  width: 25%;
  position: relative;
}

.document-link .tooltiptext {
  visibility: hidden;
  background-color: black;
  color: white !important;
  border-radius: 6px;
  padding: 5px 0;
  position: absolute;
  z-index: 1;
  top: 1.8rem;
  font-size: 14px;
  width: 20rem;
  text-align: center;
  opacity: 0;
  transition: opacity 0.5s;
}

.document-link .tooltiptext::after {
  content: "";
  position: absolute;
  bottom: 100%;
  left: 50%;
  margin-left: -5rem;
  border-width: 5px;
  border-style: solid;
  border-color: transparent transparent black transparent;
}

.document-link:hover {
  color: red !important;
}

.document-link:hover .tooltiptext {
  visibility: visible !important;
  opacity: 1;
}

.cross-bi {
  position: relative;
  top: -0.46rem;
  right: 0.3rem;
}

.document-name:hover {
  background-color: #cbcbcb;
}

.document-name {
  background-color: #e0e0e0;
  padding: 1px 3px 0px 16px;
  border-radius: 25px;
  font-size: 15px;
  margin: 4px 5px;
  display: inline-block;
}

.register-occurrence-btn:hover {
  background-color: red !important;
  color: white !important;
}

.register-occurrence-btn {
  border: 1px solid black;
  height: 3rem;
  width: 16rem;
}

.register-occurrence-btn-div {
  margin-left: auto;
  margin-right: 4%;
}

.report-an-occurrence-div {
  padding: 0 20px 20px 20px;
}

.report-an-occurrence {
  background-color: white;
  height: fit-content;
  margin: 20px;
  padding: 40px;
}
</style>
