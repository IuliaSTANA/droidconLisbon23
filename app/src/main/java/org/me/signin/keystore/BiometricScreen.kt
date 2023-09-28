package org.me.signin.keystore

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.me.signin.keystore.biometric.BiometricSignIn
import org.me.signin.keystore.biometric.LaunchFor
import org.me.signin.keystore.biometric.SignInWithBiometricsContract

@Composable
fun BiometricScreen(
    viewModel: BiometricViewModel
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
            text = "Biometric with Cipher & Keystore",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        AndroidKeystoreContent(
            uiState = viewModel.uiState,
            onUsernameChange = viewModel::onUsernameChange,
            onPasswordChange = viewModel::onPasswordChange,
            doSignIn = viewModel::signIn,
            onBiometricResult = viewModel::onBiometricResult,
            getLaunchFor = viewModel::getLaunchFor
        )
    }

}

@Composable
private fun AndroidKeystoreContent(
    uiState: UiState,
    onUsernameChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    doSignIn: () -> Unit = {},
    onBiometricResult: (BiometricSignIn) -> Unit = {},
    getLaunchFor: (Context) -> LaunchFor
) = Column(
    modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)
) {

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

    OutlinedTextField(
        value = uiState.password,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
        ),
        label = {
            Text("Password")
        },
        onValueChange = onPasswordChange,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(TextFieldDefaults.MinHeight)
    )
    if (uiState.signedInToken != null) {
        Text("You are now signed in and have ${uiState.signedInToken} token")
    }
    OutlinedButton(
        onClick = doSignIn, modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sign In", style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
    Text(text = "Or", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    val launchBiometricSignIn = rememberLauncherForActivityResult(
        contract = SignInWithBiometricsContract(),
    ) { result ->
        when (result) {
            is BiometricSignIn.Failed -> {}
            is BiometricSignIn.Success -> {
                onBiometricResult(result)
            }
        }
    }
    val context = LocalContext.current
    OutlinedButton(
        onClick = { launchBiometricSignIn.launch(getLaunchFor(context)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sign In with Biometrics", style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
