package io.ib67.chafen.ui.screen.landing

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ib67.chafen.Config
import io.ib67.chafen.ui.component.CardListScaffold
import io.ib67.chafen.ui.component.CheckingCard
import io.ib67.chafen.ui.component.NotLoggedInCard
import io.ib67.chafen.ui.component.SimpleUserCard

@Preview
@Composable
private fun PreviewLandingScreen() {
    LandingScreen {}
}

@Composable
fun LandingScreen(
    viewModel: LandingViewModel = viewModel(),
    proxyScreen: @Composable () -> Unit
) {
    CardListScaffold(title = "同步游戏数据", onMenuButton = {}) {
        var loggedIn by remember { mutableStateOf(false) }
        UserCard(viewModel = viewModel) { loggedIn = it }
        if (loggedIn) {
            proxyScreen()
        }
    }
}

private const val NOT_LOGGED_IN = 0;
private const val CHECKING = 1;
private const val LOGGED_IN = 2;

@Composable
private fun UserCard(
    viewModel: LandingViewModel,
    onLoggedIn: (Boolean) -> Unit
) {
    var cardState by remember { mutableStateOf(CHECKING) }
    var _token by remember { mutableStateOf(Config.userToken) }
    when (cardState) {
        NOT_LOGGED_IN -> NotLoggedInCard(Config.userToken) {
            _token = it ?: return@NotLoggedInCard
            cardState = CHECKING
        }

        CHECKING -> CheckingCard()
        LOGGED_IN -> SimpleUserCard(userName = Config.userName) {
            Config.userName = ""
            Config.userToken = ""
            _token = ""
            cardState = NOT_LOGGED_IN
        }
    }
    val localContext = LocalContext.current
    LaunchedEffect(key1 = cardState) {
        onLoggedIn(cardState == LOGGED_IN)
        if (_token.isEmpty()) {
            cardState = NOT_LOGGED_IN
            return@LaunchedEffect
        }
        runCatching {
            viewModel.validateToken(_token) {
                Config.userName = it
                Config.userToken = _token
                cardState = LOGGED_IN
            }
        }.onFailure {
            Toast.makeText(localContext, "登录失败: ${it.message}", Toast.LENGTH_LONG)
                .apply { show() }
            it.printStackTrace()
            cardState = NOT_LOGGED_IN
        }
    }
}