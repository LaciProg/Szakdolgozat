@file:OptIn(ExperimentalPermissionsApi::class)

package hu.bme.aut.android.examapp.ui.camera

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun MainCameraScreen(examId: String, navigateBack: () -> Unit) {

    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    MainCameraContent(
        hasPermission = cameraPermissionState.status.isGranted,
        examId = examId,
        onRequestPermission = cameraPermissionState::launchPermissionRequest,
        navigateBack = navigateBack
    )
}

@Composable
private fun MainCameraContent(
    hasPermission: Boolean,
    examId: String,
    onRequestPermission: () -> Unit,
    navigateBack: () -> Unit
) {

    if (hasPermission) {
        CameraScreen(examId, navigateBack = navigateBack)
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}
