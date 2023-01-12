<template>
  <div style="display: flex; flex-direction: row;">
    <div style="margin: auto; width: 27%;">
      <ul style="list-style-type: none; width: 100%; margin: auto; ">
        <li>
          <button class="btn page-item" style="border-radius: 10px 0 0 10px;" @click="$emit('updateCurrentPage',1);">
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-chevron-double-left" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M8.354 1.646a.5.5 0 0 1 0 .708L2.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
              <path fill-rule="evenodd" d="M12.354 1.646a.5.5 0 0 1 0 .708L6.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
          </button>
        </li>
        <li>
          <button class="btn page-item" @click="$emit('updateCurrentPage',currentPage !== 1 ? currentPage = currentPage-1 : null)">
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-chevron-left" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
          </button>
        </li>
        <li v-for="page in pageCount">
          <button class="btn page-item"
                  :class="{active: page===currentPage}"
                  type="button"
                  @click="$emit('updateCurrentPage',page);" >{{page}}</button>
        </li>
        <li>
          <button class="btn page-item" @click="$emit('updateCurrentPage',currentPage !== pageCount ? currentPage = currentPage+1 : null);">
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-chevron-right" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M4.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L10.293 8 4.646 2.354a.5.5 0 0 1 0-.708z"/>
            </svg>
          </button>
        </li>
        <li>
          <button class="btn page-item" style="border-radius: 0 10px 10px 0; border-right: 1px solid #c8c8c8" @click="$emit('updateCurrentPage',pageCount);">
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-chevron-double-right" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M3.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L9.293 8 3.646 2.354a.5.5 0 0 1 0-.708z"/>
              <path fill-rule="evenodd" d="M7.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L13.293 8 7.646 2.354a.5.5 0 0 1 0-.708z"/>
            </svg>
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: "Paginate",
  props: ['currentPage', 'pageCount'],
  emits: ['updateCurrentPage']
}
</script>

<style scoped>
.active{
  color: white !important;
  background-color: #ff5b5b !important;
}

.page-item:hover{
  background-color: #ffa7a7;
}

.page-item{
  float: left;
  padding: 0.4rem 0.8rem;
  background-color: white;
  cursor: pointer;
  border-radius: 0;
  border: 0.1px solid #c8c8c8;
  border-right: 0;
}
</style>
