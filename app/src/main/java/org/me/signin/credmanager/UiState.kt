package org.me.signin.credmanager

data class UiState(
    val isSignedIn: Boolean = false,
    val didSignInWithPasskey: Boolean = false,
    val username: String = "",
    val password: String? = null,
    val showPassword: Boolean = false,
    val inProgress: Boolean = false,
    val complete: Unit? = null,
    val error: String? = null
)