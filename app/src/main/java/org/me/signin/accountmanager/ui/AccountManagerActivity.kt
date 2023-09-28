package org.me.signin.accountmanager.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.me.signin.accountmanager.oauth.OAuthScreen
import org.me.signin.accountmanager.oauth.OAuthViewModel
import org.me.signin.ui.theme.SignInTheme

@AndroidEntryPoint
class AccountManagerActivity : ComponentActivity() {
    private val viewModel by viewModels<OAuthViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignInTheme {
                OAuthScreen(viewModel = viewModel) {
                    finish()
                }
            }
        }
    }
}

