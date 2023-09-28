package org.me.signin.keystore

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.me.signin.keystore.biometric.BiometricSignIn
import org.me.signin.keystore.biometric.LaunchFor
import org.me.signin.keystore.crypto.CryptographyManager
import javax.inject.Inject

@HiltViewModel
class BiometricViewModel @Inject constructor(
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    fun onUsernameChange(s: String) {
        uiState = uiState.copy(username = s)
    }

    fun onPasswordChange(s: String) {
        uiState = uiState.copy(password = s)
    }

    fun signIn() {
        SampleAppUser.username = uiState.username
        SampleAppUser.fakeToken = java.util.UUID.randomUUID().toString()

        uiState = uiState.copy(signedInToken = SampleAppUser.fakeToken)
    }

    fun onBiometricResult(biometricSignIn: BiometricSignIn) {
        when (biometricSignIn) {
            is BiometricSignIn.Failed -> {
                uiState = uiState.copy(error = "Failed to sign in")
            }

            BiometricSignIn.Success -> {
                SampleAppUser.username = uiState.username

                uiState = uiState.copy(signedInToken = SampleAppUser.fakeToken)
            }
        }
    }

    fun getLaunchFor(context: Context): LaunchFor {
        val cryptographyManager = CryptographyManager()
        val ciphertextWrapper = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            context, SHARED_PREFS_FILENAME, Context.MODE_PRIVATE, CIPHERTEXT_WRAPPER
        )
        return if (ciphertextWrapper != null) {
            LaunchFor.Decryption
        } else {
            LaunchFor.Encryption(uiState.username, uiState.password)
        }
    }
}