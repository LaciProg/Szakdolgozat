package hu.bme.aut.android.examapp

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle

@Composable
expect fun MainCameraScreen(savedStateHandle: SavedStateHandle, navigateBack: () -> Unit)