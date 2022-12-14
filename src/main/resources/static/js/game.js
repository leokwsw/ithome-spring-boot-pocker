var websocket = new WebSocket()
var myUsername = $("#my-username").val()
var selectedCards = new Set()

$(document).ready(() => {
  websocket.connect("/connect", () => {
    subscribeMyHands()
    subscribeHandsInfo()
    subscribeGame()
    subscribePlay()
    websocket.send("/my-hands", {})
    websocket.send("/hands-info", {})
  })
  relocateMyHands()
  relocatePlayedCards()
})

const SUITS = {
  SPADE: "♠️",
  HEART: "♥",
  DIAMOND: "♦",
  CLUB: "♣",
}
const NUMBERS = {
  ACE: "1",
  TWO: "2",
  THREE: "3",
  FOUR: "4",
  FIVE: "5",
  SIX: "6",
  SEVEN: "7",
  EIGHT: "8",
  NINE: "9",
  TEN: "10",
  JACK: "J",
  QUEEN: "Q",
  KING: "K",
}

const GENERAL_LEVEL = {
  SPADE: 4,
  HEART: 3,
  DIAMOND: 2,
  CLUB: 1,
  ACE: 1,
  TWO: 2,
  THREE: 3,
  FOUR: 4,
  FIVE: 5,
  SIX: 6,
  SEVEN: 7,
  EIGHT: 8,
  NINE: 9,
  TEN: 10,
  JACK: 11,
  QUEEN: 12,
  KING: 13,
}

const SYMBOLS = {
  "♠️": "SPADE",
  "♥": "HEART",
  "♦": "DIAMOND",
  "♣": "CLUB",
  "1": "ACE",
  "2": "TWO",
  "3": "THREE",
  "4": "FOUR",
  "5": "FIVE",
  "6": "SIX",
  "7": "SEVEN",
  "8": "EIGHT",
  "9": "NINE",
  "10": "TEN",
  "J": "JACK",
  "Q": "QUEEN",
  "K": "KING",
}

function subscribeMyHands() {
  websocket.subscribe("/user/queue/my-hands", (response) => {
    response = JSON.parse(response.body)

    let myHands = []
    for (let card of response) {
      myHands.push(convertCard(card))
    }
    generateMyHands(myHands)

    // 多了這邊~~
    setTimeout(() => {
      let sortedHands = response.sort(compareByGeneral)
      generateMyHands(sortedHands.map((card) => convertCard(card)))
    }, 1000)
  })
}

function compareByGeneral(a, b) {
  if (GENERAL_LEVEL[a.number] > GENERAL_LEVEL[b.number]) {
    return 1
  } else if (GENERAL_LEVEL[a.number] < GENERAL_LEVEL[b.number]) {
    return -1
  } else {
    return GENERAL_LEVEL[b.suit] - GENERAL_LEVEL[a.suit]
  }
}

function subscribeHandsInfo() {
  websocket.subscribe("/user/queue/hands-info", (response) => {
    response = JSON.parse(response.body)
    let allUsers = Object.keys(response)
    let index = 0
    let userIndex = 1
    let count = 1
    while (count < 4) {
      if (allUsers[index] === myUsername) {
        userIndex = 2
      }
      if (userIndex > 1) {
        generateOtherHands(userIndex, response[allUsers[index]])
        userIndex++
        count++
      }
      index = (index + 1) % 4
    }
  })
}

function generateMyHands(myHands) {
  $(".my-hands").empty()
  let index = 0
  for (let cards of myHands) {
    let tmp = cards.split("-")
    let color = (tmp[0] == SUITS.SPADE || tmp[0] == SUITS.CLUB) ? "#000000" : "#FF0000"
    $(".my-hands").append(`
            <div class="m-card text-center" style="color: ${color};" id="card-${tmp[0]}-${tmp[1]}" tabindex="${index++}">
                <div>
                ${tmp[0]}
                <br>
                ${tmp[1]}
                </div>
            </div>
        `)
  }
  relocateMyHands()

  // 清除選擇的
  selectedCards.clear()

  $(".my-hands .m-card").click((element) => {
    let id = element.currentTarget.id
    if (selectedCards.has(id)) {
      unselectCard(id)
    } else {
      selectCard(id)
    }
  })
}

function generateOtherHands(who, numbers) {
  $(`#user-${who}`).empty()
  for (let i = 0; i < numbers; i++) {
    $(`#user-${who}`).append(`
        <div class="m-card text-center"></div>
        `)
  }
}

function convertCard(card) {
  return `${SUITS[card.suit]}-${NUMBERS[card.number]}`
}

