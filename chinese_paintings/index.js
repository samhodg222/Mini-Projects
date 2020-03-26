/*
TODO
	Bug Fixing:
		-find bugs
		
	Features:
		*General*
		-more drawings
			potentially 'scene action person' thing, maybe a mix
			will definitelybe a csv online with a free list, will
			have to make csv import (not too much work)	
			
		*Big Changes*
			-Live stream to previous users *ALMOST THERE* (potentially don't worry about scaling, or don't be lazy)
				pretty simple to do, just figuring out who can see it
				also design something for players that have nothing to see.
				
			-add guessing along the way (use for label, better than 'Art') *NOT SURE ABOUT THIS*
				Implemented to some extend but not sure if good feature
		
	Extra Features:
		-Fix room generation to avoid overproduction of rooms
			simply more options, and maybe some ddos protection, or at
			least stop one client or ip(if poss) making multiple rooms
		-replay room (maybe, no problem if just refresh for now)
			possibly not necessary, depends how long games end up being, 
			and if it makes sense
		-compress images (or not even have them in image form?)
*/


var app = require("express")();
var http = require("http").Server(app);
var io = require("socket.io")(http);

const PORT = 3000;

var rooms = [];
const MAX_LOBBIES = 26*26*26*26; //456976 (limited by number of room codes, most likely limited by server capabilities)
const DRAW_TIME = 10*1000; //10 seconds to draw
const VIEW_TIME =  3*1000; //3 seconds to view

//Functions
function randomDrawing(){//TODO
	//function that returns random drawing task
	return "banana";
}

//Room Management
function createRoom(){
	for(var i = 0; i < MAX_LOBBIES; i++){
		if(rooms[i] == undefined){
			rooms[i] = {
				players: [],
				queue: [],
				drawings: [],
				drawing: randomDrawing(),
				state: "lobby"
			};
			return i;
		}
	}
	return -1;
}

function newRoom(name, socket){ //**NEEDS WORK**
	//check name
	if(check_name(name)) {
		io.to(socket.id).emit("unsuccessful_connection", "incorrect_name");
		return;
	}
	
	var room = createRoom(); //TODO add support for too many rooms
	var roomCode = generateRoomCode(room);
	rooms[room].players.push({name: name, id: socket.id, ready: false, connected: true});
	socket.join(roomCode);
	
	io.to(socket.id).emit("successful_connection", roomCode, [name], [false], name);
}

function joinRoom(roomCode, name, socket){
	var room = reverseRoomCode(roomCode);
		
	//check if room exists
	if(roomCode.length != 4 || !/^[A-Z]+$/g.test(roomCode) || room == -1 || rooms[room] == undefined){
		io.to(socket.id).emit("unsuccessful_connection", "not_exist");
		return;
	}
	
	//check if room is in lobby mode
	if(rooms[room].state != "lobby"){
		io.to(socket.id).emit("unsuccessful_connection", "game_in_play");
		return;
	}
	
	//check name
	if(check_name(name)) {
		io.to(socket.id).emit("unsuccessful_connection", "incorrect_name");
		return;
	}
	
	var relogin = false;
	for(var i = 0; i < rooms[room].players.length; i++){
		if(rooms[room].players[i].name == name && rooms[room].players[i].connected){
			io.to(socket.id).emit("unsuccessful_connection", "name_taken");
			return;
		}else if(rooms[room].players[i].name == name && !rooms[room].players[i].connected){
			rooms[room].players[i].connected = true;
			rooms[room].players[i].id = socket.id;
			relogin = true;
			break;
		}
	}
	
	if(!relogin) rooms[room].players.push({name: name, id: socket.id, ready: false, connected: true});
	socket.join(roomCode);
			
	var player_names = [];
	var readys = [];
	for(var i = 0; i < rooms[room].players.length; i++){
		player_names.push(rooms[room].players[i].name);
		readys.push(rooms[room].players[i].ready);
	}
	
	io.to(socket.id).emit("successful_connection", roomCode, player_names, readys, name);
	
	socket.broadcast.to(roomCode).emit("addPlayer", name);
}

function findPlayerFromID(id){ //returns in form [room, player]
	for(var i = 0; i < rooms.length; i++){
		if(rooms[i] == undefined) continue;
		for(var j = 0; j < rooms[i].players.length; j++){
			if(rooms[i].players[j].id == id) return [i, j];
		}
	}
	return [-1, -1];
}

function removePlayer(room, player){
	/*
		Players cannot properly leave once joined, their connection just becomes inactive until they relogin in
		potentially timeout players
		
		TODO allow players to join and leave when in lobby mode (look out for errors on client side)
	*/
	rooms[room].players[player].connected = false;
}


