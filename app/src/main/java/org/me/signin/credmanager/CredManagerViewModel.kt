package org.me.signin.credmanager

import android.content.Context
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CreatePasswordResponse
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.me.signin.storage.AppStorage
import timber.log.Timber
import java.security.SecureRandom
import javax.inject.Inject

@HiltViewModel
class CredManagerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val appStorage: AppStorage,
) : ViewModel() {

    private val credentialManager = CredentialManager.create(context)

    var uiState by mutableStateOf(UiState())
        private set

    init {
        checkIsSignedIn()
    }

    private fun checkIsSignedIn() = viewModelScope.launch {
        uiState = uiState.copy(
            isSignedIn = appStorage.isSignedIn.firstOrNull() ?: false,
            didSignInWithPasskey = appStorage.isSignedWithPasskey.firstOrNull() ?: false
        )
    }

    fun signupWithPasskey(context: Context) = viewModelScope.launch {
        val data = createPasskey(context = context)
        data?.let {
            registerResponse()
            appStorage.setSignedWithPasskey(true)
            uiState = uiState.copy(
                inProgress = false,
                isSignedIn = true,
                didSignInWithPasskey = true,
                complete = Unit
            )
        }
    }

    fun signupWithPassword(context: Context) {
        if (uiState.showPassword) {
            viewModelScope.launch {
                uiState = uiState.copy(inProgress = true)
                createPassword(context)
                delay(2000)
                appStorage.setIsSignedIn(true)
                appStorage.setSignedWithPasskey(false)
                uiState = uiState.copy(
                    inProgress = false,
                    isSignedIn = true,
                    didSignInWithPasskey = false,
                    complete = Unit
                )
            }
        } else {
            uiState = uiState.copy(showPassword = true)
        }
    }

    fun onPasswordChange(s: String) {
        uiState = uiState.copy(password = s)
    }

    fun onUsernameChange(s: String) {
        uiState = uiState.copy(username = s)
    }

    private suspend fun createPassword(context: Context) {
        val request = CreatePasswordRequest(
            uiState.username, uiState.password!!
        )
        try {
            credentialManager.createCredential(context = context, request) as CreatePasswordResponse
        } catch (e: Exception) {
            Timber.e(e, "createPassword failed with exception: ")
        }
    }

    private suspend fun createPasskey(context: Context): CreatePublicKeyCredentialResponse? {
        val request = CreatePublicKeyCredentialRequest(fetchRegistrationJsonFromServer(context))
        var response: CreatePublicKeyCredentialResponse? = null
        try {
            response = credentialManager.createCredential(
                context, request
            ) as CreatePublicKeyCredentialResponse
        } catch (e: CreateCredentialException) {
            uiState = uiState.copy(error = handlePasskeyFailure(e))
            Timber.e(e, "Failed to create passkey")
        }
        return response
    }

    private fun fetchRegistrationJsonFromServer(context: Context): String {
        //This response is based on https://w3c.github.io/webauthn/#dictdef-publickeycredentialcreationoptionsjson
        val response = context.readFromAsset("RegFromServer")

        //Update userId, name and Display name in the mock
        return response.replace("<userId>", getEncodedUserId())
            .replace("<userName>", uiState.username).replace("<userDisplayName>", uiState.username)
            .replace("<challenge>", getEncodedChallenge())
    }

    private fun getEncodedUserId(): String {
        val random = SecureRandom()
        val bytes = ByteArray(64)
        random.nextBytes(bytes)
        return Base64.encodeToString(
            bytes, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
        )
    }

    private fun getEncodedChallenge(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.encodeToString(
            bytes, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
        )
    }

    private fun registerResponse(): Boolean {
        return true
    }

    fun signOut() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true)
        appStorage.setIsSignedIn(false)
        appStorage.setSignedWithPasskey(false)
        uiState = UiState()
    }

    fun clearCompleted() {
        uiState = uiState.copy(complete = null)
    }

    private fun handlePasskeyFailure(e: CreateCredentialException): String {
        val msg = when (e) {
            is CreatePublicKeyCredentialDomException -> {
                // Handle the passkey DOM errors thrown according to the
                // WebAuthn spec using e.domError
                "An error occurred while creating a passkey, please check logs for additional details."
            }

            is CreateCredentialCancellationException -> {
                // The user intentionally canceled the operation and chose not
                // to register the credential.
                "The user intentionally canceled the operation and chose not to register the credential. Check logs for additional details."
            }

            is CreateCredentialInterruptedException -> {
                // Retry-able error. Consider retrying the call.
                "The operation was interrupted, please retry the call. Check logs for additional details."
            }

            is CreateCredentialProviderConfigurationException -> {
                // Your app is missing the provider configuration dependency.
                // Most likely, you're missing "credentials-play-services-auth".
                "Your app is missing the provider configuration dependency. Check logs for additional details."
            }

            is CreateCredentialUnknownException -> {
                "An unknown error occurred while creating passkey. Check logs for additional details."
            }

            is CreateCredentialCustomException -> {
                // You have encountered an error from a 3rd-party SDK. If you
                // make the API call with a request object that's a subclass of
                // CreateCustomCredentialRequest using a 3rd-party SDK, then you
                // should check for any custom exception type constants within
                // that SDK to match with e.type. Otherwise, drop or log the
                // exception.
                "An unknown error occurred from a 3rd party SDK. Check logs for additional details."
            }

            else -> {
                Timber.w(e, "Unexpected exception type ${e::class.java.name}")
                "An unknown error occurred."
            }
        }
        Timber.e(e, "createPasskey failed with exception: " + e.message.toString())
        return msg
    }

    fun dismissError() {
        uiState = uiState.copy(error = null)
    }

    fun signIn(context: Context) = viewModelScope.launch {
        val data = getSavedCredentials(context)
        data?.let {
            sendSignInResponseToServer()
            uiState = uiState.copy(
                isSignedIn = true,
                didSignInWithPasskey = appStorage.isSignedWithPasskey.firstOrNull() ?: false,
                complete = Unit,
            )
        }
    }

    private suspend fun getSavedCredentials(context: Context): String? {
        val getPublicKeyCredentialOption =
            GetPublicKeyCredentialOption(fetchAuthJsonFromServer(context), null)
        val getPasswordOption = GetPasswordOption()
        val result = try {
            credentialManager.getCredential(
                context,
                GetCredentialRequest(
                    listOf(
                        getPublicKeyCredentialOption,
                        getPasswordOption
                    )
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "getCredential failed with exception: " + e.message.toString())
            uiState =
                uiState.copy(error = "An error occurred while authenticating through saved credentials. Check logs for additional details")
            return null
        }

        if (result.credential is PublicKeyCredential) {
            val cred = result.credential as PublicKeyCredential
            appStorage.setSignedWithPasskey(true)
            return "Passkey: ${cred.authenticationResponseJson}"
        }
        if (result.credential is PasswordCredential) {
            val cred = result.credential as PasswordCredential
            appStorage.setSignedWithPasskey(false)
            return "Got Password - User:${cred.id} Password: ${cred.password}"
        }
        if (result.credential is CustomCredential) {
            //If you are also using any external sign-in libraries, parse them here with the
            // utility functions provided.
        }
        return null
    }

    private fun fetchAuthJsonFromServer(context: Context): String {
        return context.readFromAsset("AuthFromServer")
    }

    private fun sendSignInResponseToServer(): Boolean {
        return true
    }
}
