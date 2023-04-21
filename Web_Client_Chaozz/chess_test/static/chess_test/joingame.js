const csrftoken = Cookies.get("csrftoken");
let lastSelectedValue = undefined;
let lastSelectedElement = undefined;
let displayedItems = {};
let x;

function search(event) {
  games = document.getElementById("list").children;
  for (game of games) {
    if (
      game.innerHTML.toLowerCase().includes(event.target.value.toLowerCase())
    ) {
      game.style.display = "block";
    } else {
      game.style.display = "none";
    }
  }
}
function selectGame(event) {
  event.target.classList.toggle("bg-success");

  if (lastSelectedElement != undefined) {
    lastSelectedElement.classList.toggle("bg-success");
  }

  lastSelectedElement = event.target;
  lastSelectedValue = lastSelectedElement.innerHTML;
}
function makeAjaxCall(typeParam, dataParam, successCallback) {
  $.ajax({
    url: "",
    type: typeParam,
    headers: { "X-CSRFToken": csrftoken },
    mode: "same-origin",
    timeout: 1000,
    data: dataParam,
    success: successCallback,
  });
}

function joinGame() {
  password = document.getElementById("input-password").value;
  makeAjaxCall(
    "post",
    { game: lastSelectedValue, password: password },
    undefined
  );
}
function fetchGames() {
  makeAjaxCall("get", undefined, function (data) {
    addGamesToList(data.games);
  });
}
function addGamesToList(games) {
  // TODO add only new games and removed expired games
  list = document.getElementById("list");
  displayedItemsKeys = Object.keys(displayedItems);
  for (game of games) {
    if (!displayedItemsKeys.includes(game)) {
      listItem = document
        .getElementById("list-item-template")
        .content.cloneNode(true).children[0];
      listItem.innerHTML = game;
      list.append(listItem);
      displayedItems[game] = listItem;
    }
  }
  for (gameToRemove of displayedItemsKeys.filter((x) => !games.includes(x))) {
    list.removeChild(displayedItems[gameToRemove]);
    delete displayedItems[gameToRemove];
  }
}
function refreshList() {
  setInterval(fetchGames, 60000);
}
function createGame() {
  password = document.getElementById("input-password").value;
  gameName = document.getElementById("input-name").value;
  if (gameName != undefined && lastSelectedValue != undefined) {
    makeAjaxCall(
      "post",
      { game_name: gameName, password: password, rules: lastSelectedValue },
      undefined
    );
  }
}
