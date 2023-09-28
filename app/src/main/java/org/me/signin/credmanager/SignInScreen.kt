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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.me.signin.ui.AlertDialogWithSingleButton

@Composable
fun SignInScreen(
    viewModel: CredManagerViewModel,
    onSignInDone: () -> Unit,
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
            text = "Sign In", style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        var waitForVmEvent by rememberSaveable { mutableStateOf(false) }
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        val context = LocalContext.current
        if (waitForVmEvent) {
            val currentonSignInDone by rememberUpdatedState(newValue = onSignInDone)
            LaunchedEffect(viewModel, lifecycle) {
                snapshotFlow { viewModel.uiState }.distinctUntilChanged()
                    .filter { it.complete != null }.flowWithLifecycle(lifecycle).collect {
                        waitForVmEvent = false
                        currentonSignInDone()
                        viewModel.clearCompleted()
                    }
            }
        }

        viewModel.uiState.error?.let { errorData ->
            AlertDialogWithSingleButton(
                explanation = errorData, buttonLabel = "OK", onDismiss = viewModel::dismissError
            )
        }
        SignInContent() {
            waitForVmEvent = true
            viewModel.signIn(context)
        }
    }
}


@Composable
private fun SignInContent(
    onSignIn: () -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize()
) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = onSignIn, modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sign In with passkey/saved password"
        )
    }
}