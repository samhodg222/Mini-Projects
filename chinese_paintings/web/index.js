//index.js
//client side javascript

const DRAW_TIME = 15*1000; //15 seconds to draw
const VIEW_TIME =  3*1000; //3 seconds to view

var socket = io();

var canvas = document.getElementById("canvas");
var ctx = canvas.getContext("2d");

var current = {
	color: 'black'
}

var colours = document.getElementsByClassName('colour');
for (var i = 0; i < colours.length; i++){
	colours[i].addEventListener('click', onColorUpdate, false);
}

function onColorUpdate(e){
	current.color = e.target.style.backgroundColor;
}

var drawable = false;
var drawing = false;

var currentTab = "home";

window.onload = function(){
	document.getElementById(currentTab).style = "opacity: 1";
}

function Alert(message){
	document.getElementById("alert_text").innerHTML = message;
	document.getElementById("pop_up_alert").style = "display: intial";
	document.getElementById("pop_up_outer").style = "display: intial";
}

function closeAlert(){
	document.getElementById("pop_up_outer").style = "display: none";
}

function switchTO(name){
	var curTab = document.getElementById(currentTab);
	var newTab = document.getElementById(name);
	
	$(window).scrollTop(0,0);
	
	curTab.style = "display: none";
	newTab.style = "display: initial";
	newTab.style = "opacity: 0";
	setTimeout(function(){
		newTab.style = "opacity: 1";
	}, 50)
	
	currentTab = name;
}

function changeReady(player, r){
	if(r){
		document.getElementById('player_' + player).style = "background-color: #ffb214";
	}else{
		document.getElementById('player_' + player).style = "background-color: #a07114";
	}
}

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

function drawLine(x0, y0, x1, y1, color){
	ctx.beginPath();
	ctx.moveTo(x0, y0);
	ctx.lineTo(x1, y1);
	ctx.strokeStyle = color;
	ctx.lineWidth = 2;
	ctx.stroke();
	ctx.closePath();
	
	//socket.emit('line', x0/canvas.width, y0/canvas.height, x1/canvas.width, y1/canvas.height, color);
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

function getMousePos(canvas, evt) {
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
	if(drawable) return;
	canvas.width = document.getElementById("canvasContainer").clientWidth;
	canvas.height = document.getElementById("canvasContainer").clientHeight;
}

let vh = window.innerHeight * 0.01;
document.documentElement.style.setProperty('--vh', `${vh}px`);

$('input[type="text"]').blur(function() {
  setTimeout(function() {
    if (!$(document.activeElement).is('input[type="text"]')) {
      $(window).scrollTop(0,0);
    }
  }, 0);
});

onResize();

var me = null;
var ready = false;
var roomCode = null;

function createRoom(){
	var name = document.getElementById("c_name").value.toUpperCase()
	if(name.length < 1) { Alert("Please Enter a Name"); return;}
	
	socket.emit("newRoom", name);
}

function submitRoomCode(){
	var room_code = document.getElementById("room_code").value.toUpperCase();
	var name = document.getElementById("name").value.toUpperCase();
	
	if(room_code.length != 4) { Alert("Incorrect Room Code"); return; }	
	if(name.length < 1) { Alert("Please Enter a Name"); return;}
	//other checks probably needed
	
	$(window).scrollTop(0,0);
	socket.emit("joinRoom", room_code, name);
}

function readyButtonPress(){
	ready = !ready;
	if(ready){
		document.getElementById('readyButton').style = "background-color: #666867";
	}else{
		document.getElementById('readyButton').style = "background-color: intial";
	}
	
	socket.emit("ready", ready);
}

function return_to_lobby(){
	switchTO("lobbyScreen");
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


socket.on("successful_connection", function(roomCode_, players, readys, me_){
	me = me_;
	roomCode = roomCode_;
	
	document.getElementById("ROOMCODE").innerHTML = roomCode;
	document.getElementById("lobby").innerHTML = "";
	
	for(i = 0; i < players.length; i++){
		document.getElementById("lobby").innerHTML += "<div class='player' id='player_" + players[i] + "' >" + players[i] + "</div>";
		changeReady(players[i], readys[i]);
	}
	
	switchTO('lobbyScreen');
});

socket.on('update_lobby', function(players, readys){
	document.getElementById("lobby").innerHTML = "";
	
	for(i = 0; i < players.length; i++){
		document.getElementById("lobby").innerHTML += "<div class='player' id='player_" + players[i] + "' >" + players[i] + "</div>";
		changeReady(players[i], readys[i]);
	}
});

socket.on("unsuccessful_connection", function(e){
	switch(e){
		case 'not_exist':
			Alert("Room Does Not Exist");
			break;
		case 'game_in_play':
			Alert("The Game Has Already Started!");
			break;
		case 'incorrect_name':
			Alert("Incorrect Name");
			break;
		case 'name_taken':
			Alert("Name Taken!");
			break;
	}
});

socket.on("addPlayer", function(name){
	document.getElementById("lobby").innerHTML += "<div class='player' id='player_" + name + "' >" + name + "</div>";
});

socket.on("removePlayer", function(name){ 
//TODO

});

socket.on("ready", function(player_name, ready_){
	changeReady(player_name, ready_);
});

socket.on("start", function(){
	switchTO("play");
	document.getElementById("canvasText").innerHTML = "Get Ready!";
	onResize();
});

socket.on("drawingTask", function(task){
	document.getElementById("canvasText").innerHTML = "You Are Drawing: " + task;
});

socket.on("draw", function(player_name){
	ctx.clearRect(0, 0, canvas.width, canvas.height);
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
	//ctx.clearRect(0, 0, canvas.width, canvas.height);
});

socket.on('drawing', function(drawing, player_name){
	document.getElementById("canvasText").innerHTML = player_name + "'s Drawing!";
	var img = new Image();
	img.src = drawing;
	img.onload = function(){
		drawImage(img);
	}
	startTimer(VIEW_TIME);
});

/*
socket.on('line', function(x0, y0, x1, y1, color){
	drawLine(x0*canvas.width, y0*canvas.height, x1*canvas.width, y1*canvas.height, color);
});
*/

socket.on("stopLooking", function(){
	ctx.clearRect(0, 0, canvas.width, canvas.height);
});

socket.on('final_drawings', function(drawings, task){
	document.getElementById("task").innerHTML = task;
	document.getElementById("art_display").innerHTML = "";
	switchTO("final_drawings");
	
	for(var i = 0; i < drawings.length; i++){
		document.getElementById("art_display").innerHTML += '<div class="art"><img id="art_' + i + '" class="art_img"/><p>' + drawings[i].artist + '</p></div>';
		document.getElementById("art_" + i).src = drawings[i].art;
	}
	
	var players = document.getElementsByClassName('player');
	for(player of players) player.style = "";
	ready = false;
	document.getElementById('readyButton').style = "background-color: intial";
	document.getElementById('timer').innerHTML = "15";
});

socket.on("log", function(message){
	console.log(message);
});