//index.js
//client side javascript

const DRAW_TIME = 10*1000; //10 seconds to draw
const VIEW_TIME =  3*1000; //3 seconds to view

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
//window.addEventListener('touchend', function(e){ drawing = false; }, false);

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
	if(!drawable || !drawing) return false;
	try{
		mouse = getMousePos(canvas, e);
		drawing = false;
		drawLine(current.x, current.y, mouse.x, mouse.y, current.color);
	}catch{
		drawing = false;
	}
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
		x: ((evt.clientX||evt.touches[0].clientX) - rect.left) * scaleX,   // scale mouse coordinates after they have
		y: ((evt.clientY||evt.touches[0].clientY)  - rect.top) * scaleY     // been adjusted to be relative to element
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
	socket.emit("newRoom", prompt("Name:").toUpperCase());
}

function submitRoomCode(){
	var room_code = document.getElementById("room_code").value.toUpperCase();
	var name = document.getElementById("name").value.toUpperCase();
	
	if(room_code.length != 4) { alert("Incorrect Room Code"); return; }	
	if(name.length < 1) { alert("Please Enter a Name"); return;}
	//other checks probably needed
	
	socket.emit("joinRoom", room_code, name);
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


var timer;
function startTimer(time){
	clearInterval(timer);
	var timeOnClock = time;
	var timerEle = document.getElementById("timer");
	timerEle.innerHTML = Math.round(timeOnClock/1000);
	timer = setInterval(function(){
		timeOnClock -= 1000;
		if(timeOnClock <= 0) {
			timeOnClock = 0;
			clearInterval(timer);
		}
		timerEle.innerHTML = Math.round(timeOnClock/1000);
	}, 1000);
}

function drawImage(img){
	var img_asp = img.width / img.height;
	var canvas_asp = canvas.width / canvas.height;
	var width, height, x, y;
	
	if(img_asp < canvas_asp){
		height = canvas.height;
		width = img.width * (height / img.height);
		x = (canvas.width - width) / 2;
		y = 0;
	}else if(img_asp > canvas_asp){
		width = canvas.width;
		height = img.height * (width / img.width);
		x = 0;
		y = (canvas.height - height) / 2;
	}else{
		width = canvas.width;
		height = canvas.height;
		x = 0;
		y = 0;
	}
	
	ctx.drawImage(img, x, y, width, height);
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

socket.on("unsuccessful_connection", function(e){
	alert(e);
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

socket.on("ready", function(player_name, ready_){
	var player = players.indexOf(player_name);
	if(player < 0) return false;
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

socket.on("draw", function(player_name){
	var player = players.indexOf(player_name);
	if(player < 0) return false;
	if(player_name == me) {
		document.getElementById("canvasText").innerHTML = "Try to replicate what you saw!";
		drawable = true;
	}else document.getElementById("canvasText").innerHTML = player_name + " is Drawing!";
	
	startTimer(DRAW_TIME);
});

socket.on('requestDrawing', function(){
	drawable = false;
	document.getElementById("canvasText").innerHTML = "Times Up!";
	socket.emit('finishedDrawing', canvas.toDataURL());
	ctx.clearRect(0, 0, canvas.width, canvas.height);
});

socket.on('drawing', function(drawing, player_name){
	var player = players.indexOf(player_name);
	if(player < 0) return false;
	document.getElementById("canvasText").innerHTML = players[player] + "'s Drawing!";
	var img = new Image();
	img.src = drawing;
	img.onload = function(){
		drawImage(img);
	}
	startTimer(VIEW_TIME);
});

socket.on("stopLooking", function(){
	ctx.clearRect(0, 0, canvas.width, canvas.height);
});

socket.on('final_drawings', function(drawings, task){
	//drawings.reverse(); //show in reverse order
	var imgs = [];
	for(var i = 0; i < drawings.length; i++){
		imgs[i] = new Image();
		imgs[i].src = drawings[i].art;
	}
		
	//Assume last image in array is last to load (bad, improve later)
	imgs[imgs.length - 1].onload = function(){
		//Display all images
		var img = 0;
		drawImage(imgs[img]);
		document.getElementById("canvasText").innerHTML = drawings[img].artist + "'s Art!";
		var display_timer = setInterval(function(){
			img++;
			if(img >= imgs.length) {
				clearInterval(display_timer);
				document.getElementById("canvasText").innerHTML = "The original drawing was meant to be: " + task;
				return;
			}
			ctx.clearRect(0, 0, canvas.width, canvas.height);
			drawImage(imgs[img]);
			document.getElementById("canvasText").innerHTML = drawings[img].artist + "'s Art!";
		}, VIEW_TIME);
	}
});

socket.on("log", function(message){
	console.log(message);
});