<template>
  <div>
    <nuxt-link
      class="btn pb-3 pr-5 text-uppercase"
      :to="`/`">
      <div>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-chevron-left" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        &nbsp; Back to HomePage
      </div>
    </nuxt-link>

    <div class="occurrence-body">
      <h3 class="text-center index-header fw-bold">Unauthorized Content</h3>
      <div class="image-div"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Unauthorized"
}
</script>

<style scoped>
.image-div{
  background-image: url("@/images/unauthorized.png");
  width: 35rem;
  height: 20rem;
}

.occurrence-body{
  display: flex;
  flex-direction: column;
  align-items: center;
}

.index-header{
  margin-bottom: 3rem;
}

</style>
