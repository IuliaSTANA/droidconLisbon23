package org.me.signin.credmanager

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.me.signin.ui.AlertDialogWithSingleButton

@Composable
fun SignUpScreen(
    viewModel: CredManagerViewModel,
    onSignUpDone: () -> Unit,
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
            text = "Create a new account", style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        var waitForVmEvent by rememberSaveable { mutableStateOf(false) }
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        val context = LocalContext.current
        if (waitForVmEvent) {
            val currentonSignUpDone by rememberUpdatedState(newValue = onSignUpDone)
            LaunchedEffect(viewModel, lifecycle) {
                snapshotFlow { viewModel.uiState }.distinctUntilChanged()
                    .filter { it.complete != null }.flowWithLifecycle(lifecycle).collect {
                        waitForVmEvent = false
                        currentonSignUpDone()
                        viewModel.clearCompleted()
                    }
            }
        }

        viewModel.uiState.error?.let { errorData ->
            AlertDialogWithSingleButton(
                explanation = errorData,
                buttonLabel = "OK",
                onDismiss = viewModel::dismissError
            )
        }

        CredManagerContent(
            viewModel.uiState,
            onPasswordChange = viewModel::onPasswordChange,
            onUsernameChange = viewModel::onUsernameChange,
            signUpWithPasskey = {
                waitForVmEvent = true
                viewModel.signupWithPasskey(context)
            },
            signUpWithPassword = {
                if (viewModel.uiState.showPassword) {
                    waitForVmEvent = true
                }
                viewModel.signupWithPassword(context)
            },
        )
    }

}

@Composable
private fun CredManagerContent(
    uiState: UiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    signUpWithPasskey: () -> Unit,
    signUpWithPassword: (Context) -> Unit,
) = Column(
    modifier = Modifier.fillMaxSize()
) {
    val context = LocalContext.current

    OutlinedTextField(
        value = uiState.username,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        label = {
            Text("Username")
        },
        onValueChange = onUsernameChange,
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(TextFieldDefaults.MinHeight)
    )

    if (uiState.showPassword) {
        OutlinedTextField(
            value = uiState.password ?: "",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = {
                Text("Password")
            },
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(TextFieldDefaults.MinHeight)
        )
    }

    Text(
        text = "Sign in to your account easily and securely with a passkey. Note: Your biometric data is only stored on your devices and will never be shared with anyone."
    )
    OutlinedButton(
        onClick = signUpWithPasskey, modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sign Up with passkey"
        )
    }

    Text(text = "OR", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

    Text(
        text = "Sign up in to your account with a password. Your password will be saved securely with your password provider."
    )
    OutlinedButton(
        onClick = { signUpWithPassword(context) }, modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sign Up with password instead"
        )
    }
}