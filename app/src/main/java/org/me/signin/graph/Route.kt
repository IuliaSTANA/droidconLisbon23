package org.me.signin.graph

sealed class Route(val route: String) {
    data object Overview : Route("overview")
    data object AccountManagerRoute : Route("account_manager_start")
    data object BiometricRoute : Route("android_keystore_start")
    data object CredManagerRoute : Route("cred_manager_route")
    data object SignUpRoute : Route("sign_up_route")
    data object SignInRoute : Route("sign_in_route")
}