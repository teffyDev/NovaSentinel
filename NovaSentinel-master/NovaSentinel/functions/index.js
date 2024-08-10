const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.firestore.document('alerts/{alertId}')
    .onCreate((snap, context) => {
        const alert = snap.data();
        const entityId = alert.entityID; // Asegúrate de que sea 'entityID' como en la colección 'alerts'

        return admin.firestore().collection('entidades').doc(entityId).get() // Cambiado a 'entidades'
            .then(doc => {
                if (!doc.exists) {
                    console.log('No such document!');
                    return;
                }
                const token = doc.data().fcmToken; // Cambiado a 'fcmToken'

                const message = {
                    notification: {
                        title: alert.titulo, // Cambiado a 'titulo'
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

    admin.firestore().collection('entidades').doc(entityId).update({
        fcmToken: token // Cambiado a 'fcmToken'
    })
    .then(() => {
        response.send("Token updated successfully");
    })
    .catch((error) => {
        console.error("Error updating token: ", error);
        response.status(500).send(error);
    });
});
