package org.me.signin.keystore.biometric

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import org.me.signin.R
import org.me.signin.keystore.BIOMETRIC_CANCELED
import org.me.signin.keystore.BIOMETRIC_ERRORCODE_KEY
import org.me.signin.keystore.BIOMETRIC_ERRORMESSAGE_KEY
import org.me.signin.keystore.BIOMETRIC_FAILED
import org.me.signin.keystore.BIOMETRIC_OK
import timber.log.Timber

private var Intent.username: String?
    get() = getStringExtra("optional_username")
    set(value) {
        putExtra("optional_username", value)
    }

private var Intent.password: String?
    get() = getStringExtra("optional_password")
    set(value) {
        putExtra("optional_password", value)
    }


@AndroidEntryPoint
class SignInWithBiometricsActivity : AppCompatActivity() {
    private lateinit var biometricPrompt: BiometricPrompt
    private val viewModel by viewModels<SignInWithBiometricsViewModel>()
    private val encryptAfterSignInCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errCode, errString)
            setResult(BIOMETRIC_FAILED, Intent().apply {
                putExtra(BIOMETRIC_ERRORCODE_KEY, errCode)
                putExtra(BIOMETRIC_ERRORMESSAGE_KEY, errString)
            })
            biometricPrompt.cancelAuthentication()
            finish()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            setResult(BIOMETRIC_FAILED, Intent().apply {
                putExtra(BIOMETRIC_ERRORCODE_KEY, BIOMETRIC_FAILED)
                putExtra(BIOMETRIC_ERRORMESSAGE_KEY, getString(R.string.biometric_failed_unknown))
            })
            biometricPrompt.cancelAuthentication()
            finish()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            viewModel.encryptAndStoreServerToken(
                result,
                this@SignInWithBiometricsActivity.applicationContext
            )
            setResult(BIOMETRIC_OK)
            finish()
        }
    }
    private val decryptCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errCode, errString)
            setResult(BIOMETRIC_FAILED, Intent().apply {
                putExtra(BIOMETRIC_ERRORCODE_KEY, errCode)
                putExtra(BIOMETRIC_ERRORMESSAGE_KEY, errString)
            })
            biometricPrompt.cancelAuthentication()
            finish()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            setResult(BIOMETRIC_FAILED, Intent().apply {
                putExtra(BIOMETRIC_ERRORCODE_KEY, BIOMETRIC_FAILED)
                putExtra(BIOMETRIC_ERRORMESSAGE_KEY, getString(R.string.biometric_failed_unknown))
            })
            biometricPrompt.cancelAuthentication()
            finish()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            val ok = viewModel.decryptTokenFromStorage(result)
            if (ok) {
                setResult(BIOMETRIC_OK)
                finish()
            } else {
                setResult(BIOMETRIC_FAILED, Intent().apply {
                    putExtra(BIOMETRIC_ERRORCODE_KEY, BIOMETRIC_FAILED)
                    putExtra(BIOMETRIC_ERRORMESSAGE_KEY, "Failed to decrypt token")
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = intent.username
        val password = intent.password
        if (username != null && password != null) {
            viewModel.login(username, password)
            promptForEncryption()
        } else {
            promptForDecryption()
        }
    }

    private fun promptForDecryption() {
        try {
            val executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor, decryptCallback)
            val promptInfo = createPromptInfo()
            viewModel.getDecryptionCipher()?.let { decryptionCipher ->
                biometricPrompt.authenticate(
                    promptInfo,
                    BiometricPrompt.CryptoObject(decryptionCipher)
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to create biometric prompt for decryption")
            Toast.makeText(
                this,
                "Failed to create biometric prompt: ${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun promptForEncryption() {
        try {


            val executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor, encryptAfterSignInCallback)
            val promptInfo = createPromptInfo()
            biometricPrompt.authenticate(
                promptInfo,
                BiometricPrompt.CryptoObject(viewModel.encryptionCipher)
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to create prompt for encryption")
            Toast.makeText(
                this,
                "Failed to create biometric prompt: ${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()

        }
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle("SignIn App Authentication")
            setSubtitle("Please login to get access")
            setDescription("SignIn app is using Android biometric authentication")
            setConfirmationRequired(false)
            setNegativeButtonText(getString(R.string.auth_biometric_dialog_cancel))
        }.build()

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(BIOMETRIC_CANCELED)
    }

    companion object {
        fun newEncryptionIntent(context: Context, username: String, password: String): Intent =
            Intent(context, SignInWithBiometricsActivity::class.java).apply {
                this.username = username
                this.password = password
            }

    }
}