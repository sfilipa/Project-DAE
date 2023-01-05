<template>
  <div class="occurrences-div">
    <p style="font-size: 20px"><b>{{occurrence.objectInsured}}
      ({{ occurrence.coverageType.charAt(0).toUpperCase() + occurrence.coverageType.split('_').join(' ').slice(1).toLowerCase() }})
      - <span>{{occurrence.insuranceCode}}</span></b></p>
    <div class="occurrences-item" >
      <div class="occurrences-item-row" style="width: 30%;">
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
      </div>
    </div>
    <div v-if="occurrence.state.toUpperCase() == 'APPROVED'">
      <hr>
      <button class="btn btn-associate-repairers" @click.prevent="associateRepairer">Associate Repairer</button>
    </div>
  </div>
</template>

<script>
export default {
  name: "Occurrence",
  props: ['occurrence'],
  methods: {
    associateRepairer(){
      //Enviar mail ao repairer com o link api/occurrences/{id}
    }
  }
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

.occurrences-div{
  background-color: white;
  height: fit-content;
  padding: 20px;
  margin: 30px 0;
}

.occurrences-item{
  display: flex;
  flex-direction: row;
  align-items: center;
}

</style>
