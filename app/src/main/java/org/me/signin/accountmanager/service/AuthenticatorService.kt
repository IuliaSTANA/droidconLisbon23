package org.me.signin.accountmanager.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.me.signin.accountmanager.authenticator.Authenticator
import timber.log.Timber

class AuthenticatorService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        Timber.d("Bind authenticator to service")
        val authenticator = Authenticator(this)
        return authenticator.iBinder
    }
}
