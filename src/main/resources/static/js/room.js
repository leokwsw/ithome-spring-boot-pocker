var websocket = new WebSocket()
var myUsername = $("#my-username").val()
var roomId
var owner

websocket.connect("/connect", () => {
  subscribeRoomInfo()
  subscribeReady()  // 記得加入
  websocket.send(`/room-info`, {})
})


function subscribeRoomInfo() {
  websocket.subscribe(`/user/queue/room-info`, (response) => {
    response = JSON.parse(response.body)
    let roomInfo = response.roomInfo
    owner = roomInfo.owner
    roomId = roomInfo.roomId
    $("#room-id").text(roomId)

    let roomMembers = [owner, ...roomInfo.guests]
    if (!roomMembers.includes(myUsername)) {
      window.location.href = `/rooms/`
    }

    let userStatus = response.userStatus
    $("#room .card").each((index, element) => {
      let name = roomMembers[index]
      if (name === myUsername) {
        $(element).addClass("shadow rounded")
      }

      let closeButton = ""
      if (owner === myUsername && name !== myUsername) {
        closeButton = `
                <button type="button" class="close close-button" onclick="quitRoom('${name}')">
                    <i class="bi bi-x-circle-fill"></i>
                </button>
                `
      }

      let readyText = ""
      if (userStatus[name]) {
        readyText = "準備"
      }
      if (name === owner) {
        readyText = "房主"
      }

      $(element).prop("id", `user-${name}`)
      $(element).empty()
      if (index < roomMembers.length) {
        $(element).html(`
                ${closeButton}
                <img src="https://picsum.photos/200/200" class="card-img-top" alt="...">
                <div class="card-body">
                    <h5 class="card-title">${name}</h5>
                    <h4 class="card-body user-ready">${readyText}</h4>
                </div>
                `)
      } else {
        $(element).html(`
                <div class="card-body">
                    <h5 class="card-title"></h5>
                </div>
                `)
      }
    })

    $("#ready").text(owner === myUsername ? "開始" : "準備")
  })
}

function subscribeReady() {
  websocket.subscribe("/user/queue/ready", (response) => {
    response = JSON.parse(response.body)
    let userStatus = response.userReadyStatus
    for (let user in userStatus) {
      let readyText = userStatus[user] ? "準備" : ""
      if (user === owner) readyText = "房主"
      $(`#user-${user} .user-ready`).text(readyText)
    }

    // 當全部都準備好，跳轉到遊戲畫面
    if (response.allReady) {
      window.location.href = `/game/${roomId}`
    }
  })
}

function ready() {
  let myUsername = $("#my-username").val()

  // 判斷依據: 看是不是「準備」
  let status = $(`#user-${myUsername} .user-ready`).text() === "準備" ? true : false
  websocket.send(`/ready`, {
    ready: !status,
  })
  $("#ready").attr("disabled", true)
}

// 綁定退出圖案的按鈕
$("#quit-room-button").click(() => {
  quitRoom(myUsername)
})

function quitRoom(username) {
  let data = {
    username: username,
    roomId: roomId,
  }
  jq.post(
    "/room/quit",
    data,
    (response) => {
      console.log(response)
    }
  )
    .fail(function (e) {
      console.log(e)
    })
}
