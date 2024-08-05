import com.google.auth.oauth2.GoogleCredentials
import java.io.FileInputStream

object GoogleAuthHelper {
    private const val SCOPES = "https://www.googleapis.com/auth/firebase.messaging"

    fun getAccessToken(): String {
        val credentialsPath = "path/to/service_account.json"
        val credentials = GoogleCredentials.fromStream(FileInputStream(credentialsPath))
            .createScoped(listOf(SCOPES))
        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue
    }
}
