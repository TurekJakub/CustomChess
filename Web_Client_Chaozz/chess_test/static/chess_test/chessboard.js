let width;
let height;
let a;
let figures = null;
let positions = null;
let selected = false;
let path = null;
let moves = [];
let lastSelected = null;

async function getFigures() {
  let fig;
  await $.ajax({
    url: '',
    type: 'post',
    data: {
      requested: 'fig'
    },

    success: function (response) {
      fig = response.fig;
     
    },


  });
  return fig;
}
async function sendMove(x, y) {

  await $.ajax({
    url: '',
    type: 'post',
    data: {
      requested: 'post-fig',
      move: x + ':' + y
    },

    success: function (response) {

      console.log('s');
    },


  });

}

async function getPositions() {
  let positions;
  await $.ajax({
    url: '',
    type: 'post',
    data: {
      requested: 'pos'
    },

    success: function (response) {
      positions = response.pos;
      console.log(positions["pawn"][0]);
    },


  });
  return positions;
}
function setPath(pathname) {
  path = pathname
}
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
function resizeCanvas(canvas) {
  if (screen.width < 500) {
    canvas.width = screen.width
    canvas.height = screen.width
    a = screen.width / width;
  }
  else {
    canvas.width = (window.innerWidth - window.innerWidth / 2);
    canvas.height = (window.innerWidth - window.innerWidth / 2);
    a = (window.innerWidth - window.innerWidth / 2) / width;
  }
}

async function handleClickCanvas(event) {
  let canvas = document.getElementById('canvas-background');

  const rect = canvas.getBoundingClientRect()
  const x = event.clientX - rect.left
  const y = event.clientY - rect.top 

  if (figures === null) {

    figures = await getFigures();
  }
  if (positions === null) {

    positions = await getPositions();
  }

  for (let key in figures) {
    let coordinates = figures[key];
    if (inRange(x, coordinates[0]) && inRange(y, coordinates[1])) {
     
      if (selected) {
        unmarkMoves(document.getElementById('canvas-moves').getContext('2d'));
      }
      moves = positions[key];
      markMoves(moves, document.getElementById('canvas-moves').getContext('2d'));
      lastSelected = key;
      selected = true;


    }
    else if (selected && positions[lastSelected].includes((Math.floor(x / a) + 1) + ':' + (Math.floor(y / a) + 1))) {
      console.log(lastSelected + ' was moved at x: ' + x + ', y: ' + y);
      sendMove((Math.floor(x / a) + 1), (Math.floor(y / a) + 1));
      unmarkMoves(document.getElementById('canvas-moves').getContext('2d'));
      selected = false;
    }
    else {
      unmarkMoves(document.getElementById('canvas-moves').getContext('2d'));
      selected = false
    }
  }
}
function inRange(value, rangeBorder) {
  return value > rangeBorder * a && value < rangeBorder * a + a
}
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
function drawFigures(figure, ctx) {
  for (const [figur, position] of Object.entries(figure)) {
    img = new Image();
    img.onload = function () {
      ctx.drawImage(img, position[0] * a , position[1] *a, a, a);
      console.log( path + '/' + figur + '.svg')
    };
    img.src = path + '/' + figur + '.svg';
  }

}
function unmarkMoves( ctx){
  ctx.clearRect(0,0,ctx.canvas.width,ctx.canvas.height);
 }
function resizeAllLayers() {
  resizeCanvas(document.getElementById('canvas-background'));
  resizeCanvas(document.getElementById('canvas-figures'));
  resizeCanvas(document.getElementById('canvas-moves'));

}

function redrawCanvas() {
  drawCanvas(height, width)
}
async function drawCanvas(heightc, widthc) {
  height = heightc;
  width = widthc;
  if (figures === null) {

    figures = await getFigures();
  }
  resizeAllLayers();
  drawChessboard(height, width);
  drawFigures(figures, document.getElementById('canvas-figures').getContext('2d'));
  markMoves(moves, document.getElementById('canvas-moves').getContext('2d'));

}
