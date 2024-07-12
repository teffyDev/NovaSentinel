import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle the received message here
        Log.d("FCM", "Message received: ${remoteMessage.data}")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send the new token to your server
        Log.d("FCM", "New token: $token")
    }
}

