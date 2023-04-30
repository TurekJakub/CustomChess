let canvasRules = document.getElementById("canvas-b");
let contextRules = canvasRules.getContext("2d");

let kingLoc = []
let queenLoc = []
let rookLoc = []
let bishopLoc = []
let knightLoc = []
let pawnLoc = []
let piece = "undefined";

let ArUp = new Image();
ArUp.src = "{% static 'temp/arrows/arrowUp.png' %}";
let ArDown = new Image();
ArDown.src = "{% static 'temp/arrows/arrowDown.png' %}";
let ArLeft = new Image();
ArLeft.src = "{% static 'temp/arrows/arrowLeft.png' %}";
let ArRight = new Image();
ArRight.src = "{% static 'temp/arrows/arrowRight.png' %}";
let ArUpRight = new Image();
ArUpRight.src = "{% static 'temp/arrows/arrowUpRight.png' %}";
let ArUpLeft = new Image();
ArUpLeft.src = "{% static 'temp/arrows/arrowUpLeft.png' %}";
let ArDownRight = new Image();
ArDownRight.src = "{% static 'temp/arrows/arrowDownRight.png' %}";
let ArDownLeft = new Image();
ArDownLeft.src = "{% static 'temp/arrows/arrowDownLeft.png' %}";

let king = new Image();
king.src = "{% static 'temp/chessPieces/0.png' %}";
let queen = new Image();
queen.src = "{% static 'temp/chessPieces/1.png' %}";
let rook = new Image();
rook.src = "{% static 'temp/chessPieces/2.png' %}";
let bishop = new Image();
bishop.src = "{% static 'temp/chessPieces/3.png' %}";
let knight = new Image();
knight.src = "{% static 'temp/chessPieces/4.png' %}";
let pawn = new Image();
pawn.src = "{% static 'temp/chessPieces/5.png' %}";

size = 11;

if ((screen.width) > (screen.height)) {
  a = (screen.height / 1.35) / size;
} else {
  a = (screen.width / 1.35) / size;
}

canvasRules.width = size * a;
canvasRules.height = size * a;

contextRules.clearRect(0, 0, canvasRules.width, canvasRules.height);

