package org.me.signin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AndroidApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Setup Timber
        Timber.plant(Timber.DebugTree())
    }
}
