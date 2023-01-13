<template>
  <div class="nuxt-page">
    <div v-if="this.$auth.user" class="nuxt-sidebar justify-left overflow-y-auto">
      <h4 class="uppercase font-bold py-1 nuxt-sidebar-name">
        Incidentalists
      </h4>
      <div class="sidebar-header-welcome">
        <span>Welcome back,</span>
        <p class="nuxt-sidebar-user-name" style="margin: auto;">{{ this.$auth.user.name }}</p>
      </div>
      <hr class="sidebar-header-welcome" style="margin-bottom: 10%;">
      <nuxt-link to="/" class="flex items-center" style="padding: 10px;">
        <div class="sidebar-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-house" viewBox="0 0 16 16">
            <path d="M8.707 1.5a1 1 0 0 0-1.414 0L.646 8.146a.5.5 0 0 0 .708.708L2 8.207V13.5A1.5 1.5 0 0 0 3.5 15h9a1.5 1.5 0 0 0 1.5-1.5V8.207l.646.647a.5.5 0 0 0 .708-.708L13 5.793V2.5a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5v1.293L8.707 1.5ZM13 7.207V13.5a.5.5 0 0 1-.5.5h-9a.5.5 0 0 1-.5-.5V7.207l5-5 5 5Z"/>
          </svg>
          <span>&nbsp; Home</span>
        </div>
      </nuxt-link>
      <nuxt-link v-if="this.$auth.user.role.toLowerCase()=='client'" :to="{name: `${this.$auth.user.role.toLowerCase()}s-insurances`}" class="flex items-center" style="padding: 10px;">
        <div class="sidebar-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" class="bi bi-wallet2" viewBox="0 0 16 16">
            <path d="M12.136.326A1.5 1.5 0 0 1 14 1.78V3h.5A1.5 1.5 0 0 1 16 4.5v9a1.5 1.5 0 0 1-1.5 1.5h-13A1.5 1.5 0 0 1 0 13.5v-9a1.5 1.5 0 0 1 1.432-1.499L12.136.326zM5.562 3H13V1.78a.5.5 0 0 0-.621-.484L5.562 3zM1.5 4a.5.5 0 0 0-.5.5v9a.5.5 0 0 0 .5.5h13a.5.5 0 0 0 .5-.5v-9a.5.5 0 0 0-.5-.5h-13z"/>
          </svg>
          <span>&nbsp; My Insurances</span>
        </div>
      </nuxt-link>

      <nuxt-link v-if="this.$auth.user.role.toLowerCase()=='expert' || this.$auth.user.role.toLowerCase()=='repairer' || this.$auth.user.role.toLowerCase()=='administrator'" :to="{name: `${this.$auth.user.role.toLowerCase()}s-occurrences-current`}" class="flex items-center" style="padding: 10px;">
        <div class="sidebar-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-exclamation-triangle" viewBox="0 0 16 16">
            <path d="M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.146.146 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.163.163 0 0 1-.054.06.116.116 0 0 1-.066.017H1.146a.115.115 0 0 1-.066-.017.163.163 0 0 1-.054-.06.176.176 0 0 1 .002-.183L7.884 2.073a.147.147 0 0 1 .054-.057zm1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566z"/>
            <path d="M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995z"/>
          </svg>
          <span>&nbsp; Occurrences</span>
        </div>
      </nuxt-link>

      <nuxt-link v-if="this.$auth.user.role.toLowerCase()=='client' || this.$auth.user.role.toLowerCase()=='expert' || this.$auth.user.role.toLowerCase()=='repairer'" :to="{name: `${this.$auth.user.role.toLowerCase()}s-occurrences`}" class="flex items-center" style="padding: 10px;">
        <div class="sidebar-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-exclamation-triangle" viewBox="0 0 16 16">
            <path d="M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.146.146 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.163.163 0 0 1-.054.06.116.116 0 0 1-.066.017H1.146a.115.115 0 0 1-.066-.017.163.163 0 0 1-.054-.06.176.176 0 0 1 .002-.183L7.884 2.073a.147.147 0 0 1 .054-.057zm1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566z"/>
            <path d="M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995z"/>
          </svg>
          <span>&nbsp; My Occurrences</span>
        </div>
      </nuxt-link>

      <nuxt-link v-if="this.$auth.user.role.toLowerCase()=='administrator'" :to="{name: `${this.$auth.user.role.toLowerCase()}s-clients`}" class="flex items-center" style="padding: 10px;">
        <div class="sidebar-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-people" viewBox="0 0 16 16">
            <path d="M15 14s1 0 1-1-1-4-5-4-5 3-5 4 1 1 1 1h8Zm-7.978-1A.261.261 0 0 1 7 12.996c.001-.264.167-1.03.76-1.72C8.312 10.629 9.282 10 11 10c1.717 0 2.687.63 3.24 1.276.593.69.758 1.457.76 1.72l-.008.002a.274.274 0 0 1-.014.002H7.022ZM11 7a2 2 0 1 0 0-4 2 2 0 0 0 0 4Zm3-2a3 3 0 1 1-6 0 3 3 0 0 1 6 0ZM6.936 9.28a5.88 5.88 0 0 0-1.23-.247A7.35 7.35 0 0 0 5 9c-4 0-5 3-5 4 0 .667.333 1 1 1h4.216A2.238 2.238 0 0 1 5 13c0-1.01.377-2.042 1.09-2.904.243-.294.526-.569.846-.816ZM4.92 10A5.493 5.493 0 0 0 4 13H1c0-.26.164-1.03.76-1.724.545-.636 1.492-1.256 3.16-1.275ZM1.5 5.5a3 3 0 1 1 6 0 3 3 0 0 1-6 0Zm3-2a2 2 0 1 0 0 4 2 2 0 0 0 0-4Z"/>
          </svg>
          <span>&nbsp; Clients</span>
        </div>
      </nuxt-link>

      <nuxt-link v-if="this.$auth.user.role.toLowerCase()=='administrator'" :to="{name: `${this.$auth.user.role.toLowerCase()}s-experts`}" class="flex items-center" style="padding: 10px;">
        <div class="sidebar-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-clipboard-data" viewBox="0 0 16 16">
            <path d="M4 11a1 1 0 1 1 2 0v1a1 1 0 1 1-2 0v-1zm6-4a1 1 0 1 1 2 0v5a1 1 0 1 1-2 0V7zM7 9a1 1 0 0 1 2 0v3a1 1 0 1 1-2 0V9z"/>
            <path d="M4 1.5H3a2 2 0 0 0-2 2V14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V3.5a2 2 0 0 0-2-2h-1v1h1a1 1 0 0 1 1 1V14a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V3.5a1 1 0 0 1 1-1h1v-1z"/>
            <path d="M9.5 1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-3a.5.5 0 0 1-.5-.5v-1a.5.5 0 0 1 .5-.5h3zm-3-1A1.5 1.5 0 0 0 5 1.5v1A1.5 1.5 0 0 0 6.5 4h3A1.5 1.5 0 0 0 11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3z"/>
          </svg>
          <span>&nbsp; Experts</span>
        </div>
      </nuxt-link>

      <nuxt-link v-if="this.$auth.user.role.toLowerCase()=='administrator'" :to="{name: `${this.$auth.user.role.toLowerCase()}s-repairers`}" class="flex items-center" style="padding: 10px;">
        <div class="sidebar-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-tools" viewBox="0 0 16 16">
            <path d="M1 0 0 1l2.2 3.081a1 1 0 0 0 .815.419h.07a1 1 0 0 1 .708.293l2.675 2.675-2.617 2.654A3.003 3.003 0 0 0 0 13a3 3 0 1 0 5.878-.851l2.654-2.617.968.968-.305.914a1 1 0 0 0 .242 1.023l3.27 3.27a.997.997 0 0 0 1.414 0l1.586-1.586a.997.997 0 0 0 0-1.414l-3.27-3.27a1 1 0 0 0-1.023-.242L10.5 9.5l-.96-.96 2.68-2.643A3.005 3.005 0 0 0 16 3c0-.269-.035-.53-.102-.777l-2.14 2.141L12 4l-.364-1.757L13.777.102a3 3 0 0 0-3.675 3.68L7.462 6.46 4.793 3.793a1 1 0 0 1-.293-.707v-.071a1 1 0 0 0-.419-.814L1 0Zm9.646 10.646a.5.5 0 0 1 .708 0l2.914 2.915a.5.5 0 0 1-.707.707l-2.915-2.914a.5.5 0 0 1 0-.708ZM3 11l.471.242.529.026.287.445.445.287.026.529L5 13l-.242.471-.026.529-.445.287-.287.445-.529.026L3 15l-.471-.242L2 14.732l-.287-.445L1.268 14l-.026-.529L1 13l.242-.471.026-.529.445-.287.287-.445.529-.026L3 11Z"/>
          </svg>
          <span>&nbsp; Repairers</span>
        </div>
      </nuxt-link>

      <nuxt-link :to="{name: `${this.$auth.user.role.toLowerCase()}s-username`, params: {username:this.$auth.user.username}}" class="flex items-center" style="padding: 10px;">
        <div class="sidebar-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-person" viewBox="0 0 16 16">
            <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6Zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0Zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4Zm-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10c-2.29 0-3.516.68-4.168 1.332-.678.678-.83 1.418-.832 1.664h10Z"/>
          </svg>
          <span>&nbsp; Profile</span>
        </div>
      </nuxt-link>

      <div class="sidebar-logout">
        <hr class="vertical-hr">
        <a @click="logout" class="flex items-center sidebar-logout-link">
          <div class="sidebar-link"  style="padding: 0 20px; cursor: pointer">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-box-arrow-right" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M10 12.5a.5.5 0 0 1-.5.5h-8a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5h8a.5.5 0 0 1 .5.5v2a.5.5 0 0 0 1 0v-2A1.5 1.5 0 0 0 9.5 2h-8A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h8a1.5 1.5 0 0 0 1.5-1.5v-2a.5.5 0 0 0-1 0v2z"/>
              <path fill-rule="evenodd" d="M15.854 8.354a.5.5 0 0 0 0-.708l-3-3a.5.5 0 0 0-.708.708L14.293 7.5H5.5a.5.5 0 0 0 0 1h8.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3z"/>
            </svg>
            <span>&nbsp; Logout</span>
          </div>
        </a>
      </div>
    </div>
    <div class="nuxt-body">
      <Nuxt />
    </div>
  </div>
