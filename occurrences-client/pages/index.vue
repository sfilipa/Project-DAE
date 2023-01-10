<template>
  <div v-if="this.$auth.user">
    <ClientHome v-if="this.$auth.user.role.toLowerCase() == 'client'"></ClientHome>
    <ExpertHome v-else-if="this.$auth.user.role.toLowerCase() == 'expert'"></ExpertHome>
    <RepairerHome v-else-if="this.$auth.user.role.toLowerCase() == 'repairer'"></RepairerHome>
    <AdminHome v-else-if="this.$auth.user.role.toLowerCase() == 'administrator'"></AdminHome>
  </div>
</template>

<script>
import ClientHome from "~/pages/clients/index.vue";
import ExpertHome from "~/pages/experts/index.vue";
import RepairerHome from "~/pages/repairers/index.vue";
import AdminHome from "~/pages/administrators/index.vue";
export default {
  components: {
    ClientHome,
    ExpertHome,
    RepairerHome,
    AdminHome
  },
  name: "index.vue",
}
</script>

<style scoped>

</style>
