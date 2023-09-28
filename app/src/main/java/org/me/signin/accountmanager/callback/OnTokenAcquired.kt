package org.me.signin.accountmanager.callback

import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.content.Intent
import android.os.Bundle
import timber.log.Timber


class OnTokenAcquired : AccountManagerCallback<Bundle> {

    override fun run(result: AccountManagerFuture<Bundle>) {
        Timber.d("AccountManagerCallback with result: ${result.result}")
        // Get the result of the operation from the AccountManagerFuture.
        val bundle = result.result

        val launch: Intent? = bundle.get(AccountManager.KEY_INTENT) as? Intent
        if (launch != null) {
            // directly launch the OAuth activity, or let it be handled by the notification
        } else {
            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.
            val token = bundle.getString(AccountManager.KEY_AUTHTOKEN)
        }
    }
}