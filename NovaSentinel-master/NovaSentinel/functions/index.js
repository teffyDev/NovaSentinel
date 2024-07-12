const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendPushNotification = functions.https.onRequest((request, response) => {
    const message = {
        notification: {
            title: request.body.title,
            body: request.body.body
        },
        topic: "entidades_global"  // Envía la notificación a todas las entidades
    };

    admin.messaging().send(message)
        .then((response) => {
            console.log("Successfully sent message:", response);
            return response.status(200).send("Notification sent successfully");
        })
        .catch((error) => {
            console.error("Error sending message:", error);
            return response.status(500).send("Error sending message");
        });
});
