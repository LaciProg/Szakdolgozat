package hu.bme.aut.android.examapp.ui.camera

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.camera
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.components.UnsupportedFeatureScreen
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun MainCameraScreen(examId: String, navigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBarContent(stringResource(Res.string.camera), navigateBack)
                 },
        ){innerPadding ->
            UnsupportedFeatureScreen(modifier = Modifier.padding(innerPadding))
    }
}