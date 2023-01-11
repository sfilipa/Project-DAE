import io from 'socket.io-client'

export default (ctx, inject) => {
  const socket = io('http://localhost:8081')
  inject('socket', socket)
}
