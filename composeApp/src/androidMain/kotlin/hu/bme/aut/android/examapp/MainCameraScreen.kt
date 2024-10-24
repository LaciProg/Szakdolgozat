@file:OptIn(ExperimentalPermissionsApi::class)

package hu.bme.aut.android.examapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun MainCameraScreen(savedStateHandle: SavedStateHandle) {

    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    MainCameraContent(
        hasPermission = cameraPermissionState.status.isGranted,
        savedStateHandle = savedStateHandle,
        onRequestPermission = cameraPermissionState::launchPermissionRequest
    )
}

@Composable
private fun MainCameraContent(
    hasPermission: Boolean,
    savedStateHandle: SavedStateHandle,
    onRequestPermission: () -> Unit,
) {

    if (hasPermission) {
        CameraScreen(savedStateHandle)
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}
