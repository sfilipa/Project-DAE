<template>
  <div class="all-occurrences">
    <p style="font-size: 20px; color: red"><b>{{occurrence.objectInsured}}
      ({{ occurrence.coverageType.charAt(0).toUpperCase() + occurrence.coverageType.split('_').join(' ').slice(1).toLowerCase() }})
      - <span>{{occurrence.insuranceCompanyName}} ({{occurrence.insuranceCode}})</span></b></p>
    <div class="all-occurrences-item">
      <div class="all-occurrences-item-row" style="width: 30%;">
        <p><b>Occurrence {{ occurrence.id }} - Client {{occurrence.usernameClient}}</b></p>
        <p><b>Repairer:</b> {{occurrence.usernameRepairer==undefined ? "not associated" : occurrence.usernameRepairer}}</p>
        <p style="white-space: pre;"><b>Description:</b> <br>{{ occurrence.description }}</p>
        <p style="margin-bottom: 0;"><b>Entry Date:</b> {{occurrence.entryDate}} &nbsp; </p>
        <p><b>Final Date:</b> {{occurrence.finalDate==undefined?"---":occurrence.finalDate}}</p>
      </div>

      <b-form @submit.prevent="onSubmit" :disabled="!isFormValid" class="flex-grow-1" style="margin: 0 6%"
              v-if="occurrence.state.toLowerCase() === 'pending' && isAssigned">
        <p>Appointments on Occurrence: </p>
        <b-form-group :invalid-feedback="invalidDescriptionFeedback" :state="isDescriptionValid">
          <b-textarea :state="isDescriptionValid" class="form-control" style="margin-bottom: 20px;" placeholder="Enter some thoughts on your decision" v-model="descriptionApprovePending" required/>
        </b-form-group>

        <div style="display: flex">
          <button type="submit" value="disapprove" class="btn btn-approve-occurrence" @click="disapprove(occurrence.id)">Disapprove</button>
          <button type="submit" value="approve" class="btn btn-approve-occurrence" @click="approve(occurrence.id)" style="margin-left: auto" >Approve</button>
        </div>
      </b-form>

      <b-form @submit.prevent="onSubmit" :disabled="!isFormValid" class="flex-grow-1" style="margin: 0 6%"
              v-if="occurrence.state.toLowerCase() === 'waiting_for_approval_of_repairer_by_expert' && isAssigned">
        <p>Appointments on Repairer: </p>
        <b-form-group :invalid-feedback="invalidDescriptionFeedback" :state="isDescriptionValid">
          <b-textarea :state="isDescriptionValid" class="form-control" style="margin-bottom: 20px;" placeholder="Enter some thoughts on your decision" v-model="descriptionApprovePending" required/>
        </b-form-group>

        <div style="display: flex">
          <button type="submit" value="disapprove" class="btn btn-approve-occurrence" @click="rejectRepairer(occurrence.id)">Reject</button>
          <button type="submit" value="approve" class="btn btn-approve-occurrence" @click="acceptRepairer(occurrence.id)" style="margin-left: auto" >Accept</button>
        </div>
      </b-form>

      <div class="all-occurrences-item-row flex-grow-1" :class="{'all-occurrences-item-last': occurrence.state == 'Approved'}" style="text-align: end;">
        <p class="text-uppercase" style="width: 13rem; margin-left: auto;">{{ occurrence.state.split('_').join(' ') }}</p>
        <div v-if="!isAssigned &&
                    occurrence.state!=='REPAIRER_WAITING_LIST' &&
                    occurrence.state!=='ACTIVE' &&
                    occurrence.state!=='FAILED' &&
                    occurrence.state!=='RESOLVED' &&
                    occurrence.state!=='DISAPPROVED' && occurrence.insuranceCompanyName === this.company_username">
          <button  class="btn btn-associate-repairers" @click.prevent="assign(occurrence.id)" :disabled="waitingRefresh">Assign</button>
        </div>
        <div v-else-if="occurrence.state!=='REPAIRER_WAITING_LIST' &&
                        occurrence.state!=='ACTIVE' &&
                        occurrence.state!=='FAILED' &&
                        occurrence.state!=='RESOLVED' &&
                        occurrence.state!=='DISAPPROVED' && occurrence.insuranceCompanyName === this.company_username">
          <button class="btn btn-associate-repairers" @click.prevent="unassign(occurrence.id)" :disabled="waitingRefresh">Unassign</button>
        </div>
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
      descriptionApprovePending: null,
      approveOrDisapprove: "",
      company_username: null
    }
  },
  created() {
    this.$axios.$get(`/api/experts/${this.$auth.user.username}`)
      .then((response) => {
        this.company_username = response.company_username;
      })
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
    approve(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/approve`, {
        description: this.descriptionApprovePending
      }).then(()=> {
        this.descriptionApprovePending = "";
        this.$emit('updateOccurrences')
      })

    },
    disapprove(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/disapprove`, {
        description: this.descriptionApprovePending}
      ).then(()=> {
        this.descriptionApprovePending = "";
        this.$emit('updateOccurrences')
      })
    },
    acceptRepairer(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/acceptRepairer`, {
        description: this.descriptionApprovePending
      }).then(()=> {
        this.descriptionApprovePending = "";
        this.$emit('updateOccurrences')
      })

    },
    rejectRepairer(occurence_id)
    {
      if(this.descriptionApprovePending === null){
        return
      }
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/rejectRepairer`, {
        description: this.descriptionApprovePending}
      ).then(()=> {
        this.descriptionApprovePending = "";
        this.$emit('updateOccurrences')
      })
    },
    assign(occurence_id)
    {
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/assign`)
        .then(()=> {
          this.$emit('updateOccurrences')
        })
    },
    unassign(occurence_id)
    {
      this.$axios.$patch(`/api/experts/${this.$auth.user.username}/occurrences/${occurence_id}/unassign`)
        .then(()=> {
          this.$emit('updateOccurrences')
        })
    }
  }
}
</script>

<style scoped>

.all-occurrences-item-last{
  text-align: end;
  align-self: flex-end;
  margin-bottom: 1.5%;
}

.btn-approve-occurrence:hover{
  background-color: red !important;
  color: white !important;
}

.btn-approve-occurrence{
  border: 1px solid black;
  width: 45%;
  height: 2.5rem;
  align-self: self-end;
}

.btn-associate-repairers:hover{
  background-color: red !important;
  color: white !important;
}

.btn-associate-repairers{
  border: 1px solid black;
  width: fit-content;
  height: 3rem;
  align-self: self-end;
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
