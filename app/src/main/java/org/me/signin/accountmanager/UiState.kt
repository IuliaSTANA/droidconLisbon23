package org.me.signin.accountmanager

import android.accounts.Account

data class UiState(
    val inProgress: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val accountExists: Boolean = false,
    val haveToken: Boolean = false
)
