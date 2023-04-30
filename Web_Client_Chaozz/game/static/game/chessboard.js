let width;
let height;
let a;
let figures = null;
let positions = null;
let selected = false;
let path = null;
let moves = [];
let lastSelected = null;
let newXCordinate;
let newYCordinate;
let endXCordinate;
let endYCordinate;
let lineEquationA;
let b;
let c;
let forward;
let lastWidth;
let displayedTags = {};
let switched = false;
let firstFrame;
let figuresMap = {};
let animationImg;
let tags = {};
const csrftoken = Cookies.get("csrftoken");

// AJAX calls for data specified by requested parameter
async function makeAjaxCall(data) {
  let responseData;
  await $.ajax({
    url: "",
    headers: { "X-CSRFToken": csrftoken },
    mode: "same-origin",
    type: "post",
    data: data,

    success: function (response) {
      responseData = response;
    },
  });
  return responseData;
}
// ajax call to send data to server
async function sendMove(x, y, transcript) {
  data = {
    requested: "post-fig",
    move: x + ":" + y,
    figure: lastSelected,
    transcript: transcript,
  };
  makeAjaxCall(data);
}
// set path to game's temporary files directory
function setPath(pathname) {
  path = pathname;
}
// draw chessboard-background on canvas
function drawChessboard() {
  let can = document.getElementById("canvas-background");
  let ctx = can.getContext("2d");

  for (let y = 0; y < height; y++) {
    for (let x = 0; x < width; x++) {
      if ((x + y) % 2 == 0) {
        ctx.fillStyle = "black";
      } else {
        ctx.fillStyle = "white";
      }
      ctx.fillRect(x * a, y * a, a, a);
    }
  }
}
// calculate new size of chessboard's square
function getNewSquerSize() {
  if ($(window).width() < 576) {
    y = $(window).height() - ($(window).height() * 1) / 4;
    x = $(window).width() - 20;
  } else {
    y = $(window).height() - 90;
    x = $(window).width();
  }
  a = Math.min(y / height, x / width);
}
function initializ(
  heightParam,
  widthParam,  
  tagsParam, 
  figuresMapParam
) {
  lastWidth = $(window).width();
  height = heightParam;
  width = widthParam;
  figuresMap = figuresMapParam;
  figures = figuresParam;
  tags = tagsParam; 
}
// resize given canvas to half of current window size or to screen width on small mobile dievices
function resizeCanvas(canvas) {
  canvas.width = width * a;
  canvas.height = height * a;
}
// handel user's click on chessboar - call required functions depending on cursor cordinates
async function handleClickCanvas(event) {
  let canvas = document.getElementById("canvas-background");

  // getting cursor cordinates
  const rect = canvas.getBoundingClientRect();
  const x = event.clientX - rect.left;
  const y = event.clientY - rect.top;

  // getting figures and their possible moves
  if (figures === null) {
    figures = await makeAjaxCall({ requested: "fig" });
  }
  if (positions === null) {
    positions = await makeAjaxCall({ requested: "moves" });
  }
  document.getElementById("canvas-moves").getContext("2d").fillRect(0, a, a, a);
  for (let key in figures) {
    let coordinates = figures[key];
    if (inRange(x, coordinates[0] - 1) && inRange(y, coordinates[1] - 1)) {
      // user selected new figure
      if (selected) {
        unmarkMoves(document.getElementById("canvas-moves").getContext("2d"));
      }
      moves = positions[key];
      markMoves(
        moves,
        document.getElementById("canvas-moves").getContext("2d")
      );
      lastSelected = key;
      selected = true;
      break;
    } else if (
      selected &&
      positions[lastSelected].includes(
        Math.floor(x / a) + 1 + ":" + (Math.floor(y / a) + 1)
      )
    ) {
      // user trigger movment of previously selected figure
      sendMove(Math.floor(x / a) + 1, Math.floor(y / a) + 1, ""); // TODO: add transcript
      unmarkMoves(document.getElementById("canvas-moves").getContext("2d"));
      setAnimationCordinates(
        coordinates[0] - 1,
        coordinates[1] - 1,
        Math.floor(x / a),
        Math.floor(y / a),
        lastSelected
      );
      window.requestAnimationFrame(drawAnimationFrame);
      selected = false;
      break;
    } else {
      // user click on empty field     
      unmarkMoves(document.getElementById("canvas-moves").getContext("2d"));
      selected = false;
    }
  }
}
function inRange(value, rangeBorder) {
  return value > rangeBorder * a && value < rangeBorder * a + a;
}
// mark possible actions of selected figure
function markMoves(moves, ctx) {
  for (let i = 0; i < moves.length; i++) {
    let move = moves[i].split(":");

    ctx.beginPath();
    ctx.arc(move[0] * a - a / 2, move[1] * a - a / 2, a / 4, 0, 2 * Math.PI);
    ctx.fillStyle = "red";
    ctx.fill();
    ctx.stroke();
  }
  ctx = document.getElementById("canvas-figures").getContext("2d");
}
var onloadFunc = function (img, ctx, x, y) {
  return function () {
    ctx.drawImage(img, x, y, a, a);
  };
};
// draw figures on canvas
function drawFigures(figure, ctx) {
  for (const [figur, position] of Object.entries(figure)) {
    let figurName = figur; // debug something like 'let figurName = figur.split('separator')[0];' in production
    img = new Image();
    
    img.onload = onloadFunc(
      img,
      ctx,
      (position[0] - 1) * a,
      (position[1] - 1) * a
    );
    img.src = path + "/" + figuresMap[figurName];
  }
}
// unmark actions displayed on canvas
function unmarkMoves(ctx) {
  ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
}
// resize all canvases - layers of chessboard
function resizeAllLayers() {
  getNewSquerSize();
  resizeCanvas(document.getElementById("canvas-background"));
  resizeCanvas(document.getElementById("canvas-figures"));
  resizeCanvas(document.getElementById("canvas-moves"));
  resizeCanvas(document.getElementById("canvas-animations"));
  resizeCanvas(document.getElementById("canvas-tags"));
  adjustPageLayout();
  
}
// adjust page layout depending on screen size
function adjustPageLayout() {
  // swap toggler and list of players on small mobile devices
  if (
    ($(window).width() < 576 && (lastWidth > 576 || !switched)) ||
    ($(window).width() > 576 && lastWidth < 576)
  ) {
    topContainerContent = $("#container-top").html();
    $("#container-top").html($("#container-bottom").html());
    $("#container-bottom").html(topContainerContent);
    lastWidth = $(window).width();
    switched = true;
  }
  // adjust height of chessboard
  $("#column-1").css("height", height * a + "px");
  // adjus width of toggler and list of players
  if ($(window).width() < 576) {
    $("#container-top").width(width * a + "px");
    $("#container-bottom").width(width * a + "px");
    if (!$("#container-top").attr("class").includes("mx-auto")) {
      $("#container-top").attr(
        "class",
        $("#container-top").attr("class") + "mx-auto"
      );
      $("#container-bottom").attr(
        "class",
        $("#container-bottom").attr("class") + "mx-auto"
      );
    }
  }
  // reset defaul layout for bigger screens
  else {
    $("#container-top").attr("style", "height=12.5%");
    $("#container-bottom").attr("style", "height=12.5%");

    $("#container-top").attr(
      "class",
      $("#container-top").attr("class").replace("mx-auto", "")
    );
    $("#container-bottom").attr(
      "class",
      $("#container-bottom").attr("class").replace("mx-auto", "")
    );
  }
}
// draw whole chessboard
function drawCanvas() {
  resizeAllLayers();
  drawChessboard(height, width);
  drawFigures(
    figures,
    document.getElementById("canvas-figures").getContext("2d")
  );
  markMoves(moves, document.getElementById("canvas-moves").getContext("2d"));
  drawTags( document.getElementById("canvas-tags").getContext("2d"),tags);
}

