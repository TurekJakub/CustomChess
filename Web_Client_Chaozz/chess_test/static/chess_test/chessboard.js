let width;
let height;
let a;
let figures = null;
let positions = null;
let selected = false;
let moves = [];

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
      //console.log(fig["pawn"]);
    },


  });  
  return fig;
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
function drawChessboard() {

  let can = document.getElementById("canvas-background")
  let ctx = can.getContext("2d");

  console.log(a);
  console.log(screen.width);
  console.log((screen.width - screen.width / 5));

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
function resizeCanvas(canvas){
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
  let ctx = canvas.getContext('2d');

  const rect = canvas.getBoundingClientRect()
  const x = event.clientX - rect.left
  const y = event.clientY - rect.top

  console.log("x: " + x + " y: " + y)

  if (figures === null) {

    figures = await getFigures();
  }
  if (positions === null) {

    positions = await getPositions();
  }


  for (let key in figures) {
    let coordinates = figures[key];
    if (((x > coordinates[0] * a) && (x < (coordinates[0] * a) + a)) && ((y > coordinates[1] * a && (y < (coordinates[1] * a) + a)))) {
      console.log('in');
      if (selected) {
        unmarkMoves(moves,document.getElementById('canvas-moves').getContext('2d'));
      }
      selected = true;
      moves = positions[key];
      markMoves(moves, document.getElementById('canvas-moves').getContext('2d'));


    }
    //else if(selected){

    //}
    else {
      unmarkMoves(moves,document.getElementById('canvas-moves').getContext('2d'));
    }
  }
}

function markMoves(moves, ctx) {
  console.log(moves);
  for (let i =0; i<moves.length; i++) {
    let move = moves[i];
    ctx.beginPath();
    ctx.arc(move[0] * a - a / 2, move[1] * a - a / 2, a / 4, 0, 2 * Math.PI);
    ctx.fillStyle = 'red';
    ctx.fill();
    ctx.stroke();
  }
}
function drawFigures(figures, ctx){
  for (let  i =0; i<figures.length;i++){
    ctx.beginPath();
    ctx.arc(figures[i][0] * a - a / 2, figures[i][1] * a - a / 2, a / 4, 0, 2 * Math.PI);
    ctx.fillStyle = 'blue';
    ctx.fill();
    ctx.stroke();
  }
}
function unmarkMoves(moves, ctx){
 ctx.clearRect(0,0,ctx.canvas.width,ctx.canvas.height);
}
function resizeAllLayers(){
  resizeCanvas(document.getElementById('canvas-background'));
  resizeCanvas(document.getElementById('canvas-figures'));
  resizeCanvas(document.getElementById('canvas-moves'));

}

function redrawCanvas(){
  drawCanvas(height,width)
}
function drawCanvas(heightc,widthc) {
  height = heightc;
  width = widthc;
  resizeAllLayers();
  drawChessboard(height, width);
  drawFigures([[2,1]], document.getElementById('canvas-figures').getContext('2d'));
  markMoves(moves,document.getElementById('canvas-moves').getContext('2d'));

}
