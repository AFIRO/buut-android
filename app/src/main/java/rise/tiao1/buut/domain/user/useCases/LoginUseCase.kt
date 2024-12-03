package rise.tiao1.buut.domain.user.useCases

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.callback.Callback
import com.auth0.android.jwt.JWT
import com.auth0.android.result.Credentials
import dagger.hilt.android.qualifiers.ApplicationContext
import rise.tiao1.buut.R
import rise.tiao1.buut.utils.SharedPreferencesKeys
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authentication: AuthenticationAPIClient,
    private val credentialsManager: CredentialsManager,
    private val sharedPreferences: SharedPreferences,
    @ApplicationContext private val context: Context
) {

      operator fun invoke(
          email: String,
          password: String,
          onSuccess: () -> Unit,
          onError: (String) -> Unit){
        authentication.login(email, password)
            .setAudience(context.getString(R.string.com_auth0_audience))
            .start(object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    onError(error.message ?: context.getString(R.string.login_error))
                }

                override fun onSuccess(result: Credentials) {
                    val jwt = JWT(result.accessToken)
                    val userRoles = jwt.getClaim("http://schemas.microsoft.com/ws/2008/06/identity/claims/role").asArray(String::class.java)
                    if (userRoles != null && userRoles.contains("User")) {
                        credentialsManager.saveCredentials(result)
                        sharedPreferences.edit()
                            .putString(SharedPreferencesKeys.ACCESSTOKEN, result.accessToken)
                            .putString(SharedPreferencesKeys.IDTOKEN, result.idToken)
                            .apply()
                        onSuccess()
                    }
                    else
                        onError(context.getString(R.string.no_required_roles))

                }
            })

    }

}
