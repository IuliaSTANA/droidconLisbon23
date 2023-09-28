package org.me.signin.keystore.biometric

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.me.signin.keystore.CIPHERTEXT_WRAPPER
import org.me.signin.keystore.SHARED_PREFS_FILENAME
import org.me.signin.keystore.SampleAppUser
import org.me.signin.keystore.crypto.CryptographyManager
import timber.log.Timber
import javax.crypto.Cipher
import javax.inject.Inject

@HiltViewModel
class SignInWithBiometricsViewModel @Inject constructor(
    @ApplicationContext val applicationContext: Context
) : ViewModel() {
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext, SHARED_PREFS_FILENAME, Context.MODE_PRIVATE, CIPHERTEXT_WRAPPER
        )

    val encryptionCipher = cryptographyManager.getInitializedCipherForEncryption(secret_key_name)


    fun getDecryptionCipher(): Cipher? {
        return ciphertextWrapper?.let { textWrapper ->
            cryptographyManager.getInitializedCipherForDecryption(
                secret_key_name, textWrapper.initializationVector
            )
        }
    }

    fun encryptAndStoreServerToken(
        authResult: BiometricPrompt.AuthenticationResult, applicationContext: Context
    ) {
        Timber.d("With biometrics confirmed, we now have a cipher object we can use to encrypted the previously received server token.")
        authResult.cryptoObject?.cipher?.apply {
            SampleAppUser.fakeToken?.let { token ->
                Timber.d("The token from server is $token")
                val encryptedServerTokenWrapper = cryptographyManager.encryptData(token, this)
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    applicationContext,
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
                )
            }
        }
    }

    fun decryptTokenFromStorage(authResult: BiometricPrompt.AuthenticationResult): Boolean {
        Timber.d("With biometrics confirmed, we can use the cipher to decrypt the token we encrypted earlier.")
        return ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val plaintext = cryptographyManager.decryptData(textWrapper.ciphertext, it)
                SampleAppUser.fakeToken = plaintext
                true
            }
        } ?: false
    }

    fun login(username: String, password: String) {
        Timber.d("Do the fake login, pretend we get a token back from the server")
        SampleAppUser.username = username
        SampleAppUser.fakeToken = java.util.UUID.randomUUID().toString()
    }
}

private const val secret_key_name = "signin_biometric_sample_key_name"