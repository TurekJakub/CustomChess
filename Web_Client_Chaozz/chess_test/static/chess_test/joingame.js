const csrftoken = Cookies.get('csrftoken');
let gameName;
let lastSelectedGame = undefined;

function search(event) {
    games = document.getElementById('list-games').children
    for (game of games) {
        if (game.innerHTML.toLowerCase().includes(event.target.value.toLowerCase())) {
            game.style.display = 'block'
        } else {
            game.style.display = 'none'
        }
    }
}
function selectGame(event) {
    event.target.classList.toggle('bg-success')

    if (lastSelectedGame != undefined) {
        lastSelectedGame.classList.toggle('bg-success')
    }

    lastSelectedGame = event.target
    gameName = lastSelectedGame.innerHTML

}
function joinGame() {
    password = document.getElementById('input-password').value
    $.ajax({
        url: '',
        type: 'post',
        headers: { 'X-CSRFToken': csrftoken },
        mode: 'same-origin',
        data: {
            'game': gameName,
            'password': password,
        },
    })
}
function fetchGames() {
    $.ajax({
        url: '',
        type: 'get',
        headers: { 'X-CSRFToken': csrftoken },
        mode: 'same-origin',
        success: function (data) {
            addGamesToList(data.games)
        }
    })
}
function addGamesToList(games) { // TODO add only new games and removed expired games
    list = document.getElementById('list-games')
    for (game of games) {
        listItem = document.getElementById('list-item-template').content.cloneNode(true).children[0]
        listItem.innerHTML = game
        list.append(listItem)
    }
}
function refreshListOfGames() {
    fetchGames()
    setInterval(refreshListOfGames, 60000)
}
