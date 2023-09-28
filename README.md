# SignIn sample app

A sample app that showcases some examples for how to get users to be signed in on Android apps.

![start screen for the app](/screenshots/start_screen.png)

The app has implementations for

* an `AccountManager` API,
* biometric prompt with a `Cipher` for securing authentication data in the `AndroidKeyStore`
* an implementation example for the `CredentialManager`

The app does not:

* implement real API calls, it only either mocks the request/response or it has NOP implementations
* does not do error handling or validate user input

## AccountManager

This part is built based on this documentation:
* [AccountManager Guide](https://developer.android.com/training/id-auth)
* [Create a custom Account](https://developer.android.com/training/id-auth/custom_auth)
* [OAuth with AppAuth](https://medium.com/androiddevelopers/authenticating-on-android-with-the-appauth-library-7bea226555d5)

## Biometric with Cipher & KeyStore

To understand more about the biometric prompt and how it works with the `CryptoObject` read:
* [Android Keystore system](https://developer.android.com/training/articles/keystore)
* [Using BiometricPrompt with CryptoObject](https://medium.com/androiddevelopers/using-biometricprompt-with-cryptoobject-how-and-why-aace500ccdb7)

## CredentialManager

This portion was ported to Compose based on the code lab:
[Credential Manager codelab](https://codelabs.developers.google.com/credential-manager-api-for-android#1)

To understand what passkeys are and how they work:
[How passkeys work](https://developers.yubico.com/Passkeys/How_passkeys_work.html)

To see a passkey implementation on web follow this code lab:
[Passkeys on web codelab](https://developers.google.com/codelabs/passkey-form-autofill#0)

To learn more about passkeys security read:
[Passkeys security](https://security.googleblog.com/2022/10/SecurityofPasskeysintheGooglePasswordManager.html)