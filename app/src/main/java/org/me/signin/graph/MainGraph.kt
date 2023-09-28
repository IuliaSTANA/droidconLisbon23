import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.me.signin.accountmanager.AccountManagerScreen
import org.me.signin.accountmanager.AccountManagerViewModel
import org.me.signin.credmanager.CredManagerScreen
import org.me.signin.credmanager.CredManagerViewModel
import org.me.signin.credmanager.SignInScreen
import org.me.signin.credmanager.SignUpScreen
import org.me.signin.graph.Route
import org.me.signin.keystore.BiometricScreen
import org.me.signin.keystore.BiometricViewModel
import org.me.signin.overview.OverviewScreen

@Composable
fun MainGraph(
    navController: NavHostController,
) = NavHost(
    navController = navController, startDestination = Route.Overview.route
) {
    composable(Route.Overview.route) {
        OverviewScreen(gotToAccountManager = {
            navController.navigate(Route.AccountManagerRoute.route)
        }, goToKeystore = {
            navController.navigate(Route.BiometricRoute.route)
        }, goToCredManager = {
            navController.navigate(Route.CredManagerRoute.route)
        })
    }
    composable(route = Route.AccountManagerRoute.route) { entry ->
        val viewModel = hiltViewModel<AccountManagerViewModel>(entry)
        AccountManagerScreen(viewModel = viewModel)
    }
    composable(route = Route.BiometricRoute.route) { entry ->
        val viewModel = hiltViewModel<BiometricViewModel>(entry)
        BiometricScreen(viewModel = viewModel)
    }
    composable(route = Route.CredManagerRoute.route) { entry ->
        val viewModel = hiltViewModel<CredManagerViewModel>(entry)
        CredManagerScreen(viewModel = viewModel, goToSignUp = {
            navController.navigate(Route.SignUpRoute.route)
        }, goToSignIn = {
            navController.navigate(Route.SignInRoute.route)
        })
    }
    composable(route = Route.SignUpRoute.route) { entry ->
        val viewModel = hiltViewModel<CredManagerViewModel>(entry)
        SignUpScreen(viewModel = viewModel, onSignUpDone = {
            navController.navigate(Route.CredManagerRoute.route) {
                popUpTo(Route.CredManagerRoute.route) {
                    inclusive = true
                }
            }
        })
    }
    composable(route = Route.SignInRoute.route) { entry ->
        val viewModel = hiltViewModel<CredManagerViewModel>(entry)
        SignInScreen(viewModel = viewModel, onSignInDone = {
            navController.navigate(Route.CredManagerRoute.route) {
                popUpTo(Route.CredManagerRoute.route) {
                    inclusive = true
                }
            }
        })
    }
}