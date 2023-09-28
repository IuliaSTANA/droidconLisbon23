package org.me.signin.keystore.biometric

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import org.me.signin.keystore.BIOMETRIC_CANCELED
import org.me.signin.keystore.BIOMETRIC_ERRORCODE_KEY
import org.me.signin.keystore.BIOMETRIC_ERRORMESSAGE_KEY
import org.me.signin.keystore.BIOMETRIC_FAILED
import org.me.signin.keystore.BIOMETRIC_OK

class SignInWithBiometricsContract : ActivityResultContract<LaunchFor, BiometricSignIn>() {
    override fun createIntent(context: Context, input: LaunchFor): Intent = when (input) {
        LaunchFor.Decryption -> Intent(context, SignInWithBiometricsActivity::class.java)
        is LaunchFor.Encryption -> SignInWithBiometricsActivity.newEncryptionIntent(
            context, input.username, input.password
        )
    }

    override fun parseResult(resultCode: Int, intent: Intent?): BiometricSignIn =
        when (resultCode) {
            BIOMETRIC_OK -> BiometricSignIn.Success
            BIOMETRIC_FAILED -> {
                if (intent != null) {
                    BiometricSignIn.Failed(
                        intent.getIntExtra(
                            BIOMETRIC_ERRORCODE_KEY, BIOMETRIC_FAILED
                        ), intent.getStringExtra(BIOMETRIC_ERRORMESSAGE_KEY).orEmpty()
                    )
                } else {
                    BiometricSignIn.Failed(BIOMETRIC_FAILED, "")

                }
            }

            BIOMETRIC_CANCELED -> {
                BiometricSignIn.Failed(BIOMETRIC_CANCELED, "")
            }

            else -> BiometricSignIn.Failed(BIOMETRIC_FAILED, "")
        }

}

sealed class BiometricSignIn {
    data object Success : BiometricSignIn()
    data class Failed(val code: Int, val message: String) : BiometricSignIn()
}

sealed interface LaunchFor {
    data object Decryption : LaunchFor
    data class Encryption(val username: String, val password: String) : LaunchFor
}