<template>
  <div class="all-occurrences">
    <p style="font-size: 20px; color: red"><b>{{occurrence.objectInsured}} - <span>{{occurrence.insuranceCompanyName}} ({{occurrence.insuranceCode}})</span></b></p>
    <div class="all-occurrences-item">
      <div class="all-occurrences-item-row" style="width: 30%;">
        <p><b>Occurrence {{ occurrence.id }} - Client {{occurrence.usernameClient}}</b></p>
        <p><b>Repairer:</b> {{occurrence.usernameRepairer==undefined ? "not associated" : occurrence.usernameRepairer}}</p>
        <p style="white-space: pre;"><b>Description:</b> <br>{{ occurrence.description }}</p>
        <p style="margin-bottom: 0;"><b>Entry Date:</b> {{occurrence.entryDate}} &nbsp; </p>
        <p><b>Final Date:</b> {{occurrence.finalDate==undefined?"---":occurrence.finalDate}}</p>
      </div>

      <b-form @submit.prevent="onSubmit" :disabled="!isFormValid" class="flex-grow-1" style="margin: 0 6%"  v-if="occurrence.state.toLowerCase() === 'active' && isAssigned">
        <p>Appointments: </p>
        <b-form-group :invalid-feedback="invalidDescriptionFeedback" :state="isDescriptionValid">
          <b-textarea :state="isDescriptionValid" class="form-control" style="margin-bottom: 20px;" placeholder="Enter some thoughts on your decision" v-model="descriptionApprovePending" required/>
        </b-form-group>

        <div style="display: flex">
          <button type="submit" value="fail" class="btn btn-active-occurrence" @click="fail(occurrence.id)">Fail</button>
          <button type="submit" value="finish" class="btn btn-active-occurrence" @click="finish(occurrence.id)" style="margin-left: auto" >Finish</button>
        </div>
      </b-form>

      <div class="all-occurrences-item-row flex-grow-1" :class="{'all-occurrences-item-last': occurrence.state == 'Approved'}" style="text-align: end;">
        <p class="text-uppercase"  style="width: 13rem; margin-left: auto;">{{ occurrence.state.split('_').join(' ') }}</p>
            <div v-if="isAssigned &&
                        occurrence.state==='REPAIRER_WAITING_LIST'">
              <button  class="btn btn-repairer-button" @click.prevent="start(occurrence.id)" :disabled="waitingRefresh">Start</button>
            </div>
<!--        <div v-if="!isAssigned &&-->
<!--                    occurrence.state!=='PENDING' &&-->
<!--                    occurrence.state!=='APPROVED' &&-->
<!--                    occurrence.state!=='FAILED' &&-->
<!--                    occurrence.state!=='RESOLVED' &&-->
<!--                    occurrence.state!=='DISAPPROVED'">-->
<!--          <button  class="btn btn-associate-repairers" @click.prevent="assign(occurrence.id)" :disabled="waitingRefresh">Assign</button>-->
<!--        </div>-->
<!--        <div v-else-if="occurrence.state!=='PENDING' &&-->
<!--                    occurrence.state!=='APPROVED' &&-->
<!--                    occurrence.state!=='FAILED' &&-->
<!--                    occurrence.state!=='RESOLVED' &&-->
<!--                    occurrence.state!=='DISAPPROVED'">-->
<!--          <button class="btn btn-associate-repairers" @click.prevent="unassign(occurrence.id)" :disabled="waitingRefresh">Unassign</button>-->
<!--        </div>-->
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Occurrence",
  props: ['occurrence', 'isAssigned', 'waitingRefresh'],
  emits: ['updateOccurrences'],
  data(){
    return {
      approveOrDisapprove: "",
      descriptionApprovePending: null
    }
  },
  computed: {
    invalidDescriptionFeedback () {
      if (!this.descriptionApprovePending) {
        return null
      }
      let descriptionApprovePendingLen = this.descriptionApprovePending.length
      if (descriptionApprovePendingLen < 3 || descriptionApprovePendingLen > 15) {
        return 'The username must be between [3, 15] characters.'
      }
      return ''
    },
    isDescriptionValid () {
      if (this.invalidDescriptionFeedback === null) {
        return null
      }
      return this.invalidDescriptionFeedback === ''
    },
    isFormValid () {
      if (!this.isDescriptionValid) {
        return false
      }
      return true
    }
  },
  methods: {
    onSubmit() {
      console.log('teste')
    },
    start(occurence_id)
    {
      this.$axios.$patch(`/api/repairers/${this.$auth.user.username}/occurrences/${occurence_id}/start`)
        .then(()=> {
          this.$toast.success('Occurrence started!').goAway(3000)
          this.$emit('updateOccurrences')
      })
    },
    fail(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }

      this.$axios.$patch(`/api/repairers/${this.$auth.user.username}/occurrences/${occurence_id}/fail`, {
        description: this.descriptionApprovePending
      })
        .then(()=> {
          this.$toast.success('Occurrence failed!').goAway(3000)
          this.$emit('updateOccurrences')
        })
    },
    finish(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }

      this.$axios.$patch(`/api/repairers/${this.$auth.user.username}/occurrences/${occurence_id}/finish`, {
        description: this.descriptionApprovePending
      })
        .then(()=> {
          this.$toast.success('Occurrence finished!').goAway(3000)
          this.$emit('updateOccurrences')
        })
    },
    // assign(occurence_id)
    // {
    //   this.$axios.$patch(`/api/repairers/${this.$auth.user.username}/occurrences/${occurence_id}/assign`)
    //     .then(()=> {
    //       this.$emit('updateOccurrences')
    //     })
    // },
    // unassign(occurence_id)
    // {
    //   this.$axios.$patch(`/api/repairers/${this.$auth.user.username}/occurrences/${occurence_id}/unassign`)
    //     .then(()=> {
    //       this.$emit('updateOccurrences')
    //     })
    // }
  }
}
</script>

<style scoped>

.all-occurrences-item-last{
  text-align: end;
  align-self: flex-end;
  margin-bottom: 1.5%;
}

.btn-active-occurrence:hover{
  background-color: red !important;
  color: white !important;
}

.btn-active-occurrence{
  border: 1px solid black;
  width: 45%;
  height: 2.5rem;
  align-self: self-end;
}

/*.btn-start-occurrence:hover{*/
/*  background-color: red !important;*/
/*  color: white !important;*/
/*}*/

/*.btn-start-occurrence{*/
/*  border: 1px solid black;*/
/*  width: 45%;*/
/*  height: 2.5rem;*/
/*  align-self: self-end;*/
/*}*/

/*.btn-repairer-button-active:hover{*/
/*  background-color: red !important;*/
/*  color: white !important;*/
/*}*/

/*.btn-repairer-button-active{*/
/*  border: 1px solid black;*/
/*  height: 3rem;*/
/*  width: 5rem;*/
/*}*/

.btn-repairer-button:hover{
  background-color: red !important;
  color: white !important;
}

.btn-repairer-button{
  border: 1px solid black;
  height: 3rem;
  width: 10rem;
}

.all-occurrences-item-row{
  display: flex;
  flex-direction: column;
}

.all-occurrences-item{
  display: flex;
  flex-direction: row;
  align-items: center;
}

.all-occurrences{
  background-color: white;
  height: fit-content;
  padding: 20px;
  margin: 30px 0;
}

</style>
