const httpServer = require("http").createServer();
const io = require("socket.io")(httpServer, {
	cors: {
		// The origin is the same as the Vue app domain. Change if necessary
		origin: "http://localhost:3000",
		methods: ["GET", "POST"],
		credentials: true,
	},
});

// Listens messages from this port
httpServer.listen(8081, () => {
	console.log("listening on *:8081");
});

// Starts the connection
io.on("connection", (socket) => {
	// Client connected to web socket
	console.log(`client ${socket.id} has connected`);

	// Join Rooms
	console.log("Joining Rooms");
	socket.on("loggedIn", function (user) {
		joinRoom(socket, user);
	});

	// Leave Rooms
	socket.on("loggedOut", function (user) {
		leaveRoom(socket, user);
	});

	// Occurrence Created
	socket.on("occurrenceCreated", function () {
		updateOccurrencesOnExperts(socket);
	});
});

function update(socket) {
	socket.broadcast.emit("update");
}

function updateOccurrencesOnExperts(socket) {
	socket.to("experts").emit("update");
}

function joinRoom(socket, user) {
	// Personal Room
	socket.join(user.id);
	
	// Joins user to room of specific role
	if (user.role == "Client") {
		socket.join("clients");
	} else if (user.role == "Expert") {
		socket.join("experts");
	} else if (user.role == "Repairer") {
		socket.join("repairers");
	} else if (user.role == "Administrator") {
		socket.join("administrators");
	}
}

function leaveRoom(socket, user) {
	// User Logs Out
	socket.leave(user.id);
	socket.leave("clients");
	socket.leave("chefs");
	socket.leave("deliverers");
	socket.leave("managers");
}