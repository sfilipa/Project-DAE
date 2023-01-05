<template>
  <div class="occurrences-item" >
    <div class="occurrences-item-row" style="width: 30%;">
      <p style="font-size: 20px"><b>{{occurrence.objectInsured}} - <span>{{occurrence.insuranceCode}}</span></b></p>
      <p>Occurrence {{ occurrence.id }}</p>
      <p>Repairer: {{occurrence.usernameRepairer==undefined ? "not associated" : occurrence.usernameRepairer}}</p>
    </div>

    <div class="occurrences-item-row" style="align-self: flex-end;">
      <p>Entry Date: {{occurrence.entryDate}} &nbsp; Final Date: {{occurrence.finalDate==undefined?"---":occurrence.finalDate}}</p>
      <p>Description: {{ occurrence.description }}</p>
      <!--        <p>Documents: <span v-for="document in occurrence.documents"> {{ document.filename }};</span></p>-->
    </div>

    <div class="occurrences-item-row flex-grow-1" :class="{'occurrences-item-last': occurrence.state == 'Approved'}" style="text-align: end;">
      <p class="text-uppercase">{{ occurrence.state }}</p>
      <button v-if="occurrence.state.toUpperCase() == 'APPROVED'" class="btn btn-associate-repairers">Associate Repairer</button>
    </div>
  </div>
</template>

<script>
export default {
  name: "Occurrence",
  props: ['occurrence']
}
</script>

<style scoped>

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

.occurrences-item-last{
  text-align: end;
  align-self: flex-end;
  margin-bottom: 1.5%;
}

.occurrences-item-row{
  display: flex;
  flex-direction: column;
}

.occurrences-item{
  background-color: white;
  height: fit-content;
  padding: 20px;
  margin: 30px 0;
  display: flex;
  flex-direction: row;
  align-items: center;
}

</style>
