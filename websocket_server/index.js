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

	// Occurrence Approved
	socket.on("occurrenceApproved", function (clientUsername) {
		occurrenceApproved(socket, clientUsername)
	});

	// Occurrence Disapproved
	socket.on("occurrenceDisapproved", function (clientUsername) {
		occurrenceDisapproved(socket, clientUsername)
	});

	// Assigned Repairer Without Approval
	socket.on("repairerAssignedWithoutNeedForApproval", function (repairerUsername) {
		repairerAssignedWithoutNeedForApproval(socket, repairerUsername)
	});

	// Assigned Repairer Needs Approval
	socket.on("repairerAssignedNeedsApproval", function () {
		repairerAssignedNeedsApproval(socket)
	});

	// Expert Approved Repairer
	socket.on("expertApprovedRepairer", function (users) {
		repairerAssignedWithoutNeedForApproval(socket, users.repairerUsername)
		occurrenceApproved(socket, users.clientUsername)
	});

	socket.on("occurrenceRepairerDisapproved", function (clientUsername) {
		occurrenceDisapproved(socket, clientUsername)
	});
});

function repairerAssignedWithoutNeedForApproval(socket, repairer) {
	socket.to(repairer).emit("occurrenceAssigned");
}

function repairerAssignedNeedsApproval(socket) {
	socket.to("experts").emit("repairerAssignedNeedsApproval");
}

function updateOccurrencesOnExperts(socket) {
	socket.to("experts").emit("update");
}

function occurrenceApproved(socket, clientUsername) {
	socket.to(clientUsername).emit("occurrenceApproved");
}

function occurrenceDisapproved(socket, clientUsername) {
	socket.to(clientUsername).emit("occurrenceDisapproved");
}

function joinRoom(socket, user) {
	// Join Personal Room
	socket.join(user.username);
	console.log(`client has joined room ${user.username}`);

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
	socket.leave(user.username);
	socket.leave("clients");
	socket.leave("chefs");
	socket.leave("deliverers");
	socket.leave("managers");
}