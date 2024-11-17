package hu.bme.aut.android.examapp.ui.camera

import androidx.compose.runtime.Composable

@Composable
expect fun CameraScreen(examId: String = "0", navigateBack: ()-> Unit)