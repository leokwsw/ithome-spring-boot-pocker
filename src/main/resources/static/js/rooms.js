var websocket = new WebSocket()
websocket.connect("/connect", () => {
  getRoomList()
})

function getRoomList() {
  // 訂閱，並定義收到訊息該做什麼操作
  websocket.subscribe("/topic/room-list", (response) => {
    // 處理接收到的資料
    response = JSON.parse(response.body)
    let rooms = response["rooms"]

    // 顯示房間列表
    $("#rooms tbody").empty()
    let roomList = $("#rooms tbody").html()
    for (let room of rooms) {
      roomList += `
            <tr>
                <th scope="row">${room.roomId}</th>
                <td>${room.owner}</td>
                <td>${room.info.number} / 4</td>
                <td>
                    <button type="button" class="btn btn-outline-primary" onclick="joinRoom('${room.roomId}')">
                        加入
                    </button>
                </td>
            </tr>
            `
    }
    $("#rooms tbody").html(roomList)
  })

  // 發送訊息給 Server 表示要取得房間列表的資料
  websocket.send(`/room-list`, {})
}

function createRoom() {
  let data = {
    action: "create",
  }
  jq.post(
    "/api/room/join",
    data,
    (response) => {
      window.location.href = `/room/${response.roomId}`
    }
  )
    .fail(function (e) {
      $("#alert-toast-title").text(e.responseJSON.message)
      $("#alert-toast").toast("show")
    })
}

function joinRoom(roomId) {
  let data = {
    action: "join",
    roomId: roomId,
  }
  jq.post(
    "/api/room/join",
    data,
    (response) => {
      window.location.href = `/room/${response.roomId}`
    }
  )
    .fail(function (e) {
      $("#alert-toast-title").text(e.responseJSON.message)
      $("#alert-toast").toast("show")
    })
}

$(document).ready(() => {
  $("#create").click(function () {
    createRoom()
  })
})