function relocateMyHands() {
  let newLocationX = ($(".game-window").width() - $(".my-hands").width()) / 2
  $(".my-hands").css("left", newLocationX)
}

function relocatePlayedCards() {
  let newLocationX = ($(".game-window").width() - $(".card-type").width()) / 2
  $(".card-type").css("left", newLocationX)
}

function selectCard(id) {
  // 加入剛才建立的 set
  selectedCards.add(id)

  // 新增被選擇時的特效
  $(`#${id}`).addClass("card-selected rounded")
}

function unselectCard(id) {
  // 從 set 移除
  selectedCards.delete(id)

  // 移除特效
  $(`#${id}`).removeClass("card-selected rounded")
}

function play() {
  let cards = Array.from(selectedCards)
  cards = cards.map((val) => {
    let tmp = val.split("-")
    let card = {
      suit: SYMBOLS[tmp[1]],
      number: SYMBOLS[tmp[2]],
    }
    return card
  })

  websocket.send("/play", {
    action: "play",
    playedCards: cards,
  })
}

function pass() {
  websocket.send("/play", {
    action: "pass",
  })
}

function subscribePlay() {
  websocket.subscribe("/user/queue/play", (response) => {
    response = JSON.parse(response.body)
    if (response.message === "success") {
      // 成功的話，更新手牌，並且更新其他人的牌數資訊
      websocket.send("/my-hands", {})
      websocket.send("/hands-info", {})
    } else {
      // 如果失敗要顯示錯誤訊息，這邊留給讀者發揮~~
    }
  })
}

function subscribeGame() {
  websocket.subscribe("/user/queue/game", (response) => {
    response = JSON.parse(response.body)
    $(".timer").text(response.timer)

    let disabledButton = response.currentPlayer !== myUsername

    $("#button-play").prop("disabled", disabledButton)
    $("#button-pass").prop("disabled", disabledButton)

    generatePlayedCards(response.previousPlayedCards)
  })
}

function generatePlayedCards(playedCards) {
  $(".card-type").empty()
  if (playedCards === null) {
    return
  }

  playedCards = playedCards.map(convertCard)
  for (let card of playedCards) {
    let tmp = card.split("-")
    let color = (tmp[0] == SUITS.SPADE || tmp[0] == SUITS.CLUB) ? "#000000" : "#FF0000"
    $(".card-type").append(`
        <div class="m-card text-center" style="color: ${color};">
            <div>
                ${tmp[0]}
                <br>
                ${tmp[1]}
            </div>
        </div>
        `)
  }
  relocatePlayedCards()
}

function subscribeEnd() {
  websocket.subscribe("/user/queue/end", (response) => {
    response = JSON.parse(response.body)
    endGame(response)
  })
}

function endGame(data) {
  let winner = data.winner
  delete data.hands[winner]

  // 產生 winner 的部分
  $(".end-window").empty()
  $(".end-window").append(`
        <div class="card" id="player-${winner}">
            <div class="card-body">
                <h5 class="card-title text-center">${winner}</h5>
                <br>
                <img class="card-img-top" src="https://picsum.photos/200/200" alt="Card image cap">
                <div class="end-hands">
                    <h3 class="text-danger text-center" style="margin-top: 10px;">Winner</h3>
                </div>
            </div>
        </div>
    `)

  // 產生其他人的部分
  for (let name in data.hands) {
    $(".end-window").append(`
            <div class="card" id="player-${name}">
                <div class="card-body">
                    <h5 class="card-title text-center">${name}</h5>
                    <br>
                    <img class="card-img-top" src="https://picsum.photos/200/200" alt="Card image cap">
                    <div class="end-hands"></div>
                </div>
            </div>
        `)
    data.hands[name].sort(compareByGeneral)
    data.hands[name].map((card) => {
      card = convertCard(card)
      let tmp = card.split("-")
      let color = (tmp[0] == SUITS.SPADE || tmp[0] == SUITS.CLUB) ? "#000000" : "#FF0000"
      $(`#player-${name} .end-hands`).append(`
                <div class="m-card text-center" style="color: ${color};">
                    <div>
                    ${tmp[0]}
                    <br>
                    ${tmp[1]}
                    </div>
                </div>
            `)
    })
  }

  // 加入回到房間的按鈕
  $(".end-window").append(`
        <button type="button" class="btn btn-success btn-lg" id="button-end">回房間</button>
    `)
  $("#button-end").click(() => {
    let roomId = window.location.href.split("/").slice(-1)[0]
    window.location.href = `/room/${roomId}`
  })

  // 顯示後面的黑色遮罩
  $(".black-cover").removeClass("d-none")

  // 顯示結算頁面
  $(".end-window").removeClass("d-none")
}