//GAMEPLAY
//START
function startRoom(room){
	//generate random order of play
	while(rooms[room].queue.length < rooms[room].players.length){
		var r = Math.floor(Math.random() * rooms[room].players.length);
		if(rooms[room].queue.indexOf(r) === -1) rooms[room].queue.push(r);
	}
	
	io.to(generateRoomCode(room)).emit("start");
	
	
	rooms[room].state = 0;
	player = rooms[room].queue[rooms[room].state]
	
	setTimeout(function(){
		io.to(generateRoomCode(room)).emit("draw", rooms[room].players[player].name);
		io.to(rooms[room].players[player].id).emit("drawingTask", rooms[room].drawing);
		
		setTimeout(function(){
			io.to(rooms[room].players[player].id).emit("requestDrawing");
			addRequest(rooms[room].players[player].id, 'drawing', function(){
				finishedDrawing(room, player, null) //send black image in case of no return
			});
		}, DRAW_TIME);
	}, 4*1000);
}

//RECIEVE REQUEST [A] => if request not recieved just submit nothing;
function finishedDrawing(room, player, drawing){
	//Add drawing database
	rooms[room].drawings.push({artist: rooms[room].players[player].name, art: drawing});
		
	//find next active player *CODE MIGHT NEED CHECKING*
	var next = 1;
	while(rooms[room].state + next < rooms[room].players.length){
		if(rooms[room].players[rooms[room].queue[rooms[room].state + next]].connected) break;
		next++;
	}
	
	//Check if there is another player
	if(rooms[room].state + next < rooms[room].players.length){
		//Send previous drawing to next active player in queue	
		var nextPlayer = rooms[room].queue[rooms[room].state + next];
					
		io.to(rooms[room].players[nextPlayer].id).emit("drawing", drawing, rooms[room].players[player].name);
		setTimeout(function(){
			//after VIEW_TIME clear clients canvas
			io.to(rooms[room].players[nextPlayer].id).emit("stopLooking");
			continueRoom(room);
		}, VIEW_TIME);
	}else{
		//Finished Game
		rooms[room].state = "end";
		startRoomTimeout(room);
		io.to(generateRoomCode(room)).emit("final_drawings", rooms[room].drawings, rooms[room].drawing);
	}
}

function continueRoom(room){
	if(!Number.isInteger(rooms[room].state)) return false; //Shouldn't be needed
	
	rooms[room].state++; //set next player to current
	
	if(rooms[room].state >= rooms[room].players.length) return false; //Shouldn't be needed
	
	var player = rooms[room].queue[rooms[room].state];
	
	io.to(generateRoomCode(room)).emit("draw", rooms[room].players[player].name);
	setTimeout(function(){
		//MAKE REQUEST [A]
		io.to(rooms[room].players[player].id).emit("requestDrawing");
		addRequest(rooms[room].players[player].id, 'drawing', function(){
			finishedDrawing(room, player, null)
		});
	}, DRAW_TIME);	
}

//ROOM functions
function generateRoomCode(index){
	//Generates a 4 letter code based off the room index
	if(index < 0) return false;
	var room_26 = [0, 0, 0, 0];
	var index_26 = index.toString(26).split("");
	var offset = room_26.length - index_26.length;
	for(var i = 0; i < index_26.length; i++) room_26[i + offset] = parseInt(index_26[i], 26);
	var room_code = [];
	for(var i = 0; i < room_26.length; i++) room_code.push(alphabet[room_26[i]]);
	return room_code.join("");
}

function reverseRoomCode(roomCode){
	//returns an index based of the room code
	var room_code = roomCode.split("");
	var total = 0;
	for(var i = 0; i < room_code.length; i++) {
		var num = alphabet.indexOf(room_code[i]);
		if(num < 0) return -1;
		total += num * Math.pow(26, room_code.length - 1 - i);
	}
	return total;
}

function checkReady(room){
	if(getRoomSize(room) < 2) return false; //Need at least 2 active players to start game
	for(var i = 0; i < rooms[room].players.length; i++){
		if(!rooms[room].players[i].connected) continue; //Non-active players aren't counted
		if(!rooms[room].players[i].ready) return false;
	}
	return true;
}

function getRoomSize(room){
	if(rooms[room] == undefined) return -1;
	var numActivePlayers = 0;
	for(var i = 0; i < rooms[room].players.length; i++){
		if(rooms[room].players[i].connected) numActivePlayers++;
	}
	return numActivePlayers;
}

