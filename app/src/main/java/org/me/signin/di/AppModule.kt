package org.me.signin.di

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.me.signin.accountmanager.oauth.AuthenticationAssistant
import org.me.signin.storage.AppStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    internal fun providesStorageRepository(
        @ApplicationContext context: Context,
    ) = AppStorage(context)

    @Provides
    @Singleton
    internal fun providesAuthenticationAssist(
    ) = AuthenticationAssistant()

    @Provides
    @Singleton
    internal fun provideMoshi(): Moshi = Moshi.Builder().build()
}