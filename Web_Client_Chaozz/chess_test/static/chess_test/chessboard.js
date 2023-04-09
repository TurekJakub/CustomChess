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
let c
let forward;
let lastWidth;
let switched = false;
let img = new Image();


// AJAX calls for data specified by requested parameter

async function doAjaxCall(requested) {
  let data;
  await $.ajax({
    url: '',
    type: 'post',
    data: {
      requested: requested
    },

    success: function (response) {
      data = response;
      
    },


  });
  return data;
}
// ajax call to send data to server
async function sendMove(x, y) {
  $.ajax({
    url: '',
    type: 'post',
    data: {
      requested: 'post-fig',
      move: x + ':' + y,
      figure: lastSelected,
      transcript: 'none' // transcript is not implemented yet
    },
  });
}
// set path to game's temporary files directory
function setPath(pathname) {
  path = pathname
}
// draw chessboard-background on canvas
function drawChessboard() {
  let can = document.getElementById("canvas-background")
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
    y = $(window).height() - $(window).height() * 1 / 4
    x = $(window).width() - 20
  }
  else {
    y = $(window).height() - 90
    x = $(window).width()
  }
  a = Math.min(y / height, x / width)
  console.log($(window).width() + ':' + $(window).height() + ':' + a)

}
function initializ() {
  lastWidth = $(window).width()
}
// resize given canvas to half of current window size or to screen width on small mobile dievices
function resizeCanvas(canvas) {
  canvas.width = width * a
  canvas.height = height * a

}
// handel user's click on chessboar - call required functions depending on cursor cordinates
async function handleClickCanvas(event) {
  let canvas = document.getElementById('canvas-background');

  // getting cursor cordinates
  const rect = canvas.getBoundingClientRect()
  const x = event.clientX - rect.left
  const y = event.clientY - rect.top

  // getting figures and their possible moves
  if (figures === null) {

    figures = await doAjaxCall('fig');

  }
  if (positions === null) {

    positions = await doAjaxCall('moves');
  }

  for (let key in figures) {
    let coordinates = figures[key];
    if (inRange(x, coordinates[0] - 1) && inRange(y, coordinates[1] - 1)) { // user selected new figure
      if (selected) {
        unmarkMoves(document.getElementById('canvas-moves').getContext('2d'));
      }
      console.log(positions)
      moves = positions[key];
      markMoves(moves, document.getElementById('canvas-moves').getContext('2d'));
      lastSelected = key;
      selected = true;
    }
    else if (selected && positions[lastSelected].includes((Math.floor(x / a) + 1) + ':' + (Math.floor(y / a) + 1))) { // user trigger movment of previously selected figure
      console.log(lastSelected + ' was moved at x: ' + x + ', y: ' + y);
      sendMove((Math.floor(x / a) + 1), (Math.floor(y / a) + 1));
      unmarkMoves(document.getElementById('canvas-moves').getContext('2d'));
      console.log((Math.floor(y / a) + 1) + 'c' + (Math.floor(x / a) + 1))
      setAnimationCordinates(coordinates[0] - 1, coordinates[1] - 1, Math.floor(x / a), Math.floor(y / a))
      window.requestAnimationFrame(drawAnimationFrame);
      selected = false;
    }
    else { // user click on empty field
      unmarkMoves(document.getElementById('canvas-moves').getContext('2d'));
      selected = false
    }
  }
}
function inRange(value, rangeBorder) {
  return value > rangeBorder * a && value < rangeBorder * a + a
}
// mark possible actions of selected figure
function markMoves(moves, ctx) {
  for (let i = 0; i < moves.length; i++) {
    let move = moves[i].split(':');
    ctx.beginPath();
    ctx.arc(move[0] * a - a / 2, move[1] * a - a / 2, a / 4, 0, 2 * Math.PI);
    ctx.fillStyle = 'red';
    ctx.fill();
    ctx.stroke();
  }
  ctx = document.getElementById('canvas-figures').getContext('2d');
}
// draw figures on canvas
function drawFigures(figure, ctx) {
  for (const [figur, position] of Object.entries(figure)) {
    img.onload = function () {
      ctx.drawImage(img, (position[0] - 1) * a, (position[1] - 1) * a, a, a);
    };
    img.src = path + '/' + figur + '.svg';
  }

}
// unmark actions displayed on canvas
function unmarkMoves(ctx) {
  ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
}
// resize all canvases - layers of chessboard
function resizeAllLayers() {

  getNewSquerSize();
  resizeCanvas(document.getElementById('canvas-background'));
  resizeCanvas(document.getElementById('canvas-figures'));
  resizeCanvas(document.getElementById('canvas-moves'));
  resizeCanvas(document.getElementById('canvas-animations'));

  adjustPageLayout()
  console.log(a)
  //console.log(window.innerWidth + " " + window.innerHeight)


}
// adjust page layout depending on screen size
function adjustPageLayout() {
  // swap toggler and list of players on small mobile devices
  if (($(window).width() < 576 && (lastWidth > 576 || !switched)) || ($(window).width() > 576 && lastWidth < 576)) {
    topContainerContent = $('#container-top').html()
    $('#container-top').html($('#container-bottom').html())
    $('#container-bottom').html(topContainerContent)
    lastWidth = $(window).width()
    switched = true;
    console.log($(window).width())
  }
  // adjust height of chessboard
  $('#column-1').css('height', height * a + 'px');
  // adjus width of toggler and list of players
  if ($(window).width() < 576) {
    $('#container-top').width(width * a + 'px');
    $('#container-bottom').width(width * a + 'px');
    if (!$('#container-top').attr('class').includes('mx-auto')) {
      $('#container-top').attr('class', $('#container-top').attr('class') + 'mx-auto');
      $('#container-bottom').attr('class', $('#container-bottom').attr('class') + 'mx-auto');
    }
  }
  // reset defaul layout for bigger screens
  else {
    $('#container-top').attr('style', 'height=12.5%');
    $('#container-bottom').attr('style', 'height=12.5%');

    $('#container-top').attr('class', $('#container-top').attr('class').replace('mx-auto', ''));
    $('#container-bottom').attr('class', $('#container-bottom').attr('class').replace('mx-auto', ''));

  }


}
// redraw chessboard usually at a new scale
function redrawCanvas() {
  drawCanvas(height, width, figures)
}
// draw whole chessboard
async function drawCanvas(heightParam, widthParam, figuresParam) {
  height = heightParam;
  width = widthParam;
  /*
  if (figures === null) {

    figures = await doAjaxCall('fig');
  }
  */
  figures = figuresParam;
  resizeAllLayers();
  drawChessboard(height, width);
  console.log(figures)
  drawFigures(figures, document.getElementById('canvas-figures').getContext('2d'));
  markMoves(moves, document.getElementById('canvas-moves').getContext('2d'));

}

