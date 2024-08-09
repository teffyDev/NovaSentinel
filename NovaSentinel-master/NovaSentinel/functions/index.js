const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.firestore.document('alerts/{alertId}')
    .onCreate((snap, context) => {
        const alert = snap.data();
        const entityId = alert.entityId;

        return admin.firestore().collection('entities').doc(entityId).get()
            .then(doc => {
                if (!doc.exists) {
                    console.log('No such document!');
                    return;
                }
                const token = doc.data().token;

                const message = {
                    notification: {
                        title: alert.title,
                        body: alert.body
                    },
                    token: token,
                    data: {
                        latitude: alert.latitude,
                        longitude: alert.longitude
                    }
                };

                return admin.messaging().send(message);
            })
            .then(response => {
                console.log('Notification sent successfully:', response);
                return;
            })
            .catch(error => {
                console.error('Error sending notification:', error);
            });
    });

exports.updateToken = functions.https.onRequest((request, response) => {
    const token = request.body.token;
    const entityId = request.body.entityId;

    admin.firestore().collection('entities').doc(entityId).update({
        token: token
    })
    .then(() => {
        response.send("Token updated successfully");
    })
    .catch((error) => {
        console.error("Error updating token: ", error);
        response.status(500).send(error);
    });
});

