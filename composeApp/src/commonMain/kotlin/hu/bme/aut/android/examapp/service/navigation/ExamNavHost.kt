package hu.bme.aut.android.examapp.service.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*


@Composable
expect fun ExamNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
)
