package org.me.signin.accountmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.me.signin.accountmanager.Constants.ACCOUNT_NAME

@Composable
fun AccountManagerScreen(
    viewModel: AccountManagerViewModel
) = Scaffold {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(key1 = lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    viewModel.refreshAccountInfo()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
        AccountContent(
            viewModel.uiState,
            doSignIn = viewModel::doSignIn,
            addAccount = viewModel::addAccount,
            removeAccount = viewModel::removeAccount
        )
    }
}

@Composable
private fun AccountContent(
    uiState: UiState,
    doSignIn: () -> Unit = {},
    addAccount: () -> Unit = {},
    removeAccount: () -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    Text(
        text = "List of accounts accessible by this app",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.SemiBold
        )
    )
    if (uiState.inProgress) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    if (uiState.accounts.isNotEmpty()) {
        LazyColumn {
            uiState.accounts.forEach { account ->
                item {
                    Text("Name: ${account.name}. Type: ${account.type}")
                }
            }
        }
    } else {
        Text(
            text = "Could not find any accessible accounts.",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
    if (uiState.accountExists) {
        if (uiState.haveToken) {
            Text(
                "User is signed in, proceed to main app",
            )
        } else {
            OutlinedButton(
                onClick = doSignIn, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Do Sign In")
            }
        }
        OutlinedButton(
            onClick = removeAccount, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Remove Account")
        }
    } else {
        Text(
            "No account is registered for this app",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        OutlinedButton(
            onClick = addAccount, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add $ACCOUNT_NAME")
        }
    }

}