// setter for cordinates of start and end of a moving figure's animation
function setAnimationCordinates(startX, startY, endX, endY) {
  newXCordinate = startX * a;
  newYCordinate = startY * a
  endXCordinate = endX * a;
  endYCordinate = endY * a;
  lineEquationA = endYCordinate - newYCordinate;
  img.src = path + '/' + lastSelected + '.svg';
  b = -1 * (endXCordinate - newXCordinate);
  c = -1 * (lineEquationA * newXCordinate + b * newYCordinate)

  console.log(newXCordinate + ' ' + newYCordinate)
  console.log(endXCordinate + ' ' + endYCordinate)
}
// draw one frame of moving animation
function drawAnimationFrame() {
  ctx = document.getElementById('canvas-animations').getContext('2d');

  if (lineEquationA == 0) {
    forward = endXCordinate > newXCordinate;
    if (forward) {
      newXCordinate = newXCordinate + 1;
      forward = true;
    } else {
      newXCordinate = newXCordinate - 1;
    }
  }
  else if (b == 0) {
    forward = endYCordinate > newYCordinate;
    if (forward) {
      newYCordinate = newYCordinate + 1;
    } else {
      newYCordinate = newYCordinate - 1;
    }
  }
  else {
    forward = endXCordinate > newXCordinate;
    if (forward) {
      newXCordinate = newXCordinate + 1;
    } else {
      newXCordinate = newXCordinate - 1;
    }
    newYCordinate = Math.abs((-1 * lineEquationA * newXCordinate - c) / b);
    console.log(newXCordinate + ' ' + newYCordinate)
  }
  
  ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height)
  ctx.drawImage(img, newXCordinate, newYCordinate, a, a);
  

  if ((newYCordinate < endYCordinate || newXCordinate < endXCordinate && forward) || newYCordinate > endYCordinate && newXCordinate > endXCordinate && !forward) {
    window.requestAnimationFrame(drawAnimationFrame) // recusive call for the next animation frame
  }
  else { // if animation ends figure is draw at it's finall position    
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height)
    ctx = document.getElementById('canvas-figures').getContext('2d')
    ctx.drawImage(img, newXCordinate, newYCordinate, a, a);

  }

}

