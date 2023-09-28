package org.me.signin.keystore

data class UiState(
    val username: String = "",
    val password: String = "",
    val signedInToken: String? = null,
    val error: String? = null,
)

object SampleAppUser {
    var fakeToken: String? = null
    var username: String? = null
}

const val SHARED_PREFS_FILENAME = "biometric_prefs"
const val CIPHERTEXT_WRAPPER = "ciphertext_wrapper"

const val BIOMETRIC_FAILED = 400
const val BIOMETRIC_OK = 200
const val BIOMETRIC_CANCELED = 0
const val BIOMETRIC_ERRORCODE_KEY = "errorCodeKey"
const val BIOMETRIC_ERRORMESSAGE_KEY = "errorMessageKey"
