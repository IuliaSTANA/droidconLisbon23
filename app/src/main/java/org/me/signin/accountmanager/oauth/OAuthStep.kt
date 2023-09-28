package org.me.signin.accountmanager.oauth

import android.content.Intent

/**
 * Loading
 * Initialized with intent for launching OAuth
 * Launched && isAuthorizationLaunched == true, after intent was consumed and OAuth was started
 * ExchangingTokenRequest and isAuthorizationLaunched == false (intent response was consumed) and isFetchingToken == true
 * Authorized
 * */
sealed class OAuthStep {
    object Loading : OAuthStep()
    class Initialized(val intent: Intent) : OAuthStep()
    object Launched : OAuthStep()
    object ExchangingTokenRequest : OAuthStep()
    object Authorized : OAuthStep()
    object Error : OAuthStep()

    val isProcessing: Boolean
        get() = this is Loading || this is ExchangingTokenRequest

    override fun toString(): String = this.javaClass.simpleName
}

data class UiState(
    val oauthStep: OAuthStep,
    val error: String? = null,
)

