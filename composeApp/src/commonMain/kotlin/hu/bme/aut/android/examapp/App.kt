package hu.bme.aut.android.examapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import examapp.composeapp.generated.resources.Res
import hu.bme.aut.android.examapp.service.navigation.ExamNavHost
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import examapp.composeapp.generated.resources.*

@Composable
@Preview
fun App() {
    MaterialTheme {
        NavigationComponent()
    }
}

@Composable
fun NavigationComponent() {
    MaterialTheme {
        val navController = rememberNavController()

        Scaffold() { innerPadding ->
            ExamNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }

    }
}


@Composable
internal expect fun Notify(message: String)
