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

	// Order Placed
	socket.on("orderPlaced", function (ticket) {
		orderPlaced(socket, ticket);
	});

	// Order Ready to Deliver
	socket.on("orderReadyToDeliver", function (obj) {
		orderReadyToDeliver(socket, obj);
	});

	// Order Delivered
	socket.on("orderDelivered", function (obj) {
		orderDelivered(socket, obj);
	});

	// New Hot Dish to Prepare
	socket.on("newHotDishToPrepare", () => {
		socket.in("chefs").emit("newHotDishToPrepare");
	});

	// Hot Dish is Preparing
	socket.on("hotDishIsPreparing", (order) => {
		hotDishIsPreparing(socket, order);
	});

	// Hot Dish is Ready
	socket.on("hotDishIsReady", (order) => {
		hotDishIsReady(socket, order);
	});

	// Order Cancelled
	socket.on("orderCancelled", (order) => {
		orderCancelled(socket, order);
	});

	// For Products
	sendBroadcastMessage(socket, "newProduct");
	sendBroadcastMessage(socket, "updateProduct");
	sendBroadcastMessage(socket, "deleteProduct");

	// Join Rooms
	socket.on("loggedIn", function (user) {
		joinRoom(socket, user);
	});

	// Leave Rooms
	socket.on("loggedOut", function (user) {
		leaveRoom(socket, user);
	});

	// User Blocked
	socket.on("userBlocked", function (users) {
		userBlocked(socket, users);
	});

	// User Unblocked
	socket.on("userUnblocked", function (users) {
		userUnblocked(socket, users);
	});

	// User Deleted
	socket.on("userDeleted", function (users) {
		userDeleted(socket, users);
	});

	// Sends message to all Users in the room except himself (User that is being updated)
	socket.on("updateUser", function (user) {
		updateUser(socket, user);
	});
});

function update(socket) {
	socket.broadcast.emit("update");
}

function orderPlaced(socket, ticket	) {
	socket.in("managers").in("chefs").in("deliverers").emit("orderPlaced", ticket);
}

function orderReadyToDeliver(socket, obj) {
	socket.in("managers").in("deliverers").emit("orderReadyToDeliver", obj);
	socket.in(obj.customerUserID).emit("orderReadyToPickUp", obj);
}

function orderCancelled(socket, order) {
	socket.in(order.customerUserID).in("managers").in("chefs").in("deliverers").emit("orderCancelled", order);
	update(socket);
}

function hotDishIsPreparing(socket, order) {
	socket.in("chefs").except(order.chef.id).emit("hotDishIsPreparing", order);
	socket.in("deliverers").emit("hotDishIsPreparing", order);
}

function hotDishIsReady(socket, order) {
	socket.in("deliverers").emit("hotDishIsReady", order);
}

function orderDelivered(socket, ticket) {
	socket.in("deliverers").emit("orderDelivered", ticket);
	update(socket);
}

function userDeleted(socket, users) {
	const user = users.user;
	socket.in(user.user_id).in("managers").emit("userDeleted", users);
	update(socket);
}

function userBlocked(socket, users) {
	const user = users.user;
	socket.in(user.user_id).in("managers").emit("userBlocked", users);
	update(socket);
}

function userUnblocked(socket, users) {
	const user = users.user;
	socket.in(user.user_id).in("managers").emit("userUnblocked", users);
	update(socket);
}

function joinRoom(socket, user) {
	// Personal Room
	socket.join(user.id);
	// Joins user to room
	if (user.type == "EC") {
		socket.join("chefs");
		socket.in("chefs").except(user.id).emit("joinedRoom", user);
	} else if (user.type == "ED") {
		socket.join("deliverers");
		socket.in("deliverers").except(user.id).emit("joinedRoom", user);
	} else if (user.type == "EM") {
		socket.join("managers");
		socket.in("managers").except(user.id).emit("joinedRoom", user);
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

function updateUser(socket, user) {
	// Sends message to all Users in the room except himself (User that is being updated)
	socket.in("managers").except(user.id).emit("updateUser", user);
	socket.in(user.id).emit("updateUser", user);
	socket.broadcast.emit("update");
}

function sendBroadcastMessage(socket, message) {
	// Send the 'message' event to all clients except for the one that emitted the event.
	socket.on(message, (data) => {
		socket.broadcast.emit(message, data);
	});
}