//END ROOM functions
function startRoomTimeout(room){ //necessary until replay possible
	if(rooms[room] == undefined) return;
	if(rooms[room].state != "end") return;
	setTimeout(function(){
		if(rooms[room].state == "end") deleteRoom(room);
	}, 15*1000);
}

function startEmptyRoomTimeout(room){ //if a lobby is empty for more than 30 seconds delete
	if(rooms[room] == undefined) return;
	if(getRoomSize(room) > 0) return;
	setTimeout(function(){
		if(getRoomSize(room) < 1) {
			deleteRoom(room);
		}
	}, 30*1000);
}

function deleteRoom(room){
	var roomCode = generateRoomCode(room);
	io.of('/').in(roomCode).clients((error, socketIds) => {
		if (error) throw error;
		socketIds.forEach(socketId => io.sockets.sockets[socketId].leave(roomCode));
	});
	delete rooms[room];
}

//Game Management

//Request Management
var requests = [];
const MAX_REQUESTS = 100000; //arbitrary number
const REQUEST_TIME = 1000; //gives player 1 second to return a request
function addRequest(id, title, no_feedback){ //function in event of no return
	var request_id;
	
	for(request_id = 0; request_id < MAX_REQUESTS; request_id++){
		if(requests[request_id] == undefined){
			requests[request_id] = {id: id, title: title};
			break;
		}
	}
	
	setTimeout(function(){
		if(requests[request_id] != undefined) {
			no_feedback;
			delete requests[request_id];
		}
	}, REQUEST_TIME);
	
	return request_id;
}

function clearRequest(id, title){
	for(var i = 0; i < requests.length; i++){
		if(requests[i].id === id && requests[i].title === title){
			delete requests[i];
			break;
		}
	}
}

//Name Check
function check_name(name){ //returns true if name isn't right (sorry)
	return (!/^[A-Z]+$/g.test(name) || name.length < 1 || name.length > 12);
}


//Recieved Sockets

io.on('connection', function(socket){
	socket.on('newRoom', function(name){ newRoom(name, socket); });
	
	socket.on('joinRoom', function(roomCode, name){	joinRoom(roomCode, name, socket); });
	
	socket.on('ready', function(ready){
		var [room, player] = findPlayerFromID(socket.id);
		 
		if(room < 0 || player < 0) return; //no need to tell client, possibly reset client side?
		 
		rooms[room].players[player].ready = ready;

		io.to(generateRoomCode(room)).emit("ready", rooms[room].players[player].name, ready);
		
		if(checkReady(room)) startRoom(room);
	});
	
	socket.on('line', function(width, height, x0, y0, x1, y1, color){
		var [room, player] = findPlayerFromID(socket.id);
		if(room < 0 || player < 0) return;
		
		if(!Number.isInteger(rooms[room].state)) return false;
		
		for(var i = 0; i < rooms[room].state; i++){
			var player_ = rooms[room].queue[i];
			io.to(rooms[room].players[player_].id).emit("line", width, height, x0, y0, x1, y1, color);
		}
	});
	
	socket.on('finishedDrawing', function(drawing){
		var [room, player] = findPlayerFromID(socket.id);
		
		if(room < 0 || player < 0) return false; //possibly tell client
		
		clearRequest(socket.id, 'drawing'); //clear request
		
		finishedDrawing(room, player, drawing);
	});
});

io.on('connection', function(socket){
	//console.log('a user connected');
	
	socket.on('disconnect', function(){
		for(var i = 0; i < rooms.length; i++){
			if(rooms[i] == undefined) continue;
			for(var j = 0; j < rooms[i].players.length; j++){
				if(rooms[i].players[j].id === socket.id) {
					removePlayer(i, j);
					if(getRoomSize(i) < 1) startEmptyRoomTimeout(i);
					break;
				}
				
			}
		}
	});
});

var alphabet = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];

//HTTP set up
http.listen(PORT, '192.168.0.16', function(){
	console.log("listening on *:" + PORT);
});

app.get("/", function(req, res){
	res.sendFile(__dirname + "/web/index.html");
});
app.get("/styles.css", function(req, res){
	res.sendFile(__dirname + "/web/styles.css");
});
app.get("/index.js", function(req, res){
	res.sendFile(__dirname + "/web/index.js");
});

app.get("/jquery-3.4.1.min.js", function(req, res){
	res.sendFile(__dirname + "/web/jquery-3.4.1.min.js");
});


/*
	****IMPORTANT****
	REMOVE WHEN FINISHED
*/
app.get("/print", function(req, res){
	res.send(rooms);
});