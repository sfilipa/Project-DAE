<template>
  <div class="user-list-body" >
    <div v-for="repairer in repairers" class="user-row">
      <div class="user-column">
        <span  class="fw-bold">{{repairer.name}} ({{repairer.username}})</span>
        <span>
          Address:&nbsp; {{repairer.address}}
        </span>
        <span>
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-envelope-at" viewBox="0 0 16 16">
                  <path d="M2 2a2 2 0 0 0-2 2v8.01A2 2 0 0 0 2 14h5.5a.5.5 0 0 0 0-1H2a1 1 0 0 1-.966-.741l5.64-3.471L8 9.583l7-4.2V8.5a.5.5 0 0 0 1 0V4a2 2 0 0 0-2-2H2Zm3.708 6.208L1 11.105V5.383l4.708 2.825ZM1 4.217V4a1 1 0 0 1 1-1h12a1 1 0 0 1 1 1v.217l-7 4.2-7-4.2Z"/>
                  <path d="M14.247 14.269c1.01 0 1.587-.857 1.587-2.025v-.21C15.834 10.43 14.64 9 12.52 9h-.035C10.42 9 9 10.36 9 12.432v.214C9 14.82 10.438 16 12.358 16h.044c.594 0 1.018-.074 1.237-.175v-.73c-.245.11-.673.18-1.18.18h-.044c-1.334 0-2.571-.788-2.571-2.655v-.157c0-1.657 1.058-2.724 2.64-2.724h.04c1.535 0 2.484 1.05 2.484 2.326v.118c0 .975-.324 1.39-.639 1.39-.232 0-.41-.148-.41-.42v-2.19h-.906v.569h-.03c-.084-.298-.368-.63-.954-.63-.778 0-1.259.555-1.259 1.4v.528c0 .892.49 1.434 1.26 1.434.471 0 .896-.227 1.014-.643h.043c.118.42.617.648 1.12.648Zm-2.453-1.588v-.227c0-.546.227-.791.573-.791.297 0 .572.192.572.708v.367c0 .573-.253.744-.564.744-.354 0-.581-.215-.581-.8Z"/>
                </svg>
                <span style="position: relative; top: 2px;">Email:&nbsp; {{repairer.email}}</span>
              </span>
      </div>
      <div style="margin-left: auto;">
        <a style="cursor: pointer" @click="showModal = true; repairerToDelete = repairer">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-trash3" viewBox="0 0 16 16">
            <path d="M6.5 1h3a.5.5 0 0 1 .5.5v1H6v-1a.5.5 0 0 1 .5-.5ZM11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3A1.5 1.5 0 0 0 5 1.5v1H2.506a.58.58 0 0 0-.01 0H1.5a.5.5 0 0 0 0 1h.538l.853 10.66A2 2 0 0 0 4.885 16h6.23a2 2 0 0 0 1.994-1.84l.853-10.66h.538a.5.5 0 0 0 0-1h-.995a.59.59 0 0 0-.01 0H11Zm1.958 1-.846 10.58a1 1 0 0 1-.997.92h-6.23a1 1 0 0 1-.997-.92L3.042 3.5h9.916Zm-7.487 1a.5.5 0 0 1 .528.47l.5 8.5a.5.5 0 0 1-.998.06L5 5.03a.5.5 0 0 1 .47-.53Zm5.058 0a.5.5 0 0 1 .47.53l-.5 8.5a.5.5 0 1 1-.998-.06l.5-8.5a.5.5 0 0 1 .528-.47ZM8 4.5a.5.5 0 0 1 .5.5v8.5a.5.5 0 0 1-1 0V5a.5.5 0 0 1 .5-.5Z"/>
          </svg>
        </a>
      </div>
    </div>

    <DeleteModal v-show="showModal" @close-modal="showModal = false" @delete="deleteRepairer(repairerToDelete.username)" :message="'You sure you want to delete the '+repairerToDelete.username+'\'s account?'"/>
  </div>
</template>

<script>
import DeleteModal from "~/pages/components/DeleteModal.vue"
export default {
  components: {
    DeleteModal
  },
  name: "RepairerBody",
  props: ["repairers"],
  emits: ['updateRepairers'],
  data(){
    return {
      showModal: false,
      repairerToDelete: {}
    }
  },
  methods: {
    deleteRepairer(repairer_username){
      this.$axios.delete(`api/repairers/${repairer_username}`)
        .then(()=>{
          this.$emit('updateRepairers')
          this.$toast.success(`Repairer ${repairer_username} has been deleted!`).goAway(3000)
          this.showModal = false
        })
    }
  }
}
</script>

<style scoped>
.bi-trash3:hover{
  color: #940000 !important;
}

.bi-trash3{
  color: #c60000;
}

.user-column{
  display: flex;
  flex-direction: column;
}

.user-row{
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
  border-left: 6px solid red;
  padding: 0.5rem 1.5rem;
  margin: 1rem 0;
  background-color: white;
}


.user-list-body{
  padding: 0.5rem 2rem;
  background: white;
  margin: 1rem 3rem;
  display: flex;
  flex-direction: column !important;
  align-items: center;
}

</style>
