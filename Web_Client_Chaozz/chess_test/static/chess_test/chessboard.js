let widthS;
let heightS;
let aS;
let figures = null;
let positions = null;
let selected = false;

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
function drawChessboard(height, width) {
  heightS = height;
  widthS = width;

  let can = document.getElementById("canvas")
  let ctx = can.getContext("2d");
  let a;


  if (screen.width < 500) {
    ctx.canvas.width = screen.width
    ctx.canvas.height = screen.width
    a = screen.width / width;
  }
  else {
    ctx.canvas.width = (window.innerWidth - window.innerWidth / 2);
    ctx.canvas.height = (window.innerWidth - window.innerWidth / 2);
    a = (window.innerWidth - window.innerWidth / 2) / width;
  }


  console.log(a);
  console.log(screen.width);
  console.log((screen.width - screen.width / 5));
  aS = a;

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

async function handleClickCanvas(event) {

  let canvas = document.getElementById('canvas');
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
  if (!selected) {
    ctx.save();
    console.log("saved");
  }

  for (let key in figures) {
    let coordinates = figures[key];
    if (((x > coordinates[0] * aS) && (x < (coordinates[0] * aS) + aS)) && ((y > coordinates[1] * aS && (y < (coordinates[1] * aS) + aS)))) {
      console.log('in');
      if (selected) {
        ctx.restore();
      }
      selected = true;
      markMoves(positions[key], ctx);
      return;

    }
  }
}

function markMoves(moves, ctx) {
  console.log(moves);
  for (let move in moves) {
    move = moves[0];
    ctx.beginPath();
    ctx.arc(move[0] * aS - aS / 2, move[1] * aS - aS / 2, aS / 4, 0, 2 * Math.PI);
    ctx.fillStyle = 'red';
    ctx.fill();
    ctx.stroke();
  }
}

function rescaleCanvas() {
  drawChessboard(heightS, widthS);
}
