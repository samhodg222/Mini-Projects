/*
TODO
	Update Flow Chart

	Bug Fixing:
		-find bugs
		
	Features:
		-more drawings
		-more colors
		-Have better system than using alerts & prompt
		-general css and presentation improvement
		
		-Live stream to previous users
		-add guessing along the way (use for label, better than 'Art')
		
		
	Extra Features:
		-Fix room generation to avoid overproduction of rooms
		-replay room (maybe, no problem if just refresh for now)
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

function randomDrawing(){
	//function that returns random drawing task
	return "banana";
}

function removePlayer(room, player){
	/*
		Players cannot properly leave once joined, their connection just becomes inactive until they relogin in
		potentially timeout players
	*/
	rooms[room].players[player].connected = false;
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

function checkReady(room){
	if(rooms[room].players.length < 2) return false; //Need at least 2 players to start game
	for(var i = 0; i < rooms[room].players.length; i++){
		if(!rooms[room].players[i].ready) return false;
	}
	return true;
}

function startRoom(room){
	//generate random order of play
	while(rooms[room].queue.length < rooms[room].players.length){
		var r = Math.floor(Math.random() * rooms[room].players.length);
		if(rooms[room].queue.indexOf(r) === -1) rooms[room].queue.push(r);
	}
	
	io.to(generateRoomCode(room)).emit("start");
	
	
	rooms[room].state = 0;
	player = rooms[room].queue[rooms[room].state]
	
	io.to(generateRoomCode(room)).emit("draw", rooms[room].players[player].name);
	io.to(rooms[room].players[player].id).emit("drawingTask", rooms[room].drawing);
	
	setTimeout(function(){
		io.to(rooms[room].players[player].id).emit("requestDrawing");
	}, DRAW_TIME);
}

function continueRoom(room){
	if(!Number.isInteger(rooms[room].state)) return false;
	
	rooms[room].state++;
	
	if(rooms[room].state >= rooms[room].players.length) { //Might not be necessary
		endRoom(); //TODO
		return;
	}
	
	player = rooms[room].queue[rooms[room].state]
	
	io.to(generateRoomCode(room)).emit("draw", rooms[room].players[player].name);
	setTimeout(function(){
		io.to(rooms[room].players[player].id).emit("requestDrawing");
	}, DRAW_TIME);	
}

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

function getRoomSize(room){
	if(rooms[room] == undefined) return -1;
	var numActivePlayers = 0;
	for(var i = 0; i < rooms[room].players.length; i++){
		if(rooms[room].players[i].connected) numActivePlayers++;
	}
	return numActivePlayers;
}


//Recieved Sockets

io.on('connection', function(socket){
	socket.on('newRoom', function(name){
		//check name
		if(!/^[A-Z]+$/g.test(name) || name.length < 1 || name.length > 12) {
			io.to(socket.id).emit("unsuccessful_connection", "incorrect_name");
			return;
		}
		
		var room = createRoom(); //TODO add support for too many rooms
		var roomCode = generateRoomCode(room);
		rooms[room].players.push({name: name, id: socket.id, ready: false, connected: true});
		socket.join(roomCode);
		
		io.to(socket.id).emit("successful_connection", roomCode, [name], [false], name);
	});
	
	socket.on('joinRoom', function(roomCode, name){
		var room = reverseRoomCode(roomCode);
		
		//check if room exists
		if(roomCode.length != 4 || !/^[A-Z]+$/g.test(roomCode) || room == -1 || rooms[room] == undefined){
			io.to(socket.id).emit("unsuccessful_connection", "not_exist");
			return;
		}
		
		//check name
		if(!/^[A-Z]+$/g.test(name) || name.length < 1 || name.length > 12) {
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
	});
	
	socket.on('ready', function(ready){
		 var [room, player] = findPlayerFromID(socket.id);
		 
		 if(room < 0 || player < 0) return; //no need to tell client, possibly reset client side?
		 
		rooms[room].players[player].ready = ready;

		io.to(generateRoomCode(room)).emit("ready", rooms[room].players[player].name, ready);
		
		if(checkReady(room)) startRoom(room);
	});
	
	socket.on('finishedDrawing', function(drawing){
		var [room, player] = findPlayerFromID(socket.id);
		
		rooms[room].drawings.push({artist: rooms[room].players[player].name, art: drawing});
		
		var next = 1;
		
		while(rooms[room].state + next < rooms[room].players.length){
			if(rooms[room].players[rooms[room].queue[rooms[room].state + next]].connected) break;
			next++;
		}
		
		if(rooms[room].state + next < rooms[room].players.length){
		
			var nextPlayer = rooms[room].queue[rooms[room].state + next];
						
			io.to(rooms[room].players[nextPlayer].id).emit("drawing", drawing, rooms[room].players[player].name);
			setTimeout(function(){
				io.to(rooms[room].players[nextPlayer].id).emit("stopLooking");
				continueRoom(room);
			}, VIEW_TIME);
		}else{
			rooms[room].state = "end";
			startRoomTimeout(room);
			io.to(generateRoomCode(room)).emit("final_drawings", rooms[room].drawings, rooms[room].drawing);
		}
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
http.listen(PORT, function(){
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


/*
	****IMPORTANT****
	REMOVE WHEN FINISHED
*/
app.get("/print", function(req, res){
	res.send(rooms);
});