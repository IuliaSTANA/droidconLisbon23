package org.me.signin.accountmanager

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AuthenticatorException
import android.accounts.OperationCanceledException
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.me.signin.BuildConfig
import org.me.signin.accountmanager.Constants.ACCOUNT_NAME
import org.me.signin.accountmanager.Constants.ACCOUNT_PASSWORD
import org.me.signin.accountmanager.callback.OnTokenAcquired
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountManagerViewModel @Inject constructor(
    @ApplicationContext context: Context
) :
    ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    private val accountManager = AccountManager.get(context)


    fun addAccount() {
        if (accountManager.getAccountsByType(BuildConfig.APPLICATION_ID).isNotEmpty()) {
            return
        } else {
            Account(ACCOUNT_NAME, BuildConfig.APPLICATION_ID).also { account ->
                //Mythbusters rule: do not do this at home!
                //https://developer.android.com/training/id-auth/custom_auth#Security
                accountManager.addAccountExplicitly(account, ACCOUNT_PASSWORD, null)
            }
            refreshAccountInfo()
        }
    }

    fun refreshAccountInfo() {
        val accountExists =
            accountManager.getAccountsByType(BuildConfig.APPLICATION_ID).isNotEmpty()
        val haveToken = if (accountExists) {
            val authToken = accountManager.peekAuthToken(
                Account(ACCOUNT_NAME, BuildConfig.APPLICATION_ID),
                BuildConfig.APPLICATION_ID
            )
            !TextUtils.isEmpty(authToken)
        } else {
            false
        }

        uiState =
            uiState.copy(
                inProgress = false,
                accounts = accountManager.accounts.toList(),
                accountExists = accountExists,
                haveToken = haveToken
            )
    }

    fun doSignIn() {
        Timber.d("Sign in for this app account")
        val account = accountManager.getAccountsByType(BuildConfig.APPLICATION_ID).first()
        val options = Bundle()

        val token = accountManager.getAuthToken(
            /* account = */ account,
            /* authTokenType = */ BuildConfig.APPLICATION_ID,
            /* options = */ options,
            /* notifyAuthFailure = */ true,
            /* callback = */ OnTokenAcquired(),
            /* handler = */ null
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val bundleAvailable = token.result
                Timber.d("Received account bundle for signing in: $bundleAvailable")
            }
        }
    }

    fun removeAccount() = viewModelScope.launch {
        uiState = uiState.copy(inProgress = true)
        val account = accountManager.getAccountsByType(BuildConfig.APPLICATION_ID).first()
        if (account != null) {
            val deleted = accountManager.removeAccount(account, null, null, null)
            withContext(Dispatchers.IO) {
                try {
                    val isDeleted = deleted.result
                    Timber.d("Did delete account? $isDeleted")
                } catch (e: AuthenticatorException) {
                    Timber.e(e, "Failed to delete account")
                } catch (e: OperationCanceledException) {
                    Timber.e(e, "Deleting account was canceled")
                } finally {
                    refreshAccountInfo()
                }
            }
        }
    }

}

