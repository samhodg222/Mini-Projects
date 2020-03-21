/*
TODO
	Bug Fixing:
		-Duplicate Name
		-Allow re log on without error
		-etc.
		
	Features:
		-Room code generator
		-Live stream to previous users
		-show all paintings at end and reveal task
		-general css and presentation improvement
		-Auto capitalise the form
		-room deletion after no users for cetain period of time
		-replay room
		-more drawings
		-timer on client side
		-more colors
		
	Extra Features:
		-Fix room generation to avoid overproduction of rooms
*/


var app = require("express")();
var http = require("http").Server(app);
var io = require("socket.io")(http);

const PORT = 3000;

var blank_room = {
	players: [],
	queue: [],
	drawings: [],
	drawing: "null",
	state: "lobby"
}

var rooms = [];
const MAX_LOBBIES = 26*26*26*26; //456976
const DRAW_TIME = 10*1000; //10 seconds to draw
const VIEW_TIME =  3*1000; //3 seconds to view

//Functions

function createRoom(){
	for(var i = 0; i < MAX_LOBBIES; i++){
		if(rooms[i] == null){
			rooms[i] = blank_room;
			rooms[i].drawing = randomDrawing();
			return i;
		}
	}
}

function generateRoomCode(index){
	//function that returns characters from index. (i.e. 0 would return AAAA, 1 would return AAAB, etc)
	return "AAAA";
}

function reverseRoomCode(roomCode){
	//returns an index based of the room code
	return 0;
}

function randomDrawing(){
	//function that returns random drawing task
	return "banana";
}

function removePlayer(room, player){
	if(rooms[room].state != "lobby") return false; //only remove player if in lobby mode (need to add option for reconnection (different socket.id)
	
	roomCode = generateRoomCode(room);

	io.to(roomCode).emit("log", rooms[room].players[player].name + " Has Disconnected!");
	io.to(roomCode).emit("removePlayer", rooms[room].players[player].name);
	
	if (player > -1) {
	  rooms[room].players.splice(player, 1);
	}
}

function findPlayerFromID(id){ //returns in form [room, player]
	for(i = 0; i < rooms.length; i++){
		for(j = 0; j < rooms[i].players.length; j++){
			if(rooms[i].players[j].id == id) return [i, j];
		}
	}
	return false;
}

function checkReady(room){
	for(i = 0; i < rooms[room].players.length; i++){
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
	
	io.to(generateRoomCode(room)).emit("draw", player);
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
	
	io.to(generateRoomCode(room)).emit("draw", player);
	setTimeout(function(){
		io.to(rooms[room].players[player].id).emit("requestDrawing");
	}, DRAW_TIME);	
	
}


//Recieved Sockets
io.on('connection', function(socket){
	socket.on('newRoom', function(name){
		var room = createRoom();
		var roomCode = generateRoomCode(room);
		rooms[room].players.push({name: name, id: socket.id, ready: false});
		socket.join(roomCode);
		
		var player_names = [];
		for(i = 0; i < rooms[room].players.length; i++){
			player_names.push(rooms[room].players[i].name);
		}
		
		io.to(roomCode).emit("log", "Room Created!");
		io.to(socket.id).emit("successful_connection", roomCode, player_names, [false], rooms[room].players.length-1);
	});
	
	socket.on('joinRoom', function(roomCode, name){
		var room = reverseRoomCode(roomCode);
		rooms[room].players.push({name: name, id: socket.id, ready: false});
		socket.join(roomCode);
				
		var player_names = [];
		for(i = 0; i < rooms[room].players.length; i++){
			player_names.push(rooms[room].players[i].name);
		}
		
		var readys = [];
		for(i = 0; i < rooms[room].players.length; i++){
			readys.push(rooms[room].players[i].ready);
		}
		
		io.to(socket.id).emit("successful_connection", roomCode, player_names, readys, rooms[room].players.length-1);
		
		socket.broadcast.to(roomCode).emit("log", name + " Has Connected!");
		socket.broadcast.to(roomCode).emit("addPlayer", name);
	});
	
	socket.on('ready', function(ready){
		[room, player] = findPlayerFromID(socket.id);
		rooms[room].players[player].ready = ready;

		io.to(generateRoomCode(room)).emit("ready", player, ready);
		
		if(checkReady(room)) startRoom(room);
	});
	
	socket.on('finishedDrawing', function(drawing){
		[room, player] = findPlayerFromID(socket.id);
		
		rooms[room].drawings.push(drawing);
		
		if(rooms[room].state + 1 < rooms[room].players.length){
		
			var nextPlayer = rooms[room].queue[rooms[room].state + 1];
						
			io.to(rooms[room].players[nextPlayer].id).emit("drawing", drawing, player);
			setTimeout(function(){
				io.to(rooms[room].players[nextPlayer].id).emit("stopLooking");
				continueRoom(room);
			}, VIEW_TIME);
		}else{
			rooms[room].state = "end";
			io.to(generateRoomCode(room)).emit("final_drawing", rooms[room].drawings.pop());
		}
	});
});

io.on('connection', function(socket){
	//console.log('a user connected');
	
	socket.on('disconnect', function(){
		for(i = 0; i < rooms.length; i++){
			for(j = 0; j < rooms[i].players.length; j++){
				if(rooms[i].players[j].id === socket.id) removePlayer(i, j);
			}
		}
	});
});

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