// setter for cordinates of start and end of a moving figure's animation
function setAnimationCordinates(startX, startY, endX, endY, figure) {
  newXCordinate = startX * a;
  newYCordinate = startY * a;
  endXCordinate = endX * a;
  endYCordinate = endY * a;
  lineEquationA = endYCordinate - newYCordinate;
  animationImg = new Image();
  animationImg.src = path + "/" + figuresMap[figure]; // figur.split('separator')[0];' in production
  b = -1 * (endXCordinate - newXCordinate);
  c = -1 * (lineEquationA * newXCordinate + b * newYCordinate);
  firstFrame = true;
}
// draw one frame of moving animation
function drawAnimationFrame() {
  ctx = document.getElementById("canvas-animations").getContext("2d");

  if (firstFrame) {
    document
      .getElementById("canvas-figures")
      .getContext("2d")
      .clearRect(newXCordinate, newYCordinate, a, a);
    firstFrame = false;
  }
  if (lineEquationA == 0) {
    forward = endXCordinate > newXCordinate;
    if (forward) {
      newXCordinate = newXCordinate + 1;
      forward = true;
    } else {
      newXCordinate = newXCordinate - 1;
    }
  } else if (b == 0) {
    forward = endYCordinate > newYCordinate;
    if (forward) {
      newYCordinate = newYCordinate + 1;
    } else {
      newYCordinate = newYCordinate - 1;
    }
  } else {
    forward = endXCordinate > newXCordinate;
    if (forward) {
      newXCordinate = newXCordinate + 1;
    } else {
      newXCordinate = newXCordinate - 1;
    }
    newYCordinate = Math.abs((-1 * lineEquationA * newXCordinate - c) / b);
  }

  ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
  ctx.drawImage(animationImg, newXCordinate, newYCordinate, a, a);

  if (
    newYCordinate < endYCordinate ||
    (newXCordinate < endXCordinate && forward) ||
    (newYCordinate > endYCordinate && newXCordinate > endXCordinate && !forward)
  ) {
    window.requestAnimationFrame(drawAnimationFrame); // recusive call for the next animation frame
  } else {
    // if animation ends figure is draw at it's finall position
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
    ctx = document.getElementById("canvas-figures").getContext("2d");
    ctx.drawImage(animationImg, newXCordinate, newYCordinate, a, a);
  }
}
function openWebSocketConnection() {
  // open websocket connection
  let ws = new WebSocket("ws://" + window.location.host + "/ws/game/");
  // if websocket connection is closed it is try to by open again
  ws.onclose = function () {
    openWebSocketConnection();
  };
  // called whatever data are received via websocket and handle them
  ws.onmessage = function (event) {
    // fetch data from received message
    data = JSON.parse(event.data);
    // draw animation of other player's move if action is 'move'
    if (data["action"] == "move") {
      cordinates = data["cordinates"].split(":");
      lastSelected = data["figure"];
      setAnimationCordinates(
        cordinates[0],
        cordinates[1],
        cordinates[2],
        cordinates[3],
        data["figure"]
      );
      window.requestAnimationFrame(drawAnimationFrame);
    }
    // add new player to the list of active players if action is 'join'
    else if (data["action"] == "join") {
      username = data["username"];
      container = $("#list-players");
      playerCard = document
        .getElementById("player-card-template")
        .content.cloneNode(true).children[0];
      playerCard.children[0].src =
        "{% static 'temp' %}/${username}_picture.jpge";
      playerCard.children[1].innerHTML = username;
      container.append(playerCard);
    }
  };
}
function drawTags(context, tagsList) {
  let tagsNames = [];
  let tagColors = [];
  for ([field, tags] of Object.entries(tagsList)) {
    tagSize = a / tags.length - tags.length + 1;
    i = 0;
    x = field.split(":")[0];
    y = field.split(":")[1];
    for (tag of tags) {
      if (tag["name"] != "unavailable") {
        context.fillStyle = tag["color"];
        context.fillRect(x * a + i * tagSize + 1, y * a, tagSize, a/10);
        i++;
      } else {
        context.fillStyle = "rgba(0, 0, 0, 0.5)";
        context.fillRect(x * a, y * a, a, a);
        i++;
      }
      tagsNames.push(tag["name"]);
      tagColors.push(tag["color"]);
    }
  }
  let displayedTagsNames = Object.keys(displayedTags);
  addTagsToLegend(
    tagsNames.filter((x) => !displayedTagsNames.includes(x)),
    tagColors
  );
  removeUnnecessaryTags(
    displayedTagsNames.filter((x) => !tagsNames.includes(x))
  );
}
function addTagsToLegend(tagsToDisplay, tagsColors) {
  tagsLegend = document.getElementById("legend-tags");
  for (tagName of tagsToDisplay) {
    tagCard = document
      .getElementById("tag-entry-template")
      .content.cloneNode(true).children[0];
    tagCard.children[0].style.backgroundColor =
      tagsColors[tagsToDisplay.indexOf(tagName)];
    tagCard.children[1].innerHTML = tagName;
    tagsLegend.append(tagCard);
    displayedTags[tag["name"]] = tagCard;
  }
}

function removeUnnecessaryTags(tagsToRemove) {
  tagsLegend = document.getElementById("legend-tags");
  for (tag of tagsToRemove) {
    tagsLegend.removeChild(displayedTags[tag]);
    delete displayedTags[tag];
  }
}
