class WebSocket {
  constructor() {
    this.stompClient = null
  }

  connect(connect, callback) {
    let socket = new SockJS(connect)
    this.stompClient = Stomp.over(socket)
    this.stompClient.connect({}, function (frame) {
      callback()
    })
  }

  disconnect() {
    if (this.isConnected()) {
      this.stompClient.disconnect()
    }
  }

  send(controller, json) {
    setTimeout(() => {
      this.stompClient.send(controller, {}, JSON.stringify(json))
    }, 1000)
  }

  subscribe(topic, onMessage) {
    setTimeout(() => {
      this.stompClient.subscribe(topic, onMessage)
    }, 1000)
  }

  isConnected() {
    return this.stompClient !== null
  }
}