function initialDraw() {
  for (let y = 0; y < size; y++) {
    for (let x = 0; x < size; x++) {
      if ((x + y) % 2 == 0) {
        contextRules.fillStyle = "black";
      } else {
        contextRules.fillStyle = "white";
      }
      contextRules.fillRect(x * a, y * a, a, a);
      if (x == 0 && y == Math.floor(size / 2)) {
        ArLeft.onload = function () {
          contextRules.drawImage(ArLeft, x * a, y * a, a, a);
        }
      } else if (x == size - 1 && y == Math.floor(size / 2)) {
        ArRight.onload = function () {
          contextRules.drawImage(ArRight, x * a, y * a, a, a);
        }
      } else if (y == 0 && x == Math.floor(size / 2)) {
        ArUp.onload = function () {
          contextRules.drawImage(ArUp, x * a, y * a, a, a);
        }
      } else if (y == size - 1 && x == Math.floor(size / 2)) {
        ArDown.onload = function () {
          contextRules.drawImage(ArDown, x * a, y * a, a, a);
        }
      }
      if (x == 0 && y == 0) {
        ArUpLeft.onload = function () {
          contextRules.drawImage(ArUpLeft, x * a, y * a, a, a);
        }
      } if (x == size - 1 && y == 0) {
        ArUpRight.onload = function () {
          contextRules.drawImage(ArUpRight, x * a, y * a, a, a);
        }
      } if (x == 0 && y == size - 1) {
        ArDownLeft.onload = function () {
          contextRules.drawImage(ArDownLeft, x * a, y * a, a, a);
        }
      } if (x == size - 1 && y == size - 1) {
        ArDownRight.onload = function () {
          contextRules.drawImage(ArDownRight, x * a, y * a, a, a);
        }
      }
      pawn.onload = function () {
        contextRules.drawImage(pawn, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
        contextRules.clearRect(Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
      }
      knight.onload = function () {
        contextRules.drawImage(knight, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
        contextRules.clearRect(Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
      }
      bishop.onload = function () {
        contextRules.drawImage(bishop, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
        contextRules.clearRect(Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
      }
      rook.onload = function () {
        contextRules.drawImage(rook, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
        contextRules.clearRect(Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
      }
      queen.onload = function () {
        contextRules.drawImage(queen, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
        contextRules.clearRect(Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
      }
      king.onload = function () {
        contextRules.drawImage(king, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
        contextRules.clearRect(Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
      }
    }
  }
}

function drawBoard() {
  for (let y = 0; y < size; y++) {
    for (let x = 0; x < size; x++) {
      if ((x + y) % 2 == 0) {
        contextRules.fillStyle = "black";
      } else {
        contextRules.fillStyle = "white";
      }
      contextRules.fillRect(x * a, y * a, a, a);
      if (x == 0 && y == Math.floor(size / 2)) {
        contextRules.drawImage(ArLeft, x * a, y * a, a, a);
      } else if (x == size - 1 && y == Math.floor(size / 2)) {
        contextRules.drawImage(ArRight, x * a, y * a, a, a);
      } else if (y == 0 && x == Math.floor(size / 2)) {
        contextRules.drawImage(ArUp, x * a, y * a, a, a);
      } else if (y == size - 1 && x == Math.floor(size / 2)) {
        contextRules.drawImage(ArDown, x * a, y * a, a, a);
      }
      if (x == 0 && y == 0) {
        contextRules.drawImage(ArUpLeft, x * a, y * a, a, a);
      } if (x == size - 1 && y == 0) {
        contextRules.drawImage(ArUpRight, x * a, y * a, a, a);
      } if (x == 0 && y == size - 1) {
        contextRules.drawImage(ArDownLeft, x * a, y * a, a, a);
      } if (x == size - 1 && y == size - 1) {
        contextRules.drawImage(ArDownRight, x * a, y * a, a, a);
      }
    }
  }
}

function next() {
  if (piece == "king") {
    contextRules.drawImage(king, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
  }
  if (piece == "queen") {
    contextRules.drawImage(queen, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
  }
  if (piece == "rook") {
    contextRules.drawImage(rook, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
  }
  if (piece == "bishop") {
    contextRules.drawImage(bishop, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
  }
  if (piece == "knight") {
    contextRules.drawImage(knight, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
  }
  if (piece == "pawn") {
    contextRules.drawImage(pawn, Math.floor(size / 2) * a, Math.floor(size / 2) * a, a, a);
  }
}

initialDraw();

function getPosition(e) {
  let cursorX = Math.floor((e.clientX - document.getElementById("canvas-b").offsetLeft) / a);
  let cursorY = Math.floor((e.clientY - document.getElementById("canvas-b").offsetTop) / a);

  if (piece == "king") {
    setAvailable(cursorX, cursorY, kingLoc);
  }
  if (piece == "queen") {
    setAvailable(cursorX, cursorY, queenLoc);
  }
  if (piece == "rook") {
    setAvailable(cursorX, cursorY, rookLoc);
  }
  if (piece == "bishop") {
    setAvailable(cursorX, cursorY, bishopLoc);
  }
  if (piece == "knight") {
    setAvailable(cursorX, cursorY, knightLoc);
  }
  if (piece == "pawn") {
    setAvailable(cursorX, cursorY, pawnLoc);
  }
}

function draw(field) {
  for (let y = 0; y < size; y++) {
    for (let x = 0; x < size; x++) {
      for (let i = 0; i < field.length; i++) {
        if (y == field[i][1] && x == field[i][0]) {
          contextRules.fillStyle = "green";
          contextRules.fillRect(x * a, y * a, a, a);
        }
      }
    }
  }
}

function setAvailable(cursorX, cursorY, field) {
  if (!((cursorX == 5) && (cursorY == 5))) {
    if (cursorX == 5 && cursorY == 0) {
      for (let i = 0; i < Math.floor(size / 2); i++) {
        field[field.length] = [cursorX, cursorY + i];
        draw(field);
      }
    } if (cursorX == 5 && cursorY == size - 1) {
      for (let i = 0; i < Math.floor(size / 2); i++) {
        field[field.length] = [cursorX, cursorY - i];
        draw(field);
      }
    } if (cursorY == 5 && cursorX == 0) {
      for (let i = 0; i < Math.floor(size / 2); i++) {
        field[field.length] = [cursorX + i, cursorY];
        draw(field);
      }
    }
    if (cursorY == 5 && cursorX == size - 1) {
      for (let i = 0; i < Math.floor(size / 2); i++) {
        field[field.length] = [cursorX - i, cursorY];
        draw(field);
      }
    }
    if (cursorX == 0 && cursorY == 0) {
      for (let i = 0; i < Math.floor(size / 2); i++) {
        field[field.length] = [cursorX + i, cursorY + i];
        draw(field);
      }
    }
    if (cursorX == size - 1 && cursorY == 0) {
      for (let i = 0; i < Math.floor(size / 2); i++) {
        field[field.length] = [cursorX - i, cursorY + i];
        draw(field);
      }
    }
    if (cursorX == 0 && cursorY == size - 1) {
      for (let i = 0; i < Math.floor(size / 2); i++) {
        field[field.length] = [cursorX + i, cursorY - i];
        draw(field);
      }
    }
    if (cursorX == size - 1 && cursorY == size - 1) {
      for (let i = 0; i < Math.floor(size / 2); i++) {
        field[field.length] = [cursorX - i, cursorY - i];
        draw(field);
      }
    }
    else {
      field[field.length] = [cursorX, cursorY];
    }
    draw(field);
  }
  document.getElementById("help").innerHTML = field.size;
}

document.getElementById("WK").addEventListener('click', function () { piece = "king"; drawBoard(); next("king"); draw(kingLoc); });
document.getElementById("WQ").addEventListener('click', function () { piece = "queen"; drawBoard(); next("queen"); draw(queenLoc); });
document.getElementById("WR").addEventListener('click', function () { piece = "rook"; drawBoard(); next("rook"); draw(rookLoc); });
document.getElementById("WB").addEventListener('click', function () { piece = "bishop"; drawBoard(); next("bishop"); draw(bishopLoc); });
document.getElementById("WN").addEventListener('click', function () { piece = "knight"; drawBoard(); next("knight"); draw(knightLoc); });
document.getElementById("WP").addEventListener('click', function () { piece = "pawn"; drawBoard(); next("pawn"); draw(pawnLoc); });
document.getElementById("btn-save").addEventListener('click', function () { translate(pawnLoc); translate(knightLoc); translate(bishopLoc); translate(rookLoc); translate(queenLoc); translate(kingLoc); });


document.getElementById("canvas-b").addEventListener('click', getPosition);

function removeDuplicates(field) {
  for (let i = 0; i < field.length; i++) {
    for (let j = i + 1; j < field.length; j++) {
      if (field[i][0] == field[j][0] && field[i][1] == field[j][1]) {
        field.splice(j, 1);
      }
    }
  }
  return field;
}

function remove55(field) {
  for (let i = 0; i < field.length; i++) {
    if (field[i][0] == 5 && field[i][1] == 5) {
      field.splice(i, 1);
    }
  }
}

function translate(field) {
  removeDuplicates(field);
  remove55(field);
  toFairyNotation(field);
  return field;
}

function toFairyNotation(field) {
  let help = field;
  let counter = 0;
  let direction = "";
  let help2 = "";
  for (let i = 0; i < help.length; i++) {
    if (help[i][0] == help[i][1]) {
      for (let i = 0; i < help.length; i++) {
        if (help[i][0] + help[i][1] == size - 1 && help[i][0] > help[i][1]) {
          field[field.length] = -((help[i][1] - Math.floor(size / 2))) + "X>";
        }
      }
    }
  }
  for (let i = 0; i < help.length; i++) {
    if (help[i][0] + help[i][1] == size - 1 && help[i][0] < help[i][1]) {
      for (let i = 0; i < help.length; i++) {
        if (help[i][0] == help[i][1]) {
          field[field.length] = (help[i][0] - Math.floor(size / 2)) + "X<";
        }
      }
    }
  }
  for (let i = 0; i < field.length; i++) {
    if (field[i] == "X>") {
      for (let j = 0; j < field.length; j++) {
        if (field[j] == "X<") {
          field.splice(i);
          field.splice(j - 1);
          field[field.length] = "X";
        }
      }
    }
  }
  for (let i = 0; i < help.length; i++) {
    if (help[i][0] == Math.floor(size / 2)) {
      if (help[i][1] < Math.floor(size / 2)) {
        field[field.length] = help[i][1] + ">";
      }
      if (help[i][1] > Math.floor(size / 2)) {
        field[field.length] = help[i][1] - Math.floor(size / 2) + "<";
      }
    }
    if (help[i][1] == Math.floor(size / 2)) {
      if (help[i][0] < Math.floor(size / 2)) {
        field[field.length] = help[i][0] + "=";
      } else {
        field[field.length] = help[i][0] - Math.floor(size / 2) + "=";
      }
    }
  }
    counter = 0;
    for (let i = 0; i < field.lenght; i++) {
      if (field[i] == ">") {
        counter++;
      } if (field[i] == "<") {
        counter++;
      }
    }
    if (counter == 2) {
      field[field.length] = "<>";
      for (let i = 0; i < field.lenght; i++) {
        if (field[i] == ">") {
          field.splice(i);
        } if (field[i] == "<") {
          field.splice(i);
        }
      }
    }
    counter = 0;
    for (let i = 0; i < field.lenght; i++) {
      if (field[i] == "=") {
        counter++;
      } if (field[i] == "<") {
        direction = "<";
      } if (field[i] == ">") {
        direction = ">";
      }
    }
    if (counter == 1 && direction == "<") {
      field[field.length] = "<=";
      for (let i = 0; i < field.lenght; i++) {
        if (field[i] == "=") {
          field.splice(i);
        } if (field[i] == "<") {
          field.splice(i);
        }
      }
    }
    if (counter == 1 && direction == ">") {
      field[field.length] = ">=";
      for (let i = 0; i < field.lenght; i++) {
        if (field[i] == "=") {
          field.splice(i);
        } if (field[i] == ">") {
          field.splice(i);
        }
      }
    }
    counter = 0;
    direction = "";
    for (let i = 0; i < field.lenght; i++) {
      if (field[i] == "=") {
        counter++;
      } if (field[i] == "<>") {
        counter++;
      }
    }
    if (counter == 2) {
      field[field.length] = "+";
      for (let i = 0; i < field.lenght; i++) {
        if (field[i] == "=") {
          field.splice(i);
        } if (field[i] == "<>") {
          field.splice(i);
        }
      }
    }
    (field + " 3");
    counter = 0;
    for (let i = 0; i < field.length; i++) {
      if (field[i] == "X") {
        counter++;
      } if (field[i] == "+") {
        counter++;
      }
    }
    if (counter == 2) {
      field[field.length] = "*";
      for (let i = 0; i < field.lenght; i++) {
        if (field[i] == "X") {
          field.splice(i);
        } if (field[i] == "+") {
          field.splice(i);
        }
      }
    }
    (field + " 4");
    counter = 0;
    (piece.charAt(0) + " " + piece.charAt(1));
    for (let i = 0; i < field.length; i++) {
      help2 = field[i].slice(0, 1);
      if (help2 == "5"){
        help2 = "n";
        field[i] = help2 + field[i].slice(1);
      }
    }
    removeDuplicates(field);
    return field;
  }
