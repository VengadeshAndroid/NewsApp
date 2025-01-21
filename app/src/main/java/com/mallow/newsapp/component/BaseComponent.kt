package com.mallow.newsapp.component

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mallow.newsapp.state.BaseState
import com.mallow.newsapp.ui.theme.NewsTheme
import com.mallow.newsapp.util.showNetworkSettings
import com.mallow.newsapp.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun <State> BaseComponent(
    viewModel: BaseViewModel<State>,
    stateObserver: (State) -> Unit,
    child: @Composable () -> Unit
) {
    val isShowLoader = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            viewModel.viewState.collect {
                stateObserver(it)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            viewModel.baseState.collect {
                when (it) {
                    is BaseState.ShowLoader -> isShowLoader.value = true
                    is BaseState.DismissLoader -> isShowLoader.value = false
                    is BaseState.ShowToast -> {
                        Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                    }

                    is BaseState.UnAuthorize -> {
                        // Navigate to Login
                    }

                    is BaseState.ShowNetworkAlert -> {
                        scope.launch {
                            snackHostState.currentSnackbarData?.dismiss()
                            val actions = snackHostState.showSnackbar(
                                message = "No Network Found!",
                                actionLabel = "Settings",
                                duration = SnackbarDuration.Short
                            )
                            if (actions == SnackbarResult.ActionPerformed) {
                                context.showNetworkSettings()
                            }
                        }
                    }
                }
            }
        }
    }

    NewsTheme {
        Scaffold(snackbarHost = {
            SnackbarHost(snackHostState,
                snackbar = { snackData ->
                    // Customizing the Snackbar
                    CustomSnackbar(snackData)
                })
        }) {
            child()
            if (isShowLoader.value) {
                Loader()
            }
        }
    }
}

@Composable
fun Loader() {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun CustomSnackbar(snackData: SnackbarData) {
    Snackbar(
        snackbarData = snackData,
        containerColor = Color(0xFF6200EE), // Background color (Purple)
        contentColor = Color.White, // Text color (White)
        actionColor = Color.Cyan, // Action label color (Cyan)
        actionContentColor = Color.Yellow, // Action content color (Yellow)
        dismissActionContentColor = Color.Red // Dismiss action color (Red)
    )
}