</template>

<script>
export default {
  mounted() {
    this.$socket.emit('loggedIn', this.$auth.user)
  },
  methods: {
    logout() {
      this.$auth.logout()
      this.$router.push('/auth/login')
    }
  },
  data() {
    return {
      role: "clients"
    }
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Montserrat&display=swap');

.spinner-div{
  display: flex;
  flex-direction: column;
  align-items: center;
}


select:focus{
  border-color: darkred !important;
  box-shadow: 0 0 0 0.25rem rgba(255, 106, 106, 0.25) !important;
}

input:focus{
  border-color: darkred !important;
  box-shadow: 0 0 0 0.25rem rgba(255, 106, 106, 0.25) !important;
}

input:checked{
  background-color: red !important;
  border-color: #ff9696 !important;
}
.form-control:focus{
  border-color: darkred;
  box-shadow: 0 0 0 0.25rem rgba(255, 106, 106, 0.25);
}

.sidebar-logout-link{
  padding: 10px;
  display: contents;
}

.sidebar-logout{
  margin-top: auto;
  height: fit-content;
}

a.nuxt-link-exact-active:hover {
  color: ghostwhite !important;
}

/* exact link will show the primary color for only the exact matching link */
a.nuxt-link-exact-active {
  color: white !important;
  font-weight: bold;
  background-color: red;
}

.sidebar-link{
  display: flex;
  flex-direction: row;
  align-items: center;
}

.nuxt-sidebar-user-name{
  text-decoration: underline;
  font-weight: bold;
  color: black;
  font-size: 24px;
}

.nuxt-sidebar-name{
  color: red;
  font-weight: bold;
}

.nuxt-body{
  display: flex;
  flex-direction: column;
  padding: 40px;
  width: 100%;
  background-image: url("../images/backgroundHome.jpg");
  background-repeat: repeat;
  background-size: cover;
  overflow: auto;
}

.nuxt-sidebar{
  display: flex;
  flex-direction: column;
  width: 15rem;
  padding: 20px;
  box-shadow: -8px 0 27px -2px #888;
  font-size: 16px;
  position: relative;
}

.nuxt-page{
  display: flex;
  flex-direction: row;
  font-family: 'Montserrat', sans-serif;
  font-size: 18px;
}

a:hover{
  color: gray !important;
}

a{
  color: black !important;
  text-decoration: none;
}

html{
  overflow:hidden;
  font-family: 'Montserrat', sans-serif;
  font-size: 18px;
}

html, body, div#__nuxt, div#__layout, .nuxt-page{
  min-height: 100% !important;
  height: 100% !important;
  width: 100% !important;
}

@media only screen and (max-width: 1200px) {
  .nuxt-body{
    height: 100%;
  }

  .sidebar-header-welcome{
    display: none;
  }

  .nuxt-page{
    flex-direction: column;
  }

  .nuxt-sidebar{
    width: 100%;
    flex-direction: row;
    height: fit-content;
  }

  h4{
    width: fit-content;
  }

  .nuxt-sidebar a{
    margin: 0 1rem !important;
    height: fit-content;
  }

  .vertical-hr{
    border-top: 0;
    border-left: 1px solid;
    height: 2rem;
    margin: 0;
    width: 0;
  }

  .sidebar-logout{
    display: flex;
    margin: 3px 0;
  }


  .sidebar-link span{
    width: max-content;
  }

  .nuxt-sidebar-name{
    width: 100%;
  }

}

@media only screen and (max-width: 1050px) {
  .nuxt-sidebar{
    font-size: 15px;
  }

  h4{
    font-size: 20px;
  }

  .nuxt-sidebar a{
    margin: 0 0.5rem !important;
    height: fit-content;
  }
}

@media only screen and (max-width: 870px) {

  .nuxt-sidebar{
    font-size: 12px;
    overflow-x: auto;
    height: 25%;
  }

  h4{
    font-size: 17px;
  }

}

</style>
