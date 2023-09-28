package org.me.signin.overview

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun OverviewScreen(
    gotToAccountManager: () -> Unit = {},
    goToKeystore: () -> Unit = {},
    goToCredManager: () -> Unit = {},
) = Scaffold {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = gotToAccountManager, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "AccountManager API", style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        OutlinedButton(
            onClick = goToKeystore, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Biometric with Cipher & Keystore",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        OutlinedButton(
            onClick = goToCredManager, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Credential Manager",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }

}
