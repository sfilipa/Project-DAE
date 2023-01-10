<template>
  <div class="modal-overlay" @click="$emit('close-modal')">
    <div class="modal" @click.stop>
      <h5><b>Delete Confirmation</b></h5>
      <div class="modal-description">
        <span>{{ message }}</span>
        <span>You can't undo this action</span>
      </div>
      <button class="btn btn-cancel" @click="$emit('close-modal')">Cancel</button>
      <button class="btn btn-delete" @click="$emit('delete')">Delete</button>
    </div>
  </div>
</template>

<script>
export default {
  name: "DeleteModal",
  props: ["message"]
}
</script>

<style scoped>

.modal-description{
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 2rem;
}

.modal-overlay {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.45);
}

.modal {
  display: block;
  text-align: center;
  background-color: white !important;
  height: 16rem;
  width: 27rem;
  padding: 1rem 0;
  border-radius: 15px;
  position: relative;
  animation: drop 0.5s ease forwards;
  margin-left: 7rem;
}

@keyframes drop {
  0%{ opacity: 0}
  70%{transform: translateY(35%)}
  100%{transform: translateY(30%); opacity: 1;}
}

p,span {
  font-size: 16px;
  margin: 0px 20px;
}

.btn-cancel:hover {
  background-color: #eeeeee;
  border: 1px solid black;
}

.btn-cancel {
  margin-right: 2rem;
}

.btn-delete:hover {
  background-color: #d20202;
  color: white;
  border: 1px solid black;
}


.btn-delete {
  background-color: red;
  color: white;
}

button{
  width: 8rem;
  height: 3rem;
  border: 1px solid black;
  margin-top: 3rem;
}
</style>
