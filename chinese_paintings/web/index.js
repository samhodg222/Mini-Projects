//index.js
//client side javascript

var socket = io();

var canvas = document.getElementById("canvas");
var ctx = canvas.getContext("2d");

var current = {
	color: 'black'
}
var drawable = false;
var drawing = false;

//https://github.com/socketio/socket.io/blob/master/examples/whiteboard/public/main.js
canvas.addEventListener('mousedown', onMouseDown, false);
canvas.addEventListener('mouseenter', onMouseEnter, false);
canvas.addEventListener('mouseup', onMouseUp, false);
canvas.addEventListener('mouseout', onMouseOut, false);
canvas.addEventListener('mousemove', throttle(onMouseMove, 1), false);
window.addEventListener('mouseup', function(e){ drawing = false; }, false);

//Mobile Support
canvas.addEventListener('touchstart', onMouseDown, false);
canvas.addEventListener('touchend', onMouseUp, false);
canvas.addEventListener('touchcancel', onMouseUp, false);
canvas.addEventListener('touchmove', throttle(onMouseMove, 1), false);
window.addEventListener('touchend', function(e){ drawing = false; }, false);

function drawLine(x0, y0, x1, y1, color){
	ctx.beginPath();
	ctx.moveTo(x0, y0);
	ctx.lineTo(x1, y1);
	ctx.strokStyle = color;
	ctx.lineWidth = 2;
	ctx.stroke();
	ctx.closePath();
}

function onMouseDown(e){
	mouse = getMousePos(canvas, e);
	if(!drawable) return false;
	drawing = true;
	current.x = mouse.x;
	current.y = mouse.y;
}
function onMouseEnter(e){
	mouse = getMousePos(canvas, e);
	if(!drawable) return false;
	current.x = mouse.x;
	current.y = mouse.y;
}

function onMouseUp(e){
	mouse = getMousePos(canvas, e);
	if(!drawable || !drawing) return false;
	drawing = false;
	drawLine(current.x, current.y, mouse.x, mouse.y, current.color);
}

function onMouseOut(e){
	mouse = getMousePos(canvas, e);
	if(!drawable || !drawing) return false;
	drawLine(current.x, current.y, mouse.x, mouse.y, current.color);
}

function onMouseMove(e){
	mouse = getMousePos(canvas, e);
	if(!drawable || !drawing) return false;
	drawLine(current.x, current.y, mouse.x, mouse.y, current.color);
	current.x = mouse.x;
	current.y = mouse.y;
	
}

function throttle(callback, delay){ //limits events per second
	var previousCall = new Date().getTime();
	return function(){
		var time = new Date().getTime();
		
		if((time - previousCall) >= delay){
			previousCall = time;
			callback.apply(null, arguments);
		}
	};
}

function  getMousePos(canvas, evt) {
	var rect = canvas.getBoundingClientRect(), // abs. size of element
	scaleX = canvas.width / rect.width,    // relationship bitmap vs. element for X
	scaleY = canvas.height / rect.height;  // relationship bitmap vs. element for Y

	return {
		x: (evt.clientX - rect.left) * scaleX,   // scale mouse coordinates after they have
		y: (evt.clientY - rect.top) * scaleY     // been adjusted to be relative to element
	}
}

window.addEventListener('resize', onResize, false);

// make the canvas fill its parent
function onResize() {
	canvas.width = document.getElementById("canvasContainer").clientWidth;
	canvas.height = document.getElementById("canvasContainer").clientHeight;
}

var me = null;
var ready = false;
var roomCode = null;
var players = [];
var readys = [];

function createRoom(){
	socket.emit("newRoom", prompt("Name:"));
}

function submitRoomCode(){
	socket.emit("joinRoom", document.getElementById("room_code").value, document.getElementById("name").value);
}

function readyButtonPress(){
	ready = !ready;
	if(ready){
		document.getElementById("readyButton").style = "background-color: green";
	}else{
		document.getElementById("readyButton").style = "background-color: blue";
	}
	
	socket.emit("ready", ready);
}

function updateLobby(){
	document.getElementById("lobby").innerHTML = "";
	
	for(i = 0; i < players.length; i++){
		document.getElementById("lobby").innerHTML += "<div class='player' id='player_" + i + "' >" + players[i] + "</div>";
		if(readys[i]) document.getElementById("player_" + i).style = "background-color: #FFA500";
		else document.getElementById("player_" + i).style = "";
	}
}



socket.on("successful_connection", function(roomCode_, players_, readys_, me_){
	me = me_;
	roomCode = roomCode_;
	players = players_;
	readys = readys_;
	
	document.getElementById("ROOMCODE").innerHTML = roomCode;
	updateLobby();
	
	document.getElementById("home").style = "display: none";
	document.getElementById("lobbyScreen").style = "display: initial";
});

socket.on("addPlayer", function(name){
	players.push(name);
	updateLobby();
});

socket.on("removePlayer", function(name){
	index = players.indexOf(name);
	if(index > -1){
		players.splice(index, 1);
	}
	updateLobby();
});

socket.on("ready", function(player, ready_){
	readys[player] = ready_;
	updateLobby();
});

socket.on("start", function(){
	document.getElementById("lobbyScreen").style = "display: none";	
	document.getElementById("play").style = "display: initial";
	onResize();
});

socket.on("drawingTask", function(task){
	document.getElementById("canvasText").innerHTML = "You Are Drawing A:" + task;
});

socket.on("draw", function(player){
	if(player == me) {
		document.getElementById("canvasText").innerHTML = "Try to replicate what you saw!";
		drawable = true;
	}else document.getElementById("canvasText").innerHTML = players[player] + " is Drawing!";
});

socket.on('requestDrawing', function(){
	drawable = false;
	document.getElementById("canvasText").innerHTML = "Times Up!";
	socket.emit('finishedDrawing', canvas.toDataURL());
	ctx.clearRect(0, 0, canvas.width, canvas.height);
});

socket.on('drawing', function(drawing, player){
	document.getElementById("canvasText").innerHTML = players[player] + "'s Drawing!";
	var img = new Image();
	img.src = drawing;
	img.onload = function(){
		ctx.drawImage(img, 0, 0, canvas.width, canvas.height); //Need to consider aspect ratios and dimensions, should be based around center
	}
});

socket.on("stopLooking", function(){
	ctx.clearRect(0, 0, canvas.width, canvas.height);
});

socket.on('final_drawing', function(drawing){
	var img = new Image();
	img.src = drawing;
	img.onload = function(){
		ctx.drawImage(img, 0, 0); //Need to consider aspect ratios and dimensions, should be based around center
	}
	document.getElementById("canvasText").innerHTML = "The Final Result!";
});

socket.on("log", function(message){
	console.log(message);
});