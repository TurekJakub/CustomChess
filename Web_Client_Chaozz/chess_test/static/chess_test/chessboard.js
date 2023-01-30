let widthS;
let heightS;

function generateChessboard(height, width) {
  const body = document.body;
  tbl = document.createElement('table');
  for (let i = 0; i < height; i++) {
    let tr = document.createElement("tr");
    for (let j = 0; j < width; j++) {
      let td = document.createElement("td");
      td.setAttribute("id", 8 * (i + 1) + j)
      td.style.width = '50px';
      td.style.height = '50px';
      td.style.border = '1px solid black';
      td.addEventListener("mousedown", test)
      if ((j + i) % 2 == 0) {
        td.style.backgroundColor = 'black';
        let text = document.createTextNode("Čest práci, soudruhu");
        td.appendChild(text);
      }
      tr.appendChild(td);
    }
    tbl.appendChild(tr);
  }
  body.appendChild(tbl);


}
function createF(x) {
  const t = document.getElementById(x);
  let b = document.createElement("div");
  b.setAttribute("class", "test");
  b.setAttribute("onclick", "a()");
  b.style.backgroundColor = 'red';
  b.style.height = '50px';
  b.style.width = '50px';
  //  b.style.left = '50px';
  t.appendChild(b);


}
function a() {
  $.post("",
    {
      name: "Donald Duck",
      city: "Duckburg"
    },
    function (data, status) {
      console.log(data.s)
    });

}
function test(lis) {
  console.log("OwO")
}
function drawChessboard(height, width) {
  heightS = height;
  widthS = width;

  let ctx = document.getElementById("canvas").getContext("2d");
  let a;
  if (screen.width < 500) {
    ctx.canvas.width = screen.width
    ctx.canvas.height = screen.width
    a = screen.width/width;
  }
  else {
    ctx.canvas.width = (window.innerWidth - window.innerWidth / 2);
    ctx.canvas.height = (window.innerWidth - window.innerWidth / 2);
    a = (window.innerWidth - window.innerWidth / 2)/ width;
  }

  
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
function rescaleCanvas() {
  drawChessboard(heightS, widthS);
}
