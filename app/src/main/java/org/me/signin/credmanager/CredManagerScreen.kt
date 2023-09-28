package org.me.signin.credmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CredManagerScreen(
    goToSignUp: () -> Unit = {},
    goToSignIn: () -> Unit = {},
    viewModel: CredManagerViewModel,
) = Scaffold {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Try passkeys demo", style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        CredManagerContent(viewModel.uiState, goToSignUp, goToSignIn, viewModel::signOut)
    }

}

@Composable
private fun CredManagerContent(
    uiState: UiState,
    goToSignUp: () -> Unit,
    goToSignIn: () -> Unit,
    signOut: () -> Unit
) {
    if (uiState.isSignedIn) {
        AuthenticatedContent(uiState.didSignInWithPasskey, signOut)
    } else {
        GetStartedContent(goToSignUp, goToSignIn)
    }
}

@Composable
private fun GetStartedContent(goToSignUp: () -> Unit, goToSignIn: () -> Unit) = Column(
    modifier = Modifier.fillMaxSize()
) {
    OutlinedButton(
        onClick = goToSignUp, modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sign Up"
        )
    }
    OutlinedButton(
        onClick = goToSignIn, modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sign In",
        )
    }
}

@Composable
private fun AuthenticatedContent(didSignWithPasskey: Boolean = false, signOut: () -> Unit = {}) =
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (didSignWithPasskey) {
            Text(
                text = "Logged in successfully through passkeys",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        } else {
            Text(
                text = "Logged with username & password",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        OutlinedButton(
            onClick = signOut, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sign out and try again",
            )
        }
    }
